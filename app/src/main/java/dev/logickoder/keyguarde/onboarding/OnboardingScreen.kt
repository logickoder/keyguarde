package dev.logickoder.keyguarde.onboarding

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.analytics.TrackScreen
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingPopTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.onboardingTransition
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.onboarding.components.OnboardingBottomBar
import dev.logickoder.keyguarde.onboarding.components.OnboardingTopBar
import dev.logickoder.keyguarde.onboarding.domain.OnboardingAction
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import dev.logickoder.keyguarde.onboarding.domain.OnboardingState
import dev.logickoder.keyguarde.onboarding.domain.SetupTest
import dev.logickoder.keyguarde.onboarding.pages.AccessPage
import dev.logickoder.keyguarde.onboarding.pages.AppsPage
import dev.logickoder.keyguarde.onboarding.pages.IntroPage
import dev.logickoder.keyguarde.onboarding.pages.KeywordsPage
import dev.logickoder.keyguarde.onboarding.pages.TestPage
import kotlinx.coroutines.flow.first

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

    val enableAlerts = NotificationHelper.rememberEnableNotifications()

    // A missed test was never read, so nothing cleared it from the shade.
    TrackScreen("onboarding_${state.currentPage.name.lowercase()}")

    LaunchedEffect(state.test) {
        if (state.test == SetupTest.Missed) NotificationHelper.cancelSetupTest(context)
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
        onAllowAccess = { NotificationHelper.launchListenerSettings(context) },
        onSendTest = {
            val keyword = state.testKeyword
            if (keyword != null && NotificationHelper.postSetupTest(context, keyword)) {
                viewModel.onAction(OnboardingAction.TestSent)
            }
        },
        // The row only shows on Android 13+, where alerts need asking for.
        onEnableAlerts = enableAlerts,
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    onAllowAccess: () -> Unit,
    onEnableAlerts: () -> Unit,
    onSendTest: () -> Unit,
    modifier: Modifier = Modifier,
) {
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

                            OnboardingPage.Access -> AccessPage(
                                accessGranted = state.permissionGranted,
                                alertsAllowed = state.alertsAllowed,
                                showAlerts = NotificationHelper.REQUIRES_NOTIFICATION_PERMISSION,
                                onEnableAlerts = onEnableAlerts,
                            )

                            OnboardingPage.Test -> TestPage(
                                test = state.test,
                                keyword = state.testKeyword.orEmpty(),
                                canSendTest = state.alertsAllowed,
                                appNames = remember(state.apps, state.selectedApps) {
                                    state.apps.filter { it.packageName in state.selectedApps }.joinToString { it.name }
                                },
                                onTryAgain = onSendTest,
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
            OnboardingActions(
                state = state,
                onAction = onAction,
                onAllowAccess = onAllowAccess,
                onEnableAlerts = onEnableAlerts,
                onSendTest = onSendTest,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
            )
        },
    )
}

/**
 * The bottom actions for the current step. Each step has one main action; the access and test
 * steps change theirs as the user progresses, so the next thing to do is always the big button.
 */
@Composable
private fun OnboardingActions(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    onAllowAccess: () -> Unit,
    onEnableAlerts: () -> Unit,
    onSendTest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val save = { onAction(OnboardingAction.Save(context)) }
    val next = { onAction(OnboardingAction.Next) }

    val (label, onClick) = when (state.currentPage) {
        OnboardingPage.Intro -> stringResource(R.string.onboarding_get_started) to next
        OnboardingPage.Access -> when (state.permissionGranted) {
            true -> stringResource(R.string.onboarding_continue) to next
            else -> stringResource(R.string.access_allow) to onAllowAccess
        }

        OnboardingPage.Test -> when {
            !state.alertsAllowed -> stringResource(R.string.test_finish) to save
            state.test is SetupTest.Caught -> stringResource(R.string.test_go_to_matches) to save
            state.test == SetupTest.Waiting -> stringResource(R.string.test_status_waiting) to {}
            // The miss means access needs a toggle; going to fix it resets the test for a clean retry.
            state.test == SetupTest.Missed -> stringResource(R.string.test_open_access) to {
                onAction(OnboardingAction.ResetTest)
                onAllowAccess()
            }
            else -> stringResource(R.string.test_send) to onSendTest
        }

        else -> stringResource(R.string.onboarding_continue) to next
    }
    val secondary: Pair<String, () -> Unit>? = when {
        state.currentPage != OnboardingPage.Test -> null
        !state.alertsAllowed -> stringResource(R.string.test_turn_on_alerts) to onEnableAlerts
        state.test == SetupTest.Idle -> stringResource(R.string.test_skip) to save
        state.test == SetupTest.Missed -> stringResource(R.string.test_finish_anyway) to save
        else -> null
    }

    OnboardingBottomBar(
        modifier = modifier,
        label = label,
        enabled = state.nextEnabled && state.test != SetupTest.Waiting && !state.isSaving,
        hint = when (state.currentPage) {
            OnboardingPage.Keywords -> stringResource(R.string.onboarding_hint_keywords)
            OnboardingPage.Apps -> stringResource(R.string.onboarding_hint_apps)
            else -> null
        },
        onClick = onClick,
        secondaryLabel = secondary?.first,
        onSecondary = secondary?.second ?: {},
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentPreview() = AppTheme {
    OnboardingContent(
        state = OnboardingState(),
        onAction = {},
        onAllowAccess = {},
        onEnableAlerts = {},
        onSendTest = {},
    )
}
