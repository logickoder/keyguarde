package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.app.navigation.SettingsRoute
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.components.SettingsCategory
import dev.logickoder.keyguarde.settings.components.SettingsTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onNavigate: (SettingsRoute) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Settings", onBack)
        },
        content = { scaffoldPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentPadding = PaddingValues(vertical = 8.dp),
                content = {
                    item {
                        SettingsCategory(
                            title = "Keyword Filters",
                            icon = Icons.Rounded.TextFields,
                            description = "Manage words to watch for in messages",
                            onClick = {
                                onNavigate(SettingsRoute.Keywords)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = "Watched Apps",
                            icon = Icons.Rounded.Apps,
                            description = "Select which apps to monitor for keywords",
                            onClick = {
                                onNavigate(SettingsRoute.Apps)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = "Notification Settings",
                            icon = Icons.Rounded.Notifications,
                            description = "Control how you're alerted about matches",
                            onClick = {
                                onNavigate(SettingsRoute.Notifications)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = "Battery & Background",
                            icon = Icons.Rounded.BatteryChargingFull,
                            description = "Optimize for reliable background operation",
                            onClick = {
                                onNavigate(SettingsRoute.Battery)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = "Privacy",
                            icon = Icons.Rounded.Security,
                            description = "How your data is handled",
                            onClick = {
                                onNavigate(SettingsRoute.Privacy)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = "FAQ",
                            icon = Icons.AutoMirrored.Rounded.HelpOutline,
                            description = "Frequently asked questions",
                            onClick = {
                                onNavigate(SettingsRoute.Faqs)
                            }
                        )
                    }
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() = AppTheme {
    SettingsScreen(
        onBack = {},
        onNavigate = {}
    )
}