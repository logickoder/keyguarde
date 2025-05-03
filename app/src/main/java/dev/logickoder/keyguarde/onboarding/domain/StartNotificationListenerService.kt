package dev.logickoder.keyguarde.onboarding.domain

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import dev.logickoder.keyguarde.app.AppListenerService

fun Context.startListenerService() = runCatching {
    if (isListenerServiceEnabled()) {
        startService(Intent(this, AppListenerService::class.java))
    }
}

fun Context.isListenerServiceEnabled(): Boolean {
    val flat = Settings.Secure.getString(
        contentResolver,
        "enabled_notification_listeners"
    )
    if (!TextUtils.isEmpty(flat)) {
        val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (i in names.indices) {
            val name = ComponentName.unflattenFromString(names[i]) ?: continue
            if (TextUtils.equals(packageName, name.packageName)) {
                return true
            }
        }
    }
    return false
}

fun Context.launchListenerSettings() {
    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
}