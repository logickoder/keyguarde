package dev.logickoder.keyguarde.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dev.logickoder.keyguarde.app.domain.AppScope
import io.github.aakira.napier.Napier
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Asks for ad consent with Google's User Messaging Platform before any ad is requested. Where the
 * law needs consent (the EEA, the UK and similar), Google shows the message set up in AdMob, under
 * Privacy & messaging; elsewhere it shows nothing and ads may load.
 */
class AdsConsent(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context.applicationContext)

    private val _canRequestAds = MutableStateFlow(hasConsentAnswer())

    /** True once ads may be requested: consent given, or not needed where the user is. */
    val canRequestAds: StateFlow<Boolean> = _canRequestAds.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(isPrivacyOptionsRequired())

    /** Whether Settings must offer a way to change the answer later; the law requires it there. */
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    private val gathering = AtomicBoolean(false)
    private val adsStarted = AtomicBoolean(false)

    init {
        // An answer from an earlier run is still valid; ads can start before this run's check.
        if (hasConsentAnswer()) startAds(context)
    }

    /**
     * Refreshes the consent status and shows Google's consent form if this user still needs to
     * answer. Runs once per launch; later calls do nothing.
     */
    fun gather(activity: Activity) {
        if (!gathering.compareAndSet(false, true)) return
        consentInformation.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                    if (error != null) Napier.w { "Consent form failed: ${error.errorCode} ${error.message}" }
                    refresh(activity)
                }
            },
            { error ->
                // Offline or misconfigured: an earlier answer may still allow ads.
                Napier.w { "Consent status update failed: ${error.errorCode} ${error.message}" }
                refresh(activity)
            },
        )
    }

    /** Opens Google's form to change the ad consent answer, from Settings. */
    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            if (error != null) Napier.w { "Privacy options form failed: ${error.errorCode} ${error.message}" }
            refresh(activity)
        }
    }

    private fun refresh(context: Context) {
        val allowed = hasConsentAnswer()
        _canRequestAds.update { allowed }
        _privacyOptionsRequired.update { isPrivacyOptionsRequired() }
        if (allowed) startAds(context)
    }

    // Stricter than canRequestAds(), which also allows ads while the status is unknown: when the
    // check fails (offline, or no consent message published in AdMob), nothing loads.
    private fun hasConsentAnswer() = consentInformation.consentStatus in setOf(
        ConsentInformation.ConsentStatus.OBTAINED,
        ConsentInformation.ConsentStatus.NOT_REQUIRED,
    )

    private fun isPrivacyOptionsRequired() =
        consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    // Initialising the ads SDK makes network calls, so it waits for consent too.
    private fun startAds(context: Context) {
        if (!adsStarted.compareAndSet(false, true)) return
        val appContext = context.applicationContext
        AppScope.launch(Dispatchers.IO) { MobileAds.initialize(appContext) {} }
    }
}
