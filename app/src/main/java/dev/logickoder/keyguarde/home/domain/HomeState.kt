package dev.logickoder.keyguarde.home.domain

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeState(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val repository = AppRepository.getInstance(context)

    var filter by mutableStateOf<WatchedApp?>(null)
        private set

    var isKeywordDialogVisible by mutableStateOf(false)
        private set

    val watchedApps = repository.watchedApps.map { it.toImmutableList() }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val matches = snapshotFlow { filter }.flatMapLatest { filter ->
        when (filter) {
            null -> repository.matches
            else -> repository.getKeywordMatchesForApp(filter.packageName)
        }
    }.map { it.toImmutableList() }.flowOn(Dispatchers.Default).stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )

    fun onFilterChange(app: WatchedApp?) {
        filter = app
    }

    fun toggleKeywordDialog() {
        isKeywordDialogVisible = !isKeywordDialogVisible
    }

    fun onSaveKeyword(word: String, isCaseSensitive: Boolean) {
        if (word.isNotBlank()) {
            scope.launch {
                repository.addKeyword(Keyword(word, isCaseSensitive = isCaseSensitive))
            }
        }
        isKeywordDialogVisible = false
    }
}

@Composable
fun rememberHomeState(): HomeState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember {
        HomeState(
            context,
            scope,
        )
    }
}