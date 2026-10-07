package dev.logickoder.keyguarde.onboarding.domain

import android.content.Context
import dev.logickoder.keyguarde.app.data.model.Keyword

sealed interface OnboardingAction {
    data object Next : OnboardingAction

    data object Previous : OnboardingAction

    data class ToggleApp(val packageName: String) : OnboardingAction

    data class AddKeyword(val word: String) : OnboardingAction

    data class RemoveKeyword(val keyword: Keyword) : OnboardingAction

    data class PermissionChecked(val granted: Boolean) : OnboardingAction

    class Save(val context: Context) : OnboardingAction
}
