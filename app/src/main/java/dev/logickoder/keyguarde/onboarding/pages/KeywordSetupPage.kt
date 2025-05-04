package dev.logickoder.keyguarde.onboarding.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.home.components.KeywordDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordSetupPage(
    keywords: SnapshotStateList<Keyword>,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Text(
                text = "Set Up Your Keywords",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add keywords that matter to you. Keyguarde will alert you when these words appear in notifications.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Text(
                        text = "Add Keyword",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = keywords.isEmpty(),
                content = { noKeywords ->
                    when (noKeywords) {
                        true -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center,
                            content = {
                                Text(
                                    text = "No keywords added yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                        )

                        else -> Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            content = {
                                keywords.forEach { keyword ->
                                    KeywordItem(
                                        keyword = keyword.word,
                                        onDelete = {
                                            keywords.remove(keyword)
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Examples: urgent, meeting, deadline, ASAP",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    )

    if (showAddDialog) {
        KeywordDialog(
            onDismiss = { showAddDialog = false },
            onSave = { word, sensitive ->
                keywords.add(Keyword(word = word, isCaseSensitive = sensitive))
            }
        )
    }
}

@Composable
private fun KeywordItem(
    keyword: String,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Text(
                        text = keyword,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDelete,
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun KeywordSetupPagePreview() = AppTheme {
    KeywordSetupPage(
        keywords = remember {
            mutableStateListOf(
                Keyword(word = "urgent"),
                Keyword(word = "meeting"),
                Keyword(word = "deadline"),
            )
        }
    )
}
