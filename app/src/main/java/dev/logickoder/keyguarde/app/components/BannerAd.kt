package dev.logickoder.keyguarde.app.components

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.app.AppContainer

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current
    val consent = AppContainer.from(LocalContext.current).adsConsent
    val canRequestAds by consent.canRequestAds.collectAsStateWithLifecycle()
    // Asked here, on the main screen, so the consent message never covers onboarding.
    LaunchedEffect(activity) { activity?.let(consent::gather) }
    // No ad, and no request, until Google's consent check allows it.
    if (canRequestAds) {
        AdBanner(modifier = modifier)
    }
}

@Composable
private fun AdBanner(modifier: Modifier = Modifier) {
    var isAdLoaded by remember { mutableStateOf(false) }

    // The view stays in the tree from the start, since it's the view that requests the ad. It
    // takes no space until an ad arrives, so a failed or empty load leaves no gap.
    AndroidView(
        modifier = when (isAdLoaded) {
            true -> modifier.fillMaxWidth()
            else -> Modifier.fillMaxWidth().height(0.dp)
        },
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = when (BuildConfig.DEBUG) {
                    true -> "ca-app-pub-3940256099942544/9214589741"
                    else -> "ca-app-pub-9535789616988908/5814771713"
                }
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        isAdLoaded = true
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isAdLoaded = false
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}