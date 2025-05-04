package dev.logickoder.keyguarde.onboarding.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingState(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onDone: () -> Unit,
) {
    private val repository = AppRepository.getInstance(context)

    val selectedApps = mutableStateListOf<String>()
    val keywords = mutableStateListOf<String>()
    val apps = mutableStateListOf<AppInfo>()

    var isSaving by mutableStateOf(false)
        private set

    init {
        scope.launch(Dispatchers.Default) {
            apps.addAll(repository.getInstalledApps())
            // automatically select whatsapp and telegram if available
            apps.find { it.packageName == WHATSAPP_PACKAGE_NAME }?.let {
                selectedApps.add(it.packageName)
            }
            apps.find { it.packageName == TELEGRAM_PACKAGE_NAME }?.let {
                selectedApps.add(it.packageName)
            }
        }
    }

    fun save() {
        if (isSaving) {
            return
        }

        scope.launch {
            repository.onboardingCompleted()

            repository.addKeyword(*keywords.map { Keyword(word = it) }.toTypedArray())

            val watchedApps = apps.filter { selectedApps.contains(it.packageName) }.map { app ->
                WatchedApp(
                    packageName = app.packageName,
                    name = app.name,
                    icon = saveIconToFile(
                        app.icon,
                        app.packageName,
                        context
                    )
                )
            }
            repository.addWatchedApp(*watchedApps.toTypedArray())

            onDone()
        }.invokeOnCompletion {
            isSaving = false
        }
    }
}

@Composable
fun rememberOnboardingState(onDone: () -> Unit): OnboardingState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember {
        OnboardingState(
            context = context,
            scope = scope,
            onDone = onDone,
        )
    }
}