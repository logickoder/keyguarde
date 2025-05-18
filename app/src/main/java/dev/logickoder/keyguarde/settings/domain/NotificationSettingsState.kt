package dev.logickoder.keyguarde.settings.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationSettingState(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val repository = SettingsRepository.getInstance(context)

    val usePersistentSilentNotification = repository.usePersistentSilentNotification.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )
    val showHeadsUpAlert = repository.showHeadsUpAlert.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )
    val resetMatchCountOnAppOpen = repository.resetMatchCountOnAppOpen.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    fun toggleUsePersistentSilentNotification() {
        scope.launch {
            repository.toggleUsePersistentSilentNotification()
        }
    }

    fun toggleShowHeadsUpAlert() {
        scope.launch {
            repository.toggleShowHeadsUpAlert()
        }
    }

    fun toggleResetMatchCountOnAppOpen() {
        scope.launch {
            repository.toggleResetMatchCountOnAppOpen()
        }
    }

    fun testNotification() {
        scope.launch {
            if (usePersistentSilentNotification.first()) {
                NotificationHelper.showPersistentNotification(context, 5, 1)
            }
            if (showHeadsUpAlert.first()) {
                NotificationHelper.showKeywordMatchNotification(context, setOf("test 1", "test 2"), "test app")
            }
        }
    }
}

@Composable
fun rememberNotificationSettingState(): NotificationSettingState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember {
        NotificationSettingState(
            context,
            scope,
        )
    }
}