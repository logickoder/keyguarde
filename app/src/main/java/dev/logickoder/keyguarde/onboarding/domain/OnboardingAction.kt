package dev.logickoder.keyguarde.onboarding.domain

import android.content.Context
import dev.logickoder.keyguarde.app.data.model.Keyword

sealed interface OnboardingAction {
    data object Next : OnboardingAction

    data object Previous : OnboardingAction

    data class ToggleApp(val packageName: String) : OnboardingAction

    data class AddKeyword(val word: String) : OnboardingAction

    data class RemoveKeyword(val keyword: Keyword) : OnboardingAction

    /** The screen re-read both permissions, e.g. on returning from system Settings. */
    data class PermissionsChecked(val listenerGranted: Boolean, val alertsAllowed: Boolean) : OnboardingAction

    /** The screen posted the setup test notification; start waiting for the listener. */
    data object TestSent : OnboardingAction

    /** Back to a fresh test, e.g. after leaving to fix notification access. */
    data object ResetTest : OnboardingAction

    class Save(val context: Context) : OnboardingAction
}
