package dev.logickoder.keyguarde.onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing

/**
 * One full-width action per step. When it's disabled, [hint] says what unlocks it, so a grey
 * button never sits there without a reason.
 */
@Composable
fun OnboardingBottomBar(
    label: String,
    enabled: Boolean,
    hint: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Spacing.xl, end = Spacing.xl, bottom = Spacing.l),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            AnimatedVisibility(
                visible = !enabled && hint != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
                content = {
                    Text(
                        text = hint.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = Spacing.m),
                    )
                }
            )
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(Radius.l),
                // Dark neutral, like the match sheet: teal is kept for keywords.
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                content = {
                    Text(text = label, style = MaterialTheme.typography.titleMedium)
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingBottomBarPreview() = AppTheme {
    OnboardingBottomBar(label = "Continue", enabled = false, hint = "Add at least one keyword.", onClick = {})
}
