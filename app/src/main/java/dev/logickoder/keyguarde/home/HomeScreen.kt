package dev.logickoder.keyguarde.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
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
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.components.ClearAllDialog
import dev.logickoder.keyguarde.home.components.EmptyMatchesState
import dev.logickoder.keyguarde.home.components.HomeTopAppBar
import dev.logickoder.keyguarde.home.components.MatchFilterSheet
import dev.logickoder.keyguarde.home.components.MatchRow
import dev.logickoder.keyguarde.home.components.MatchRowDivider
import dev.logickoder.keyguarde.home.components.NewSinceLastVisitHeader
import dev.logickoder.keyguarde.home.components.SelectionTopBar
import dev.logickoder.keyguarde.home.domain.HomeAction
import dev.logickoder.keyguarde.home.domain.HomeEffect
import dev.logickoder.keyguarde.home.domain.HomeState
import dev.logickoder.keyguarde.home.domain.MatchListItem
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val toastManager = LocalToastManager.current
    val viewModel = viewModel<HomeViewModel>(factory = HomeViewModel.factory(context))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val matches = viewModel.matches.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.onAction(HomeAction.RefreshLastVisit)
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            val undoMessage = when (effect) {
                is HomeEffect.MatchesDeleted -> resources.getQuantityString(
                    R.plurals.deleted_match,
                    effect.count,
                    effect.count,
                )

                is HomeEffect.MatchesCleared -> resources.getQuantityString(
                    R.plurals.cleared_match,
                    effect.count,
                    effect.count,
                )

                is HomeEffect.OpenInAppFailed -> {
                    toastManager.show(
                        message = resources.getString(
                            R.string.open_in_app_failed,
                            effect.reason ?: resources.getString(R.string.unknown_error),
                        ),
                        type = ToastType.Error
                    )
                    null
                }
            }
            if (undoMessage != null) {
                val result = snackbarHostState.showSnackbar(
                    message = undoMessage,
                    actionLabel = resources.getString(R.string.undo),
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onAction(HomeAction.UndoDelete)
                }
            }
        }
    }

    HomeContent(
        modifier = modifier,
        state = state,
        query = viewModel.query,
        matches = matches,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    query: String,
    matches: LazyPagingItems<MatchListItem>,
    snackbarHostState: SnackbarHostState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appsByPackage = remember(state.watchedApps) { state.watchedApps.associateBy { it.packageName } }

    BackHandler(enabled = state.isSelectionMode) {
        onAction(HomeAction.ExitSelection)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            when (state.isSelectionMode) {
                true -> SelectionTopBar(
                    selectedCount = state.selectedMatches.size,
                    onExit = { onAction(HomeAction.ExitSelection) },
                    onSelectAll = {
                        onAction(
                            HomeAction.SelectVisibleMatches(
                                matches.itemSnapshotList.items
                                    .filterIsInstance<MatchListItem.Match>()
                                    .map { it.match.id }
                            )
                        )
                    },
                    onDelete = { onAction(HomeAction.DeleteSelectedMatches) },
                )

                else -> Column(
                    content = {
                        HomeTopAppBar(
                            searchQuery = query,
                            onSearchQueryChange = { onAction(HomeAction.SearchQueryChanged(it)) },
                            isFilterActive = state.filter != null,
                            onFilter = { onAction(HomeAction.ShowFilterSheet) },
                            onSelect = { onAction(HomeAction.StartSelection) },
                            onResetCounter = { onAction(HomeAction.ResetCount) },
                            onClearAll = { onAction(HomeAction.ShowClearAllConfirm) },
                        )
                        state.filter?.let { app ->
                            InputChip(
                                selected = true,
                                onClick = { onAction(HomeAction.FilterChanged(null)) },
                                label = { Text(stringResource(R.string.filtered_by, app.name)) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.clear_filter),
                                    )
                                },
                                modifier = Modifier.padding(horizontal = Spacing.l),
                            )
                        }
                    }
                )
            }
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
        content = { scaffoldPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                content = {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                            content = {
                                NotificationPermissionBanner()
                                NotificationListenerBanner()
                            }
                        )
                    }

                    when (matches.itemCount) {
                        0 -> item {
                            EmptyMatchesState(modifier = Modifier.animateItem())
                        }

                        else -> items(
                            matches.itemCount,
                            key = matches.itemKey { it.key },
                            itemContent = { index ->
                                when (val item = matches[index]) {
                                    null -> Unit

                                    MatchListItem.NewDivider -> NewSinceLastVisitHeader(
                                        count = state.newSinceLastVisit,
                                        modifier = Modifier.animateItem(),
                                    )

                                    is MatchListItem.Match -> Column(
                                        modifier = Modifier.animateItem(),
                                        content = {
                                            MatchRow(
                                                match = item.match,
                                                app = appsByPackage[item.match.app],
                                                isNew = item.isNew,
                                                isSelected = when (state.isSelectionMode) {
                                                    true -> item.match.id in state.selectedMatches
                                                    else -> null
                                                },
                                                onClick = {
                                                    when {
                                                        state.isSelectionMode -> onAction(
                                                            HomeAction.ToggleMatchSelection(item.match.id)
                                                        )

                                                        item.match.id in state.openableMatchIds -> onAction(
                                                            HomeAction.OpenInApp(item.match)
                                                        )
                                                    }
                                                },
                                                onLongClick = {
                                                    when (state.isSelectionMode) {
                                                        true -> onAction(HomeAction.ToggleMatchSelection(item.match.id))
                                                        else -> onAction(HomeAction.StartSelectionWith(item.match.id))
                                                    }
                                                },
                                            )
                                            MatchRowDivider()
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            )

            if (state.isFilterSheetVisible) {
                MatchFilterSheet(
                    apps = state.watchedApps,
                    selected = state.filter,
                    onSelect = { onAction(HomeAction.FilterChanged(it)) },
                    onDismiss = { onAction(HomeAction.DismissFilterSheet) },
                )
            }

            if (state.isClearAllConfirmVisible) {
                ClearAllDialog(
                    onConfirm = { onAction(HomeAction.ClearAllMatches) },
                    onDismiss = { onAction(HomeAction.DismissClearAllConfirm) },
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() = AppTheme {
    HomeContent(
        state = HomeState(),
        query = "",
        matches = flowOf(PagingData.empty<MatchListItem>()).collectAsLazyPagingItems(),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
    )
}
