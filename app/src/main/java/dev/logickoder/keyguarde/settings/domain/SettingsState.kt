package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.CatchStats
import dev.logickoder.keyguarde.home.domain.ListenerIssue
import dev.logickoder.keyguarde.onboarding.domain.SetupTest

data class SettingsState(
    val listenerIssue: ListenerIssue = ListenerIssue.None,
    val notificationsAllowed: Boolean = true,
    val test: SetupTest = SetupTest.Idle,
    /** The keyword the test message carries; null when the user has none to test with. */
    val testKeyword: String? = null,
    val watchedAppCount: Int = 0,
    val showHeadsUpAlert: Boolean = true,
    val usePersistentNotification: Boolean = true,
    val resetCountOnOpen: Boolean = false,
    /** Null until the screen has checked. */
    val isBatteryUnrestricted: Boolean? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val catches: CatchStats = CatchStats(),
    val ratePromptDone: Boolean = true,
) {
    /**
     * Asks for a rating only once Keyguarde has proved itself, and never next to a problem.
     */
    val showRatePrompt: Boolean
        get() = !ratePromptDone &&
            catches.messages >= RATE_PROMPT_MIN_CATCHES &&
            listenerIssue == ListenerIssue.None &&
            test == SetupTest.Idle

    val canPreviewAlerts: Boolean
        get() = notificationsAllowed && (showHeadsUpAlert || usePersistentNotification)
}

private const val RATE_PROMPT_MIN_CATCHES = 10
