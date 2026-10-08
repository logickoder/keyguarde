package dev.logickoder.keyguarde.settings

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.domain.appBatterySettings
import dev.logickoder.keyguarde.app.domain.appDetailsSettings
import dev.logickoder.keyguarde.app.domain.isBatteryUnrestricted
import dev.logickoder.keyguarde.app.domain.startActivitySafely
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.settings.components.LinkButton
import dev.logickoder.keyguarde.settings.components.SettingsTopBar

@Composable
fun BatterySettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current
    var unrestricted by remember { mutableStateOf(!inPreview && isBatteryUnrestricted(context)) }
    // The user changes this in system settings, then comes back here.
    LifecycleResumeEffect(Unit) {
        if (!inPreview) unrestricted = isBatteryUnrestricted(context)
        onPauseOrDispose {}
    }

    BatterySettingsContent(
        unrestricted = unrestricted,
        onBack = onBack,
        onOpenBatterySettings = { context.startActivitySafely(appBatterySettings(context)) },
        onOpenAppSettings = { context.startActivitySafely(appDetailsSettings(context)) },
        modifier = modifier,
    )
}

@Composable
private fun BatterySettingsContent(
    unrestricted: Boolean,
    onBack: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.settings_battery), onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.l, vertical = Spacing.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl),
                content = {
                    BatteryStatus(unrestricted = unrestricted, onOpenBatterySettings = onOpenBatterySettings)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    AutostartNote(onOpenAppSettings = onOpenAppSettings)
                }
            )
        }
    )
}

/**
 * Restricted gets the one primary action on the screen. Unrestricted only needs to say so, with a
 * quiet way back into settings.
 */
@Composable
private fun BatteryStatus(unrestricted: Boolean, onOpenBatterySettings: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
        content = {
            Column(
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                content = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                imageVector = if (unrestricted) Icons.Outlined.CheckCircle else Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(
                                    if (unrestricted) R.string.battery_title_unrestricted
                                    else R.string.battery_title_restricted
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.semantics { heading() },
                            )
                        }
                    )
                    // Full width under the title, so the screen keeps one left edge.
                    Text(
                        text = stringResource(
                            if (unrestricted) R.string.battery_body_unrestricted
                            else R.string.battery_body_restricted
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )
            when (unrestricted) {
                true -> LinkButton(
                    text = stringResource(R.string.battery_open_settings),
                    onClick = onOpenBatterySettings,
                )

                else -> {
                    Button(
                        onClick = onOpenBatterySettings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = RoundedCornerShape(Radius.l),
                        // Dark neutral like every primary button: teal is reserved for matched keywords.
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface,
                        ),
                        content = { Text(stringResource(R.string.battery_allow)) },
                    )
                    Text(
                        text = stringResource(batterySteps()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    )
}

// Android 15 moved Unrestricted one level down, behind Allow background usage.
@StringRes
private fun batterySteps(): Int = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM -> R.string.battery_steps_background_usage
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> R.string.battery_steps
    else -> R.string.battery_steps_legacy
}

@Composable
private fun AutostartNote(onOpenAppSettings: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            Text(
                text = stringResource(R.string.autostart_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.autostart_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinkButton(
                text = stringResource(R.string.autostart_open),
                onClick = onOpenAppSettings,
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun BatterySettingsContentPreview() = AppTheme {
    BatterySettingsContent(
        unrestricted = false,
        onBack = {},
        onOpenBatterySettings = {},
        onOpenAppSettings = {},
    )
}
