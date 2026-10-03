package dev.logickoder.keyguarde.settings.domain

import androidx.annotation.StringRes

data class FaqItem(
    @param:StringRes val question: Int,
    @param:StringRes val answer: Int,
)