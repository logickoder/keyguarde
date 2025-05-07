package dev.logickoder.keyguarde

import android.app.Application
import android.content.BroadcastReceiver
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App : Application() {
    private var resetCountReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }

        resetCountReceiver = NotificationHelper.registerResetCountReceiver(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        resetCountReceiver?.let {
            unregisterReceiver(it)
            resetCountReceiver = null
        }
    }
}