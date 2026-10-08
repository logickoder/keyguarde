package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.KeywordField
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.components.KeywordDialog
import dev.logickoder.keyguarde.settings.components.KeywordRow
import dev.logickoder.keyguarde.settings.components.SettingsDivider
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.KeywordsAction
import dev.logickoder.keyguarde.settings.domain.KeywordsState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Composable
fun KeywordsScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<KeywordsViewModel>(
        factory = KeywordsViewModel.factory(LocalContext.current)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    KeywordsContent(
        modifier = modifier,
        state = state,
        onAction = viewModel::onAction,
    )
}

/**
 * The field to add sits on top, so adding never hides behind a button over the list. Each row
 * shows how often its keyword fires; tapping one edits it.
 */
@Composable
private fun KeywordsContent(
    state: KeywordsState,
    onAction: (KeywordsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val existing = remember(state.keywords) { state.keywords.map { it.word } }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.tab_keywords))
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = Spacing.l),
                content = {
                    item(key = "field") {
                        KeywordField(
                            existing = existing,
                            onAdd = { onAction(KeywordsAction.Add(it)) },
                            modifier = Modifier.padding(horizontal = Spacing.l, vertical = Spacing.s),
                        )
                    }

                    when (state.keywords.isEmpty()) {
                        true -> item(key = "empty") { EmptyKeywords() }

                        else -> {
                            item(key = "label") { ListLabel() }
                            itemsIndexed(state.keywords, key = { _, keyword -> keyword.word }) { index, keyword ->
                                Column(
                                    modifier = Modifier.animateItem(),
                                    content = {
                                        if (index > 0) SettingsDivider()
                                        KeywordRow(
                                            word = keyword.word,
                                            matchCount = state.matchCounts[keyword.word.lowercase()] ?: 0,
                                            onClick = { onAction(KeywordsAction.OpenDialog(keyword)) },
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
    )

    if (state.isDialogVisible) {
        KeywordDialog(
            initialKeyword = state.editing,
            onDismiss = { onAction(KeywordsAction.DismissDialog) },
            onSave = { onAction(KeywordsAction.Save(it)) }
        )
    }
}

@Composable
private fun ListLabel() {
    Text(
        text = stringResource(R.string.keywords_list_label),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(start = Spacing.l, end = Spacing.l, top = Spacing.l, bottom = Spacing.xs)
            .semantics { heading() },
    )
}

@Composable
private fun EmptyKeywords() {
    Column(
        modifier = Modifier.padding(horizontal = Spacing.l, vertical = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            Text(
                text = stringResource(R.string.no_keywords_added),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.no_keywords_added_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun KeywordsContentPreview() = AppTheme {
    KeywordsContent(
        state = KeywordsState(
            keywords = persistentListOf(Keyword(word = "urgent"), Keyword(word = "meeting")),
            matchCounts = persistentMapOf("urgent" to 12),
        ),
        onAction = {},
    )
}
