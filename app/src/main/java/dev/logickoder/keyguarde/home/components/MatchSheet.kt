package dev.logickoder.keyguarde.home.components

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.PrimaryButton
import dev.logickoder.keyguarde.app.components.keywordSpanStyle
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.KeywordPillStyle
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.domain.MessageSpan
import dev.logickoder.keyguarde.home.domain.keywordsByFirstMention
import dev.logickoder.keyguarde.home.domain.messageSpans
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * The whole match: keywords, who sent it and when, the full message, and a way back to the chat.
 * The chat sits beside the title, not in a card below it, so its name shows once.
 *
 * @param canOpenInApp whether the notification's intent is still alive. When it isn't, the
 * primary action falls back to opening the app itself, with the reason shown above it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchSheet(
    match: KeywordMatch,
    app: WatchedApp?,
    canOpenInApp: Boolean,
    onOpenInApp: () -> Unit,
    onLaunchApp: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        // Half-open would hide the Open button below the fold.
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        content = {
            MatchSheetContent(
                match = match,
                app = app,
                canOpenInApp = canOpenInApp,
                onOpenInApp = onOpenInApp,
                onLaunchApp = onLaunchApp,
                onDelete = onDelete,
                modifier = Modifier.navigationBarsPadding(),
            )
        }
    )
}

// Leaves the list visible behind the sheet, so it reads as a peek, not a new screen.
private const val MaxHeightFraction = 0.75f
private val DragHandleHeight = 48.dp

@Composable
private fun MatchSheetContent(
    match: KeywordMatch,
    app: WatchedApp?,
    canOpenInApp: Boolean,
    onOpenInApp: () -> Unit,
    onLaunchApp: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appName = app?.name ?: match.app
    val scrollState = rememberScrollState()
    val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    // The drag handle sits above this content, so it comes out of the budget.
    val maxHeight = windowHeight * MaxHeightFraction - DragHandleHeight

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight),
        content = {
            // Pinned so delete stays in reach while a long message scrolls.
            // Top-aligned so the delete icon stays level with the first pill row when pills wrap.
            Row(
                modifier = Modifier.padding(start = Spacing.xl, end = Spacing.m, bottom = Spacing.l),
                verticalAlignment = Alignment.Top,
                content = {
                    KeywordPills(
                        keywords = remember(match.message, match.keywords) {
                            keywordsByFirstMention(match.message, match.keywords)
                        },
                        // Centres a 28dp pill row on the 48dp icon button.
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 10.dp),
                    )
                    CopyButton(text = match.message)
                    IconButton(
                        onClick = onDelete,
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete_match),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    )
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(scrollState)
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.l),
                content = {
                    // Top-aligned so the avatar stays by the title when a large font wraps the header.
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                        content = {
                            // No app badge: the line beside it names the app, so TalkBack would say it twice.
                            ChatAvatar(chat = match.chat, packageName = match.app, app = null, size = 48.dp)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                content = {
                                    Text(
                                        text = match.chat,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.semantics { heading() },
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.match_source_time,
                                            appName,
                                            formatSheetTimestamp(match.timestamp),
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            )
                        }
                    )

                    MessageBody(match = match)
                }
            )

            // Marks where the message is cut while more of it sits below.
            HorizontalDivider(
                color = when (scrollState.canScrollForward) {
                    true -> MaterialTheme.colorScheme.outlineVariant
                    else -> Color.Transparent
                },
            )

            OpenAction(
                appName = appName,
                canOpenInApp = canOpenInApp,
                onOpenInApp = onOpenInApp,
                onLaunchApp = onLaunchApp,
            )
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeywordPills(keywords: List<String>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            keywords.forEach { keyword ->
                Text(
                    text = keyword,
                    style = KeywordPillStyle,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(Radius.pill))
                        .padding(horizontal = Spacing.m, vertical = 6.dp),
                )
            }
        }
    )
}

@Composable
private fun MessageBody(match: KeywordMatch) {
    val keywordColor = MaterialTheme.colorScheme.primary
    val linkColor = MaterialTheme.colorScheme.onSurface
    val text = remember(match.message, match.keywords, keywordColor, linkColor) {
        highlightedMessage(match, keywordColor, linkColor)
    }
    // Not selectable: the copy button covers copying, links are tappable, and selection
    // handling on a long message slowed the sheet's opening.
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

// How long the copy button shows a tick after copying.
private const val CopiedFeedbackMillis = 2_000L

/**
 * Copies the whole message. The icon turns into a tick for a moment, because a toast or snackbar
 * would sit behind the sheet, and Android before 13 shows no clipboard confirmation of its own.
 */
@Composable
private fun CopyButton(text: String) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(CopiedFeedbackMillis)
            copied = false
        }
    }

    IconButton(
        onClick = {
            scope.launch {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(null, text)))
                copied = true
            }
        },
        content = {
            Icon(
                imageVector = if (copied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                contentDescription = stringResource(if (copied) R.string.message_copied else R.string.copy_message),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    )
}

@Composable
private fun OpenAction(
    appName: String,
    canOpenInApp: Boolean,
    onOpenInApp: () -> Unit,
    onLaunchApp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.xl, bottom = Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
        content = {
            if (!canOpenInApp) {
                Text(
                    text = stringResource(R.string.match_expired),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            PrimaryButton(
                text = when (canOpenInApp) {
                    true -> stringResource(R.string.match_open_in, appName)
                    else -> stringResource(R.string.match_open_app, appName)
                },
                onClick = if (canOpenInApp) onOpenInApp else onLaunchApp,
                // Grey when it can only open the app, so the note and keywords lead instead.
                quiet = !canOpenInApp,
            )
        }
    )
}

private fun highlightedMessage(match: KeywordMatch, keywordColor: Color, linkColor: Color): AnnotatedString {
    val message = match.message
    val spans = messageSpans(message, match.keywords)
    // A phone number split across lines reads as two numbers. Same length, so ranges still line up.
    val display = StringBuilder(message).apply {
        spans.filterIsInstance<MessageSpan.Link>().filter { it.target.startsWith("tel:") }.forEach { link ->
            for (i in link.range) {
                when (this[i]) {
                    ' ' -> setCharAt(i, '\u00A0')
                    '-' -> setCharAt(i, '\u2011')
                }
            }
        }
    }.toString()
    return buildAnnotatedString {
        append(display)
        spans.forEach { span ->
            val end = span.range.last + 1
            when (span) {
                is MessageSpan.Keyword -> addStyle(
                    keywordSpanStyle(keywordColor),
                    span.range.first,
                    end,
                )

                // Underlined, not coloured: colour alone can't say "tap me", and teal means "match".
                is MessageSpan.Link -> addLink(
                    LinkAnnotation.Url(
                        url = span.target,
                        styles = TextLinkStyles(
                            style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
                        ),
                    ),
                    span.range.first,
                    end,
                )
            }
        }
    }
}

@Composable
private fun formatSheetTimestamp(instant: Instant): String {
    val locale = LocalConfiguration.current.locales[0]
    val timestamp = remember(instant) { LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) }
    val today = LocalDate.now()
    val time = remember(timestamp, locale) {
        timestamp.format(localizedFormatter(locale, "jmm"))
    }
    return when (timestamp.toLocalDate()) {
        today -> stringResource(R.string.match_today_at, time)
        today.minusDays(1) -> stringResource(R.string.match_yesterday_at, time)
        else -> remember(timestamp, locale) {
            val skeleton = when (timestamp.year == today.year) {
                true -> "EEEEMMMMdjmm"
                else -> "EEEEMMMMdyjmm"
            }
            timestamp.format(localizedFormatter(locale, skeleton))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchSheetContentPreview() = AppTheme {
    MatchSheetContent(
        match = KeywordMatch(
            id = 1,
            keywords = setOf("invoice", "urgent"),
            message = "Morning all. The invoice for March is overdue and this is urgent. " +
                "Details at www.example.com/pay or call +234 801 234 5678.",
            chat = "Accounts Payable",
            app = "com.whatsapp",
            timestamp = Instant.now().minus(3, ChronoUnit.HOURS),
        ),
        app = WatchedApp("com.whatsapp", "WhatsApp", ""),
        canOpenInApp = false,
        onOpenInApp = {},
        onLaunchApp = {},
        onDelete = {},
    )
}
