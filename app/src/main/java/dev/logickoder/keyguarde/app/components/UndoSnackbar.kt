package dev.logickoder.keyguarde.app.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** A snackbar host whose action stays neutral; the default action colour is teal (inversePrimary). */
@Composable
fun NeutralSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { data ->
            Snackbar(snackbarData = data, actionColor = MaterialTheme.colorScheme.inverseOnSurface)
        },
    )
}

/**
 * Shows [message] with an Undo action. A newer call replaces the current snackbar instead of
 * queueing behind it.
 *
 * @return true when the user tapped Undo.
 */
suspend fun SnackbarHostState.showUndo(
    message: String,
    undoLabel: String,
    duration: SnackbarDuration,
): Boolean {
    currentSnackbarData?.dismiss()
    return showSnackbar(message = message, actionLabel = undoLabel, duration = duration) ==
        SnackbarResult.ActionPerformed
}
