package dev.logickoder.keyguarde.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import dev.logickoder.keyguarde.app.components.NotificationListenerBanner
import dev.logickoder.keyguarde.app.components.NotificationPermissionBanner
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.home.components.EmptyMatchesState
import dev.logickoder.keyguarde.home.components.FilterChips
import dev.logickoder.keyguarde.home.components.HomeHeader
import dev.logickoder.keyguarde.home.components.HomeTopAppBar
import dev.logickoder.keyguarde.home.components.KeywordDialog
import dev.logickoder.keyguarde.home.components.MatchItem
import dev.logickoder.keyguarde.home.components.MatchSummaryCard
import dev.logickoder.keyguarde.home.domain.rememberHomeState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettings: () -> Unit,
) {
    val state = rememberHomeState()
    val watchedApps by state.watchedApps.collectAsStateWithLifecycle()
    val matches = state.matches.collectAsLazyPagingItems()
    val recentCount by state.recentCount.collectAsStateWithLifecycle()
    val openInAppIntents by state.openInAppIntents.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopAppBar(
                searchQuery = state.query,
                onSearchQueryChange = state::onSearchQueryChange,
                onSettings = onSettings
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            content = {
                                NotificationPermissionBanner()
                                NotificationListenerBanner()
                            }
                        )
                    }

                    item {
                        MatchSummaryCard(
                            matchCount = recentCount,
                            onResetClick = state::resetCount
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
                        HomeHeader(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .animateItem(),
                            hasMatches = matches.itemCount > 0,
                            selectedMatchesSize = when (state.isSelectionMode) {
                                true -> state.selectedMatches.size
                                else -> null
                            },
                            toggleSelectionMode = state::toggleSelectionMode,
                            clearAllMatches = state::clearAllMatches,
                            selectVisibleMatches = {
                                state.selectVisibleMatches(matches.itemSnapshotList.items)
                            },
                            clearSelection = state::clearSelection,
                            deleteSelectedMatches = state::deleteSelectedMatches,
                        )
                    }

                    when (matches.itemCount) {
                        0 -> item {
                            EmptyMatchesState(modifier = Modifier.animateItem())
                        }

                        else -> items(
                            matches.itemCount,
                            key = { index -> matches[index]?.id ?: 0L },
                            itemContent = {
                                matches[it]?.let { match ->
                                    MatchItem(
                                        modifier = Modifier.animateItem(),
                                        match = match,
                                        apps = watchedApps,
                                        showOpenInApp = openInAppIntents.containsKey(match.id),
                                        isSelected = when (state.isSelectionMode) {
                                            true -> state.selectedMatches.contains(match.id)
                                            else -> null
                                        },
                                        onDeleteMatch = {
                                            state.deleteMatch(match)
                                        },
                                        onOpenInApp = {
                                            state.openInApp(match)
                                        },
                                        onToggleSelection = {
                                            state.toggleMatchSelection(match.id)
                                        }
                                    )
                                }
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
