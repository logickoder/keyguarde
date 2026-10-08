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
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.RecentMatches
import dev.logickoder.keyguarde.app.data.saveChatAvatar
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.home.domain.keywordRegex
import io.github.aakira.napier.Napier
import java.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListenerService : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val repository by lazy { AppContainer.from(this).appRepository }
    private val settings by lazy { AppContainer.from(this).settingsRepository }

    private var watchedPackages = emptySet<String>()
    private var keywords = emptyList<Pair<String, Regex>>()
    private var showHeadsUpNotifications = true
    private var isPaused = false

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
        _isConnected.update { true }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Skip if notification is from this app, except the setup test, which is reported back
        // (not saved) and cleared so it doesn't linger in the shade.
        if (sbn.packageName == packageName) {
            if (sbn.notification.extras.getBoolean(NotificationHelper.EXTRA_SETUP_TEST)) {
                val text = sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
                _setupTestReceived.tryEmit(text)
                cancelNotification(sbn.key)
            }
            return
        }

        if (isPaused) return

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
        _isConnected.update { false }

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
                // One pattern per word, so a match can report which keywords it caught.
                keywords = words.mapNotNull { (word) -> keywordRegex(listOf(word))?.let { word to it } }
            }
        }

        scope.launch {
            settings.showHeadsUpAlert.collectLatest {
                showHeadsUpNotifications = it
            }
        }

        scope.launch {
            settings.isPaused.collectLatest { isPaused = it }
        }

        scope.launch {
            // The one place the count notification is shown or removed: everything else only
            // changes the stored count and settings. While paused it would read as "still
            // watching", so it hides; a reset to zero clears it.
            combine(
                repository.recentMatchCount,
                repository.recentChats,
                settings.isPaused,
                settings.usePersistentSilentNotification,
            ) { count, chats, paused, enabled ->
                RecentMatches(count, chats.size).takeIf { enabled && !paused && count > 0 }
            }.distinctUntilChanged().collect { recent ->
                when (recent) {
                    null -> NotificationHelper.cancelPersistentNotification(this@AppListenerService)
                    else -> NotificationHelper.showPersistentNotification(
                        this@AppListenerService,
                        recent.count,
                        recent.sourceCount,
                    )
                }
            }
        }
    }

    private fun extractTitle(packageName: String, extras: Bundle): String? {
        return when (packageName) {
            WHATSAPP_PACKAGE_NAME -> {
                // WhatsApp groups typically show the group name in EXTRA_CONVERSATION_TITLE
                val raw = extras.getString(Notification.EXTRA_CONVERSATION_TITLE)
                    ?: extras.getString(Notification.EXTRA_TITLE)
                raw?.replace(WhatsAppMessageCount, "")?.trim()
            }

            TELEGRAM_PACKAGE_NAME -> {
                // Telegram often uses EXTRA_TITLE for the group name
                val raw = extras.getString(Notification.EXTRA_TITLE)
                raw?.replace(TelegramMessageCount, "")?.trim()
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
                    ?.replace(MessageCount, "") // remove (28 messages)
                    ?.replace(SenderPrefix, "") // remove "Jeffery: Hello" style
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
                    timestamp = Instant.ofEpochMilli(notification.notification.`when`),
                )
            )

            if (result == -1L) {
                return@launch
            }

            repository.recordCatch(chat = title.ifBlank { appName })
            cacheChatAvatar(notification, title)

            // Create a pending intent for the notification
            _notificationIntents.update { prev ->
                val pendingIntent = notification.notification.contentIntent
                when (pendingIntent) {
                    null -> prev
                    else -> prev + (result to pendingIntent)
                }
            }

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

    /**
     * Keeps the sender's or group's picture (the notification's large icon) for the Matches list.
     */
    private suspend fun cacheChatAvatar(notification: StatusBarNotification, chat: String) {
        val icon = notification.notification.getLargeIcon() ?: return
        withContext(Dispatchers.IO) {
            try {
                icon.loadDrawable(this@AppListenerService)?.let { drawable ->
                    saveChatAvatar(this@AppListenerService, notification.packageName, chat, drawable)
                }
            } catch (e: Exception) {
                Napier.w(e) { "Could not cache chat avatar" }
            }
        }
    }

    companion object {
        /**
         * A flow that holds the pending intents for notifications.
         */
        private val _notificationIntents = MutableStateFlow(emptyMap<Long, PendingIntent>())
        val notificationIntents: StateFlow<Map<Long, PendingIntent>>
            get() = _notificationIntents

        private val _setupTestReceived = MutableSharedFlow<String>(extraBufferCapacity = 1)

        /** The text of each setup test notification the listener receives. */
        val setupTestReceived: SharedFlow<String> = _setupTestReceived.asSharedFlow()

        private val _isConnected = MutableStateFlow(false)

        /**
         * Whether the system has bound this listener in the current process. A process killed to
         * save battery gets no disconnect callback, so this starts false and only the connect
         * callback can make it true.
         */
        val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    }
}

// Title clean-up, compiled once instead of on every notification.
private val WhatsAppMessageCount = Regex("\\s*\\(\\d+\\s+(new\\s+)?messages?\\)", RegexOption.IGNORE_CASE)
private val TelegramMessageCount = Regex("\\s*\\(\\d+\\)")
private val MessageCount = Regex("\\s*\\(\\d+\\s*(new\\s+)?messages?\\)", RegexOption.IGNORE_CASE)
private val SenderPrefix = Regex("^[^:]+:\\s*")
