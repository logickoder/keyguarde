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
import dev.logickoder.keyguarde.settings.domain.WatchedAppsEffect
import dev.logickoder.keyguarde.settings.domain.WatchedAppsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchedAppsViewModel(
    private val repository: AppRepository,
    backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val installedApps = flow {
        emit(repository.getInstalledApps())
    }.flowOn(backgroundDispatcher)

    private val _effects = Channel<WatchedAppsEffect>(Channel.BUFFERED)
    val effects: Flow<WatchedAppsEffect> = _effects.receiveAsFlow()

    val state: StateFlow<WatchedAppsState> = combine(
        installedApps,
        repository.watchedApps,
    ) { apps, watchedApps ->
        val installed = apps.mapTo(mutableSetOf()) { it.packageName }
        WatchedAppsState(
            apps = apps.toImmutableList(),
            watchedPackages = watchedApps.map { it.packageName }.toImmutableSet(),
            missingApps = watchedApps.filter { it.packageName !in installed }.toImmutableList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WatchedAppsState(),
    )

    /**
     * Watches the app if it isn't watched yet, otherwise stops watching it. Takes a context to save
     * the app's icon, which the Matches list shows.
     *
     * The last installed app stays ticked: with none left, Keyguarde would quietly catch nothing.
     * Apps no longer on the phone can always be unticked, since they send nothing anyway.
     */
    fun toggleApp(context: Context, packageName: String) {
        val state = state.value
        val installedWatched = state.apps.filter { it.packageName in state.watchedPackages }
        when {
            packageName !in state.watchedPackages ->
                state.apps.firstOrNull { it.packageName == packageName }?.let { addApp(context, it) }

            installedWatched.singleOrNull()?.packageName == packageName ->
                _effects.trySend(WatchedAppsEffect.LastAppKept(installedWatched.single().name))

            else -> removeApp(packageName)
        }
    }

    private fun addApp(context: Context, app: AppInfo) {
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

    private fun removeApp(packageName: String) {
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
