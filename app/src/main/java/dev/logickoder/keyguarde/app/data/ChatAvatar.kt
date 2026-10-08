package dev.logickoder.keyguarde.app.data

import android.content.Context
import android.graphics.drawable.Drawable
import dev.logickoder.keyguarde.onboarding.domain.writeWebp
import java.io.File
import java.security.MessageDigest

// Group photos are personal data, so they live in the private cache, never external storage.
// Other apps supply these images, so they're redrawn at a fixed size instead of kept as sent.
private const val AvatarSizePx = 128

/**
 * Where the avatar for [chat] in [app] is cached. Names are hashes so chat titles never end up on
 * disk. The OS may clear the cache at any time, so callers must handle a missing file.
 */
fun chatAvatarFile(context: Context, app: String, chat: String): File =
    File(chatAvatarDir(context, app), "${sha1("$app|$chat")}.webp")

// A busy group matches often; its picture is re-encoded at most this often, so a change still shows.
private const val AvatarMaxAgeMillis = 24 * 60 * 60 * 1000L

/**
 * Caches [icon] as the avatar for [chat] in [app], unless a recent one is already cached.
 */
fun saveChatAvatar(context: Context, app: String, chat: String, icon: Drawable) {
    val file = chatAvatarFile(context, app, chat)
    if (System.currentTimeMillis() - file.lastModified() < AvatarMaxAgeMillis) return
    icon.writeWebp(file, maxSizePx = AvatarSizePx)
}

/**
 * Removes cached avatars for [app], or for every app when [app] is null.
 */
fun deleteChatAvatars(context: Context, app: String? = null) {
    val dir = when (app) {
        null -> File(context.cacheDir, ChatsFolder)
        else -> chatAvatarDir(context, app)
    }
    dir.deleteRecursively()
}

private const val ChatsFolder = "chats"

private fun chatAvatarDir(context: Context, app: String) =
    File(context.cacheDir, "$ChatsFolder/${sha1(app)}")

private fun sha1(value: String): String = MessageDigest.getInstance("SHA-1")
    .digest(value.toByteArray())
    .toHexString()
