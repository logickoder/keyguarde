package dev.logickoder.keyguarde.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing

/**
 * A neutral warning with explicit actions, for problems that stop Keyguarde catching messages.
 * The icon and wording carry the warning, not colour.
 *
 * @param actions label and handler for each button, most useful first.
 */
@Composable
fun StatusBanner(
    message: String,
    actions: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(Radius.m),
        content = {
            Column(
                modifier = Modifier.padding(start = Spacing.l, end = Spacing.s, top = Spacing.m, bottom = Spacing.xs),
                content = {
                    Row(
                        modifier = Modifier.padding(end = Spacing.s),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    )
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        content = {
                            actions.forEach { (label, onClick) ->
                                TextButton(
                                    onClick = onClick,
                                    content = {
                                        Text(
                                            text = label,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            )
        }
    )
}

@Preview
@Composable
private fun StatusBannerPreview() = AppTheme {
    StatusBanner(
        message = "Keyguarde stopped checking new messages. Android closed its listener in the background.",
        actions = listOf("Restart" to {}, "Battery settings" to {}),
    )
}
