package dev.logickoder.keyguarde.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.analytics.TrackScreen
import dev.logickoder.keyguarde.app.components.AnimatedStatusBanner
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.components.NeutralSnackbarHost
import dev.logickoder.keyguarde.app.components.ToastType
import dev.logickoder.keyguarde.app.components.showUndo
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.domain.rememberOpenBatterySettings
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.KeywordPillStyle
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.HomeViewModel
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * @param onOpenKeywords switches to the Keywords tab, from the empty state.
 */
@Composable
fun HomeScreen(
    onOpenKeywords: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val toastManager = LocalToastManager.current
    val viewModel = viewModel<HomeViewModel>(factory = HomeViewModel.factory(context))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val matches = viewModel.matches.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    // The newest match an Undo put back. Rows restored above the screen would otherwise return
    // unseen, since the list holds its scroll position when rows are inserted above it.
    var restoredMatchId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(restoredMatchId) {
        val id = restoredMatchId ?: return@LaunchedEffect
        val index = withTimeoutOrNull(RESTORE_SCROLL_TIMEOUT_MILLIS) {
            snapshotFlow {
                matches.itemSnapshotList.indexOfFirst { (it as? MatchListItem.Match)?.match?.id == id }
            }.first { it >= 0 }
        }
        // By key, not index: the layout can still be the one from before the row came back.
        if (index != null && listState.layoutInfo.visibleItemsInfo.none { it.key == id }) {
            listState.animateScrollToItem(index)
        }
        restoredMatchId = null
    }

    // A match that arrives while the list sits at the top goes in above the first row, and the
    // list would hold on to that row and leave the new one just out of view. Follow it instead.
    // A side effect runs before the list measures the new rows, so the request lands in time.
    val firstKey = if (matches.itemCount > 0) matches.peek(0)?.key else null
    var shownFirstKey by remember { mutableStateOf(firstKey) }
    SideEffect {
        if (firstKey == shownFirstKey) return@SideEffect
        shownFirstKey = firstKey
        if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
            listState.requestScrollToItem(0)
        }
    }

    val enableNotifications = NotificationHelper.rememberEnableNotifications()
    TrackScreen("matches")

    LaunchedEffect(viewModel) {
        // Launched, so a pending snackbar never holds up later effects.
        fun offerUndo(message: String, removed: List<KeywordMatch>) = launch {
            val undone = snackbarHostState.showUndo(
                message = message,
                undoLabel = resources.getString(R.string.undo),
                duration = SnackbarDuration.Long,
            )
            if (undone) viewModel.onAction(HomeAction.UndoDelete(removed))
        }

        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.MatchesDeleted -> offerUndo(
                    message = resources.getQuantityString(R.plurals.deleted_match, effect.matches.size, effect.matches.size),
                    removed = effect.matches,
                )

                is HomeEffect.MatchesCleared -> offerUndo(
                    message = resources.getQuantityString(R.plurals.cleared_match, effect.matches.size, effect.matches.size),
                    removed = effect.matches,
                )

                is HomeEffect.MatchesRestored -> restoredMatchId = effect.newestId

                is HomeEffect.LaunchApp -> {
                    when (val intent = context.packageManager.getLaunchIntentForPackage(effect.packageName)) {
                        null -> toastManager.show(
                            message = resources.getString(R.string.launch_app_failed),
                            type = ToastType.Error,
                        )

                        else -> context.startActivity(intent)
                    }
                }

                is HomeEffect.OpenInAppFailed -> toastManager.show(
                    message = resources.getString(
                        R.string.open_in_app_failed,
                        effect.reason ?: resources.getString(R.string.unknown_error),
                    ),
                    type = ToastType.Error,
                )
            }
        }
    }

    HomeContent(
        modifier = modifier,
        state = state,
        query = viewModel.query,
        matches = matches,
        snackbarHostState = snackbarHostState,
        listState = listState,
        onAction = viewModel::onAction,
        onOpenKeywords = onOpenKeywords,
        onOpenListenerSettings = { NotificationHelper.launchListenerSettings(context) },
        onRestartListener = {
            NotificationHelper.restartListener(context)
            viewModel.onAction(HomeAction.ListenerRestartRequested)
        },
        onOpenBatterySettings = rememberOpenBatterySettings(from = "matches"),
        onEnableNotifications = enableNotifications,
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
    listState: LazyListState = rememberLazyListState(),
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
                            FilterChips(state = state, onAction = onAction)
                        }
                    )
                }
            }
        },
        snackbarHost = {
            NeutralSnackbarHost(hostState = snackbarHostState)
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
                        isPaused = state.isPaused,
                        onResume = { onAction(HomeAction.Resume) },
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
                        state = listState,
                        content = {

                            // Rows capture these, not the whole state, so opening a sheet or other
                            // state changes don't recompose every visible row.
                            val isSelectionMode = state.isSelectionMode
                            val selectedMatches = state.selectedMatches
                            val newSinceLastVisit = state.newSinceLastVisit

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
                                        keyword = state.keywordFilter,
                                        onClearKeyword = { onAction(HomeAction.ClearKeywordFilter) },
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
                                                count = newSinceLastVisit,
                                                modifier = Modifier.animateItem(),
                                            )

                                            is MatchListItem.Match -> Column(
                                                modifier = Modifier.animateItem(),
                                                content = {
                                                    MatchRow(
                                                        match = item.match,
                                                        snippet = item.snippet,
                                                        app = appsByPackage[item.match.app],
                                                        isNew = item.isNew,
                                                        isSelected = when (isSelectionMode) {
                                                            true -> item.match.id in selectedMatches
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
    isPaused: Boolean,
    onResume: () -> Unit,
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
            AnimatedStatusBanner(
                visible = isPaused,
                message = stringResource(R.string.banner_paused),
                actions = listOf(stringResource(R.string.status_resume) to onResume),
            )
            ListenerIssueBanners(
                issue = listenerIssue,
                onOpenListenerSettings = onOpenListenerSettings,
                onRestartListener = onRestartListener,
                onOpenBatterySettings = onOpenBatterySettings,
            )
            AnimatedStatusBanner(
                visible = !notificationsAllowed,
                message = stringResource(R.string.banner_notifications_off),
                actions = listOf(stringResource(R.string.banner_turn_on) to onEnableNotifications),
            )
        }
    )
}

/**
 * What the list is limited to, each with its own ×. The keyword chip shows the word in teal, the
 * same way the matched word shows in each row.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterChips(state: HomeState, onAction: (HomeAction) -> Unit) {
    if (state.keywordFilter != null || state.filter.isNotEmpty()) {
        FlowRow(
            modifier = Modifier.padding(horizontal = Spacing.l),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            content = {
                state.keywordFilter?.let { word ->
                    InputChip(
                        selected = true,
                        onClick = { onAction(HomeAction.ClearKeywordFilter) },
                        label = {
                            Text(
                                text = word,
                                style = KeywordPillStyle,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.clear_keyword_filter, word),
                            )
                        },
                    )
                }
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
                    )
                }
            }
        )
    }
}

// How long Undo waits for its restored row to reload before giving up on scrolling to it.
private const val RESTORE_SCROLL_TIMEOUT_MILLIS = 2_000L
