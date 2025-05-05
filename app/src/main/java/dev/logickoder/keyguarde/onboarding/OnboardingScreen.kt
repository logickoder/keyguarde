package dev.logickoder.keyguarde.onboarding

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingEnterTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingExitTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingWelcomeEnterTransition
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.onboarding.components.OnboardingBottomBar
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import dev.logickoder.keyguarde.onboarding.domain.rememberOnboardingState
import dev.logickoder.keyguarde.onboarding.pages.*
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
            NavHost(
                state.controller,
                startDestination = OnboardingPage.Welcome.name,
                modifier = Modifier.padding(innerPadding),
                builder = {
                    screen(OnboardingPage.Welcome) {
                        WelcomePage()
                    }

                    screen(OnboardingPage.HowItWorks) {
                        HowItWorksPage()
                    }

                    screen(OnboardingPage.Permissions) {
                        PermissionsPage(state.permissionGranted)
                    }

                    screen(OnboardingPage.AppSelection) {
                        AppSelectionPage(
                            apps = remember(state.apps) {
                                state.apps.toImmutableList()
                            },
                            state.selectedApps
                        )
                    }

                    screen(OnboardingPage.KeywordSetup) {
                        KeywordSetupPage(state.keywords)
                    }

                    screen(OnboardingPage.ReadyScreen) {
                        ReadyPage(
                            isSaving = state.isSaving,
                            onFinish = state::save
                        )
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = currentScreen != OnboardingPage.ReadyScreen,
                content = {
                    OnboardingBottomBar(
                        currentPage = currentScreen,
                        onPrevious = {
                            if (state.controller.previousBackStackEntry != null) {
                                state.controller.popBackStack()
                            }
                        },
                        nextEnabled = when (currentScreen) {
                            OnboardingPage.Permissions -> state.permissionGranted
                            OnboardingPage.AppSelection -> state.selectedApps.isNotEmpty()
                            OnboardingPage.KeywordSetup -> state.keywords.isNotEmpty()
                            else -> true
                        },
                        onNext = {
                            state.controller.navigate(
                                OnboardingPage.entries[currentScreen.ordinal + 1].name
                            )
                        }
                    )
                }
            )
        },
    )
}

private fun NavGraphBuilder.screen(
    page: OnboardingPage,
    content: @Composable() (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) = composable(
    route = page.name,
    enterTransition = when (page) {
        OnboardingPage.Welcome -> onboardingWelcomeEnterTransition
        else -> onboardingEnterTransition
    },
    exitTransition = when (page) {
        OnboardingPage.ReadyScreen -> null
        else -> onboardingExitTransition
    },
    content = content
)


@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() = AppTheme {
    OnboardingScreen {}
}
