package dev.logickoder.keyguarde.analytics

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * Logs [name] as the screen in view, each time it comes into view.
 */
@Composable
fun TrackScreen(name: String) {
    val analytics = LocalAnalytics.current
    LifecycleResumeEffect(name) {
        analytics.log(Events.screenView(name))
        onPauseOrDispose {}
    }
}
