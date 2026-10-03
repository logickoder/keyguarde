package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ContactSupport
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.navigation.SettingsRoute
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.components.SettingsCategory
import dev.logickoder.keyguarde.settings.components.SettingsTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigate: (SettingsRoute) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.settings))
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
                            title = stringResource(R.string.watched_apps),
                            icon = Icons.Rounded.Apps,
                            description = stringResource(R.string.watched_apps_desc),
                            onClick = {
                                onNavigate(SettingsRoute.Apps)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = stringResource(R.string.notification_settings),
                            icon = Icons.Rounded.Notifications,
                            description = stringResource(R.string.notification_settings_desc),
                            onClick = {
                                onNavigate(SettingsRoute.Notifications)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = stringResource(R.string.battery_background),
                            icon = Icons.Rounded.BatteryChargingFull,
                            description = stringResource(R.string.battery_background_desc),
                            onClick = {
                                onNavigate(SettingsRoute.Battery)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = stringResource(R.string.privacy),
                            icon = Icons.Rounded.Security,
                            description = stringResource(R.string.privacy_desc),
                            onClick = {
                                onNavigate(SettingsRoute.Privacy)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = stringResource(R.string.faq),
                            icon = Icons.AutoMirrored.Rounded.HelpOutline,
                            description = stringResource(R.string.faq_desc_short),
                            onClick = {
                                onNavigate(SettingsRoute.Faqs)
                            }
                        )
                    }

                    item {
                        SettingsCategory(
                            title = stringResource(R.string.contact_support),
                            icon = Icons.AutoMirrored.Rounded.ContactSupport,
                            description = stringResource(R.string.contact_support_desc),
                            onClick = {
                                onNavigate(SettingsRoute.Contact)
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
        onNavigate = {}
    )
}