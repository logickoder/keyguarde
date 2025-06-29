package dev.logickoder.keyguarde.settings.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppList(
    apps: ImmutableList<AppInfo>,
    modifier: Modifier = Modifier,
    isSelected: (String) -> Boolean,
    addItem: (AppInfo) -> Unit,
    removeItem: (String) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) {
            apps
        } else {
            apps.filter { app ->
                app.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier,
        content = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = "Search apps...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = apps.isEmpty() to (filteredApps.isEmpty() && searchQuery.isNotBlank()),
                content = { (appsEmpty, searchEmpty) ->
                    when {
                        appsEmpty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                        strokeWidth = 4.dp
                                    )
                                }
                            )
                        }

                        searchEmpty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    Text(
                                        text = "No apps found matching \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            )
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(16.dp),
                                content = {
                                    items(
                                        count = filteredApps.size,
                                        key = { filteredApps[it].packageName },
                                        itemContent = { index ->
                                            val app = filteredApps[index]
                                            AppSelectionItem(
                                                modifier = Modifier.animateItem(),
                                                appName = app.name,
                                                icon = app.icon,
                                                isSelected = isSelected(app.packageName),
                                                onSelectionChanged = { isSelected ->
                                                    when (isSelected) {
                                                        true -> addItem(app)
                                                        else -> removeItem(app.packageName)
                                                    }
                                                }
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    )
}