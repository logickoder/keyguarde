package dev.logickoder.keyguarde.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.absoluteValue

/** Background and ink for an initials avatar. */
data class AvatarColors(val background: Color, val ink: Color)

/**
 * A neutral tone picked deterministically from [key], so a chat keeps the same avatar colour
 * across launches.
 */
@Composable
fun avatarColors(key: String): AvatarColors {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val tones = if (dark) AvatarTonesDark else AvatarTonesLight
    return AvatarColors(
        background = tones[key.hashCode().absoluteValue % tones.size],
        ink = if (dark) AvatarInkDark else AvatarInkLight,
    )
}
