package dev.logickoder.keyguarde.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.home.components.KeywordDialog
import dev.logickoder.keyguarde.settings.components.KeywordItem
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.KeywordsAction
import dev.logickoder.keyguarde.settings.domain.KeywordsState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun KeywordsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val viewModel = viewModel<KeywordsViewModel>(
        factory = KeywordsViewModel.factory(LocalContext.current)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    KeywordsContent(
        modifier = modifier,
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KeywordsContent(
    state: KeywordsState,
    onBack: () -> Unit,
    onAction: (KeywordsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Keyword Filters", onBack)
        },
        content = { paddingValues ->
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                targetState = state.keywords.isEmpty(),
                content = { isEmpty ->
                    when (isEmpty) {
                        true -> Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            content = {
                                Icon(
                                    imageVector = Icons.Outlined.TextFields,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "No keywords added yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Add keywords to get alerts when they appear in messages",
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        )

                        else ->
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                                content = {
                                    items(
                                        state.keywords.size,
                                        key = { state.keywords[it].word },
                                        itemContent = {
                                            val keyword = state.keywords[it]
                                            KeywordItem(
                                                modifier = Modifier.animateItem(),
                                                keyword = keyword,
                                                onEdit = { onAction(KeywordsAction.OpenDialog(keyword)) },
                                                onDelete = { onAction(KeywordsAction.Delete(keyword)) }
                                            )
                                        }
                                    )
                                }
                            )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(KeywordsAction.OpenDialog()) },
                containerColor = MaterialTheme.colorScheme.primary,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Keyword"
                    )
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

@Preview(showBackground = true)
@Composable
private fun KeywordsContentPreview() = AppTheme {
    KeywordsContent(
        state = KeywordsState(
            keywords = persistentListOf(Keyword(word = "urgent"), Keyword(word = "meeting")),
        ),
        onBack = {},
        onAction = {},
    )
}
