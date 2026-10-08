package dev.logickoder.keyguarde.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing

/**
 * Back on the left, progress on the right. Back hides on the first step, where there's nowhere
 * to go back to.
 *
 * @param step the current step, counting from 1.
 */
@Composable
fun OnboardingTopBar(
    step: Int,
    stepCount: Int,
    canGoBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(start = Spacing.xs, end = Spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            // Faded out, not removed, so the progress bar never shifts sideways.
            val backAlpha by animateFloatAsState(if (canGoBack) 1f else 0f, label = "BackAlpha")
            IconButton(
                onClick = onBack,
                enabled = canGoBack,
                modifier = Modifier
                    .alpha(backAlpha)
                    .then(if (canGoBack) Modifier else Modifier.clearAndSetSemantics {}),
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            )
            Box(modifier = Modifier.weight(1f))
            SegmentedProgress(step = step, stepCount = stepCount)
        }
    )
}

@Composable
private fun SegmentedProgress(step: Int, stepCount: Int) {
    val label = stringResource(R.string.onboarding_step, step, stepCount)
    Row(
        modifier = Modifier.clearAndSetSemantics {
            contentDescription = label
            progressBarRangeInfo = ProgressBarRangeInfo(step.toFloat(), 1f..stepCount.toFloat())
        },
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        content = {
            repeat(stepCount) { index ->
                // Done and current steps are dark; upcoming ones a grey that still clears 3:1.
                val color by animateColorAsState(
                    targetValue = when (index < step) {
                        true -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.outline
                    },
                    label = "ProgressSegment",
                )
                Box(
                    modifier = Modifier
                        .width(if (index == step - 1) 24.dp else 12.dp)
                        .height(4.dp)
                        .background(color, RoundedCornerShape(2.dp))
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingTopBarPreview() = AppTheme {
    OnboardingTopBar(step = 2, stepCount = 5, canGoBack = true, onBack = {})
}
