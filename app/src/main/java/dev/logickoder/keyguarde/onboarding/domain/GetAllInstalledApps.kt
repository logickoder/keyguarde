package dev.logickoder.keyguarde.onboarding.domain

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

const val WhatsappPackageName = "com.whatsapp"
const val TelegramPackageName = "org.telegram.messenger"

private val priorityApps = listOf(
    WhatsappPackageName,
    TelegramPackageName,
    "org.thoughtcrime.securesms", // Signal
    "com.android.mms", // SMS/Messages
    "com.google.android.gm" // Gmail
)

/**
 * Get all installed apps that can post notifications on the device.
 *
 * @return List of AppInfo containing app name, package name, and icon.
 */
@SuppressLint("QueryPermissionsNeeded")
fun Context.getInstalledApps() = buildList {
    val installedApplications = packageManager.getInstalledApplications(
        PackageManager.GET_META_DATA
    )
    for (app in installedApplications) {
        if (packageManager.checkPermission(
                android.Manifest.permission.POST_NOTIFICATIONS,
                app.packageName
            ) == PackageManager.PERMISSION_GRANTED
            && app.packageName != packageName
        ) {
            add(
                AppInfo(
                    name = packageManager.getApplicationLabel(app).toString(),
                    app.packageName,
                    icon = packageManager.getApplicationIcon(app)
                )
            )
        }
    }
}.sortedWith(
    compareByDescending<AppInfo> {
        it.packageName in priorityApps
    }.thenBy {
        it.name.lowercase()
    }
)