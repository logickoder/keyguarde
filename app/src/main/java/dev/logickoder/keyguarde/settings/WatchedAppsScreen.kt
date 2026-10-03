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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.components.AppList
import dev.logickoder.keyguarde.settings.components.InfoCard
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.settings.domain.WatchedAppsState

@Composable
fun WatchedAppsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = viewModel<WatchedAppsViewModel>(
        factory = WatchedAppsViewModel.factory(context)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    WatchedAppsContent(
        modifier = modifier,
        state = state,
        onBack = onBack,
        onAddApp = { viewModel.addApp(context, it) },
        onRemoveApp = viewModel::removeApp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchedAppsContent(
    state: WatchedAppsState,
    onBack: () -> Unit,
    onAddApp: (AppInfo) -> Unit,
    onRemoveApp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.watched_apps), onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(16.dp),
                content = {
                    Text(
                        text = stringResource(R.string.watched_apps_desc),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppList(
                        modifier = Modifier.weight(1f),
                        apps = state.apps,
                        isSelected = { packageName -> packageName in state.watchedPackages },
                        addItem = onAddApp,
                        removeItem = onRemoveApp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    InfoCard(
                        title = stringResource(R.string.coming_soon),
                        body = stringResource(R.string.coming_soon_desc),
                        icon = Icons.Outlined.Update
                    )
                }
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun WatchedAppsContentPreview() = AppTheme {
    WatchedAppsContent(
        state = WatchedAppsState(),
        onBack = {},
        onAddApp = {},
        onRemoveApp = {},
    )
}
