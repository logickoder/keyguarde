package dev.logickoder.keyguarde.settings.domain

sealed interface WatchedAppsEffect {
    /** The user tried to untick the last installed app; Keyguarde would watch nothing. */
    data class LastAppKept(val appName: String) : WatchedAppsEffect
}
