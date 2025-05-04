package dev.logickoder.keyguarde.onboarding.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream

fun saveIconToFile(icon: Drawable, packageName: String, context: Context): String {
    val destination = getIconFile(context, packageName)

    // store the icon in the apps cache directory
    val file = destination.apply {
        parentFile?.mkdirs()
        createNewFile()
    }

    val bitmap = run {
        if (icon is BitmapDrawable && icon.bitmap != null) {
            return@run icon.bitmap
        }

        val bitmap = createBitmap(
            icon.intrinsicWidth.coerceAtLeast(1),
            icon.intrinsicHeight.coerceAtLeast(1)
        )

        val canvas = Canvas(bitmap)
        icon.setBounds(0, 0, canvas.width, canvas.height)
        icon.draw(canvas)
        return@run bitmap
    }

    FileOutputStream(file).use { output ->
        bitmap.compress(
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
        context.provider,
        file
    ).toString()
}


fun Context.getCachedAppIcon(packageName: String): Uri = FileProvider.getUriForFile(
    this,
    provider,
    getIconFile(this, packageName)
)

/**
 * The icon is stored in the user's external cache dir
 */
private fun getIconFile(context: Context, packageName: String) = File(
    context.externalCacheDir,
    "icons/apps/$packageName.png"
)

private val Context.provider: String
    get() = "${packageName}.provider"