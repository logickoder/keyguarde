package dev.logickoder.keyguarde.settings.domain

sealed interface SettingsAction {
    /** What the screen read from the system on resume; any of it can change in system settings. */
    data class SystemChecked(
        val hasListenerAccess: Boolean,
        val notificationsAllowed: Boolean,
        val isBatteryUnrestricted: Boolean,
    ) : SettingsAction

    data object ListenerRestartRequested : SettingsAction

    /** The screen posted the test notification; wait for the listener to report it. */
    data object TestSent : SettingsAction
    data object ResetTest : SettingsAction

    data object ToggleHeadsUpAlert : SettingsAction
    data object TogglePersistentNotification : SettingsAction
    data object ToggleResetCountOnOpen : SettingsAction
    data class SetThemeMode(val mode: ThemeMode) : SettingsAction

    data class SetPaused(val paused: Boolean) : SettingsAction

    /** The user rated or dismissed the prompt. */
    data object RatePromptDone : SettingsAction
}
