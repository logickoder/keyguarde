package dev.logickoder.keyguarde.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingPopTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingTransition
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.onboarding.components.OnboardingBottomBar
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import dev.logickoder.keyguarde.onboarding.domain.rememberOnboardingState
import dev.logickoder.keyguarde.onboarding.pages.AppSelectionPage
import dev.logickoder.keyguarde.onboarding.pages.HowItWorksPage
import dev.logickoder.keyguarde.onboarding.pages.KeywordSetupPage
import dev.logickoder.keyguarde.onboarding.pages.PermissionsPage
import dev.logickoder.keyguarde.onboarding.pages.ReadyPage
import dev.logickoder.keyguarde.onboarding.pages.WelcomePage
import kotlinx.collections.immutable.toImmutableList

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
) {
    val state = rememberOnboardingState(onDone)
    val currentScreen by state.currentScreen.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        content = { innerPadding ->
            NavDisplay(
                modifier = Modifier.padding(innerPadding),
                backStack = state.backStack,
                transitionSpec = onboardingTransition,
                popTransitionSpec = onboardingPopTransition,
                predictivePopTransitionSpec = { onboardingPopTransition() },
                entryProvider = entryProvider {
                    entry<OnboardingPage> { page ->
                        when (page) {
                            OnboardingPage.Welcome -> WelcomePage()
                            OnboardingPage.HowItWorks -> HowItWorksPage()
                            OnboardingPage.Permissions -> PermissionsPage(state.permissionGranted)
                            OnboardingPage.AppSelection -> AppSelectionPage(
                                apps = remember(state.apps) {
                                    state.apps.toImmutableList()
                                },
                                state.selectedApps
                            )

                            OnboardingPage.KeywordSetup -> KeywordSetupPage(state.keywords)
                            OnboardingPage.ReadyScreen -> ReadyPage(
                                isSaving = state.isSaving,
                                onFinish = state::save
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                modifier = Modifier.navigationBarsPadding(),
                visible = currentScreen != OnboardingPage.ReadyScreen,
                content = {
                    OnboardingBottomBar(
                        currentPage = currentScreen,
                        onPrevious = {
                            if (state.backStack.size > 1) {
                                state.backStack.removeLastOrNull()
                            }
                        },
                        nextEnabled = when (currentScreen) {
                            OnboardingPage.Permissions -> state.permissionGranted
                            OnboardingPage.AppSelection -> state.selectedApps.isNotEmpty()
                            OnboardingPage.KeywordSetup -> state.keywords.isNotEmpty()
                            else -> true
                        },
                        onNext = {
                            state.backStack.add(
                                OnboardingPage.entries[currentScreen.ordinal + 1]
                            )
                        }
                    )
                }
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() = AppTheme {
    OnboardingScreen {}
}
