package dev.logickoder.keyguarde.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.KeywordPillStyle
import dev.logickoder.keyguarde.app.theme.Spacing
import kotlin.math.abs

/**
 * One keyword, in teal like everywhere a keyword shows, with how many matches it has caught, so a
 * keyword that never fires stands out. Tap to edit; swipe left to delete. TalkBack gets both as
 * actions, since a swipe is hard to find and harder to perform with a screen reader.
 */
@Composable
fun KeywordRow(
    word: String,
    matchCount: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val deleteLabel = stringResource(R.string.keyword_delete)
    var rowWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    // Not saveable on purpose: after Undo the keyword returns under the same list key, and a
    // restored "swiped away" state would delete it again.
    val swipeState = remember {
        lateinit var state: SwipeToDismissBoxState
        state = SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            density = density,
            // Only a drag past half the row deletes. Material also deletes on a fast flick, which
            // fired on slow, short drags too.
            confirmValueChange = { value ->
                value != SwipeToDismissBoxValue.EndToStart || abs(state.requireOffset()) >= rowWidth / 2f
            },
            positionalThreshold = { distance -> distance / 2 },
        )
        state
    }

    SwipeToDismissBox(
        state = swipeState,
        modifier = modifier
            .onSizeChanged { rowWidth = it.width }
            .semantics {
                customActions = listOf(CustomAccessibilityAction(deleteLabel) { onDelete(); true })
            },
        enableDismissFromStartToEnd = false,
        onDismiss = { value -> if (value == SwipeToDismissBoxValue.EndToStart) onDelete() },
        backgroundContent = { DeleteBackground() },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        onClickLabel = stringResource(R.string.keyword_edit_label),
                        role = Role.Button,
                        onClick = onClick,
                    )
                    .padding(horizontal = Spacing.l),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                content = {
                    Text(
                        text = word,
                        style = KeywordPillStyle.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = when (matchCount) {
                            0 -> stringResource(R.string.keyword_no_matches)
                            else -> pluralStringResource(R.plurals.keyword_match_count, matchCount, matchCount)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )
        },
    )
}

/**
 * Quiet grey with a bin, not red and not a bright block: the undo snackbar is the safety net. Icon
 * only, since a label would be cut off while the strip is still narrow.
 */
@Composable
private fun DeleteBackground() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(horizontal = Spacing.l),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun KeywordRowPreview() = AppTheme {
    KeywordRow(word = "invoice", matchCount = 12, onClick = {}, onDelete = {})
}
