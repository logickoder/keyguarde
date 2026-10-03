package dev.logickoder.keyguarde.app.data

import android.content.Context
import android.graphics.drawable.Drawable
import dev.logickoder.keyguarde.onboarding.domain.writeWebp
import java.io.File
import java.security.MessageDigest

/**
 * Where the avatar for [chat] in [app] is cached. The name is a hash so chat titles never end up
 * in file names. The OS may clear the cache at any time, so callers must handle a missing file.
 */
fun chatAvatarFile(context: Context, app: String, chat: String): File {
    val digest = MessageDigest.getInstance("SHA-1")
        .digest("$app|$chat".toByteArray())
        .joinToString("") { "%02x".format(it) }
    return File(context.externalCacheDir, "icons/chats/$digest.webp")
}

/**
 * Caches [icon] as the avatar for [chat] in [app], replacing any older one.
 */
fun saveChatAvatar(context: Context, app: String, chat: String, icon: Drawable) {
    icon.writeWebp(chatAvatarFile(context, app, chat))
}
