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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.components.NotificationListenerBanner
import dev.logickoder.keyguarde.app.components.NotificationPermissionBanner
import dev.logickoder.keyguarde.app.components.ToastType
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.home.components.EmptyMatchesState
import dev.logickoder.keyguarde.home.components.FilterChips
import dev.logickoder.keyguarde.home.components.HomeHeader
import dev.logickoder.keyguarde.home.components.HomeTopAppBar
import dev.logickoder.keyguarde.home.components.KeywordDialog
import dev.logickoder.keyguarde.home.components.MatchItem
import dev.logickoder.keyguarde.home.components.MatchSummaryCard
import dev.logickoder.keyguarde.home.domain.HomeAction
import dev.logickoder.keyguarde.home.domain.HomeEffect
import dev.logickoder.keyguarde.home.domain.HomeState
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettings: () -> Unit,
) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val viewModel = viewModel<HomeViewModel>(factory = HomeViewModel.factory(context))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val matches = viewModel.matches.collectAsLazyPagingItems()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.MatchesDeleted -> toastManager.show(
                    message = context.resources.getQuantityString(
                        R.plurals.deleted_match,
                        effect.count,
                        effect.count,
                    ),
                    type = ToastType.Success
                )

                is HomeEffect.MatchesCleared -> toastManager.show(
                    message = context.resources.getQuantityString(
                        R.plurals.cleared_match,
                        effect.count,
                        effect.count,
                    ),
                    type = ToastType.Success
                )

                is HomeEffect.OpenInAppFailed -> toastManager.show(
                    message = "Failed to open app: ${effect.reason ?: "Unknown error"}",
                    type = ToastType.Error
                )
            }
        }
    }

    HomeContent(
        modifier = modifier,
        state = state,
        query = viewModel.query,
        matches = matches,
        onSettings = onSettings,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    query: String,
    matches: LazyPagingItems<KeywordMatch>,
    onSettings: () -> Unit,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopAppBar(
                searchQuery = query,
                onSearchQueryChange = { onAction(HomeAction.SearchQueryChanged(it)) },
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
                            matchCount = state.recentCount,
                            onResetClick = { onAction(HomeAction.ResetCount) }
                        )
                    }

                    item {
                        FilterChips(
                            selected = state.filter,
                            apps = state.watchedApps,
                            onSelected = { onAction(HomeAction.FilterChanged(it)) }
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
                            toggleSelectionMode = { onAction(HomeAction.ToggleSelectionMode) },
                            clearAllMatches = { onAction(HomeAction.ClearAllMatches) },
                            selectVisibleMatches = {
                                onAction(
                                    HomeAction.SelectVisibleMatches(
                                        matches.itemSnapshotList.items.map { it.id }
                                    )
                                )
                            },
                            clearSelection = { onAction(HomeAction.ClearSelection) },
                            deleteSelectedMatches = { onAction(HomeAction.DeleteSelectedMatches) },
                        )
                    }

                    when (matches.itemCount) {
                        0 -> item {
                            EmptyMatchesState(modifier = Modifier.animateItem())
                        }

                        else -> items(
                            matches.itemCount,
                            key = matches.itemKey { it.id },
                            itemContent = {
                                matches[it]?.let { match ->
                                    MatchItem(
                                        modifier = Modifier.animateItem(),
                                        match = match,
                                        apps = state.watchedApps,
                                        showOpenInApp = match.id in state.openableMatchIds,
                                        isSelected = when (state.isSelectionMode) {
                                            true -> match.id in state.selectedMatches
                                            else -> null
                                        },
                                        onDeleteMatch = {
                                            onAction(HomeAction.DeleteMatch(match))
                                        },
                                        onOpenInApp = {
                                            onAction(HomeAction.OpenInApp(match))
                                        },
                                        onToggleSelection = {
                                            onAction(HomeAction.ToggleMatchSelection(match.id))
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
                    onDismiss = { onAction(HomeAction.ToggleKeywordDialog) },
                    onSave = { onAction(HomeAction.SaveKeyword(it)) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(HomeAction.ToggleKeywordDialog) },
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
private fun HomeContentPreview() = AppTheme {
    HomeContent(
        state = HomeState(recentCount = 3),
        query = "",
        matches = flowOf(PagingData.empty<KeywordMatch>()).collectAsLazyPagingItems(),
        onSettings = {},
        onAction = {},
    )
}
