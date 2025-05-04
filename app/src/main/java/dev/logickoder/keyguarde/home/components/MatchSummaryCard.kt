package dev.logickoder.keyguarde.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MatchSummaryCard(
    matchCount: Int,
    modifier: Modifier = Modifier,
    onResetClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        content = {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Companion.CenterVertically,
                content = {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.Companion.width(12.dp))
                    Column(
                        modifier = Modifier.Companion.weight(1f),
                        content = {
                            Text(
                                text = "$matchCount matches since last opened",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Companion.Medium
                            )
                            Text(
                                text = "Tap to reset counter",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    FilledTonalButton(
                        onClick = onResetClick,
                        content = {
                            Text("Reset")
                        }
                    )
                }
            )
        }
    )
}