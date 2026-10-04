package dev.logickoder.keyguarde.home.components

import android.text.format.DateFormat
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.KeywordPillStyle
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.domain.MessageSpan
import dev.logickoder.keyguarde.home.domain.messageSpans
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    Column(
        modifier = modifier.fillMaxWidth(),
        content = {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.l),
                content = {
                    // Top-aligned so the delete icon stays level with the first pill row when pills wrap.
                    Row(
                        verticalAlignment = Alignment.Top,
                        content = {
                            KeywordPills(
                                keywords = match.keywords,
                                // Centres a 28dp pill row on the 48dp icon button.
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 10.dp),
                            )
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
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
                                            formatFullTimestamp(match.timestamp),
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
private fun KeywordPills(keywords: Set<String>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            keywords.sorted().forEach { keyword ->
                Text(
                    text = keyword.uppercase(),
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
    // Selectable so a code, address or number can be copied out.
    SelectionContainer(
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
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
            Button(
                onClick = if (canOpenInApp) onOpenInApp else onLaunchApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(Radius.l),
                // Dark neutral like the reference: teal is kept for matched keywords.
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                ),
                content = {
                    Text(
                        text = when (canOpenInApp) {
                            true -> stringResource(R.string.match_open_in, appName)
                            else -> stringResource(R.string.match_open_app, appName)
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            )
        }
    )
}

private fun highlightedMessage(match: KeywordMatch, keywordColor: Color, linkColor: Color): AnnotatedString {
    val message = match.message
    return buildAnnotatedString {
        append(message)
        messageSpans(message, match.keywords).forEach { span ->
            val end = span.range.last + 1
            when (span) {
                is MessageSpan.Keyword -> addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, color = keywordColor),
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
private fun formatFullTimestamp(timestamp: LocalDateTime): String {
    val locale = LocalConfiguration.current.locales[0]
    return remember(timestamp, locale) {
        val skeleton = when (timestamp.year == LocalDateTime.now().year) {
            true -> "EEEEMMMMdjmm"
            else -> "EEEEMMMMdyjmm"
        }
        timestamp.format(DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, skeleton), locale))
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
            timestamp = LocalDateTime.now().minusHours(3),
        ),
        app = WatchedApp("com.whatsapp", "WhatsApp", ""),
        canOpenInApp = false,
        onOpenInApp = {},
        onLaunchApp = {},
        onDelete = {},
    )
}
