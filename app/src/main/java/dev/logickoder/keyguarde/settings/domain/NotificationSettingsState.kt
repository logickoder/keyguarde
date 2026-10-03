package dev.logickoder.keyguarde.settings.domain

data class NotificationSettingsState(
    val usePersistentSilentNotification: Boolean = false,
    val showHeadsUpAlert: Boolean = false,
    val resetMatchCountOnAppOpen: Boolean = false,
) {
    val canTestNotification: Boolean
        get() = usePersistentSilentNotification || showHeadsUpAlert
}
