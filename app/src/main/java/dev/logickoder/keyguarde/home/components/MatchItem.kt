package dev.logickoder.keyguarde.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun MatchItem(
    match: KeywordMatch,
    apps: ImmutableList<WatchedApp>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        onClick = { expanded = !expanded },
        content = {
            Column(
                modifier = Modifier.animateContentSize()
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    Row(
                        modifier = Modifier.Companion.fillMaxWidth(),
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        content = {
                            Text(
                                text = match.chat,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Companion.SemiBold,
                                modifier = Modifier.Companion.weight(1f)
                            )

                            val app = remember(apps, match.app) {
                                apps.firstOrNull { it.packageName == match.app }
                            }

                            AnimatedVisibility(
                                visible = app != null,
                                content = {
                                    AppSourceBadge(app = app!!)
                                }
                            )
                        }
                    )

                    SelectionContainer {
                        Text(
                            text = buildText(match),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = if (expanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Companion.Ellipsis
                        )
                    }

                    Text(
                        text = remember(match.timestamp) {
                            formatTime(match.timestamp)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    )
}

@Composable
private fun AppSourceBadge(app: WatchedApp) {
    val color = when (app.packageName) {
        WHATSAPP_PACKAGE_NAME -> MaterialTheme.colorScheme.secondary
        TELEGRAM_PACKAGE_NAME -> MaterialTheme.colorScheme.primary
        else -> contentColorFor(MaterialTheme.colorScheme.surface)
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f),
        contentColor = color,
        content = {
            Text(
                text = app.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    )
}

private fun formatTime(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val minutes = ChronoUnit.MINUTES.between(timestamp, now)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        minutes < 24 * 60 -> "${minutes / 60} hr ago"
        else -> timestamp.format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))
    }
}

@Composable
private fun buildText(match: KeywordMatch): AnnotatedString {
    val keywordRegex = remember {
        match.keywords
            .filter { it.isNotBlank() }
            .joinToString("|") { Regex.escape(it) }
            .toRegex(RegexOption.IGNORE_CASE)
    }

    val message = match.message

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    return remember {
        buildAnnotatedString {
            var currentIndex = 0

            // Merge matches from both keywords and links
            val keywordMatches = keywordRegex.findAll(message).map { "keyword" to it }.toList()
            val linkMatches = LinkRegex.findAll(message).map { "link" to it }.toList()

            val allMatches = (keywordMatches + linkMatches)
                .sortedBy { it.second.range.first }

            for ((type, matchResult) in allMatches) {
                val range = matchResult.range
                if (currentIndex < range.first) {
                    append(message.substring(currentIndex, range.first))
                }

                val matchedText = matchResult.value
                when (type) {
                    "keyword" -> {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        ) {
                            append(matchedText)
                        }
                    }

                    "link" -> {
                        val normalizedLink = when {
                            matchResult.groups["www"] != null || matchResult.groups["domain"] != null -> "https://$matchedText"
                            matchResult.groups["email"] != null -> "mailto:$matchedText"
                            matchResult.groups["phone"] != null -> "tel:$matchedText"
                            else -> matchedText
                        }

                        withLink(
                            LinkAnnotation.Url(
                                normalizedLink,
                                TextLinkStyles(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = secondaryColor
                                    )
                                )
                            )
                        ) {
                            append(matchedText)
                        }
                    }
                }

                currentIndex = range.last + 1
            }

            if (currentIndex < message.length) {
                append(message.substring(currentIndex))
            }
        }
    }
}

private val LinkRegex = run {
    val urlPattern = "(?<url>https?://[\\w./?=&%-]+)"
    val wwwPattern = "(?<www>www\\.[\\w.-]+\\.[a-zA-Z]{2,})"
    val domainPattern = "(?<domain>[\\w-]+\\.[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})*)"
    val emailPattern = "(?<email>[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
    val phonePattern = "(?<phone>\\+?\\d{10,15}|0\\d{9,10})"

    "$urlPattern|$wwwPattern|$domainPattern|$emailPattern|$phonePattern".toRegex()
}