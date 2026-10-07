package dev.logickoder.keyguarde.onboarding.domain

import dev.logickoder.keyguarde.app.data.model.Keyword
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class OnboardingState(
    val backStack: ImmutableList<OnboardingPage> = persistentListOf(OnboardingPage.Intro),
    val apps: ImmutableList<AppInfo> = persistentListOf(),
    /** Filled from the installed apps once they load; see [hasDefaultedApps]. */
    val selectedApps: ImmutableSet<String> = persistentSetOf(),
    /** Whether the default ticks were applied, so they never override the user's own choice. */
    val hasDefaultedApps: Boolean = false,
    val keywords: ImmutableList<Keyword> = persistentListOf(),
    val permissionGranted: Boolean = false,
    /** Whether Keyguarde may post its own alerts. Optional: matches are saved either way. */
    val alertsAllowed: Boolean = false,
    val isSaving: Boolean = false,
    val isComplete: Boolean = false,
) {
    val currentPage: OnboardingPage
        get() = backStack.last()

    val nextEnabled: Boolean
        get() = when (currentPage) {
            OnboardingPage.Keywords -> keywords.isNotEmpty()
            // Counted against installed apps, so a stale pick can't unlock the step.
            OnboardingPage.Apps -> apps.any { it.packageName in selectedApps }
            else -> true
        }
}
