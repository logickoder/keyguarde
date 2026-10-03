package dev.logickoder.keyguarde.onboarding.domain

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
enum class OnboardingPage : NavKey {
    Welcome,
    HowItWorks,
    Permissions,
    AppSelection,
    KeywordSetup,
    ReadyScreen
}
