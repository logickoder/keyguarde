package dev.logickoder.keyguarde.home.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.AnimatedStatusBanner
import dev.logickoder.keyguarde.home.domain.ListenerIssue

/**
 * At most one warning that the listener isn't catching messages, with the fix for each cause.
 * Animated in and out so the content below moves instead of jumping.
 */
@Composable
fun ColumnScope.ListenerIssueBanners(
    issue: ListenerIssue,
    onOpenListenerSettings: () -> Unit,
    onRestartListener: () -> Unit,
    onOpenBatterySettings: () -> Unit,
) {
    AnimatedStatusBanner(
        visible = issue == ListenerIssue.AccessOff,
        message = stringResource(R.string.banner_access_off),
        actions = listOf(stringResource(R.string.banner_turn_on_access) to onOpenListenerSettings),
    )
    AnimatedStatusBanner(
        visible = issue == ListenerIssue.Stopped,
        message = stringResource(R.string.banner_listener_stopped),
        actions = listOf(
            stringResource(R.string.banner_battery_settings) to onOpenBatterySettings,
            stringResource(R.string.banner_restart) to onRestartListener,
        ),
    )
    AnimatedStatusBanner(
        visible = issue == ListenerIssue.StillStopped,
        message = stringResource(R.string.banner_listener_still_stopped),
        actions = listOf(
            stringResource(R.string.banner_battery_settings) to onOpenBatterySettings,
            stringResource(R.string.banner_open_access) to onOpenListenerSettings,
        ),
    )
}

