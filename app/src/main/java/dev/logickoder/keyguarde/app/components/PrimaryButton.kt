package dev.logickoder.keyguarde.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius

/**
 * The one primary action on a screen or sheet: full width, dark neutral, never teal, which is
 * reserved for matched keywords.
 *
 * @param quiet grey instead of dark, for a primary action that can only do part of its job.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    quiet: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(Radius.l),
        colors = ButtonDefaults.buttonColors(
            containerColor = when (quiet) {
                true -> MaterialTheme.colorScheme.surfaceContainerHighest
                else -> MaterialTheme.colorScheme.onSurface
            },
            contentColor = when (quiet) {
                true -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.surface
            },
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        content = { Text(text = text, style = MaterialTheme.typography.titleMedium) },
    )
}

@Preview
@Composable
private fun PrimaryButtonPreview() = AppTheme {
    PrimaryButton(text = "Save", onClick = {})
}
