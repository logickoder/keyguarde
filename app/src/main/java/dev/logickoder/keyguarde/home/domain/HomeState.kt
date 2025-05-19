package dev.logickoder.keyguarde.home.domain

import android.app.ActivityOptions
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.resetMatchCount
import dev.logickoder.keyguarde.app.service.AppListenerService
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

    val openInAppIntents = AppListenerService.notificationIntents.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap(),
    )

    val recentCount = repository.recentMatchCount.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

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
    }

    fun resetCount() = resetMatchCount(context)
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