package dev.logickoder.keyguarde.onboarding.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing

/**
 * The one permission Keyguarde can't work without, asked last so the user knows what it's for.
 * The picture is the toggle they're about to flip, and it flips here too once they have.
 *
 * @param showAlerts false below Android 13, where apps may post alerts without asking.
 */
@Composable
fun AccessPage(
    accessGranted: Boolean,
    alertsAllowed: Boolean,
    showAlerts: Boolean,
    onEnableAlerts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = Spacing.xl),
                        contentAlignment = Alignment.Center,
                        content = { AccessToggle(on = accessGranted) }
                    )

                    Text(
                        text = stringResource(if (accessGranted) R.string.access_title_done else R.string.access_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(if (accessGranted) R.string.access_body_done else R.string.access_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = Spacing.m, bottom = Spacing.xl),
                    )

                    if (showAlerts) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        AlertsRow(
                            allowed = alertsAllowed,
                            onEnable = onEnableAlerts,
                            modifier = Modifier.padding(top = Spacing.s, bottom = Spacing.l),
                        )
                    }
                }
            )
        }
    )
}

/**
 * A copy of the row the user will see in Settings. Pictures only, so hidden from TalkBack.
 */
@Composable
private fun AccessToggle(on: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(Radius.l))
            .padding(horizontal = Spacing.l, vertical = Spacing.l)
            .clearAndSetSemantics {},
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                content = {
                    Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = stringResource(R.string.access_toggle_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )
            Switch(
                checked = on,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.onSurface,
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    )
}

/**
 * Optional: matches are saved either way, alerts only say when one arrives. Ends in a word, not
 * a disabled button, once it's on, so the state stays readable.
 */
@Composable
private fun AlertsRow(allowed: Boolean, onEnable: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        content = {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                content = {
                    Text(text = stringResource(R.string.access_alerts_title), style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = stringResource(R.string.access_alerts_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )
            AnimatedContent(
                targetState = allowed,
                label = "AlertsState",
                content = { isOn ->
                    when (isOn) {
                        true -> Row(
                            modifier = Modifier.padding(horizontal = Spacing.m),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = stringResource(R.string.access_alerts_on),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        )

                        else -> TextButton(
                            onClick = onEnable,
                            content = {
                                Text(
                                    text = stringResource(R.string.access_alerts_turn_on),
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

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun AccessPagePreview() = AppTheme {
    AccessPage(accessGranted = false, alertsAllowed = false, showAlerts = true, onEnableAlerts = {})
}
