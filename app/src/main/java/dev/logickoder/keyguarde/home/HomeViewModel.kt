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
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.app.service.AppListenerService
import dev.logickoder.keyguarde.home.domain.HomeAction
import dev.logickoder.keyguarde.home.domain.HomeEffect
import dev.logickoder.keyguarde.home.domain.HomeState
import dev.logickoder.keyguarde.home.domain.MatchListItem
import kotlinx.collections.immutable.PersistentSet
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel(
    private val repository: AppRepository,
    private val resetMatchCount: ResetMatchCountUsecase,
) : ViewModel() {
    // Compose state, not a flow: a TextField value must update synchronously or the cursor jumps.
    var query by mutableStateOf("")
        private set

    private val inputs = MutableStateFlow(Inputs())

    // Read once per visit, so the "new" divider holds still while the user scrolls.
    private val lastVisit = MutableStateFlow<LocalDateTime?>(null)

    // Copies of the last deleted or cleared matches, so the snackbar's Undo can put them back.
    private var undoable: List<KeywordMatch> = emptyList()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects: Flow<HomeEffect> = _effects.receiveAsFlow()

    // A saved filter for an app that's no longer watched would show an empty list, so it's ignored.
    private val effectiveFilter: Flow<Set<String>> = combine(
        inputs.map { it.filterPackages },
        repository.watchedApps,
    ) { packageNames, apps ->
        packageNames intersect apps.mapTo(mutableSetOf()) { it.packageName }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val newSinceLastVisit: Flow<Int> = combine(lastVisit, effectiveFilter) { since, packageNames ->
        since to packageNames
    }.flatMapLatest { (since, packageNames) ->
        when (since) {
            null -> flowOf(0)
            else -> repository.countMatchesSince(since, packageNames)
        }
    }

    val state: StateFlow<HomeState> = combine(
        inputs,
        repository.watchedApps,
        AppListenerService.notificationIntents,
        newSinceLastVisit,
        repository.matchCountsByApp,
    ) { inputs, watchedApps, intents, newCount, counts ->
        HomeState(
            filter = watchedApps.filter { it.packageName in inputs.filterPackages }.toImmutableList(),
            watchedApps = watchedApps.toImmutableList(),
            matchCounts = counts.toImmutableMap(),
            filterDraft = inputs.filterDraft,
            newSinceLastVisit = newCount,
            openableMatchIds = intents.keys.toImmutableSet(),
            isFilterSheetVisible = inputs.isFilterSheetVisible,
            isClearAllConfirmVisible = inputs.isClearAllConfirmVisible,
            isSelectionMode = inputs.isSelectionMode,
            selectedMatches = inputs.selectedMatches,
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
        lastVisit,
    ) { packageNames, query, since -> Triple(packageNames, query, since) }
        .distinctUntilChanged()
        .flatMapLatest { (packageNames, query, since) ->
            repository.getMatches(packageNames, query).map { page ->
                page.map { match ->
                    MatchListItem.Match(match, isNew = since != null && match.timestamp > since)
                }.insertSeparators { before, after ->
                    // The count ignores search, so the divider only shows on the unsearched list.
                    when {
                        before == null && after?.isNew == true && query.isBlank() -> MatchListItem.NewDivider
                        else -> null
                    }
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .cachedIn(viewModelScope)

    init {
        refreshLastVisit()
        viewModelScope.launch {
            val saved = repository.matchesFilter.first()
            inputs.update { it.copy(filterPackages = saved.toPersistentSet()) }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SearchQueryChanged -> query = action.query

            HomeAction.ShowFilterSheet -> inputs.update {
                it.copy(isFilterSheetVisible = true, filterDraft = it.filterPackages)
            }

            HomeAction.DismissFilterSheet -> inputs.update { it.copy(isFilterSheetVisible = false) }

            is HomeAction.ToggleFilterApp -> inputs.update {
                val draft = it.filterDraft
                it.copy(
                    filterDraft = when (action.packageName in draft) {
                        true -> draft.remove(action.packageName)
                        else -> draft.add(action.packageName)
                    }
                )
            }

            HomeAction.ClearFilterDraft -> inputs.update { it.copy(filterDraft = persistentSetOf()) }

            HomeAction.ApplyFilter -> {
                inputs.update { it.copy(filterPackages = it.filterDraft, isFilterSheetVisible = false) }
                saveFilter()
            }

            HomeAction.ClearFilter -> {
                inputs.update { it.copy(filterPackages = persistentSetOf()) }
                saveFilter()
            }

            is HomeAction.OpenInApp -> openInApp(action.match)

            HomeAction.ResetCount -> viewModelScope.launch { resetMatchCount() }

            HomeAction.RefreshLastVisit -> refreshLastVisit()

            HomeAction.StartSelection -> inputs.update {
                it.copy(isSelectionMode = true, selectedMatches = persistentSetOf())
            }

            is HomeAction.StartSelectionWith -> inputs.update {
                it.copy(isSelectionMode = true, selectedMatches = persistentSetOf(action.matchId))
            }

            HomeAction.ExitSelection -> inputs.update {
                it.copy(isSelectionMode = false, selectedMatches = persistentSetOf())
            }

            is HomeAction.ToggleMatchSelection -> inputs.update {
                val selected = it.selectedMatches
                it.copy(
                    selectedMatches = when (action.matchId in selected) {
                        true -> selected.remove(action.matchId)
                        else -> selected.add(action.matchId)
                    }
                )
            }

            is HomeAction.SelectVisibleMatches -> inputs.update {
                it.copy(selectedMatches = it.selectedMatches.addAll(action.matchIds))
            }

            HomeAction.DeleteSelectedMatches -> deleteSelectedMatches()

            HomeAction.ShowClearAllConfirm -> inputs.update { it.copy(isClearAllConfirmVisible = true) }

            HomeAction.DismissClearAllConfirm -> inputs.update { it.copy(isClearAllConfirmVisible = false) }

            HomeAction.ClearAllMatches -> clearAllMatches()

            HomeAction.UndoDelete -> undoDelete()
        }
    }

    private fun saveFilter() {
        val packageNames = inputs.value.filterPackages
        viewModelScope.launch { repository.saveMatchesFilter(packageNames) }
    }

    private fun refreshLastVisit() {
        viewModelScope.launch {
            lastVisit.value = repository.lastVisitAt.first()
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
        } catch (e: Exception) {
            _effects.trySend(HomeEffect.OpenInAppFailed(e.message))
        }
    }

    private fun deleteSelectedMatches() {
        val selected = inputs.value.selectedMatches.toList()
        if (selected.isEmpty()) return
        viewModelScope.launch {
            undoable = repository.getMatchesByIds(selected)
            repository.deleteKeywordMatches(selected)
            inputs.update { it.copy(isSelectionMode = false, selectedMatches = persistentSetOf()) }
            _effects.send(HomeEffect.MatchesDeleted(selected.size))
        }
    }

    private fun clearAllMatches() {
        inputs.update { it.copy(isClearAllConfirmVisible = false) }
        viewModelScope.launch {
            undoable = repository.getAllMatches()
            _effects.send(HomeEffect.MatchesCleared(repository.clearMatches()))
        }
    }

    private fun undoDelete() {
        val matches = undoable
        undoable = emptyList()
        if (matches.isEmpty()) return
        viewModelScope.launch { repository.restoreMatches(matches) }
    }

    private data class Inputs(
        val filterPackages: PersistentSet<String> = persistentSetOf(),
        val filterDraft: PersistentSet<String> = persistentSetOf(),
        val isFilterSheetVisible: Boolean = false,
        val isClearAllConfirmVisible: Boolean = false,
        val isSelectionMode: Boolean = false,
        val selectedMatches: PersistentSet<Long> = persistentSetOf(),
    )

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    HomeViewModel(
                        repository = container.appRepository,
                        resetMatchCount = container.resetMatchCount,
                    )
                }
            }
        }
    }
}
