package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.KeywordField
import dev.logickoder.keyguarde.app.components.SuggestionPill
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordStats
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.settings.components.KeywordEditSheet
import dev.logickoder.keyguarde.settings.components.KeywordRow
import dev.logickoder.keyguarde.settings.components.SettingsDivider
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.KeywordSort
import dev.logickoder.keyguarde.settings.domain.KeywordsAction
import dev.logickoder.keyguarde.settings.domain.KeywordsEffect
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
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is KeywordsEffect.Deleted -> launch {
                    // A newer delete replaces the snackbar instead of queueing behind it.
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = resources.getString(R.string.keyword_deleted, effect.keyword.word),
                        actionLabel = resources.getString(R.string.undo),
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onAction(KeywordsAction.UndoDelete(effect.keyword))
                    }
                }
            }
        }
    }

    KeywordsContent(
        modifier = modifier,
        state = state,
        snackbarHostState = snackbarHostState,
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
    snackbarHostState: SnackbarHostState,
    onAction: (KeywordsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val existing = remember(state.keywords) { state.keywords.map { it.word } }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.tab_keywords))
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    // The default action colour is teal (inversePrimary); keep it neutral.
                    Snackbar(snackbarData = data, actionColor = MaterialTheme.colorScheme.inverseOnSurface)
                }
            )
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
                        true -> item(key = "empty") { EmptyKeywords(onAdd = { onAction(KeywordsAction.Add(it)) }) }

                        else -> {
                            item(key = "label") {
                                ListHeader(
                                    count = state.keywords.size,
                                    sort = state.sort,
                                    onSort = { onAction(KeywordsAction.SetSort(it)) },
                                )
                            }
                            itemsIndexed(state.keywords, key = { _, keyword -> keyword.word }) { index, keyword ->
                                Column(
                                    modifier = Modifier.animateItem(),
                                    content = {
                                        if (index > 0) SettingsDivider()
                                        KeywordRow(
                                            word = keyword.word,
                                            lastMatchAt = state.stats[keyword.word.lowercase()]?.lastMatchAt,
                                            onClick = { onAction(KeywordsAction.Edit(keyword)) },
                                            onDelete = { onAction(KeywordsAction.Delete(keyword)) },
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

    state.editing?.let { keyword ->
        KeywordEditSheet(
            word = keyword.word,
            others = existing - keyword.word,
            onSave = { onAction(KeywordsAction.Save(it)) },
            onDelete = { onAction(KeywordsAction.Delete(keyword)) },
            onDismiss = { onAction(KeywordsAction.DismissEdit) },
        )
    }
}

/**
 * How many keywords there are, and the order they're in. The sort sits here, next to what it
 * sorts, not in a top-bar menu.
 */
@Composable
private fun ListHeader(count: Int, sort: KeywordSort, onSort: (KeywordSort) -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.l, end = Spacing.xs, top = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = pluralStringResource(R.plurals.keyword_count, count, count),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
            )
            Box(
                content = {
                    val sortLabel = stringResource(R.string.keyword_sort_by, stringResource(sort.label))
                    TextButton(
                        onClick = { menuOpen = true },
                        modifier = Modifier.semantics { contentDescription = sortLabel },
                        content = {
                            Text(
                                text = stringResource(sort.label),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                // The button already says "Sort: …"; reading the label again is noise.
                                modifier = Modifier.clearAndSetSemantics {},
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        content = {
                            KeywordSort.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(option.label)) },
                                    onClick = {
                                        onSort(option)
                                        menuOpen = false
                                    },
                                    modifier = Modifier.semantics { selected = option == sort },
                                    trailingIcon = {
                                        if (option == sort) Icon(Icons.Default.Check, contentDescription = null)
                                    },
                                )
                            }
                        }
                    )
                }
            )
        }
    )
}

/**
 * With nothing to list, the screen offers the same starting words as setup. Keyguarde catches
 * nothing without a keyword, so getting one in is the only job here.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyKeywords(onAdd: (String) -> Unit) {
    val suggestions = stringArrayResource(R.array.keyword_suggestions)
    Column(
        modifier = Modifier.padding(horizontal = Spacing.l, vertical = Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Spacing.l),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                content = {
                    Text(
                        text = stringResource(R.string.no_keywords_added),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(R.string.no_keywords_added_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                content = {
                    Text(
                        text = stringResource(R.string.keywords_suggestions_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        content = {
                            suggestions.forEach { word -> SuggestionPill(word = word, onAdd = { onAdd(word) }) }
                        }
                    )
                }
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun KeywordsEmptyPreview() = AppTheme {
    KeywordsContent(
        state = KeywordsState(),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
    )
}

@PreviewLightDark
@Composable
private fun KeywordsContentPreview() = AppTheme {
    KeywordsContent(
        state = KeywordsState(
            keywords = persistentListOf(Keyword(word = "urgent"), Keyword(word = "meeting")),
            stats = persistentMapOf("urgent" to KeywordStats("urgent", 12, LocalDateTime.now().minusHours(2))),
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
    )
}
