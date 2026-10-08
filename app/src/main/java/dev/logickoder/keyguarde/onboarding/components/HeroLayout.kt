package dev.logickoder.keyguarde.onboarding.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.logickoder.keyguarde.app.theme.Spacing

// Where the picture's top edge sits, as a share of the screen. Fixed, so the picture stays put
// across steps and states while the words below it change length.
private const val HeroTopFraction = 0.15f

/**
 * A picture near the top and words at the bottom, the layout of the picture-led setup steps.
 * Scrolls when a large font needs more room than the screen has.
 */
@Composable
fun HeroLayout(
    hero: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        content = {
            val screenHeight = maxHeight
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = screenHeight)
                    .padding(horizontal = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Spacer(modifier = Modifier.height(screenHeight * HeroTopFraction))
                    hero()
                    // Takes the leftover height, so the words sit at the bottom; never less than xl.
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = Spacing.xl)
                    )
                    content()
                }
            )
        }
    )
}
