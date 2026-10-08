package dev.logickoder.keyguarde.app.domain

/**
 * What Android currently allows Keyguarde. The user changes all of it in system settings.
 *
 * @property hasListenerAccess notification access, which lets the listener read notifications.
 * @property notificationsAllowed whether Keyguarde may post its own alerts.
 * @property isBatteryUnrestricted whether Android lets Keyguarde run without battery limits.
 */
data class SystemState(
    val hasListenerAccess: Boolean = true,
    val notificationsAllowed: Boolean = true,
    val isBatteryUnrestricted: Boolean = true,
)
