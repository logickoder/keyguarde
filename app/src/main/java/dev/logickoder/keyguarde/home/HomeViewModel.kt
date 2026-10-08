package dev.logickoder.keyguarde.home

import android.app.ActivityOptions
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.domain.SystemState
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.app.service.AppListenerService
import dev.logickoder.keyguarde.home.HomeViewModel
import dev.logickoder.keyguarde.home.domain.HomeAction
import dev.logickoder.keyguarde.home.domain.HomeEffect
import dev.logickoder.keyguarde.home.domain.HomeState
import dev.logickoder.keyguarde.home.domain.ListenerHealth
import dev.logickoder.keyguarde.home.domain.MatchListItem
import dev.logickoder.keyguarde.settings.SettingsRepository
import java.time.Instant
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AppRepository,
    private val resetMatchCount: ResetMatchCountUsecase,
    private val settings: SettingsRepository,
    private val systemState: Flow<SystemState>,
    listenerConnected: Flow<Boolean> = AppListenerService.isConnected,
) : ViewModel() {
    // Compose state, not a flow: a TextField value must update synchronously or the cursor jumps.
    var query by mutableStateOf("")
        private set

    // What the screen itself changes. Everything read from elsewhere is filled in by [state].
    private val ui = MutableStateFlow(HomeState())

    // Only written while the app is in the background (AppVisibilityObserver), so it holds still
    // for the whole visit, and the "new" divider with it.
    private val lastVisitAt: Flow<Instant?> = repository.lastVisitAt

    private val listenerHealth = ListenerHealth(systemState.map { it.hasListenerAccess }, listenerConnected, settings.isPaused)

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects: Flow<HomeEffect> = _effects.receiveAsFlow()

    // Read straight from the saved setting, so the list waits for it instead of loading unfiltered
    // first. A saved app that's no longer watched would show an empty list, so it's ignored.
    private val effectiveFilter: Flow<Set<String>> = combine(
        repository.matchesFilter,
        repository.watchedApps,
    ) { packageNames, apps ->
        packageNames intersect apps.mapTo(mutableSetOf()) { it.packageName }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val newSinceLastVisit: Flow<Int> = combine(lastVisitAt, effectiveFilter, ::Pair).flatMapLatest { (since, packageNames) ->
        when (since) {
            null -> flowOf(0)
            else -> repository.countMatchesSince(since, packageNames)
        }
    }

    val state: StateFlow<HomeState> = combine(
        ui,
        combine(repository.watchedApps, effectiveFilter, ::Pair),
        combine(AppListenerService.notificationIntents, systemState, ::Pair),
        combine(newSinceLastVisit, listenerHealth.issue, settings.isPaused, ::Triple),
        repository.matchCountsByApp,
    ) { ui, (watchedApps, filter), (intents, system), (newCount, issue, isPaused), counts ->
        ui.copy(
            listenerIssue = issue,
            isPaused = isPaused,
            notificationsAllowed = system.notificationsAllowed,
            filter = watchedApps.filter { it.packageName in filter }.toImmutableList(),
            watchedApps = watchedApps.toImmutableList(),
            matchCounts = counts.toImmutableMap(),
            newSinceLastVisit = newCount,
            openableMatchIds = intents.keys.toImmutableSet(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val matches: Flow<PagingData<MatchListItem>> = combine(
        effectiveFilter,
        snapshotFlow { query },
        ui.map { it.keywordFilter }.distinctUntilChanged(),
        lastVisitAt,
    ) { packageNames, query, keyword, since -> ListQuery(packageNames, query, keyword, since) }
        .distinctUntilChanged()
        .flatMapLatest { (packageNames, query, keyword, since) ->
            repository.getMatches(packageNames, query, keyword).map { page ->
                page.map { match ->
                    MatchListItem.Match(match, isNew = since != null && match.timestamp > since)
                }.insertSeparators { before, after ->
                    // The count ignores search and keyword, so the divider only shows without them.
                    when {
                        before == null && after?.isNew == true && query.isBlank() && keyword == null ->
                            MatchListItem.NewDivider
                        else -> null
                    }
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .cachedIn(viewModelScope)

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SearchQueryChanged -> query = action.query

            HomeAction.ShowFilterSheet -> ui.update {
                val current = state.value.filter.map { app -> app.packageName }
                it.copy(isFilterSheetVisible = true, filterDraft = current.toPersistentSet())
            }

            HomeAction.DismissFilterSheet -> ui.update { it.copy(isFilterSheetVisible = false) }

            is HomeAction.ToggleFilterApp -> ui.update {
                it.copy(filterDraft = it.filterDraft.toggle(action.packageName))
            }

            HomeAction.ClearFilterDraft -> ui.update { it.copy(filterDraft = persistentSetOf()) }

            HomeAction.ApplyFilter -> {
                val draft = ui.value.filterDraft
                ui.update { it.copy(isFilterSheetVisible = false) }
                viewModelScope.launch { repository.saveMatchesFilter(draft) }
            }

            HomeAction.ClearFilter -> viewModelScope.launch { repository.saveMatchesFilter(emptySet()) }

            is HomeAction.OpenMatch -> ui.update { it.copy(openMatch = action.match) }

            HomeAction.DismissMatch -> ui.update { it.copy(openMatch = null) }

            is HomeAction.OpenInApp -> openInApp(action.match)

            is HomeAction.LaunchApp -> {
                ui.update { it.copy(openMatch = null) }
                _effects.trySend(HomeEffect.LaunchApp(action.packageName))
            }

            is HomeAction.DeleteMatch -> deleteMatches(listOf(action.match.id))

            HomeAction.ResetCount -> viewModelScope.launch { resetMatchCount() }

            HomeAction.ListenerRestartRequested -> listenerHealth.restartRequested()

            HomeAction.Resume -> viewModelScope.launch { settings.setPaused(false) }

            is HomeAction.FilterByKeyword -> ui.update { it.copy(keywordFilter = action.word) }

            HomeAction.ClearKeywordFilter -> ui.update { it.copy(keywordFilter = null) }

            HomeAction.StartSelection -> ui.update {
                it.copy(isSelectionMode = true, selectedMatches = persistentSetOf())
            }

            is HomeAction.StartSelectionWith -> ui.update {
                it.copy(isSelectionMode = true, selectedMatches = persistentSetOf(action.matchId))
            }

            HomeAction.ExitSelection -> ui.update {
                it.copy(isSelectionMode = false, selectedMatches = persistentSetOf())
            }

            is HomeAction.ToggleMatchSelection -> ui.update {
                it.copy(selectedMatches = it.selectedMatches.toggle(action.matchId))
            }

            is HomeAction.SelectVisibleMatches -> ui.update {
                it.copy(selectedMatches = it.selectedMatches.toPersistentSet().addingAll(action.matchIds))
            }

            HomeAction.DeleteSelectedMatches -> deleteSelectedMatches()

            HomeAction.ShowClearAllConfirm -> ui.update { it.copy(isClearAllConfirmVisible = true) }

            HomeAction.DismissClearAllConfirm -> ui.update { it.copy(isClearAllConfirmVisible = false) }

            HomeAction.ClearAllMatches -> clearAllMatches()

            is HomeAction.UndoDelete -> viewModelScope.launch {
                repository.restoreMatches(action.matches)
                action.matches.maxByOrNull { it.timestamp }?.let { newest ->
                    _effects.send(HomeEffect.MatchesRestored(newest.id))
                }
            }
        }
    }

    private fun openInApp(match: KeywordMatch) {
        try {
            val intent = AppListenerService.notificationIntents.value[match.id] ?: return
            when {
                SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    intent.send(
                        null,
                        0,
                        null,
                        null,
                        null,
                        null,
                        ActivityOptions.makeBasic().apply {
                            @Suppress("DEPRECATION")
                            pendingIntentBackgroundActivityStartMode = when {
                                SDK_INT >= Build.VERSION_CODES.BAKLAVA -> ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS
                                else -> ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                            }
                        }.toBundle(),
                    )
                }

                else -> {
                    intent.send()
                }
            }
            ui.update { it.copy(openMatch = null) }
        } catch (e: Exception) {
            _effects.trySend(HomeEffect.OpenInAppFailed(e.message))
        }
    }

    private fun deleteSelectedMatches() {
        val selected = ui.value.selectedMatches.toList()
        if (selected.isEmpty()) return
        ui.update { it.copy(isSelectionMode = false, selectedMatches = persistentSetOf()) }
        deleteMatches(selected)
    }

    private fun deleteMatches(ids: List<Long>) {
        if (ids.isEmpty()) return
        ui.update { it.copy(openMatch = null) }
        viewModelScope.launch {
            // Copied first, so Undo can put them back with their original ids.
            val removed = repository.getMatchesByIds(ids)
            repository.deleteKeywordMatches(ids)
            _effects.send(HomeEffect.MatchesDeleted(removed))
        }
    }

    private fun clearAllMatches() {
        ui.update { it.copy(isClearAllConfirmVisible = false) }
        viewModelScope.launch {
            val removed = repository.getAllMatches()
            repository.clearMatches()
            _effects.send(HomeEffect.MatchesCleared(removed))
        }
    }

    private data class ListQuery(
        val packageNames: Set<String>,
        val query: String,
        val keyword: String?,
        val since: Instant?,
    )

    private fun <T> ImmutableSet<T>.toggle(item: T): ImmutableSet<T> = toPersistentSet().let {
        if (item in it) it.removing(item) else it.adding(item)
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    HomeViewModel(
                        repository = container.appRepository,
                        resetMatchCount = container.resetMatchCount,
                        settings = container.settingsRepository,
                        systemState = container.systemStatus.state,
                    )
                }
            }
        }
    }
}
