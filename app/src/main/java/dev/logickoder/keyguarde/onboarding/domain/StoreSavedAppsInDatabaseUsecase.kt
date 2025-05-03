package dev.logickoder.keyguarde.onboarding.domain

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import dev.logickoder.keyguarde.app.data.AppDatabase
import dev.logickoder.keyguarde.app.data.model.SelectedApp
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class StoreSavedAppsInDatabaseUsecase(private val context: Context) {
    private val priorityApps = listOf(
        WHATSAPP_PACKAGE_NAME,
        TELEGRAM_PACKAGE_NAME,
        "org.thoughtcrime.securesms", // Signal
        "com.android.mms", // SMS/Messages
        "com.google.android.gm" // Gmail
    )

    suspend operator fun invoke(apps: List<AppInfo>) = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getInstance(context)
            apps.forEach { app ->
                val icon = saveIconToFile(
                    drawableToBitmap(app.icon),
                    app.packageName,
                    context
                )
                database.selectedAppDao().insert(
                    SelectedApp(
                        packageName = app.packageName,
                        name = app.name,
                        icon = icon
                    )
                )
            }
        } catch (e: Exception) {
            Napier.e("Failed to store apps in the database", e)
        }
    }

    /**
     * Get all installed apps that can post notifications on the device.
     *
     * @return List of AppInfo containing app name, package name, and icon.
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledApps() = buildList {
        val packageManager = context.packageManager
        val installedApplications = packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA
        )
        for (app in installedApplications) {
            if (packageManager.checkPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    app.packageName
                ) == PackageManager.PERMISSION_GRANTED
                && app.packageName != context.packageName
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

    private fun saveIconToFile(icon: Bitmap, packageName: String, context: Context): String {
        val destination = getIconFile(context, packageName)

        // store the icon in the apps cache directory
        val file = destination.apply {
            parentFile?.mkdirs()
            createNewFile()
        }
        FileOutputStream(file).use { output ->
            icon.compress(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                },
                80,
                output
            )
        }
        return FileProvider.getUriForFile(
            context,
            "${packageName}.provider",
            file
        ).toString()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val bitmap = createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1)
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        const val WHATSAPP_PACKAGE_NAME = "com.whatsapp"

        const val TELEGRAM_PACKAGE_NAME = "org.telegram.messenger"

        fun Context.getCachedAppIcon(packageName: String): Uri = FileProvider.getUriForFile(
            this,
            "${this.packageName}.provider",
            getIconFile(this, packageName)
        )

        /**
         * The icon is stored in the user's external cache dir
         */
        fun getIconFile(context: Context, packageName: String) = File(
            context.externalCacheDir,
            "icons/apps/$packageName.png"
        )
    }
}