package dev.logickoder.keyguarde.settings.domain

sealed interface SettingsAction {
    data object ListenerRestartRequested : SettingsAction

    /** The screen posted the test notification; wait for the listener to report it. */
    data object TestSent : SettingsAction
    data object ResetTest : SettingsAction

    data object ToggleHeadsUpAlert : SettingsAction
    data object TogglePersistentNotification : SettingsAction
    data object ToggleResetCountOnOpen : SettingsAction
    data class SetThemeMode(val mode: ThemeMode) : SettingsAction

    data class SetPaused(val paused: Boolean) : SettingsAction

    /** The user opened the Battery screen, from the card's Fix or the Battery use row. */
    data object BatteryScreenOpened : SettingsAction

    /** The user rated or dismissed the prompt. */
    data object RatePromptDone : SettingsAction
}
