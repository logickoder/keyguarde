package dev.logickoder.keyguarde.onboarding.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import dev.logickoder.keyguarde.app.components.KeywordField
import dev.logickoder.keyguarde.app.components.KeywordPillText
import dev.logickoder.keyguarde.app.components.SuggestionPill

/**
 * Type keywords one after another, or tap a suggestion. Added keywords show as the same pills
 * the rest of the app uses, so the user sees what a match will look like.
 */
@Composable
fun KeywordsPage(
    keywords: ImmutableList<Keyword>,
    onAdd: (String) -> Unit,
    onRemove: (Keyword) -> Unit,
    modifier: Modifier = Modifier,
) {
    val suggestions = stringArrayResource(R.array.keyword_suggestions)
        .filter { suggestion -> keywords.none { it.word.equals(suggestion, ignoreCase = true) } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.l),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                content = {
                    Text(
                        text = stringResource(R.string.keywords_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(R.string.keywords_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )

            KeywordField(existing = keywords.map { it.word }, onAdd = onAdd)

            if (keywords.isNotEmpty()) {
                PillSection(
                    label = stringResource(R.string.keywords_added_label),
                    content = {
                        keywords.forEach { keyword ->
                            AddedPill(word = keyword.word, onRemove = { onRemove(keyword) })
                        }
                    }
                )
            }

            if (suggestions.isNotEmpty()) {
                PillSection(
                    label = stringResource(R.string.keywords_suggestions_label),
                    content = {
                        suggestions.forEach { word -> SuggestionPill(word = word, onAdd = { onAdd(word) }) }
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PillSection(label: String, content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        content = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                modifier = Modifier.animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                content = { content() }
            )
        }
    )
}

/** Tapping anywhere on the pill removes it; the × only says so. */
@Composable
private fun AddedPill(word: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            // Clipped before clickable, so the press ripple follows the pill instead of a square.
            .clip(RoundedCornerShape(Radius.pill))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(
                onClickLabel = stringResource(R.string.keywords_remove, word),
                role = Role.Button,
                onClick = onRemove,
            )
            .padding(start = Spacing.m, end = Spacing.s, top = Spacing.s, bottom = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        content = {
            KeywordPillText(word = word, color = MaterialTheme.colorScheme.primary)
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun KeywordsPagePreview() = AppTheme {
    KeywordsPage(
        keywords = persistentListOf(Keyword(word = "invoice"), Keyword(word = "call me")),
        onAdd = {},
        onRemove = {},
    )
}
