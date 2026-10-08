package dev.logickoder.keyguarde.analytics

import androidx.compose.runtime.staticCompositionLocalOf

/** The app's [Analytics]; previews and tests get [Analytics.None]. */
val LocalAnalytics = staticCompositionLocalOf<Analytics> { Analytics.None }
