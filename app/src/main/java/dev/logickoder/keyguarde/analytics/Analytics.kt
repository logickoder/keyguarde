package dev.logickoder.keyguarde.analytics

/** Records which screens people see and which features they use. */
fun interface Analytics {
    fun log(event: AnalyticsEvent)

    companion object {
        /** For previews and tests. */
        val None = Analytics {}
    }
}

/** Logs [event] when there is one, so mappers that return null for non-events can be passed straight in. */
fun Analytics.log(event: AnalyticsEvent?) {
    if (event != null) log(event)
}
