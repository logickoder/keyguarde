package dev.logickoder.keyguarde.onboarding.domain

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)