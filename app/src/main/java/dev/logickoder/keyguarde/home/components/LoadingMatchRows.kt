package dev.logickoder.keyguarde.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing

// Varied line lengths so the placeholder reads as a list, not a grid of identical bars.
private val SnippetWidths = listOf(0.92f, 0.78f, 0.86f, 0.7f, 0.9f, 0.74f)

/**
 * Grey stand-ins for match rows, shaped like the real ones, while the first page loads.
 */
@Composable
fun LoadingMatchRows(modifier: Modifier = Modifier, count: Int = 6) {
    val label = stringResource(R.string.loading_matches)
    val pulse by rememberInfiniteTransition(label = "LoadingPulse").animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 800), RepeatMode.Reverse),
        label = "LoadingAlpha",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            // Alpha in the layer, so the pulse redraws without recomposing every bar.
            .graphicsLayer { alpha = pulse }
            .clearAndSetSemantics { contentDescription = label },
        content = {
            repeat(count) { index ->
                PlaceholderRow(snippetWidth = SnippetWidths[index % SnippetWidths.size])
                MatchRowDivider()
            }
        }
    )
}

@Composable
private fun PlaceholderRow(snippetWidth: Float) {
    val color = MaterialTheme.colorScheme.surfaceContainerHigh
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(horizontal = Spacing.l, vertical = Spacing.m),
        content = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(Spacing.m))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                content = {
                    Bar(widthFraction = 0.4f, height = 14.dp)
                    Bar(widthFraction = snippetWidth, height = 12.dp)
                    Bar(widthFraction = snippetWidth - 0.25f, height = 12.dp)
                }
            )
        }
    )
}

@Composable
private fun Bar(widthFraction: Float, height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(4.dp))
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingMatchRowsPreview() = AppTheme {
    LoadingMatchRows()
}
