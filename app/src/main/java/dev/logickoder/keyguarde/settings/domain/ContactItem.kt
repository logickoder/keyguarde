package dev.logickoder.keyguarde.settings.domain

import androidx.compose.ui.graphics.vector.ImageVector

data class ContactItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val action: () -> Unit
)