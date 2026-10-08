package dev.logickoder.keyguarde.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.logickoder.keyguarde.app.components.AppIcon
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.AppTheme

private val Monochrome = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

/**
 * A watched app's icon in greyscale. See the other overload.
 */
@Composable
fun AppIcon(
    app: WatchedApp,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
) = AppIcon(
    name = app.name,
    icon = app.icon.ifBlank { null },
    contentDescription = contentDescription,
    modifier = modifier,
    size = size,
)

/**
 * An app's icon in greyscale, so app colours never compete with the teal keywords. Apps without
 * an icon get the first letter of their name, so every row carries a badge.
 *
 * @param icon anything Coil loads: a saved file's URI, or the app's Drawable. Null for the letter.
 * @param contentDescription null when the app name is already read nearby.
 */
@Composable
fun AppIcon(
    name: String,
    icon: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
) {
    when (icon == null) {
        true -> Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                // The letter alone means nothing to TalkBack; say the app name or nothing.
                .clearAndSetSemantics {
                    if (contentDescription != null) this.contentDescription = contentDescription
                },
            contentAlignment = Alignment.Center,
            content = {
                // Scales with the icon, not the user's font size, so it always fits the circle.
                val letterSize = with(LocalDensity.current) { (size * 0.6f).toSp() / fontScale }
                Text(
                    text = name.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "#",
                    // Line height trimmed to the glyph, or the letter sits low in small circles.
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = letterSize,
                        lineHeight = letterSize,
                        fontWeight = FontWeight.Bold,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both,
                        ),
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        )

        else -> AsyncImage(
            model = icon,
            contentDescription = contentDescription,
            colorFilter = Monochrome,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppIconPreview() = AppTheme {
    AppIcon(app = WatchedApp("com.whatsapp", "WhatsApp", ""), contentDescription = null)
}
