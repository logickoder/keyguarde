package dev.logickoder.keyguarde.app.domain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dev.logickoder.keyguarde.app.AppContainer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Reacts to the whole app entering and leaving the foreground. Observes the process, not an
 * Activity, so a rotation or moving between screens never counts as leaving.
 */
class AppVisibilityObserver(private val container: AppContainer) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> AppScope.launch {
                if (container.settingsRepository.resetMatchCountOnAppOpen.first()) {
                    container.resetMatchCount()
                }
            }

            // Back from system settings: re-read what the user changed there.
            Lifecycle.Event.ON_RESUME -> container.systemStatus.refresh()

            // Marks where this visit ended, so the next one can tell what's new.
            Lifecycle.Event.ON_STOP -> AppScope.launch { container.appRepository.markVisited() }

            else -> Unit
        }
    }
}
