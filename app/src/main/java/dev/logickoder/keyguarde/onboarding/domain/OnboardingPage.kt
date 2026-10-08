package dev.logickoder.keyguarde.onboarding.domain

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * The setup steps, in order. Access comes after keywords and apps, so the user knows what the
 * permission is for before Android asks.
 */
@Serializable
enum class OnboardingPage : NavKey {
    Intro,
    Keywords,
    Apps,
    Access,
    Test,
}
