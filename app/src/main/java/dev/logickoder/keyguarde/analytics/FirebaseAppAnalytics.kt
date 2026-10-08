package dev.logickoder.keyguarde.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/** Sends [Analytics] to Firebase. */
class FirebaseAppAnalytics(context: Context) : Analytics {
    private val firebase = FirebaseAnalytics.getInstance(context)

    override fun log(event: AnalyticsEvent) {
        val params = Bundle()
        event.params.forEach { (key, value) ->
            when (value) {
                is Number -> params.putLong(key, value.toLong())
                else -> params.putString(key, value.toString())
            }
        }
        firebase.logEvent(event.name, params)
    }
}
