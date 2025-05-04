package dev.logickoder.keyguarde.home.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeState(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val repository = AppRepository.getInstance(context)

    var filter by mutableStateOf<WatchedApp?>(null)
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