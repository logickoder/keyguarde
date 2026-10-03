package dev.logickoder.keyguarde.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.onboarding.domain.saveIconToFile
import dev.logickoder.keyguarde.settings.domain.WatchedAppsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchedAppsViewModel(
    private val repository: AppRepository,
) : ViewModel() {
    private val installedApps = flow {
        emit(repository.getInstalledApps())
    }.flowOn(Dispatchers.Default)

    val state: StateFlow<WatchedAppsState> = combine(
        installedApps,
        repository.watchedApps,
    ) { apps, watchedApps ->
        WatchedAppsState(
            apps = apps.toImmutableList(),
            watchedPackages = watchedApps.map { it.packageName }.toImmutableSet(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WatchedAppsState(),
    )

    fun addApp(context: Context, app: AppInfo) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            repository.addWatchedApp(
                WatchedApp(
                    packageName = app.packageName,
                    name = app.name,
                    icon = saveIconToFile(
                        app.icon,
                        app.packageName,
                        appContext,
                    )
                )
            )
        }
    }

    fun removeApp(packageName: String) {
        viewModelScope.launch {
            repository.deleteWatchedApp(packageName)
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    WatchedAppsViewModel(
                        repository = container.appRepository,
                    )
                }
            }
        }
    }
}
