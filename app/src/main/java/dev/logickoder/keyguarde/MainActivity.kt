package dev.logickoder.keyguarde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.components.ToastManager
import dev.logickoder.keyguarde.app.components.globalToastManager
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.domain.NotificationHelper
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
            AppRepository.getInstance(this@MainActivity).onboardingComplete.first()
        }

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        NotificationHelper.requestListenerServiceRebind(this)

        val toastManager = ToastManager()
        globalToastManager = toastManager

        setContent {
            AppTheme {
                CompositionLocalProvider(
                    LocalToastManager provides toastManager,
                    content = {
                        AppNavigation(
                            start = when (isOnboardingComplete) {
                                true -> AppRoute.Main
                                else -> AppRoute.Onboarding
                            }
                        )
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

    override fun onDestroy() {
        globalToastManager = null
        super.onDestroy()
    }
}