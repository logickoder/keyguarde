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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.StatusBanner
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.domain.appNotificationSettings
import dev.logickoder.keyguarde.app.domain.batteryOptimizationSettings
import dev.logickoder.keyguarde.app.domain.isBatteryUnrestricted
import dev.logickoder.keyguarde.app.domain.openStoreListing
import dev.logickoder.keyguarde.app.domain.startActivitySafely
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
    val viewModel = viewModel<SettingsViewModel>(factory = SettingsViewModel.factory(context))
    val state by viewModel.state.collectAsStateWithLifecycle()

    val checkSystem = {
        viewModel.onAction(
            SettingsAction.SystemChecked(
                hasListenerAccess = NotificationHelper.isListenerServiceEnabled(context),
                notificationsAllowed = NotificationHelper.isNotificationPermissionGranted(context),
                isBatteryUnrestricted = isBatteryUnrestricted(context),
            )
        )
    }
    // All three can change in system settings while the app is in the background.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { checkSystem() }

    val notificationPermission = NotificationHelper.requestNotificationPermissionLauncher { granted ->
        checkSystem()
        // Android stops showing the prompt after repeated denials; settings is the only way left.
        if (!granted) context.startActivitySafely(appNotificationSettings(context))
    }

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
            NotificationHelper.startListenerService(context)
            NotificationHelper.requestListenerServiceRebind(context)
            viewModel.onAction(SettingsAction.ListenerRestartRequested)
        },
        onOpenBatterySettings = { context.startActivitySafely(batteryOptimizationSettings()) },
        onEnableNotifications = {
            if (NotificationHelper.REQUIRES_NOTIFICATION_PERMISSION) {
                notificationPermission.launch(NotificationHelper.PERMISSION)
            } else {
                context.startActivitySafely(appNotificationSettings(context))
            }
        },
        onRate = {
            context.openStoreListing()
            viewModel.onAction(SettingsAction.RatePromptDone)
        },
        onPreviewAlerts = {
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
                                onClick = { onNavigate(SettingsRoute.Battery) },
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
                                onClick = { onNavigate(SettingsRoute.Faqs) },
                            )
                            SettingsDivider()
                            SettingsRow(
                                title = stringResource(R.string.settings_contact),
                                onClick = { onNavigate(SettingsRoute.Contact) },
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
                        catches = state.catches,
                        showRatePrompt = state.showRatePrompt,
                        onRunTest = onRunTest,
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
