package dev.logickoder.keyguarde.analytics

import dev.logickoder.keyguarde.settings.domain.KeywordsAction

/** The usage event for a Keywords action. Never carries the word itself. */
fun KeywordsAction.analyticsEvent(): AnalyticsEvent? = when (this) {
    is KeywordsAction.Add -> AnalyticsEvent("keyword_added")
    is KeywordsAction.Save -> AnalyticsEvent("keyword_edited")
    is KeywordsAction.Delete -> AnalyticsEvent("keyword_deleted")
    is KeywordsAction.UndoDelete -> AnalyticsEvent("keyword_restored")
    is KeywordsAction.SetSort -> AnalyticsEvent("keywords_sorted", mapOf("sort" to sort.name.lowercase()))
    else -> null
}
