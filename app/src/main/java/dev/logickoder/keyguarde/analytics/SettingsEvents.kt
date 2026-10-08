package dev.logickoder.keyguarde.analytics

import dev.logickoder.keyguarde.settings.domain.SettingsAction

/** The usage event for a Settings action, or null when it isn't a feature use on its own. */
fun SettingsAction.analyticsEvent(): AnalyticsEvent? = when (this) {
    SettingsAction.ListenerRestartRequested -> AnalyticsEvent("listener_restarted", mapOf("from" to "settings"))
    SettingsAction.ToggleHeadsUpAlert -> settingChanged("popup_alerts")
    SettingsAction.TogglePersistentNotification -> settingChanged("count_notification")
    SettingsAction.ToggleResetCountOnOpen -> settingChanged("reset_on_open")
    is SettingsAction.SetThemeMode -> AnalyticsEvent("theme_changed", mapOf("mode" to mode.name.lowercase()))
    is SettingsAction.SetPaused -> when (paused) {
        true -> AnalyticsEvent("paused")
        else -> AnalyticsEvent("resumed", mapOf("from" to "settings"))
    }
    else -> null
}

private fun settingChanged(setting: String) = AnalyticsEvent("setting_changed", mapOf("setting" to setting))
