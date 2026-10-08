package dev.logickoder.keyguarde.app.domain

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The one place that reads [SystemState]. The app refreshes it whenever it comes back to the
 * foreground (see App), so every screen sees changes made in system settings without checking
 * for itself.
 */
class SystemStatus(private val context: Context) {
    private val _state = MutableStateFlow(read())
    val state: StateFlow<SystemState> = _state.asStateFlow()

    fun refresh() {
        _state.update { read() }
    }

    private fun read() = SystemState(
        hasListenerAccess = NotificationHelper.isListenerServiceEnabled(context),
        notificationsAllowed = NotificationHelper.isNotificationPermissionGranted(context),
        isBatteryUnrestricted = isBatteryUnrestricted(context),
    )
}
