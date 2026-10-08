package dev.logickoder.keyguarde.onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.logickoder.keyguarde.app.components.PrimaryButton
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing

/**
 * One full-width action per step. When it's disabled, [hint] says what unlocks it, so a grey
 * button never sits there without a reason. [secondaryLabel] adds a quiet way out underneath.
 */
@Composable
fun OnboardingBottomBar(
    label: String,
    enabled: Boolean,
    hint: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    onSecondary: () -> Unit = {},
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
            PrimaryButton(text = label, onClick = onClick, enabled = enabled)
            if (secondaryLabel != null) {
                TextButton(
                    onClick = onSecondary,
                    modifier = Modifier.padding(top = Spacing.xs),
                    content = {
                        Text(
                            text = secondaryLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingBottomBarPreview() = AppTheme {
    OnboardingBottomBar(label = "Continue", enabled = false, hint = "Add at least one keyword.", onClick = {})
}
