package dev.logickoder.keyguarde.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.AppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchItem(
    match: KeywordMatch,
    apps: ImmutableList<WatchedApp>,
    showOpenInApp: Boolean,
    modifier: Modifier = Modifier,
    isSelected: Boolean? = null,
    onDeleteMatch: () -> Unit,
    onOpenInApp: () -> Unit,
    onToggleSelection: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            isSelected == true -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "MatchItemContainerColor"
    )
    val titleColor by animateColorAsState(
        targetValue = when {
            isSelected == true -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "MatchItemContainerColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = {
            when (isSelected) {
                null -> expanded = !expanded
                else -> onToggleSelection()
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            AnimatedContent(
                                targetState = isSelected,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(150)) + scaleIn(
                                        animationSpec = tween(
                                            150
                                        )
                                    ) togetherWith
                                            fadeOut(animationSpec = tween(150)) + scaleOut(
                                        animationSpec = tween(150)
                                    )
                                },
                                content = { isChecked ->
                                    when (isChecked) {
                                        null -> {
                                            // no-op
                                        }

                                        else -> Icon(
                                            imageVector = if (isChecked) {
                                                Icons.Filled.CheckCircle
                                            } else {
                                                Icons.Outlined.RadioButtonUnchecked
                                            },
                                            contentDescription = if (isChecked) "Selected" else "Not selected",
                                            tint = if (isChecked) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            modifier = Modifier
                                                .size(24.dp)
                                                .padding(end = 12.dp)
                                        )
                                    }

                                }
                            )

                            Text(
                                text = match.chat,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = titleColor,
                                modifier = Modifier.weight(1f)
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
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Text(
                                text = remember(match.timestamp) {
                                    formatTime(match.timestamp)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.MoreHoriz,
                                            contentDescription = "Actions",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )

                                ActionMenu(
                                    expanded = showMenu,
                                    onDismiss = { showMenu = false },
                                    showOpen = showOpenInApp,
                                    onDelete = onDeleteMatch,
                                    onOpen = onOpenInApp
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

@Composable
private fun ActionMenu(
    expanded: Boolean,
    showOpen: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss,
        content = {
            AnimatedVisibility(
                visible = showOpen,
                content = {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Open in app",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            onOpen()
                            onDismiss()
                        }
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "Delete match",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    onDelete()
                    onDismiss()
                }
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

@Preview
@Composable
private fun MatchItemPreview() = AppTheme {
    MatchItem(
        match = KeywordMatch(
            id = 1,
            keywords = setOf("keyword", "test"),
            message = "This is a test message with a keyword and a link: https://example.com",
            chat = "Chat Name",
            app = "com.example.app",
            timestamp = LocalDateTime.now()
        ),
        apps = persistentListOf(
            WatchedApp("com.example.app", "Example App", "")
        ),
        showOpenInApp = true,
        onDeleteMatch = {},
        onOpenInApp = {}
    )
}