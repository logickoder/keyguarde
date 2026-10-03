package dev.logickoder.keyguarde.settings.domain

import android.content.Context

sealed interface NotificationSettingsAction {
    data object TogglePersistentSilentNotification : NotificationSettingsAction

    data object ToggleHeadsUpAlert : NotificationSettingsAction

    data object ToggleResetMatchCountOnAppOpen : NotificationSettingsAction

    class TestNotification(val context: Context) : NotificationSettingsAction
}
