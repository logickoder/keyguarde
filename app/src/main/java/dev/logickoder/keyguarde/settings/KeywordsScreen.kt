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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.logickoder.keyguarde.home.components.KeywordDialog
import dev.logickoder.keyguarde.settings.components.KeywordItem
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.rememberKeywordsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val state = rememberKeywordsState()
    val keywords by state.keywords.collectAsStateWithLifecycle()

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
                targetState = keywords.isEmpty(),
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
                                        keywords.size,
                                        key = { keywords[it].word },
                                        itemContent = {
                                            val keyword = keywords[it]
                                            KeywordItem(
                                                modifier = Modifier.animateItem(),
                                                keyword = keyword,
                                                onEdit = { state.toggleDialog(keyword) },
                                                onDelete = { state.deleteKeyword(keyword) }
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
                onClick = state::toggleDialog,
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
            initialKeyword = state.edit,
            onDismiss = state::toggleDialog,
            onSave = state::saveKeyword
        )
    }
}