package dev.logickoder.keyguarde.app.service

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class AppListenerService : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val repository by lazy { AppRepository.getInstance(this) }
    private val settings by lazy { SettingsRepository.getInstance(this) }

    private var watchedPackages = emptySet<String>()
    private var keywords = emptyList<Pair<String, Regex>>()
    private var showHeadsUpNotifications = true
    private var usePersistentSilentNotification = true

    private var componentName: ComponentName? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let {
            requestRebind(it)
            NotificationHelper.startListenerService(this)
        }
        return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Skip if notification is from this app
        if (sbn.packageName == packageName) return

        scope.launch {
            if (!fetch) {
                fetch = true
                loadSettings()
            }

            // Check if this package should be monitored
            if (!watchedPackages.contains(sbn.packageName)) return@launch

            // Extract notification text
            val notification = sbn.notification
            val title = extractTitle(sbn.packageName, notification.extras) ?: return@launch
            val text =
                notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
            val bigText =
                notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

            // Use the longer text between text and bigText
            val notificationText = if (bigText.length > text.length) bigText else text
            if (notificationText.isBlank()) return@launch

            // Check for keywords
            checkForKeywords(title, notificationText, sbn)
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let { requestRebind(it) }
    }

    private suspend fun loadSettings() {
        // Load monitored apps
        watchedPackages = repository.watchedApps.first().map { it.packageName }.toSet()

        // Load keywords
        keywords = repository.keywords.first().map { (word) ->
            word to "\\b${Regex.escape(word)}\\b".toRegex(RegexOption.IGNORE_CASE)
        }

        // Load notification preferences
        showHeadsUpNotifications = settings.showHeadsUpAlert.first()
        usePersistentSilentNotification = settings.usePersistentSilentNotification.first()
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
        val matchedKeywords = keywords.filter { (_, pattern) ->
            pattern.containsMatchIn(text)
        }.map { it.first }.toSet()

        if (matchedKeywords.isEmpty()) return

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
                    keywords = matchedKeywords,
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

            // Update match counters
            updateMatchCounters(appName)

            // Show heads-up notification for matched keywords (if enabled)
            if (showHeadsUpNotifications) {
                NotificationHelper.showKeywordMatchNotification(
                    context = this@AppListenerService,
                    keywords = matchedKeywords,
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
        var fetch = false
    }
}