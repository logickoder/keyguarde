package dev.logickoder.keyguarde.onboarding.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.domain.NotificationHelper.launchListenerSettings
import dev.logickoder.keyguarde.app.theme.AppTheme

@Composable
fun PermissionsPage(
    listenerGranted: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            val context = LocalContext.current
            var permissionGranted by remember {
                mutableStateOf(NotificationHelper.isNotificationPermissionGranted(context))
            }
            val requiresPermission = NotificationHelper.REQUIRES_NOTIFICATION_PERMISSION
            val permissionLauncher = NotificationHelper.requestNotificationPermissionLauncher {
                permissionGranted = it
            }

            Text(
                text = stringResource(
                    when (requiresPermission) {
                        true -> R.string.permissions_needed
                        else -> R.string.notification_access
                    }
                ),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Notification Listener Card
            PermissionCard(
                icon = Icons.Outlined.Notifications,
                title = stringResource(R.string.notification_access),
                description = stringResource(R.string.notifications_access_desc),
                isGranted = listenerGranted,
                buttonText = stringResource(
                    when (listenerGranted) {
                        true -> R.string.permission_granted
                        else -> R.string.grant_permission
                    }
                ),
                onClick = { launchListenerSettings(context) }
            )

            if (requiresPermission) {
                Spacer(modifier = Modifier.height(16.dp))

                // Post Notifications Card
                PermissionCard(
                    icon = Icons.Outlined.NotificationsActive,
                    title = stringResource(R.string.post_notifications),
                    description = stringResource(R.string.post_notifications_desc),
                    isGranted = permissionGranted,
                    buttonText = stringResource(
                        when (permissionGranted) {
                            true -> R.string.permission_granted
                            else -> R.string.grant_permission
                        }
                    ),
                    onClick = {
                        permissionLauncher.launch(NotificationHelper.PERMISSION)
                    }
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(
                    when (requiresPermission) {
                        true -> R.string.why_are_these_needed
                        else -> R.string.why_is_this_needed
                    }
                ),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.notifications_access_desc_1),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.notifications_access_desc_2),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            if (requiresPermission) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.post_notification_desc_details),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    )
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        enabled = !isGranted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (isGranted) {
                                true -> MaterialTheme.colorScheme.secondary
                                false -> MaterialTheme.colorScheme.primary
                            }
                        ),
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                content = {
                                    if (isGranted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = buttonText,
                                        style = MaterialTheme.typography.labelLarge
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

@Preview(showBackground = true)
@Composable
private fun PermissionsPagePreview() = AppTheme {
    PermissionsPage(false)
}
