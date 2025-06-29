package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.components.AppList
import dev.logickoder.keyguarde.settings.components.InfoCard
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.rememberWatchedAppsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchedAppsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val state = rememberWatchedAppsState()
    val apps by state.apps.collectAsStateWithLifecycle()
    val watchedApps by state.watchedApps.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Watched Apps", onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(16.dp),
                content = {
                    Text(
                        text = "Select which apps to monitor for keywords",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppList(
                        modifier = Modifier.weight(1f),
                        apps = apps,
                        isSelected = { packageName ->
                            watchedApps.find { it.packageName == packageName } != null
                        },
                        addItem = state::addApp,
                        removeItem = state::removeApp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    InfoCard(
                        title = "Coming Soon",
                        body = "Support for more messaging apps will be added in future updates.",
                        icon = Icons.Outlined.Update
                    )
                }
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun WatchedAppsScreenPreview() = AppTheme {
    WatchedAppsScreen(
        onBack = {}
    )
}