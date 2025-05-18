package dev.logickoder.keyguarde.settings.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.onboarding.domain.saveIconToFile
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchedAppsState(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val repository = AppRepository.getInstance(context)

    val apps = flow {
        emit(repository.getInstalledApps())
    }.map { it.toImmutableList() }.flowOn(Dispatchers.Default).stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )

    val watchedApps = repository.watchedApps.map { it.toImmutableList() }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )

    fun addApp(app: AppInfo) {
        scope.launch {
            repository.addWatchedApp(
                WatchedApp(
                    packageName = app.packageName,
                    name = app.name,
                    icon = saveIconToFile(
                        app.icon,
                        app.packageName,
                        context
                    )
                )
            )
        }
    }

    fun removeApp(packageName: String) {
        scope.launch {
            repository.deleteWatchedApp(packageName)
        }
    }
}

@Composable
fun rememberWatchedAppsState(): WatchedAppsState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember {
        WatchedAppsState(
            context,
            scope,
        )
    }
}