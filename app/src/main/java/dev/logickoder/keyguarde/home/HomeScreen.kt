package dev.logickoder.keyguarde.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.NotificationListenerBanner
import dev.logickoder.keyguarde.app.components.NotificationPermissionBanner
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.home.components.EmptyMatchesState
import dev.logickoder.keyguarde.home.components.FilterChips
import dev.logickoder.keyguarde.home.components.HomeHeader
import dev.logickoder.keyguarde.home.components.KeywordDialog
import dev.logickoder.keyguarde.home.components.MatchItem
import dev.logickoder.keyguarde.home.components.MatchSummaryCard
import dev.logickoder.keyguarde.home.domain.rememberHomeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettings: () -> Unit,
) {
    val state = rememberHomeState()
    val watchedApps by state.watchedApps.collectAsStateWithLifecycle()
    val matches by state.matches.collectAsStateWithLifecycle()
    val recentCount by state.recentCount.collectAsStateWithLifecycle()
    val openInAppIntents by state.openInAppIntents.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        content = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                                content = {
                                    Icon(
                                        painter = painterResource(R.drawable.logo),
                                        contentDescription = "Keyguarde Logo",
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                            Text(text = stringResource(R.string.app_name))
                        }
                    )
                },
                actions = {
                    IconButton(
                        onSettings,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        content = { scaffoldPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentPadding = PaddingValues(bottom = 80.dp),
                content = {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            content = {
                                NotificationPermissionBanner()
                                NotificationListenerBanner()
                            }
                        )
                    }

                    item {
                        MatchSummaryCard(
                            matchCount = recentCount,
                            onResetClick = state::resetCount
                        )
                    }

                    item {
                        FilterChips(
                            selected = state.filter,
                            apps = watchedApps,
                            onSelected = state::changeFilter
                        )
                    }

                    item {
                        HomeHeader(
                            modifier = Modifier.padding(top = 8.dp).animateItem(),
                            hasMatches = matches.isNotEmpty(),
                            selectedMatchesSize = when (state.isSelectionMode) {
                                true -> state.selectedMatches.size
                                else -> null
                            },
                            toggleSelectionMode = state::toggleSelectionMode,
                            clearAllMatches = state::clearAllMatches,
                            selectAllMatches = state::selectAllMatches,
                            clearSelection = state::clearSelection,
                            deleteSelectedMatches = state::deleteSelectedMatches,
                        )
                    }

                    when (matches.isEmpty()) {
                        true -> item {
                            EmptyMatchesState(modifier = Modifier.animateItem())
                        }

                        else -> items(
                            matches.size,
                            key = { matches[it].id },
                            itemContent = {
                                val match = matches[it]
                                MatchItem(
                                    modifier = Modifier.animateItem(),
                                    match = match,
                                    apps = watchedApps,
                                    showOpenInApp = openInAppIntents.containsKey(match.id),
                                    isSelected = when (state.isSelectionMode) {
                                        true -> state.selectedMatches.contains(match.id)
                                        else -> null
                                    },
                                    onDeleteMatch = {
                                        state.deleteMatch(match)
                                    },
                                    onOpenInApp = {
                                        state.openInApp(match)
                                    },
                                    onToggleSelection = {
                                        state.toggleMatchSelection(match.id)
                                    }
                                )
                            }
                        )
                    }
                }
            )

            if (state.isKeywordDialogVisible) {
                KeywordDialog(
                    onDismiss = state::toggleKeywordDialog,
                    onSave = state::saveKeyword
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = state::toggleKeywordDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Keyword",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() = AppTheme {
    HomeScreen {}
}