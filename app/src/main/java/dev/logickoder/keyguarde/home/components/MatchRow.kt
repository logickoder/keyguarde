package dev.logickoder.keyguarde.home.components

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.domain.RelativeTime
import dev.logickoder.keyguarde.home.domain.relativeTime
import dev.logickoder.keyguarde.home.domain.windowSnippet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

private val AvatarSize = 40.dp

/** Start inset for the row divider, so it lines up with the text column, not the avatar. */
val MatchRowDividerInset = Spacing.l + AvatarSize + Spacing.m

/**
 * One match: who it came from, when, and a two-line preview cut around the matched keyword.
 * Teal appears only on the keyword itself.
 *
 * @param isSelected null outside selection mode; otherwise whether this row is checked.
 */
@Composable
fun MatchRow(
    match: KeywordMatch,
    app: WatchedApp?,
    isNew: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean? = null,
) {
    val keywordColor = MaterialTheme.colorScheme.primary
    val newLabel = stringResource(R.string.match_new)
    val snippet = remember(match.id, match.message, match.keywords, keywordColor) {
        snippetText(match, keywordColor)
    }
    val isSelectable = isSelected != null
    // Keeps the box ticked while it slides out, instead of unticking mid-animation.
    val isChecked = rememberLastWhile(isSelectable, isSelected == true)
    val selectedColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val background by animateColorAsState(
        targetValue = if (isSelected == true) selectedColor else selectedColor.copy(alpha = 0f),
        label = "MatchRowBackground",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .semantics {
                if (isSelected != null) selected = isSelected
                // The dot is visual only; say it for TalkBack too.
                if (isNew) stateDescription = newLabel
            }
            .heightIn(min = 72.dp)
            .padding(horizontal = Spacing.l, vertical = Spacing.m),
        verticalAlignment = Alignment.Top,
        content = {
            AnimatedVisibility(
                visible = isSelectable,
                modifier = Modifier.align(Alignment.CenterVertically),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
                content = {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.onSurface,
                            checkmarkColor = MaterialTheme.colorScheme.surface,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        modifier = Modifier.padding(end = Spacing.m),
                    )
                }
            )

            ChatAvatar(
                chat = match.chat,
                packageName = match.app,
                app = app,
                size = AvatarSize,
            )

            Spacer(modifier = Modifier.width(Spacing.m))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Text(
                                text = match.chat,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )

                            Spacer(modifier = Modifier.width(Spacing.s))

                            if (isNew) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = Spacing.s)
                                        .size(8.dp)
                                        .background(MaterialTheme.colorScheme.onSurface, CircleShape)
                                )
                            }

                            Text(
                                text = formatRelativeTime(match.timestamp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    )

                    Text(
                        text = snippet,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isNew) FontWeight.Medium else FontWeight.Normal,
                        color = when (isNew) {
                            true -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            )
        }
    )
}

/**
 * Heading above the matches that arrived since the user last left the app.
 */
@Composable
fun NewSinceLastVisitHeader(count: Int, modifier: Modifier = Modifier) {
    Text(
        text = pluralStringResource(R.plurals.new_since_last_visit, count, count),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Spacing.l, end = Spacing.l, top = Spacing.l, bottom = Spacing.xs),
    )
}

@Composable
fun MatchRowDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = MatchRowDividerInset),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

private fun snippetText(match: KeywordMatch, keywordColor: Color): AnnotatedString {
    val snippet = windowSnippet(match.message, match.keywords)
    return buildAnnotatedString {
        append(snippet.text)
        snippet.keywordRanges.forEach { range ->
            addStyle(
                SpanStyle(fontWeight = FontWeight.Bold, color = keywordColor),
                range.first,
                range.last + 1,
            )
        }
    }
}

@Composable
internal fun formatRelativeTime(timestamp: LocalDateTime): String {
    val locale = LocalConfiguration.current.locales[0]
    return when (val time = relativeTime(timestamp, LocalDateTime.now())) {
        RelativeTime.JustNow -> stringResource(R.string.just_now)
        is RelativeTime.Minutes -> stringResource(R.string.minutes_ago, time.count)
        is RelativeTime.Hours -> stringResource(R.string.hours_ago, time.count)
        RelativeTime.Yesterday -> stringResource(R.string.yesterday)
        is RelativeTime.Weekday -> time.day.getDisplayName(TextStyle.FULL, locale)
        is RelativeTime.Date -> {
            val skeleton = if (time.showYear) "MMMdy" else "MMMd"
            time.date.format(DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, skeleton), locale))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchRowPreview() = AppTheme {
    Column(
        content = {
            NewSinceLastVisitHeader(count = 1)
            MatchRow(
                match = KeywordMatch(
                    id = 1,
                    keywords = setOf("invoice"),
                    message = "Morning all, quick round-up from yesterday. The invoice for March is overdue, please chase.",
                    chat = "Design Team",
                    app = "com.whatsapp",
                    timestamp = LocalDateTime.now().minusMinutes(5),
                ),
                app = null,
                isNew = true,
                onClick = {},
                onLongClick = {},
            )
            MatchRowDivider()
            MatchRow(
                match = KeywordMatch(
                    id = 2,
                    keywords = setOf("urgent"),
                    message = "Urgent: the landlord needs the rent receipt today",
                    chat = "Mum",
                    app = "com.whatsapp",
                    timestamp = LocalDateTime.now().minusDays(2),
                ),
                app = null,
                isNew = false,
                onClick = {},
                onLongClick = {},
            )
        }
    )
}
