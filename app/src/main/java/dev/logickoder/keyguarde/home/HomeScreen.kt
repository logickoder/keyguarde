package dev.logickoder.keyguarde.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.home.components.*
import dev.logickoder.keyguarde.home.domain.rememberHomeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettings: () -> Unit,
) {
    val state = rememberHomeState()
    val watchedApps by state.watchedApps.collectAsStateWithLifecycle()
    val matches by state.matches.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        content = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                                content = {
                                    Icon(
                                        painter = painterResource(R.drawable.logo),
                                        contentDescription = "Keyguarde Logo",
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                            Text(text = stringResource(R.string.app_name))
                        }
                    )
                },
                actions = {
                    IconButton(
                        onSettings,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        content = { scaffoldPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentPadding = PaddingValues(bottom = 80.dp),
                content = {
                    item {
                        MatchSummaryCard(
                            matchCount = 3,
                            onResetClick = {}
                        )
                    }

                    item {
                        FilterChips(
                            selected = state.filter,
                            apps = watchedApps,
                            onSelected = state::changeFilter
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recent Matches",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    when (matches.isEmpty()) {
                        true -> item {
                            EmptyMatchesState(modifier = Modifier.animateItem())
                        }

                        else -> items(
                            matches.size,
                            key = { matches[it].id },
                            itemContent = {
                                MatchItem(
                                    modifier = Modifier.animateItem(),
                                    match = matches[it],
                                    apps = watchedApps,
                                )
                            }
                        )
                    }
                }
            )

            if (state.isKeywordDialogVisible) {
                KeywordDialog(
                    onDismiss = state::toggleKeywordDialog,
                    onSave = state::saveKeyword
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = state::toggleKeywordDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Keyword",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() = AppTheme {
    HomeScreen {}
}