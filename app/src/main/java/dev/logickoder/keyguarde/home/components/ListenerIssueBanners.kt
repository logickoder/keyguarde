package dev.logickoder.keyguarde.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.StatusBanner
import dev.logickoder.keyguarde.app.theme.Spacing
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
    AnimatedVisibility(
        visible = issue == ListenerIssue.AccessOff,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        content = {
            StatusBanner(
                message = stringResource(R.string.banner_access_off),
                actions = listOf(stringResource(R.string.banner_turn_on_access) to onOpenListenerSettings),
                modifier = Modifier.padding(top = Spacing.s),
            )
        }
    )
    AnimatedVisibility(
        visible = issue == ListenerIssue.Stopped,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        content = {
            StatusBanner(
                message = stringResource(R.string.banner_listener_stopped),
                actions = listOf(
                    stringResource(R.string.banner_battery_settings) to onOpenBatterySettings,
                    stringResource(R.string.banner_restart) to onRestartListener,
                ),
                modifier = Modifier.padding(top = Spacing.s),
            )
        }
    )
    AnimatedVisibility(
        visible = issue == ListenerIssue.StillStopped,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        content = {
            StatusBanner(
                message = stringResource(R.string.banner_listener_still_stopped),
                actions = listOf(
                    stringResource(R.string.banner_battery_settings) to onOpenBatterySettings,
                    stringResource(R.string.banner_open_access) to onOpenListenerSettings,
                ),
                modifier = Modifier.padding(top = Spacing.s),
            )
        }
    )
}

