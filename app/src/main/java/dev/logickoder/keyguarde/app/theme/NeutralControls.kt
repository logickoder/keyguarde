package dev.logickoder.keyguarde.app.theme

import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable

// Controls are dark neutral: teal is reserved for matched keywords.

@Composable
fun neutralCheckboxColors(): CheckboxColors = CheckboxDefaults.colors(
    checkedColor = MaterialTheme.colorScheme.onSurface,
    checkmarkColor = MaterialTheme.colorScheme.surface,
    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
fun neutralSwitchColors(): SwitchColors = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.surface,
    checkedTrackColor = MaterialTheme.colorScheme.onSurface,
    checkedBorderColor = MaterialTheme.colorScheme.onSurface,
    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    uncheckedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
fun neutralTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.onSurface,
)
