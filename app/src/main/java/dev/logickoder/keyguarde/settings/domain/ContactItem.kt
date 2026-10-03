package dev.logickoder.keyguarde.settings.domain

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class ContactItem(
    @param:StringRes val title: Int,
    @param:StringRes val description: Int,
    val icon: ImageVector,
    val action: () -> Unit
)