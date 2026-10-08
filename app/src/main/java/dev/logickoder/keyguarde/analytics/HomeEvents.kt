package dev.logickoder.keyguarde.analytics

import dev.logickoder.keyguarde.home.domain.HomeAction

/**
 * The usage event for a Matches action, or null for steps that aren't a feature use on their
 * own (typing, ticking boxes, opening and closing sheets).
 */
fun HomeAction.analyticsEvent(): AnalyticsEvent? = when (this) {
    is HomeAction.OpenInApp -> AnalyticsEvent("match_opened", mapOf("target" to "chat"))
    is HomeAction.LaunchApp -> AnalyticsEvent("match_opened", mapOf("target" to "app"))
    HomeAction.ApplyFilter -> AnalyticsEvent("matches_filtered")
    is HomeAction.DeleteMatch -> AnalyticsEvent("matches_deleted", mapOf("scope" to "one"))
    HomeAction.DeleteSelectedMatches -> AnalyticsEvent("matches_deleted", mapOf("scope" to "selected"))
    HomeAction.ClearAllMatches -> AnalyticsEvent("matches_deleted", mapOf("scope" to "all"))
    is HomeAction.UndoDelete -> AnalyticsEvent("matches_restored")
    HomeAction.ResetCount -> AnalyticsEvent("count_reset")
    HomeAction.ListenerRestartRequested -> AnalyticsEvent("listener_restarted", mapOf("from" to "matches"))
    HomeAction.Resume -> AnalyticsEvent("resumed", mapOf("from" to "matches"))
    else -> null
}
