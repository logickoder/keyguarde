package dev.logickoder.keyguarde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import dev.logickoder.keyguarde.app.data.AppStore
import dev.logickoder.keyguarde.app.domain.resetMatchCount
import dev.logickoder.keyguarde.app.navigation.AppNavigation
import dev.logickoder.keyguarde.app.navigation.AppRoute
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val settings by lazy {
        SettingsRepository.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isOnboardingComplete = runBlocking {
            AppStore.getInstance(this@MainActivity).get(AppStore.onboardingComplete).first()
        }

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        setContent {
            AppTheme {
                AppNavigation(
                    start = when (isOnboardingComplete) {
                        true -> AppRoute.Main
                        else -> AppRoute.Onboarding
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            if (settings.resetMatchCountOnAppOpen.first()) {
                resetMatchCount(this@MainActivity)
            }
        }
    }
}