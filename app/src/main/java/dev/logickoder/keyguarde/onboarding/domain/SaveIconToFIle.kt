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
    val file = getIconFile(context, packageName)
    icon.writeWebp(file)
    return FileProvider.getUriForFile(
        context,
        context.provider,
        file
    ).toString()
}

/**
 * Writes this drawable to [file] as WebP, creating parent folders. Shared by app icons and chat
 * avatars so both use one format.
 */
internal fun Drawable.writeWebp(file: File) {
    file.parentFile?.mkdirs()

    val bitmap = run {
        if (this is BitmapDrawable && bitmap != null) {
            return@run bitmap
        }

        val bitmap = createBitmap(
            intrinsicWidth.coerceAtLeast(1),
            intrinsicHeight.coerceAtLeast(1)
        )

        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
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