package dev.logickoder.keyguarde.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun AppSelectionItem(
    appName: String,
    icon: Any,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    AsyncImage(
                        model = icon,
                        contentDescription = appName,
                        modifier = Modifier.size(32.dp),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = appName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelectionChanged,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            )
        }
    )
}