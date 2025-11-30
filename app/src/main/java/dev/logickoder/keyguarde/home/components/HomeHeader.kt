package dev.logickoder.keyguarde.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeHeader(
    selectedMatchesSize: Int?,
    hasMatches: Boolean,
    modifier: Modifier = Modifier,
    toggleSelectionMode: () -> Unit,
    clearAllMatches: () -> Unit,
    selectVisibleMatches: () -> Unit,
    clearSelection: () -> Unit,
    deleteSelectedMatches: () -> Unit,
) {
    val isSelectionMode = selectedMatchesSize != null
    val hasSelection = selectedMatchesSize != null && selectedMatchesSize > 0
    Column(
        modifier = modifier,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Text(
                        text = when (isSelectionMode) {
                            true -> "$selectedMatchesSize selected"
                            else -> "Recent Matches"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = if (isSelectionMode) FontWeight.Medium else FontWeight.Normal
                    )

                    if (hasMatches) {
                        var showMenu by remember { mutableStateOf(false) }

                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Options",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                content = {
                                    if (!isSelectionMode) {
                                        DropdownMenuItem(
                                            text = { Text("Select matches") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            },
                                            onClick = {
                                                toggleSelectionMode()
                                                showMenu = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Clear all matches") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.DeleteSweep,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            },
                                            onClick = {
                                                clearAllMatches()
                                                showMenu = false
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            )
            AnimatedVisibility(
                visible = isSelectionMode && hasMatches,
                content = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.shapes.medium
                            )
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            TextButton(
                                onClick = selectVisibleMatches,
                                content = {
                                    Icon(
                                        Icons.Default.SelectAll,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text("Select Visible")
                                }
                            )

                            TextButton(
                                onClick = clearSelection,
                                content = {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text("Clear")
                                }
                            )

                            OutlinedButton(
                                onClick = deleteSelectedMatches,
                                enabled = hasSelection,
                                content = {
                                    val tint by animateColorAsState(
                                        targetValue = when (hasSelection) {
                                            true -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = tint
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(
                                        "Delete",
                                        color = tint
                                    )
                                }
                            )

                            IconButton(
                                onClick = toggleSelectionMode,
                                content = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Exit selection",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}