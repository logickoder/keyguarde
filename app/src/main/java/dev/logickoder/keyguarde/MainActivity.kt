package dev.logickoder.keyguarde

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.components.ToastContainer
import dev.logickoder.keyguarde.app.components.ToastManager
import dev.logickoder.keyguarde.app.components.globalToastManager
import dev.logickoder.keyguarde.app.domain.AppScope
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.navigation.AppNavigation
import dev.logickoder.keyguarde.app.navigation.AppRoute
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.domain.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val settings by lazy {
        AppContainer.from(this@MainActivity).settingsRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isOnboardingComplete = runBlocking {
            AppContainer.from(this@MainActivity).appRepository.onboardingComplete.first()
        }
        // Read before the first frame, so a dark-mode user doesn't see one light frame.
        val savedThemeMode = runBlocking { settings.themeMode.first() }

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        NotificationHelper.requestListenerServiceRebind(this)

        val toastManager = ToastManager()
        globalToastManager = toastManager

        setContent {
            val themeMode by settings.themeMode.collectAsStateWithLifecycle(savedThemeMode)
            val isDark = when (themeMode) {
                ThemeMode.System -> isSystemInDarkTheme()
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }
            // Status and navigation bar icons follow the app's theme, not the system's.
            DisposableEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                        detectDarkMode = { isDark },
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = LightNavigationBarScrim,
                        darkScrim = DarkNavigationBarScrim,
                        detectDarkMode = { isDark },
                    ),
                )
                onDispose {}
            }

            AppTheme(useDarkTheme = isDark) {
                CompositionLocalProvider(
                    LocalToastManager provides toastManager,
                    content = {
                        Box(
                            content = {
                                AppNavigation(
                                    start = when (isOnboardingComplete) {
                                        true -> AppRoute.Main
                                        else -> AppRoute.Onboarding
                                    }
                                )
                                ToastContainer()
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
                AppContainer.from(this@MainActivity).resetMatchCount()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // A rotation isn't leaving; marking it would wipe the "new since last visit" divider.
        if (!isChangingConfigurations) {
            val repository = AppContainer.from(this).appRepository
            AppScope.launch { repository.markVisited() }
        }
    }

    override fun onDestroy() {
        globalToastManager = null
        super.onDestroy()
    }
}

// The scrims enableEdgeToEdge uses by default for three-button navigation.
private val LightNavigationBarScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val DarkNavigationBarScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
