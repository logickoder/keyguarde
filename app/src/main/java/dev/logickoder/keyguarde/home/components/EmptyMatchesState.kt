package dev.logickoder.keyguarde.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing

/**
 * Shown when the list is empty. Says which kind of empty it is, so a search or filter that hides
 * everything never looks like missing matches.
 *
 * @param query the active search, or blank.
 * @param filterNames the apps the list is limited to, joined for display, or null for every app.
 * @param keyword the keyword the list is limited to, or null for every keyword.
 */
@Composable
fun EmptyMatchesState(
    query: String,
    filterNames: String?,
    onReviewKeywords: () -> Unit,
    onClearSearch: () -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier,
    keyword: String? = null,
    onClearKeyword: () -> Unit = {},
) {
    val isSearching = query.isNotBlank()
    val isFiltered = filterNames != null
    val isKeyword = keyword != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xxl, vertical = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            Text(
                text = stringResource(
                    if (isSearching || isFiltered || isKeyword) R.string.empty_filtered_title else R.string.empty_title
                ),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = when {
                    // The keyword is the narrowest limit, so its message covers search and app too.
                    keyword != null -> stringResource(R.string.empty_keyword_body, keyword)
                    isSearching && isFiltered -> stringResource(R.string.empty_search_filter_body, query, filterNames)
                    isSearching -> stringResource(R.string.empty_search_body, query)
                    isFiltered -> stringResource(R.string.empty_filter_body, filterNames)
                    else -> stringResource(R.string.empty_body)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier.padding(top = Spacing.s),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                content = {
                    when {
                        !isSearching && !isFiltered && !isKeyword -> EmptyAction(
                            label = stringResource(R.string.empty_review_keywords),
                            onClick = onReviewKeywords,
                            prominent = true,
                        )

                        else -> {
                            // One action alone is the way out, so it gets the outline; more share the weight.
                            val prominent = listOf(isSearching, isFiltered, isKeyword).count { it } == 1
                            if (isKeyword) {
                                EmptyAction(stringResource(R.string.clear_keyword), onClearKeyword, prominent)
                            }
                            if (isSearching) {
                                EmptyAction(stringResource(R.string.clear_search), onClearSearch, prominent)
                            }
                            if (isFiltered) {
                                EmptyAction(stringResource(R.string.clear_filter), onClearFilter, prominent)
                            }
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun EmptyAction(label: String, onClick: () -> Unit, prominent: Boolean) {
    val text: @Composable () -> Unit = {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface)
    }
    when (prominent) {
        true -> OutlinedButton(onClick = onClick, content = { text() })
        else -> TextButton(onClick = onClick, content = { text() })
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyMatchesStatePreview() = AppTheme {
    Column(
        content = {
            EmptyMatchesState(query = "", filterNames = null, onReviewKeywords = {}, onClearSearch = {}, onClearFilter = {})
            EmptyMatchesState(
                query = "invoice",
                filterNames = "WhatsApp",
                onReviewKeywords = {},
                onClearSearch = {},
                onClearFilter = {},
            )
        }
    )
}
