package dev.logickoder.keyguarde.settings.domain

import androidx.annotation.StringRes
import dev.logickoder.keyguarde.R

enum class ThemeMode(@param:StringRes val label: Int) {
    System(R.string.theme_system),
    Light(R.string.theme_light),
    Dark(R.string.theme_dark),
}
