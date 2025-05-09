package dev.logickoder.keyguarde.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import dev.logickoder.keyguarde.BuildConfig

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = when (BuildConfig.DEBUG) {
                    true -> "ca-app-pub-3940256099942544/9214589741"
                    else -> "ca-app-pub-9535789616988908/5814771713"
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}