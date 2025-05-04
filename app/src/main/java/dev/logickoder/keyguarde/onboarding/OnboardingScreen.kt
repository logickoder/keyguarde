package dev.logickoder.keyguarde.onboarding

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

    val welcomeEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        remember {
            {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            }
        }

    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        remember {
            {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            }
        }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        remember {
            {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        }


    Scaffold(
        modifier = modifier,
        content = { innerPadding ->
            NavHost(
                state.controller,
                startDestination = OnboardingPage.Welcome.name,
                modifier = Modifier.padding(innerPadding),
                builder = {
                    composable(
                        route = OnboardingPage.Welcome.name,
                        enterTransition = welcomeEnterTransition,
                        exitTransition = exitTransition
                    ) {
                        WelcomePage()
                    }

                    composable(
                        route = OnboardingPage.HowItWorks.name,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition
                    ) {
                        HowItWorksPage()
                    }

                    composable(
                        route = OnboardingPage.Permissions.name,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition
                    ) {
                        PermissionsPage(state.permissionGranted)
                    }

                    composable(
                        route = OnboardingPage.AppSelection.name,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition
                    ) {
                        AppSelectionPage(
                            apps = remember(state.apps) {
                                state.apps.toImmutableList()
                            },
                            state.selectedApps
                        )
                    }

                    composable(
                        route = OnboardingPage.KeywordSetup.name,
                        enterTransition = enterTransition,
                        exitTransition = exitTransition
                    ) {
                        KeywordSetupPage(state.keywords)
                    }

                    composable(
                        route = OnboardingPage.ReadyScreen.name,
                        enterTransition = enterTransition
                    ) {
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

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() = AppTheme {
    OnboardingScreen {}
}
