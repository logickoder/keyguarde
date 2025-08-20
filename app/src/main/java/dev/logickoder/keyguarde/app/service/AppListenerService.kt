package dev.logickoder.keyguarde.app.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.ai.SemanticMatchingService
import dev.logickoder.keyguarde.settings.SettingsRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class AppListenerService : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val repository by lazy { AppRepository.getInstance(this) }
    private val settings by lazy { SettingsRepository.getInstance(this) }
    private val semanticMatchingService by lazy { SemanticMatchingService.getInstance(this) }

    private var watchedPackages = emptySet<String>()
    private var keywords = emptyList<Pair<String, Regex>>()
    private var allKeywords = emptyList<Keyword>()
    private var showHeadsUpNotifications = true
    private var usePersistentSilentNotification = true

    private var componentName: ComponentName? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Napier.i { "Listener start command received" }

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let {
            requestRebind(it)
            NotificationHelper.startListenerService(this)
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Napier.i { "Listener created" }
        NotificationHelper.createNotificationChannels(this)
        loadSettings()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Napier.i { "Listener connected" }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Skip if notification is from this app
        if (sbn.packageName == packageName) return

        // Early return if keywords or watched packages are empty
        if (keywords.isEmpty() || watchedPackages.isEmpty()) return

        // Check if this package is monitored
        if (!watchedPackages.contains(sbn.packageName)) return

        // Extract notification text
        val notification = sbn.notification
        val title = extractTitle(sbn.packageName, notification.extras) ?: return
        val text =
            notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText =
            notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
                .orEmpty()

        // Use the longer text between text and bigText
        val notificationText = if (bigText.length > text.length) bigText else text
        if (notificationText.isBlank()) return

        // Check for keywords
        checkForKeywords(title, notificationText, sbn)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        Napier.i { "Listener disconnected" }

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let { requestRebind(it) }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Napier.i { "Listener removed" }
        // Set an alarm to restart the service
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent
        )
    }

    private fun loadSettings() {
        scope.launch {
            repository.watchedApps.collectLatest { apps ->
                watchedPackages = apps.map { it.packageName }.toSet()
            }
        }

        scope.launch {
            repository.keywords.collectLatest { words ->
                allKeywords = words
                keywords = words.map { keyword ->
                    keyword.word to "\\b${Regex.escape(keyword.word)}\\b".toRegex(RegexOption.IGNORE_CASE)
                }
            }
        }

        scope.launch {
            settings.showHeadsUpAlert.collectLatest {
                showHeadsUpNotifications = it
            }
        }

        scope.launch {
            settings.usePersistentSilentNotification.collectLatest {
                usePersistentSilentNotification = it
            }
        }
    }

    private fun extractTitle(packageName: String, extras: Bundle): String? {
        return when (packageName) {
            WHATSAPP_PACKAGE_NAME -> {
                // WhatsApp groups typically show the group name in EXTRA_CONVERSATION_TITLE
                val raw = extras.getString(Notification.EXTRA_CONVERSATION_TITLE)
                    ?: extras.getString(Notification.EXTRA_TITLE)
                raw?.replace(
                    Regex(
                        "\\s*\\(\\d+\\s+(new\\s+)?messages?\\)",
                        RegexOption.IGNORE_CASE
                    ), ""
                )?.trim()
            }

            TELEGRAM_PACKAGE_NAME -> {
                // Telegram often uses EXTRA_TITLE for the group name
                val raw = extras.getString(Notification.EXTRA_TITLE)
                raw?.replace(Regex("\\s*\\(\\d+\\)", RegexOption.IGNORE_CASE), "")?.trim()
            }

            else -> {
                // Fallback for other apps – try multiple fields in order
                val fallbackKeys = listOf(
                    Notification.EXTRA_SUB_TEXT,
                    Notification.EXTRA_TITLE,
                    Notification.EXTRA_CONVERSATION_TITLE
                )

                fallbackKeys.mapNotNull { extras.getString(it) }
                    .firstOrNull { it.isNotBlank() }
                    ?.replace(
                        Regex("\\s*\\(\\d+\\s*(new\\s+)?messages?\\)", RegexOption.IGNORE_CASE),
                        ""
                    ) // remove (28 messages)
                    ?.replace(Regex("^[^:]+:\\s*"), "") // remove "Jeffery: Hello" style
                    ?.trim()
            }
        }
    }

    private fun checkForKeywords(title: String, text: String, notification: StatusBarNotification) {
        // Exact keyword matching (existing functionality)
        val exactMatchedKeywords = keywords.filter { (_, pattern) ->
            pattern.containsMatchIn(text)
        }.map { it.first }.toSet()

        // Semantic keyword matching (new AI functionality)
        val semanticMatchedKeywords = try {
            semanticMatchingService.findSemanticMatches(text, allKeywords)
        } catch (e: Exception) {
            Napier.e(e) { "Error during semantic matching" }
            emptySet()
        }

        // Combine both types of matches
        val allMatchedKeywords = exactMatchedKeywords + semanticMatchedKeywords

        if (allMatchedKeywords.isEmpty()) return

        // Get app name for better display
        val appName = try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    notification.packageName,
                    0
                )
            )
                .toString()
        } catch (_: Exception) {
            notification.packageName
        }

        scope.launch {
            // save matches to db
            val result = repository.addKeywordMatch(
                KeywordMatch(
                    keywords = allMatchedKeywords,
                    app = notification.packageName,
                    chat = title,
                    message = text,
                    timestamp = LocalDateTime.ofEpochSecond(
                        notification.notification.`when` / 1000,
                        0,
                        ZoneId.systemDefault().rules.getOffset(Instant.now())
                    ),
                )
            )

            if (result == -1L) {
                return@launch
            }

            // Create a pending intent for the notification
            _notificationIntents.update { prev ->
                val pendingIntent = notification.notification.contentIntent
                when (pendingIntent) {
                    null -> prev
                    else -> prev + (result to pendingIntent)
                }
            }

            // Update match counters
            updateMatchCounters(appName)

            // Show heads-up notification for matched keywords (if enabled)
            if (showHeadsUpNotifications) {
                NotificationHelper.showKeywordMatchNotification(
                    context = this@AppListenerService,
                    keywords = allMatchedKeywords,
                    sourceName = title.ifBlank { appName },
                    showHeadsUp = true
                )
            }
        }
    }

    private suspend fun updateMatchCounters(sourceName: String) {
        // Get current values
        val recentMatchCount = repository.recentMatchCount.first()
        val sources = repository.recentChats.first().toMutableSet()

        // Add the new source name and update match count
        sources.add(sourceName)

        // Save updated values
        repository.updateRecentMatchCount(recentMatchCount + 1)
        repository.updateRecentChats(sources)

        // Update the persistent notification
        if (usePersistentSilentNotification) {
            NotificationHelper.showPersistentNotification(
                this@AppListenerService,
                recentMatchCount + 1,
                sources.size
            )
        }
    }

    companion object {
        /**
         * A flow that holds the pending intents for notifications.
         */
        private val _notificationIntents = MutableStateFlow(emptyMap<Long, PendingIntent>())
        val notificationIntents: StateFlow<Map<Long, PendingIntent>>
            get() = _notificationIntents
    }
}