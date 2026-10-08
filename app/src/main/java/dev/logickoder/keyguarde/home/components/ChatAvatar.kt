package dev.logickoder.keyguarde.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.logickoder.keyguarde.app.components.AppIcon
import dev.logickoder.keyguarde.app.data.chatAvatarFile
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.avatarColors

/**
 * The chat's picture with a small monochrome badge for the source app. Falls back to initials
 * when no picture was captured or the cache was cleared.
 */
@Composable
fun ChatAvatar(
    chat: String,
    packageName: String,
    app: WatchedApp?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    val context = LocalContext.current
    val file = remember(packageName, chat) { chatAvatarFile(context, packageName, chat) }

    Box(
        modifier = modifier.size(size),
        content = {
            // Initials sit under the picture, so they show while it loads, and stay when there is
            // none (never captured, or the cache was cleared).
            InitialsAvatar(chat = chat, size = size)
            AsyncImage(
                model = file,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
            )

            if (app != null) {
                AppIcon(
                    app = app,
                    // The app name is no longer printed on the row, so the badge carries it for TalkBack.
                    contentDescription = app.name,
                    size = 14.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(2.dp),
                )
            }
        }
    )
}

@Composable
private fun InitialsAvatar(chat: String, size: Dp) {
    val colors = avatarColors(chat)
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.background)
            // The chat name is read right after, so the initials would just be noise for TalkBack.
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
        content = {
            Text(
                text = remember(chat) { initialsOf(chat) },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            )
        }
    )
}

private fun initialsOf(chat: String): String {
    val initials = chat.split(' ')
        .mapNotNull { word -> word.firstOrNull { it.isLetterOrDigit() } }
        .take(2)
        .joinToString("")
        .uppercase()
    return initials.ifEmpty { "#" }
}

@Preview
@Composable
private fun ChatAvatarPreview() = AppTheme {
    ChatAvatar(
        chat = "Design Team",
        packageName = "com.whatsapp",
        app = null,
    )
}
