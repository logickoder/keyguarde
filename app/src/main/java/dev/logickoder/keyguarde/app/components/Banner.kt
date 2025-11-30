package dev.logickoder.keyguarde.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.domain.NotificationHelper

@Composable
fun Banner(
    message: String,
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    showDismiss: Boolean = false,
    onDismiss: (() -> Unit)? = null
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
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

        Banner(
            modifier = modifier,
            visible = !permissionGranted,
            message = stringResource(R.string.notification_permission_banner_message),
            onClick = {
                permissionLauncher.launch(NotificationHelper.PERMISSION)
            },
        )
    }
}

@Composable
fun NotificationListenerBanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current
    var listenerEnabled by remember {
        mutableStateOf(if (inPreview) false else NotificationHelper.isListenerServiceEnabled(context))
    }

    Banner(
        message = stringResource(R.string.notification_listener_banner_message),
        onClick = {
            NotificationHelper.launchListenerSettings(context)
        },
        modifier = modifier,
        visible = !listenerEnabled,
    )
}
