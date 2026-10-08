package dev.logickoder.keyguarde.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.components.StatusBanner
import dev.logickoder.keyguarde.app.components.ToastType
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.domain.appNotificationSettings
import dev.logickoder.keyguarde.app.domain.appBatterySettings
import dev.logickoder.keyguarde.app.domain.startActivitySafely
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.components.ClearAllDialog
import dev.logickoder.keyguarde.home.components.EmptyMatchesState
import dev.logickoder.keyguarde.home.components.HomeTopAppBar
import dev.logickoder.keyguarde.home.components.ListenerIssueBanners
import dev.logickoder.keyguarde.home.components.LoadingMatchRows
import dev.logickoder.keyguarde.home.components.MatchFilterSheet
import dev.logickoder.keyguarde.home.components.MatchRow
import dev.logickoder.keyguarde.home.components.MatchRowDivider
import dev.logickoder.keyguarde.home.components.MatchSheet
import dev.logickoder.keyguarde.home.components.NewSinceLastVisitHeader
import dev.logickoder.keyguarde.home.components.SelectionTopBar
import dev.logickoder.keyguarde.home.components.rememberLastWhile
import dev.logickoder.keyguarde.home.domain.HomeAction
import dev.logickoder.keyguarde.home.domain.HomeEffect
import dev.logickoder.keyguarde.home.domain.HomeState
import dev.logickoder.keyguarde.home.domain.ListenerIssue
import dev.logickoder.keyguarde.home.domain.MatchListItem
import kotlinx.coroutines.flow.flowOf

/**
 * @param onOpenKeywords switches to the Keywords tab, from the empty state.
 */
@Composable
fun HomeScreen(onOpenKeywords: () -> Unit, modifier: Modifier = Modifier) {
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

    val checkPermissions = {
        viewModel.onAction(
            HomeAction.PermissionsChecked(
                hasListenerAccess = NotificationHelper.isListenerServiceEnabled(context),
                notificationsAllowed = NotificationHelper.isNotificationPermissionGranted(context),
            )
        )
    }
    // Both can change in system settings while the app is in the background.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { checkPermissions() }

    val notificationPermission = NotificationHelper.requestNotificationPermissionLauncher { granted ->
        checkPermissions()
        // Android stops showing the prompt after repeated denials; settings is the only way left.
        if (!granted) context.startActivitySafely(appNotificationSettings(context))
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

                is HomeEffect.LaunchApp -> {
                    val intent = context.packageManager.getLaunchIntentForPackage(effect.packageName)
                    when (intent) {
                        null -> toastManager.show(
                            message = resources.getString(R.string.launch_app_failed),
                            type = ToastType.Error,
                        )

                        else -> context.startActivity(intent)
                    }
                    null
                }

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
        onOpenKeywords = onOpenKeywords,
        onOpenListenerSettings = { NotificationHelper.launchListenerSettings(context) },
        onRestartListener = {
            NotificationHelper.startListenerService(context)
            NotificationHelper.requestListenerServiceRebind(context)
            viewModel.onAction(HomeAction.ListenerRestartRequested)
        },
        onOpenBatterySettings = {
            context.startActivitySafely(appBatterySettings(context))
        },
        onEnableNotifications = {
            if (NotificationHelper.REQUIRES_NOTIFICATION_PERMISSION) {
                notificationPermission.launch(NotificationHelper.PERMISSION)
            } else {
                context.startActivitySafely(appNotificationSettings(context))
            }
        },
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    query: String,
    matches: LazyPagingItems<MatchListItem>,
    snackbarHostState: SnackbarHostState,
    onAction: (HomeAction) -> Unit,
    onOpenKeywords: () -> Unit,
    onOpenListenerSettings: () -> Unit,
    onRestartListener: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onEnableNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appsByPackage = remember(state.watchedApps) { state.watchedApps.associateBy { it.packageName } }

    BackHandler(enabled = state.isSelectionMode) {
        onAction(HomeAction.ExitSelection)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            val selectedCount = rememberLastWhile(state.isSelectionMode, state.selectedMatches.size)
            // Fade through, like the search bar's swap, so entering and leaving selection doesn't jump.
            AnimatedContent(
                targetState = state.isSelectionMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 90)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 90)) using
                        SizeTransform(clip = false)
                },
                label = "HomeTopBar",
            ) { isSelecting ->
                when (isSelecting) {
                    true -> SelectionTopBar(
                        selectedCount = selectedCount,
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
                                filterCount = state.filter.size,
                                onFilter = { onAction(HomeAction.ShowFilterSheet) },
                                onSelect = { onAction(HomeAction.StartSelection) },
                                onResetCounter = { onAction(HomeAction.ResetCount) },
                                onClearAll = { onAction(HomeAction.ShowClearAllConfirm) },
                            )
                            if (state.filter.isNotEmpty()) {
                                val names = remember(state.filter) { state.filter.joinToString { it.name } }
                                InputChip(
                                    selected = true,
                                    onClick = { onAction(HomeAction.ClearFilter) },
                                    label = {
                                        Text(
                                            text = stringResource(R.string.filtered_by, names),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                content = {
                    // Above the list, not in it: a warning that matches could be missed stays in view
                    // however far the user scrolls, and opening it pushes the list down instead of
                    // growing off-screen above the first row.
                    StatusBanners(
                        listenerIssue = state.listenerIssue,
                        notificationsAllowed = state.notificationsAllowed,
                        onOpenListenerSettings = onOpenListenerSettings,
                        onRestartListener = onRestartListener,
                        onOpenBatterySettings = onOpenBatterySettings,
                        onEnableNotifications = onEnableNotifications,
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        content = {

                            // Row callbacks capture this, not the whole state, so opening a sheet or other
                            // state changes don't recompose every visible row.
                            val isSelectionMode = state.isSelectionMode

                            val refresh = matches.loadState.refresh
                            // Before the first page arrives, the list reports "not loading" with nothing
                            // in it; only a finished, empty load means there's really nothing to show.
                            val isLoaded = refresh is LoadState.NotLoading && matches.loadState.append.endOfPaginationReached

                            when {
                                matches.itemCount == 0 && !isLoaded -> item(key = "loading") {
                                    LoadingMatchRows(modifier = Modifier.animateItem())
                                }

                                matches.itemCount == 0 -> item(key = "empty") {
                                    EmptyMatchesState(
                                        query = query,
                                        filterNames = state.filter.takeIf { it.isNotEmpty() }?.joinToString { it.name },
                                        onReviewKeywords = onOpenKeywords,
                                        onClearSearch = { onAction(HomeAction.SearchQueryChanged("")) },
                                        onClearFilter = { onAction(HomeAction.ClearFilter) },
                                        modifier = Modifier.animateItem(),
                                    )
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
                                                        isSelected = when (isSelectionMode) {
                                                            true -> item.match.id in state.selectedMatches
                                                            else -> null
                                                        },
                                                        onClick = {
                                                            when (isSelectionMode) {
                                                                true -> onAction(HomeAction.ToggleMatchSelection(item.match.id))
                                                                else -> onAction(HomeAction.OpenMatch(item.match))
                                                            }
                                                        },
                                                        onLongClick = {
                                                            when (isSelectionMode) {
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
                }
            )

            state.openMatch?.let { match ->
                MatchSheet(
                    match = match,
                    app = appsByPackage[match.app],
                    canOpenInApp = match.id in state.openableMatchIds,
                    onOpenInApp = { onAction(HomeAction.OpenInApp(match)) },
                    onLaunchApp = { onAction(HomeAction.LaunchApp(match.app)) },
                    onDelete = { onAction(HomeAction.DeleteMatch(match)) },
                    onDismiss = { onAction(HomeAction.DismissMatch) },
                )
            }

            if (state.isFilterSheetVisible) {
                MatchFilterSheet(
                    apps = state.watchedApps,
                    counts = state.matchCounts,
                    selected = state.filterDraft,
                    onToggle = { onAction(HomeAction.ToggleFilterApp(it)) },
                    onClear = { onAction(HomeAction.ClearFilterDraft) },
                    onApply = { onAction(HomeAction.ApplyFilter) },
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
        onOpenKeywords = {},
        onOpenListenerSettings = {},
        onRestartListener = {},
        onOpenBatterySettings = {},
        onEnableNotifications = {},
    )
}

/**
 * The warnings that mean matches could be missed: at most one listener problem, plus blocked
 * alerts. Animated in and out like the rest of the list.
 */
@Composable
private fun StatusBanners(
    listenerIssue: ListenerIssue,
    notificationsAllowed: Boolean,
    onOpenListenerSettings: () -> Unit,
    onRestartListener: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onEnableNotifications: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = Spacing.l),
        content = {
            ListenerIssueBanners(
                issue = listenerIssue,
                onOpenListenerSettings = onOpenListenerSettings,
                onRestartListener = onRestartListener,
                onOpenBatterySettings = onOpenBatterySettings,
            )
            AnimatedVisibility(
                visible = !notificationsAllowed,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
                content = {
                    StatusBanner(
                        message = stringResource(R.string.banner_notifications_off),
                        actions = listOf(stringResource(R.string.banner_turn_on) to onEnableNotifications),
                        modifier = Modifier.padding(top = Spacing.s),
                    )
                }
            )
        }
    )
}
