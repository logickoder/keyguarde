package dev.logickoder.keyguarde.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.analytics.Events
import dev.logickoder.keyguarde.analytics.LocalAnalytics
import dev.logickoder.keyguarde.analytics.TrackScreen
import dev.logickoder.keyguarde.analytics.log
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.components.StatusBanner
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.domain.openStoreListing
import dev.logickoder.keyguarde.app.domain.rememberOpenBatterySettings
import dev.logickoder.keyguarde.app.navigation.SettingsRoute
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.components.ListenerIssueBanners
import dev.logickoder.keyguarde.home.domain.ListenerIssue
import dev.logickoder.keyguarde.onboarding.domain.SetupTest
import dev.logickoder.keyguarde.settings.components.ListenerStatusCard
import dev.logickoder.keyguarde.settings.components.SettingsDivider
import dev.logickoder.keyguarde.settings.components.SettingsRow
import dev.logickoder.keyguarde.settings.components.SettingsSection
import dev.logickoder.keyguarde.settings.components.SettingsSwitchRow
import dev.logickoder.keyguarde.settings.components.SettingsText
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.components.ThemeSheet
import dev.logickoder.keyguarde.settings.domain.SettingsAction
import dev.logickoder.keyguarde.settings.domain.SettingsState

@Composable
fun SettingsScreen(
    onNavigate: (SettingsRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val analytics = LocalAnalytics.current
    TrackScreen("settings")
    val linkMissing = stringResource(R.string.link_app_missing)
    val viewModel = viewModel<SettingsViewModel>(factory = SettingsViewModel.factory(context))
    val state by viewModel.state.collectAsStateWithLifecycle()

    val enableNotifications = NotificationHelper.rememberEnableNotifications()

    val previewKeywords = setOf(
        stringResource(R.string.test_notification_keyword_1),
        stringResource(R.string.test_notification_keyword_2),
    )
    val previewApp = stringResource(R.string.test_notification_app)

    // A test the listener never reported stays in the shade; clear it so it isn't mistaken for a match.
    LaunchedEffect(state.test) {
        if (state.test == SetupTest.Missed) NotificationHelper.cancelSetupTest(context)
    }

    SettingsContent(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        onRunTest = {
            val keyword = state.testKeyword
            if (keyword != null && NotificationHelper.postSetupTest(context, keyword)) {
                viewModel.onAction(SettingsAction.TestSent)
            }
        },
        onOpenListenerSettings = { NotificationHelper.launchListenerSettings(context) },
        onRestartListener = {
            NotificationHelper.restartListener(context)
            viewModel.onAction(SettingsAction.ListenerRestartRequested)
        },
        onOpenBatterySettings = rememberOpenBatterySettings(from = "settings"),
        onEnableNotifications = enableNotifications,
        onRate = {
            analytics.log(Events.rateTapped(from = "status_card"))
            if (context.openStoreListing()) {
                viewModel.onAction(SettingsAction.RatePromptDone)
            } else {
                toastManager.show(linkMissing)
            }
        },
        onPreviewAlerts = {
            analytics.log(Events.sampleAlertSent)
            if (state.usePersistentNotification) {
                NotificationHelper.showPersistentNotification(context, 5, 1)
            }
            if (state.showHeadsUpAlert) {
                NotificationHelper.showKeywordMatchNotification(context, previewKeywords, previewApp)
            }
        },
    )
}

@Composable
private fun SettingsContent(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigate: (SettingsRoute) -> Unit,
    onRunTest: () -> Unit,
    onOpenListenerSettings: () -> Unit,
    onRestartListener: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onEnableNotifications: () -> Unit,
    onRate: () -> Unit,
    onPreviewAlerts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isThemeSheetVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.settings))
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = Spacing.l),
                content = {
                    Status(
                        state = state,
                        onAction = onAction,
                        onRunTest = onRunTest,
                        onOpenListenerSettings = onOpenListenerSettings,
                        onRestartListener = onRestartListener,
                        onOpenBatterySettings = onOpenBatterySettings,
                        onOpenBatteryScreen = {
                            onAction(SettingsAction.BatteryScreenOpened)
                            onNavigate(SettingsRoute.Battery)
                        },
                        onRate = onRate,
                    )

                    SettingsSection(
                        title = stringResource(R.string.section_watching),
                        content = {
                            SettingsRow(
                                title = stringResource(R.string.settings_apps),
                                value = pluralStringResource(
                                    R.plurals.settings_app_count,
                                    state.watchedAppCount,
                                    state.watchedAppCount,
                                ),
                                onClick = { onNavigate(SettingsRoute.Apps) },
                            )
                        }
                    )

                    AlertsSection(
                        state = state,
                        onAction = onAction,
                        onEnableNotifications = onEnableNotifications,
                        onPreviewAlerts = onPreviewAlerts,
                    )

                    SettingsSection(
                        title = stringResource(R.string.section_reliability),
                        content = {
                            SettingsRow(
                                title = stringResource(R.string.settings_battery),
                                value = when (state.isBatteryUnrestricted) {
                                    true -> stringResource(R.string.battery_unrestricted)
                                    false -> stringResource(R.string.battery_restricted)
                                    null -> null
                                },
                                onClick = {
                                    onAction(SettingsAction.BatteryScreenOpened)
                                    onNavigate(SettingsRoute.Battery)
                                },
                            )
                        }
                    )

                    SettingsSection(
                        title = stringResource(R.string.section_appearance),
                        content = {
                            SettingsRow(
                                title = stringResource(R.string.theme),
                                value = stringResource(state.themeMode.label),
                                onClick = { isThemeSheetVisible = true },
                            )
                        }
                    )

                    SettingsSection(
                        title = stringResource(R.string.section_about),
                        content = {
                            SettingsRow(
                                title = stringResource(R.string.settings_help),
                                onClick = { onNavigate(SettingsRoute.Help) },
                            )
                            SettingsDivider()
                            SettingsRow(
                                title = stringResource(R.string.privacy),
                                onClick = { onNavigate(SettingsRoute.Privacy) },
                            )
                            SettingsText(stringResource(R.string.settings_version, BuildConfig.VERSION_NAME))
                        }
                    )
                }
            )
        }
    )

    if (isThemeSheetVisible) {
        ThemeSheet(
            selected = state.themeMode,
            onSelect = { mode ->
                onAction(SettingsAction.SetThemeMode(mode))
                isThemeSheetVisible = false
            },
            onDismiss = { isThemeSheetVisible = false },
        )
    }
}

/**
 * A listener problem replaces the status card, so the screen never says "Listening" next to a
 * warning that it isn't.
 */
@Composable
private fun Status(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onRunTest: () -> Unit,
    onOpenListenerSettings: () -> Unit,
    onRestartListener: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenBatteryScreen: () -> Unit,
    onRate: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = Spacing.l),
        content = {
            ListenerIssueBanners(
                issue = state.listenerIssue,
                onOpenListenerSettings = onOpenListenerSettings,
                onRestartListener = onRestartListener,
                onOpenBatterySettings = onOpenBatterySettings,
            )
            AnimatedVisibility(
                visible = state.listenerIssue == ListenerIssue.None,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
                content = {
                    ListenerStatusCard(
                        test = state.test,
                        keyword = state.testKeyword,
                        canSendTest = state.notificationsAllowed,
                        caughtCount = state.caughtCount,
                        showRatePrompt = state.showRatePrompt,
                        batteryRestricted = state.showBatteryNotice,
                        isPaused = state.isPaused,
                        onRunTest = onRunTest,
                        onPause = { onAction(SettingsAction.SetPaused(true)) },
                        onResume = { onAction(SettingsAction.SetPaused(false)) },
                        onFixBattery = onOpenBatteryScreen,
                        onResetTest = { onAction(SettingsAction.ResetTest) },
                        onOpenListenerSettings = {
                            onAction(SettingsAction.ResetTest)
                            onOpenListenerSettings()
                        },
                        onRate = onRate,
                        onDismissRate = { onAction(SettingsAction.RatePromptDone) },
                        modifier = Modifier.padding(top = Spacing.s),
                    )
                }
            )
        }
    )
}

@Composable
private fun AlertsSection(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onEnableNotifications: () -> Unit,
    onPreviewAlerts: () -> Unit,
) {
    SettingsSection(
        title = stringResource(R.string.section_alerts),
        content = {
            AnimatedVisibility(
                visible = !state.notificationsAllowed,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
                content = {
                    StatusBanner(
                        message = stringResource(R.string.banner_notifications_off),
                        actions = listOf(stringResource(R.string.banner_turn_on) to onEnableNotifications),
                        modifier = Modifier.padding(horizontal = Spacing.l, vertical = Spacing.s),
                    )
                }
            )
            SettingsSwitchRow(
                title = stringResource(R.string.alerts_heads_up),
                checked = state.showHeadsUpAlert,
                onCheckedChange = { onAction(SettingsAction.ToggleHeadsUpAlert) },
            )
            SettingsDivider()
            SettingsSwitchRow(
                title = stringResource(R.string.alerts_persistent),
                checked = state.usePersistentNotification,
                onCheckedChange = { onAction(SettingsAction.TogglePersistentNotification) },
            )
            SettingsDivider()
            SettingsSwitchRow(
                title = stringResource(R.string.alerts_reset_on_open),
                checked = state.resetCountOnOpen,
                onCheckedChange = { onAction(SettingsAction.ToggleResetCountOnOpen) },
            )
            SettingsDivider()
            SettingsRow(
                title = stringResource(R.string.alerts_preview),
                enabled = state.canPreviewAlerts,
                showChevron = false,
                onClick = onPreviewAlerts,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() = AppTheme {
    SettingsContent(
        state = SettingsState(testKeyword = "invoice", watchedAppCount = 3, isBatteryUnrestricted = false),
        onAction = {},
        onNavigate = {},
        onRunTest = {},
        onOpenListenerSettings = {},
        onRestartListener = {},
        onOpenBatterySettings = {},
        onEnableNotifications = {},
        onRate = {},
        onPreviewAlerts = {},
    )
}
