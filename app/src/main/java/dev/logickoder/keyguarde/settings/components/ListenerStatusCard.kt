package dev.logickoder.keyguarde.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.domain.SetupTest

/**
 * Answers "is it working?" while the listener is healthy, and runs the same live test as setup.
 * Listener problems show as banners instead, so this card never shows next to a warning.
 *
 * @param keyword the word the test message carries; null when the user has no keywords yet.
 * @param canSendTest false when Keyguarde can't post notifications, so a test can't be sent.
 */
@Composable
fun ListenerStatusCard(
    test: SetupTest,
    keyword: String?,
    canSendTest: Boolean,
    onRunTest: () -> Unit,
    onResetTest: () -> Unit,
    onOpenListenerSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (title, body) = when (test) {
        SetupTest.Idle -> stringResource(R.string.status_listening) to when {
            keyword == null -> stringResource(R.string.status_no_keywords)
            !canSendTest -> stringResource(R.string.status_body_no_alerts)
            else -> stringResource(R.string.status_body)
        }
        SetupTest.Waiting -> stringResource(R.string.test_title_waiting) to
            stringResource(R.string.test_body, keyword.orEmpty())
        is SetupTest.Caught -> stringResource(R.string.test_title_caught) to
            stringResource(R.string.status_body_caught, test.keyword)
        SetupTest.Missed -> stringResource(R.string.test_title_missed) to stringResource(R.string.test_body_missed)
    }
    val actions = when (test) {
        SetupTest.Idle -> when (keyword != null && canSendTest) {
            true -> listOf(stringResource(R.string.status_run_test) to onRunTest)
            else -> emptyList()
        }
        SetupTest.Waiting -> emptyList()
        is SetupTest.Caught -> listOf(stringResource(R.string.status_done) to onResetTest)
        SetupTest.Missed -> listOf(
            stringResource(R.string.test_try_again) to onResetTest,
            stringResource(R.string.test_open_access) to onOpenListenerSettings,
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(Radius.m),
        content = {
            Column(
                modifier = Modifier.padding(
                    start = Spacing.l,
                    end = Spacing.s,
                    top = Spacing.l,
                    bottom = if (actions.isEmpty()) Spacing.l else Spacing.xs,
                ),
                content = {
                    Row(
                        modifier = Modifier.padding(end = Spacing.s),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                        content = {
                            StatusIcon(test)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { liveRegion = LiveRegionMode.Polite },
                                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                                content = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    Text(
                                        text = body,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            )
                        }
                    )
                    if (actions.isNotEmpty()) {
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
                }
            )
        }
    )
}

@Composable
private fun StatusIcon(test: SetupTest) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
        content = {
            when (test) {
                SetupTest.Waiting -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                SetupTest.Missed -> Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                else -> Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    )
}

@Preview
@Composable
private fun ListenerStatusCardPreview() = AppTheme {
    ListenerStatusCard(
        test = SetupTest.Idle,
        keyword = "invoice",
        canSendTest = true,
        onRunTest = {},
        onResetTest = {},
        onOpenListenerSettings = {},
    )
}
