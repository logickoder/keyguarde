package dev.logickoder.keyguarde.home.domain

import android.app.ActivityOptions
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.paging.cachedIn
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.components.ToastManager
import dev.logickoder.keyguarde.app.components.ToastType
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.app.service.AppListenerService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeState(
    private val context: Context,
    private val scope: CoroutineScope,
    private val toastManager: ToastManager,
    inPreview: Boolean,
) {
    private val repository = AppRepository.getInstance(context)

    var filter by mutableStateOf<WatchedApp?>(null)
        private set

    var query by mutableStateOf("")
        private set

    var isKeywordDialogVisible by mutableStateOf(false)
        private set

    var isSelectionMode by mutableStateOf(false)
        private set

    var selectedMatches = mutableStateSetOf<Long>()
        private set

    val watchedApps = repository.watchedApps.map { it.toImmutableList() }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val matches = snapshotFlow { filter to query }.flatMapLatest { (filter, query) ->
        repository.getMatches(filter?.packageName, query)
    }.flowOn(Dispatchers.Default).cachedIn(scope)

    val openInAppIntents = when {
        inPreview -> flowOf(emptyMap())
        else -> AppListenerService.notificationIntents
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap(),
    )

    val recentCount = repository.recentMatchCount.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    fun onSearchQueryChange(query: String) {
        this.query = query
    }

    fun changeFilter(app: WatchedApp?) {
        filter = app
    }

    fun toggleKeywordDialog() {
        isKeywordDialogVisible = !isKeywordDialogVisible
    }

    fun saveKeyword(word: String) {
        if (word.isNotBlank()) {
            scope.launch {
                repository.addKeyword(Keyword(word = word))
            }.invokeOnCompletion {
                toggleKeywordDialog()
            }
        }
    }

    fun deleteMatch(match: KeywordMatch) {
        scope.launch {
            repository.deleteKeywordMatch(match)
        }
    }

    fun openInApp(match: KeywordMatch) {
        try {
            val intent = openInAppIntents.value[match.id] ?: return
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
            toastManager.show(
                message = "Failed to open app: ${e.message ?: "Unknown error"}",
                type = ToastType.Error
            )
        }
    }

    fun resetCount() {
        scope.launch {
            ResetMatchCountUsecase(context)
        }
    }

    fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        if (!isSelectionMode) {
            clearSelection()
        }
    }

    fun toggleMatchSelection(matchId: Long) {
        when (selectedMatches.contains(matchId)) {
            true -> selectedMatches -= matchId
            else -> selectedMatches += matchId
        }
    }

    fun selectVisibleMatches(matches: List<KeywordMatch>) {
        selectedMatches.addAll(matches.map { it.id })
    }

    fun clearSelection() {
        selectedMatches.clear()
    }

    fun deleteSelectedMatches() {
        scope.launch {
            val deletedCount = selectedMatches.size
            repository.deleteKeywordMatches(selectedMatches.toList())
            clearSelection()
            toggleSelectionMode()
            toastManager.show(
                message = context.resources.getQuantityString(
                    R.plurals.deleted_match,
                    deletedCount,
                    deletedCount,
                ),
                type = ToastType.Success
            )
        }
    }

    fun clearAllMatches() {
        scope.launch {
            val deletedCount = repository.clearMatches()
            toastManager.show(
                message = context.resources.getQuantityString(
                    R.plurals.cleared_match,
                    deletedCount,
                    deletedCount,
                ),
                type = ToastType.Success
            )
        }
    }
}

@Composable
fun rememberHomeState(): HomeState {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val inPreview = LocalInspectionMode.current
    val scope = rememberCoroutineScope()

    return remember {
        HomeState(
            context,
            scope,
            toastManager,
            inPreview,
        )
    }
}
