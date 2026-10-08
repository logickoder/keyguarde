package dev.logickoder.keyguarde.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.logickoder.keyguarde.R

/**
 * Confirms clearing every match. Both actions are neutral; the snackbar's Undo is the safety net.
 */
@Composable
fun ClearAllDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.clear_all_title)) },
        text = { Text(stringResource(R.string.clear_all_body)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = {
                    Text(
                        text = stringResource(R.string.clear_all_confirm),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                content = {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )
        },
    )
}
