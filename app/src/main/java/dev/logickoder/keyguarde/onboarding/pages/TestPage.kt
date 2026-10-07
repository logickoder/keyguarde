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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.domain.SetupTest

/**
 * Proves setup worked before the user leaves it: Keyguarde sends itself a notification with
 * one of their keywords and shows whether its listener caught it.
 *
 * @param canSendTest false when Android blocks Keyguarde's own notifications, so no test can post.
 * @param appNames the picked apps, joined for display.
 */
@Composable
fun TestPage(
    test: SetupTest,
    keyword: String,
    canSendTest: Boolean,
    appNames: String,
    onOpenAccessSettings: () -> Unit,
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
                        content = { TestNotification(keyword = keyword, test = test) }
                    )

                    // Announced as it changes, since the result arrives without the user acting.
                    Column(
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(
                                text = title(test, canSendTest),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.semantics { heading() },
                            )
                            Text(
                                text = body(test, canSendTest, keyword, appNames),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = Spacing.m),
                            )
                        }
                    )

                    if (test == SetupTest.Missed) {
                        TextButton(
                            onClick = onOpenAccessSettings,
                            modifier = Modifier.padding(top = Spacing.s),
                            content = {
                                Text(
                                    text = stringResource(R.string.test_open_access),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        )
                    }
                    Box(modifier = Modifier.padding(bottom = Spacing.xl))
                }
            )
        }
    )
}

@Composable
private fun title(test: SetupTest, canSendTest: Boolean): String = stringResource(
    when {
        test is SetupTest.Caught -> R.string.test_title_caught
        test == SetupTest.Missed -> R.string.test_title_missed
        test == SetupTest.Waiting -> R.string.test_title_waiting
        !canSendTest -> R.string.test_title_no_alerts
        else -> R.string.test_title
    }
)

@Composable
private fun body(test: SetupTest, canSendTest: Boolean, keyword: String, appNames: String): String = when {
    test is SetupTest.Caught -> stringResource(R.string.test_body_caught, test.keyword, appNames)
    test == SetupTest.Missed -> stringResource(R.string.test_body_missed)
    !canSendTest -> stringResource(R.string.test_body_no_alerts)
    else -> stringResource(R.string.test_body, keyword)
}

/** The test as it'll look in the shade, with a status line for where it got to. */
@Composable
private fun TestNotification(keyword: String, test: SetupTest) {
    val keywordColor = MaterialTheme.colorScheme.primary
    val text = stringResource(R.string.setup_test_notification_text, keyword)
    val styled = remember(text, keyword, keywordColor) { highlight(text, keyword, keywordColor) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(Radius.l))
                    .padding(Spacing.l),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                content = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape),
                        contentAlignment = Alignment.Center,
                        content = { Icon(Icons.Outlined.Notifications, contentDescription = null) }
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        content = {
                            Text(
                                text = stringResource(R.string.setup_test_notification_title),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = styled,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    )
                }
            )
            AnimatedContent(targetState = test, label = "TestStatus", content = { TestStatus(it) })
        }
    )
}

@Composable
private fun TestStatus(test: SetupTest) {
    Row(
        modifier = Modifier.heightIn(min = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            when (test) {
                SetupTest.Idle -> Unit
                SetupTest.Waiting -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    StatusText(stringResource(R.string.test_status_waiting))
                }

                is SetupTest.Caught -> {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    StatusText(stringResource(R.string.test_status_caught))
                }

                SetupTest.Missed -> {
                    Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    StatusText(stringResource(R.string.test_status_missed))
                }
            }
        }
    )
}

@Composable
private fun StatusText(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
}

private fun highlight(text: String, keyword: String, color: Color): AnnotatedString =
    buildAnnotatedString {
        append(text)
        val start = text.indexOf(keyword, ignoreCase = true)
        if (start >= 0) addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color), start, start + keyword.length)
    }

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun TestPagePreview() = AppTheme {
    TestPage(
        test = SetupTest.Caught("invoice"),
        keyword = "invoice",
        canSendTest = true,
        appNames = "WhatsApp",
        onOpenAccessSettings = {},
    )
}
