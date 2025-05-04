package dev.logickoder.keyguarde.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.AppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FilterChips(
    selected: WatchedApp?,
    apps: ImmutableList<WatchedApp>,
    onSelected: (WatchedApp?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            FilterChip(
                selected = selected == null,
                onClick = { onSelected(null) },
                label = { Text("All") },
                leadingIcon = if (selected == null) {
                    {
                        ChipSelected()
                    }
                } else null
            )

            apps.forEach { app ->
                FilterChip(
                    selected = selected == app,
                    onClick = { onSelected(app) },
                    label = { Text(app.name) },
                    leadingIcon = if (selected == app) {
                        {
                            ChipSelected()
                        }
                    } else null
                )
            }
        }
    )
}

@Composable
private fun ChipSelected() {
    Icon(
        imageVector = Icons.Rounded.Check,
        contentDescription = null,
        modifier = Modifier.Companion.size(18.dp)
    )
}

@Preview
@Composable
private fun FilterChipsPreview() = AppTheme {
    FilterChips(
        selected = null,
        apps = persistentListOf(
            WatchedApp("com.example.app1", "App 1", icon = ""),
            WatchedApp("com.example.app2", "App 2", icon = ""),
            WatchedApp("com.example.app3", "App 3", icon = ""),
        ),
        onSelected = {}
    )
}