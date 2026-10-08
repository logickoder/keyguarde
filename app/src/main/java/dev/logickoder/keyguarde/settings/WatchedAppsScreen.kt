package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.analytics.TrackScreen
import dev.logickoder.keyguarde.app.components.AppIcon
import dev.logickoder.keyguarde.app.components.AppPicker
import dev.logickoder.keyguarde.app.components.AppPickerRow
import dev.logickoder.keyguarde.app.components.AppPickerSectionLabel
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.WatchedAppsEffect
import dev.logickoder.keyguarde.settings.domain.WatchedAppsState

@Composable
fun WatchedAppsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel = viewModel<WatchedAppsViewModel>(
        factory = WatchedAppsViewModel.factory(context)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val toastManager = LocalToastManager.current
    val resources = LocalResources.current
    TrackScreen("settings_apps")

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is WatchedAppsEffect.LastAppKept -> toastManager.show(
                    resources.getString(R.string.settings_apps_keep_one, effect.appName)
                )
            }
        }
    }

    WatchedAppsContent(
        modifier = modifier,
        state = state,
        onBack = onBack,
        onToggle = { viewModel.toggleApp(context, it) },
    )
}

@Composable
private fun WatchedAppsContent(
    state: WatchedAppsState,
    onBack: () -> Unit,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.settings_apps), onBack)
        },
        content = { scaffoldPadding ->
            AppPicker(
                apps = state.apps,
                selected = state.watchedPackages,
                onToggle = onToggle,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                header = {
                    Text(
                        text = stringResource(R.string.settings_apps_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = Spacing.l, end = Spacing.l, bottom = Spacing.s),
                    )
                },
                gutter = Spacing.l,
                footer = {
                    if (state.missingApps.isNotEmpty()) {
                        item(key = "missing-label") {
                            AppPickerSectionLabel(
                                text = stringResource(R.string.settings_apps_missing),
                                modifier = Modifier.padding(top = Spacing.m),
                                gutter = Spacing.l,
                            )
                        }
                        items(state.missingApps, key = { "missing-${it.packageName}" }) { app ->
                            AppPickerRow(
                                name = app.name,
                                checked = true,
                                onToggle = { onToggle(app.packageName) },
                                modifier = Modifier.animateItem(),
                                gutter = Spacing.l,
                                icon = { AppIcon(app = app, contentDescription = null, size = 32.dp) },
                            )
                        }
                    }
                },
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
        onToggle = {},
    )
}
