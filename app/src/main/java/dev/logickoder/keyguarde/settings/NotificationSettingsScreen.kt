package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.NotificationListenerBanner
import dev.logickoder.keyguarde.app.components.NotificationPermissionBanner
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.components.InfoCard
import dev.logickoder.keyguarde.settings.components.SettingsSwitchItem
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.NotificationSettingsAction
import dev.logickoder.keyguarde.settings.domain.NotificationSettingsState

@Composable
fun NotificationSettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val viewModel = viewModel<NotificationSettingsViewModel>(
        factory = NotificationSettingsViewModel.factory(LocalContext.current)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    NotificationSettingsContent(
        modifier = modifier,
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationSettingsContent(
    state: NotificationSettingsState,
    onBack: () -> Unit,
    onAction: (NotificationSettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.notification_settings), onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            NotificationPermissionBanner()
                            NotificationListenerBanner()
                        }
                    )

                    SettingsSwitchItem(
                        title = stringResource(R.string.use_persistent_notification),
                        subtitle = stringResource(R.string.use_persistent_notification_desc),
                        checked = state.usePersistentSilentNotification,
                        onCheckedChange = {
                            onAction(NotificationSettingsAction.TogglePersistentSilentNotification)
                        }
                    )

                    SettingsSwitchItem(
                        title = stringResource(R.string.show_heads_up_alerts),
                        subtitle = stringResource(R.string.show_heads_up_alerts_desc),
                        checked = state.showHeadsUpAlert,
                        onCheckedChange = { onAction(NotificationSettingsAction.ToggleHeadsUpAlert) }
                    )

                    SettingsSwitchItem(
                        title = stringResource(R.string.reset_count_on_open),
                        subtitle = stringResource(R.string.reset_count_on_open_desc),
                        checked = state.resetMatchCountOnAppOpen,
                        onCheckedChange = {
                            onAction(NotificationSettingsAction.ToggleResetMatchCountOnAppOpen)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onAction(NotificationSettingsAction.TestNotification(context)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.canTestNotification,
                        content = {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsActive,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.test_notification))
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    InfoCard(
                        title = stringResource(R.string.how_notifications_work),
                        body = stringResource(R.string.how_notifications_work_desc),
                        icon = Icons.Outlined.Info
                    )
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun NotificationSettingsContentPreview() = AppTheme {
    NotificationSettingsContent(
        state = NotificationSettingsState(
            usePersistentSilentNotification = true,
            showHeadsUpAlert = true,
        ),
        onBack = {},
        onAction = {},
    )
}
