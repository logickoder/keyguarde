package dev.logickoder.keyguarde.app.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.MainActivity
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.app.service.AppListenerService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

/**
 * Helper class to manage all notification-related functionality
 */
object NotificationHelper {
    // Channel IDs
    private const val CHANNEL_ID_BACKGROUND = "background_info"
    private const val CHANNEL_ID_MATCH_ALERTS = "match_alerts"

    // Notification IDs
    private const val NOTIFICATION_ID_PERSISTENT = 1001
    private const val NOTIFICATION_ID_MATCH = 2001

    // Action IDs
    private const val ACTION_RESET_COUNT = "${BuildConfig.APPLICATION_ID}.RESET_COUNT"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val PERMISSION = Manifest.permission.POST_NOTIFICATIONS

    val REQUIRES_NOTIFICATION_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    /**
     * Create notification channels required by the app (Android 8.0+)
     */
    fun createNotificationChannels(context: Context) {
        // Background channel (low importance, for persistent notification)
        val backgroundChannel = NotificationChannel(
            CHANNEL_ID_BACKGROUND,
            context.getString(R.string.channel_background_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.channel_background_description)
            setShowBadge(false)
            enableLights(false)
            enableVibration(false)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        // Match alerts channel (high importance, for keyword matches)
        val matchAlertsChannel = NotificationChannel(
            CHANNEL_ID_MATCH_ALERTS,
            context.getString(R.string.channel_match_alerts_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.channel_match_alerts_description)
            setShowBadge(true)
            enableLights(true)
            lightColor = ContextCompat.getColor(context, R.color.primary)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 250, 250)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannels(
            listOf(
                backgroundChannel,
                matchAlertsChannel
            )
        )
    }

    /**
     * Create the persistent silent notification showing keyword match count
     *
     * @param context Application context
     * @param matchCount Number of matches found since last app open
     * @param chatCount Number of chats with matches
     * @return The built notification object
     */
    @SuppressLint("MissingPermission")
    fun showPersistentNotification(
        context: Context,
        matchCount: Int = 0,
        chatCount: Int = 0
    ) {
        if (!isNotificationPermissionGranted(context)) {
            return
        }

        // Main pending intent (opens the app)
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        // Reset action pending intent
        val resetIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_RESET_COUNT).setPackage(context.packageName),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Notification content
        val title = context.getString(R.string.persistent_notification_title)
        val content = when {
            matchCount <= 0 -> context.getString(R.string.persistent_notification_content_no_matches)
            chatCount == 1 -> context.resources.getQuantityString(
                R.plurals.persistent_notification_matches,
                matchCount,
                matchCount,
                chatCount
            )

            else -> context.resources.getQuantityString(
                R.plurals.persistent_notification_matches_multiple_chats,
                matchCount,
                matchCount,
                chatCount
            )
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BACKGROUND)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setColor(ContextCompat.getColor(context, R.color.primary))
            .setOngoing(true) // Makes it persistent
            .setOnlyAlertOnce(true)
            .setSilent(true) // No sound or vibration
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_reset,
                context.getString(R.string.action_reset),
                resetIntent
            )
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PERSISTENT, notification)
    }

    /**
     * Show a heads-up notification when a keyword match is detected
     *
     * @param context Application context
     * @param keywords The matched keywords
     * @param sourceName Chat/group/app where the match was found
     * @param showHeadsUp Whether to show as heads-up (user toggleable)
     */
    @SuppressLint("MissingPermission")
    fun showKeywordMatchNotification(
        context: Context,
        keywords: Set<String>,
        sourceName: String,
        showHeadsUp: Boolean = true
    ) {
        if (!isNotificationPermissionGranted(context)) {
            return
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("KEYWORD_MATCHES", keywords.joinToString(", "))
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        // Use proper pluralization for keyword match titles
        val title = when {
            keywords.size > 3 -> {
                // Use count-based pluralization for many keywords
                context.resources.getQuantityString(
                    R.plurals.keyword_match_notification_title_count,
                    keywords.size,
                    keywords.size
                )
            }

            else -> {
                // Use keyword list with proper singular/plural
                context.resources.getQuantityString(
                    R.plurals.keyword_match_notification_title_plural,
                    keywords.size,
                    keywords.joinToString(", ")
                )
            }
        }
        val content = context.getString(R.string.keyword_match_notification_content, sourceName)

        // Build the notification with or without heads-up based on user preference
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MATCH_ALERTS)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(content)
            .setColor(ContextCompat.getColor(context, R.color.primary))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        // Add app icon as large icon if desired
        context.packageManager.getApplicationIcon(context.packageName).let {
            builder.setLargeIcon(IconCompat.createWithBitmap(it.toBitmap()).toIcon(context))
        }

        // Set priority based on user preference for heads-up
        if (showHeadsUp) {
            builder.priority = NotificationCompat.PRIORITY_HIGH
        } else {
            builder.priority = NotificationCompat.PRIORITY_DEFAULT
            builder.setSilent(true)
        }

        // Show the notification
        val notificationId = NOTIFICATION_ID_MATCH + System.currentTimeMillis().toInt() % 1000
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    /**
     * Register a broadcast receiver to listen for the reset count action
     * This should be called in your service or activity
     */
    fun registerResetCountReceiver(context: Context): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == ACTION_RESET_COUNT) {
                    AppScope.launch {
                        ResetMatchCountUsecase(context)
                    }
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(ACTION_RESET_COUNT),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        return receiver
    }

    fun startListenerService(context: Context) {
        try {
            val pm = context.packageManager
            val componentName = ComponentName(context, AppListenerService::class.java)

            pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            Napier.e("Failed to trigger rebind", e)
        }
    }

    fun requestListenerServiceRebind(context: Context) {
        if (!isListenerServiceEnabled(context)) {
            return
        }

        try {
            NotificationListenerService.requestRebind(
                ComponentName(
                    context,
                    AppListenerService::class.java
                )
            )
        } catch (e: Exception) {
            Napier.e(e) { "Failed to rebind notification listener" }
        }
    }

    fun isListenerServiceEnabled(context: Context): Boolean {
        val enabledListeners =
            Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        val componentName = ComponentName(context, AppListenerService::class.java).flattenToString()
        return enabledListeners?.split(":")?.contains(componentName) == true
    }

    fun launchListenerSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    @Composable
    fun requestNotificationPermissionLauncher(onResult: (Boolean) -> Unit) =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = onResult
        )

    @SuppressLint("NewApi")
    fun canRequestNotificationPermission(activity: Activity): Boolean {
        return when (REQUIRES_NOTIFICATION_PERMISSION) {
            true -> ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                PERMISSION
            )

            else -> false
        }
    }

    @SuppressLint("NewApi")
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return when (REQUIRES_NOTIFICATION_PERMISSION) {
            true -> ContextCompat.checkSelfPermission(
                context,
                PERMISSION
            ) == PackageManager.PERMISSION_GRANTED

            else -> true // Permissions are granted by default on versions below Android 13
        }
    }
}