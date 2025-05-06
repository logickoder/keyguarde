package dev.logickoder.keyguarde.app.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.domain.NotificationHelper

@Composable
fun Banner(
    message: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    showDismiss: Boolean = false,
    onDismiss: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                content = {
                    if (showIcon) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )

                    if (showDismiss && onDismiss != null) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.dismiss),
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun NotificationPermissionBanner(
    modifier: Modifier = Modifier,
) {
    if (NotificationHelper.REQUIRES_NOTIFICATION_PERMISSION) {
        val context = LocalContext.current
        var permissionGranted by remember {
            mutableStateOf(NotificationHelper.isNotificationPermissionGranted(context))
        }
        val permissionLauncher = NotificationHelper.requestNotificationPermissionLauncher {
            permissionGranted = it
        }
        AnimatedVisibility(
            visible = !permissionGranted,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
            content = {
                Banner(
                    message = stringResource(R.string.notification_permission_banner_message),
                    onClick = {
                        permissionLauncher.launch(NotificationHelper.PERMISSION)
                    },
                    modifier = modifier,
                )
            }
        )
    }
}