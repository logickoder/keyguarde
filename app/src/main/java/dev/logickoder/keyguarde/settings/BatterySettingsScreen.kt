package dev.logickoder.keyguarde.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.settings.components.InfoCard
import dev.logickoder.keyguarde.settings.components.SettingsTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatterySettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val openBatterySettings = remember {
        {
            val action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            val intent = Intent()
            intent.action = action
            intent.data = Uri.parse("package:${context.packageName}")
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback to general battery optimization settings
                context.startActivity(Intent(action))
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Battery & Background", onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(16.dp),
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        content = {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                content = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        content = {
                                            Icon(
                                                imageVector = Icons.Rounded.BatteryChargingFull,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "Battery Optimization",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "For Keyguarde to work reliably, you need to disable battery optimization for this app. This ensures notifications are monitored even when your device is idle.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = openBatterySettings,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            contentColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        content = {
                                            Text("Open Battery Settings")
                                        }
                                    )
                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoCard(
                        title = "Why This Matters",
                        body = "Modern Android systems can restrict background apps to save battery. For Keyguarde to monitor notifications reliably, it needs to be exempt from these restrictions.",
                        icon = Icons.Outlined.Info
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        content = {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                content = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        content = {
                                            Icon(
                                                imageVector = Icons.Outlined.AutoAwesome,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Auto-start Settings",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    )

                                    Text(
                                        text = "Some devices (especially Xiaomi, Huawei, Samsung) have additional restrictions for auto-starting apps. You may need to enable auto-start permission for Keyguarde in your device settings.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}