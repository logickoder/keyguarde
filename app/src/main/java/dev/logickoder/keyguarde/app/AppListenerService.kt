package dev.logickoder.keyguarde.app

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.github.aakira.napier.Napier

class AppListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            Napier.d("Notification received: ${it.notification.tickerText}")
            // Add logic to process the notification
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Napier.d("Notification removed: ${sbn?.notification?.tickerText}")
    }
}