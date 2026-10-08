package dev.logickoder.keyguarde.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.settings.domain.ThemeMode

/**
 * Picks light, dark or the system's choice. A pick applies at once and closes the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSheet(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = Spacing.l)
                    .selectableGroup(),
                content = {
                    Text(
                        text = stringResource(R.string.theme),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(horizontal = Spacing.xl, vertical = Spacing.s)
                            .semantics { heading() },
                    )
                    ThemeMode.entries.forEach { mode ->
                        ThemeOption(
                            mode = mode,
                            selected = mode == selected,
                            onClick = { onSelect(mode) },
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun ThemeOption(
    mode: ThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = Spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.l),
        content = {
            RadioButton(
                selected = selected,
                onClick = null,
                // Neutral like the filter checkboxes: teal is reserved for matched keywords.
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.onSurface,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            Text(
                text = stringResource(mode.label),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    )
}
