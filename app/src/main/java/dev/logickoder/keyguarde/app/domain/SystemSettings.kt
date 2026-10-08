package dev.logickoder.keyguarde.app.domain

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.net.toUri

fun appNotificationSettings(context: Context): Intent =
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)

private fun batteryOptimizationSettings(): Intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)

/**
 * Where the user lifts Keyguarde's battery limit. Android 12+ has it on the app's own page under
 * App battery usage; older versions only have the list of every app.
 */
fun appBatterySettings(context: Context): Intent = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    )
    else -> batteryOptimizationSettings()
}

/** Keyguarde's page in Android settings, where makers put autostart and background switches. */
fun appDetailsSettings(context: Context): Intent =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))

/** Whether Android lets Keyguarde run in the background without battery limits. */
fun isBatteryUnrestricted(context: Context): Boolean =
    context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName)

// Some OEM builds drop settings screens; fall back to the app's own details page.
fun Context.startActivitySafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        )
    }
}

/**
 * Opens Keyguarde's Play Store page, in the Play Store app when it's installed, else the browser.
 * Not the in-app review API: Google rate-limits its dialog, so a tap could show nothing.
 */
fun Context.openStoreListing() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()))
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri())
        )
    }
}
