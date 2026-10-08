package dev.logickoder.keyguarde.analytics

import dev.logickoder.keyguarde.onboarding.domain.SetupTest

/**
 * The usage event for a finished setup test, or null while it hasn't finished.
 *
 * @param from where the test ran: "onboarding" or "settings".
 */
fun SetupTest.analyticsEvent(from: String): AnalyticsEvent? = when (this) {
    is SetupTest.Caught -> AnalyticsEvent("setup_test", mapOf("result" to "caught", "from" to from))
    SetupTest.Missed -> AnalyticsEvent("setup_test", mapOf("result" to "missed", "from" to from))
    else -> null
}
