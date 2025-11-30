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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.logickoder.keyguarde.app.components.NotificationListenerBanner
import dev.logickoder.keyguarde.app.components.NotificationPermissionBanner
import dev.logickoder.keyguarde.settings.components.InfoCard
import dev.logickoder.keyguarde.settings.components.SettingsSwitchItem
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.rememberNotificationSettingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val state = rememberNotificationSettingState()
    val usePersistentSilentNotification by state.usePersistentSilentNotification.collectAsStateWithLifecycle()
    val showHeadsUpAlert by state.showHeadsUpAlert.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Notification Settings", onBack)
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
                        title = "Use persistent silent notification",
                        subtitle = "Shows count of matches in the notification bar",
                        checked = usePersistentSilentNotification,
                        onCheckedChange = { state.toggleUsePersistentSilentNotification() }
                    )

                    SettingsSwitchItem(
                        title = "Show heads-up alerts for matches",
                        subtitle = "Display a pop-up when keywords are detected",
                        checked = showHeadsUpAlert,
                        onCheckedChange = { state.toggleShowHeadsUpAlert() }
                    )

                    SettingsSwitchItem(
                        title = "Reset match count when app opens",
                        subtitle = "Clear the counter each time you open Keyguarde",
                        checked = state.resetMatchCountOnAppOpen.collectAsStateWithLifecycle().value,
                        onCheckedChange = { state.toggleResetMatchCountOnAppOpen() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = state::testNotification,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = usePersistentSilentNotification || showHeadsUpAlert,
                        content = {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsActive,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Notification")
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    InfoCard(
                        title = "How Notifications Work",
                        body = "Keyguarde uses a silent notification to show how many keywords have been detected since you last opened the app. This helps you stay aware without being interrupted.",
                        icon = Icons.Outlined.Info
                    )
                }
            )
        }
    )
}