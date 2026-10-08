package dev.logickoder.keyguarde.analytics

/**
 * One usage event. Names and values describe what the user did with the app, never what they
 * received: no message text, chat names or keywords, as the privacy policy promises.
 *
 * @param params String or number values only.
 */
data class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any> = emptyMap(),
)
