package dev.logickoder.keyguarde.analytics

/**
 * Events that don't come from a screen's actions. Kept here with the action mappers, so every
 * event name lives in this package.
 */
object Events {
    // Firebase's own names for a screen view, so its reports treat these as screens.
    fun screenView(name: String) = AnalyticsEvent("screen_view", mapOf("screen_name" to name, "screen_class" to name))

    fun onboardingComplete(apps: Int, keywords: Int) =
        AnalyticsEvent("onboarding_complete", mapOf("apps" to apps, "keywords" to keywords))

    /** @param from "settings", "status_card" or "help". */
    fun rateTapped(from: String) = AnalyticsEvent("rate_tapped", mapOf("from" to from))

    /** @param from "matches", "settings" or "battery". */
    fun batterySettingsOpened(from: String) = AnalyticsEvent("battery_settings_opened", mapOf("from" to from))

    val contactTapped = AnalyticsEvent("contact_tapped")
    val keywordMatchesViewed = AnalyticsEvent("keyword_matches_viewed")
    val sampleAlertSent = AnalyticsEvent("sample_alert_sent")
    val privacyPolicyOpened = AnalyticsEvent("privacy_policy_opened")
}
