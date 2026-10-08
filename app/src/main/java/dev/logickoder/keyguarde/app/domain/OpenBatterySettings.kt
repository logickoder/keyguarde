package dev.logickoder.keyguarde.app.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.analytics.Events
import dev.logickoder.keyguarde.analytics.LocalAnalytics

/**
 * Opens the system screen where the user lifts Keyguarde's battery limit, and counts it.
 *
 * @param from where the user tapped: "matches", "settings" or "battery".
 */
@Composable
fun rememberOpenBatterySettings(from: String): () -> Unit {
    val context = LocalContext.current
    val analytics = LocalAnalytics.current
    return remember(context, analytics, from) {
        {
            analytics.log(Events.batterySettingsOpened(from))
            context.startActivitySafely(appBatterySettings(context))
        }
    }
}
