package dev.logickoder.keyguarde.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

                    Text(
                        text = buildAnnotatedString {
                            val message = match.message
                            val keywordIndex = message.indexOf(match.keyword, ignoreCase = true)

                            if (keywordIndex != -1) {
                                append(message.substring(0, keywordIndex))
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Companion.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    block = {
                                        append(
                                            message.substring(
                                                keywordIndex,
                                                keywordIndex + match.keyword.length
                                            )
                                        )
                                    }
                                )
                                append(message.substring(keywordIndex + match.keyword.length))
                            } else {
                                append(message)
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (expanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Companion.Ellipsis
                    )

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
