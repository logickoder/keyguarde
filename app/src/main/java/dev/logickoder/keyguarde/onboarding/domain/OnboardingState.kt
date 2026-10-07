package dev.logickoder.keyguarde.onboarding.domain

import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.Keyword
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class OnboardingState(
    val backStack: ImmutableList<OnboardingPage> = persistentListOf(OnboardingPage.Intro),
    val apps: ImmutableList<AppInfo> = persistentListOf(),
    val selectedApps: ImmutableSet<String> = persistentSetOf(
        WHATSAPP_PACKAGE_NAME,
        TELEGRAM_PACKAGE_NAME,
    ),
    val keywords: ImmutableList<Keyword> = persistentListOf(),
    val permissionGranted: Boolean = false,
    val isSaving: Boolean = false,
    val isComplete: Boolean = false,
) {
    val currentPage: OnboardingPage
        get() = backStack.last()

    val nextEnabled: Boolean
        get() = when (currentPage) {
            OnboardingPage.Keywords -> keywords.isNotEmpty()
            OnboardingPage.Apps -> selectedApps.isNotEmpty()
            OnboardingPage.Access -> permissionGranted
            else -> true
        }
}
