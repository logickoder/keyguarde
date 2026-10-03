package dev.logickoder.keyguarde.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.settings.SettingsRepository
import dev.logickoder.keyguarde.settings.domain.NotificationSettingsAction
import dev.logickoder.keyguarde.settings.domain.NotificationSettingsState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationSettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {
    val state: StateFlow<NotificationSettingsState> = combine(
        repository.usePersistentSilentNotification,
        repository.showHeadsUpAlert,
        repository.resetMatchCountOnAppOpen,
    ) { usePersistentSilentNotification, showHeadsUpAlert, resetMatchCountOnAppOpen ->
        NotificationSettingsState(
            usePersistentSilentNotification = usePersistentSilentNotification,
            showHeadsUpAlert = showHeadsUpAlert,
            resetMatchCountOnAppOpen = resetMatchCountOnAppOpen,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotificationSettingsState(),
    )

    fun onAction(action: NotificationSettingsAction) {
        viewModelScope.launch {
            when (action) {
                NotificationSettingsAction.TogglePersistentSilentNotification -> {
                    repository.toggleUsePersistentSilentNotification()
                }

                NotificationSettingsAction.ToggleHeadsUpAlert -> repository.toggleShowHeadsUpAlert()

                NotificationSettingsAction.ToggleResetMatchCountOnAppOpen -> {
                    repository.toggleResetMatchCountOnAppOpen()
                }

                is NotificationSettingsAction.TestNotification -> testNotification(action.context)
            }
        }
    }

    private fun testNotification(context: Context) {
        val state = state.value
        if (state.usePersistentSilentNotification) {
            NotificationHelper.showPersistentNotification(context, 5, 1)
        }
        if (state.showHeadsUpAlert) {
            NotificationHelper.showKeywordMatchNotification(
                context,
                setOf("test 1", "test 2"),
                "test app",
            )
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    NotificationSettingsViewModel(
                        repository = container.settingsRepository,
                    )
                }
            }
        }
    }
}
