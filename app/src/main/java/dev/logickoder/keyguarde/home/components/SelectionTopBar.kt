package dev.logickoder.keyguarde.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme

/**
 * Replaces the app bar while matches are being selected. Icons only, each with a description.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onExit: () -> Unit,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onExit,
                content = {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.exit_selection))
                }
            )
        },
        title = { Text(stringResource(R.string.selected_count, selectedCount)) },
        actions = {
            IconButton(
                onClick = onSelectAll,
                content = {
                    Icon(Icons.Default.SelectAll, contentDescription = stringResource(R.string.select_all))
                }
            )
            IconButton(
                onClick = onDelete,
                enabled = selectedCount > 0,
                content = {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    )
}

@Preview
@Composable
private fun SelectionTopBarPreview() = AppTheme {
    SelectionTopBar(selectedCount = 3, onExit = {}, onSelectAll = {}, onDelete = {})
}
