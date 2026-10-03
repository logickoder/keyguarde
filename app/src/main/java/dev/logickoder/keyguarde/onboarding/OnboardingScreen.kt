package dev.logickoder.keyguarde.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.logickoder.keyguarde.app.domain.NotificationHelper.isListenerServiceEnabled
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingPopTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingTransition
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.onboarding.components.OnboardingBottomBar
import dev.logickoder.keyguarde.onboarding.domain.OnboardingAction
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import dev.logickoder.keyguarde.onboarding.domain.OnboardingState
import dev.logickoder.keyguarde.onboarding.pages.AppSelectionPage
import dev.logickoder.keyguarde.onboarding.pages.HowItWorksPage
import dev.logickoder.keyguarde.onboarding.pages.KeywordSetupPage
import dev.logickoder.keyguarde.onboarding.pages.PermissionsPage
import dev.logickoder.keyguarde.onboarding.pages.ReadyPage
import dev.logickoder.keyguarde.onboarding.pages.WelcomePage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = viewModel<OnboardingViewModel>(
        factory = OnboardingViewModel.factory(context)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.currentPage) {
        if (state.currentPage == OnboardingPage.Permissions) {
            while (isActive) {
                viewModel.onAction(
                    OnboardingAction.PermissionChecked(isListenerServiceEnabled(context))
                )
                delay(1_000)
            }
        }
    }

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onDone()
        }
    }

    OnboardingContent(
        modifier = modifier,
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        content = { innerPadding ->
            NavDisplay(
                modifier = Modifier.padding(innerPadding),
                backStack = state.backStack,
                onBack = { onAction(OnboardingAction.Previous) },
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
                                apps = state.apps,
                                selected = state.selectedApps,
                                onAdd = { onAction(OnboardingAction.AddApp(it)) },
                                onRemove = { onAction(OnboardingAction.RemoveApp(it)) },
                            )

                            OnboardingPage.KeywordSetup -> KeywordSetupPage(
                                keywords = state.keywords,
                                onAdd = { onAction(OnboardingAction.AddKeyword(it)) },
                                onRemove = { onAction(OnboardingAction.RemoveKeyword(it)) },
                            )

                            OnboardingPage.ReadyScreen -> ReadyPage(
                                isSaving = state.isSaving,
                                onFinish = { onAction(OnboardingAction.Save(context)) }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                modifier = Modifier.navigationBarsPadding(),
                visible = state.currentPage != OnboardingPage.ReadyScreen,
                content = {
                    OnboardingBottomBar(
                        currentPage = state.currentPage,
                        onPrevious = { onAction(OnboardingAction.Previous) },
                        nextEnabled = state.nextEnabled,
                        onNext = { onAction(OnboardingAction.Next) }
                    )
                }
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentPreview() = AppTheme {
    OnboardingContent(
        state = OnboardingState(),
        onAction = {},
    )
}
