package dev.logickoder.keyguarde.onboarding.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
enum class OnboardingPage : Parcelable {
    Welcome,
    HowItWorks,
    Permissions,
    AppSelection,
    KeywordSetup,
    ReadyScreen
}