package dev.logickoder.keyguarde.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing

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
                // Neutral, not red: teal and colour are reserved for matches, and the icon carries the warning.
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
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
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
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
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
        // The user can change this in system settings while the app is in the background.
        LifecycleResumeEffect(Unit) {
            permissionGranted = NotificationHelper.isNotificationPermissionGranted(context)
            onPauseOrDispose {}
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
    // The user can change this in system settings while the app is in the background.
    LifecycleResumeEffect(Unit) {
        if (!inPreview) listenerEnabled = NotificationHelper.isListenerServiceEnabled(context)
        onPauseOrDispose {}
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

/**
 * A neutral warning with explicit actions, for problems that stop Keyguarde catching messages.
 * The icon and wording carry the warning, not colour.
 *
 * @param actions label and handler for each button, most useful first.
 */
@Composable
fun StatusBanner(
    message: String,
    actions: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(Radius.m),
        content = {
            Column(
                modifier = Modifier.padding(start = Spacing.l, end = Spacing.s, top = Spacing.m, bottom = Spacing.xs),
                content = {
                    Row(
                        modifier = Modifier.padding(end = Spacing.s),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    )
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        content = {
                            actions.forEach { (label, onClick) ->
                                TextButton(
                                    onClick = onClick,
                                    content = {
                                        Text(
                                            text = label,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold,
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

@Preview
@Composable
private fun StatusBannerPreview() = AppTheme {
    StatusBanner(
        message = "Keyguarde stopped checking new messages. Android closed its listener in the background.",
        actions = listOf("Restart" to {}, "Battery settings" to {}),
    )
}
