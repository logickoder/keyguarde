package dev.logickoder.keyguarde.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.domain.NotificationHelper.isListenerServiceEnabled
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingPopTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingTransition
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.onboarding.components.OnboardingBottomBar
import dev.logickoder.keyguarde.onboarding.components.OnboardingTopBar
import dev.logickoder.keyguarde.onboarding.domain.OnboardingAction
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import dev.logickoder.keyguarde.onboarding.domain.OnboardingState
import dev.logickoder.keyguarde.onboarding.pages.AppsPage
import dev.logickoder.keyguarde.onboarding.pages.IntroPage
import dev.logickoder.keyguarde.onboarding.pages.KeywordsPage
import dev.logickoder.keyguarde.onboarding.pages.PermissionsPage
import dev.logickoder.keyguarde.onboarding.pages.ReadyPage
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
        if (state.currentPage == OnboardingPage.Access) {
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
                            OnboardingPage.Intro -> IntroPage()

                            OnboardingPage.Keywords -> KeywordsPage(
                                keywords = state.keywords,
                                onAdd = { onAction(OnboardingAction.AddKeyword(it)) },
                                onRemove = { onAction(OnboardingAction.RemoveKeyword(it)) },
                            )

                            OnboardingPage.Apps -> AppsPage(
                                apps = state.apps,
                                selected = state.selectedApps,
                                onToggle = { onAction(OnboardingAction.ToggleApp(it)) },
                            )

                            OnboardingPage.Access -> PermissionsPage(state.permissionGranted)

                            OnboardingPage.Test -> ReadyPage(
                                isSaving = state.isSaving,
                                onFinish = { onAction(OnboardingAction.Save(context)) }
                            )
                        }
                    }
                }
            )
        },
        topBar = {
            OnboardingTopBar(
                modifier = Modifier.statusBarsPadding(),
                step = state.currentPage.ordinal + 1,
                stepCount = OnboardingPage.entries.size,
                canGoBack = state.backStack.size > 1,
                onBack = { onAction(OnboardingAction.Previous) },
            )
        },
        bottomBar = {
            // The last step brings its own action.
            AnimatedVisibility(
                // Above the keyboard too, so Continue stays reachable while typing keywords.
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
                visible = state.currentPage != OnboardingPage.Test,
                content = {
                    OnboardingBottomBar(
                        label = stringResource(
                            when (state.currentPage) {
                                OnboardingPage.Intro -> R.string.onboarding_get_started
                                else -> R.string.onboarding_continue
                            }
                        ),
                        enabled = state.nextEnabled,
                        hint = when (state.currentPage) {
                            OnboardingPage.Keywords -> stringResource(R.string.onboarding_hint_keywords)
                            OnboardingPage.Apps -> stringResource(R.string.onboarding_hint_apps)
                            OnboardingPage.Access -> stringResource(R.string.onboarding_hint_access)
                            else -> null
                        },
                        onClick = { onAction(OnboardingAction.Next) },
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
