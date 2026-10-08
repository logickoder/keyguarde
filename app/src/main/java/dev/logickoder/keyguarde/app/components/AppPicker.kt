package dev.logickoder.keyguarde.app.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.onboarding.domain.splitMessagingApps
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

private val Monochrome = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

/**
 * Picks which apps Keyguarde watches. Chat apps first, everything else folded away behind search,
 * so the common case is one glance and the rare case is still reachable.
 *
 * @param header scrolls with the list, above the apps.
 * @param footer extra rows after the apps, such as watched apps that are no longer installed.
 */
@Composable
fun AppPicker(
    apps: ImmutableList<AppInfo>,
    selected: ImmutableSet<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
) {
    val (suggested, others) = remember(apps) { splitMessagingApps(apps) { it.packageName } }
    // No chat apps to suggest means the full list is the only place to look, so it starts open.
    var showAll by rememberSaveable(suggested.isEmpty()) { mutableStateOf(suggested.isEmpty()) }
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(others, query) {
        others.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        content = {
            item(key = "header") { header() }

            when {
                apps.isEmpty() -> item(key = "loading") { Loading() }

                else -> {
                    item(key = "suggested-label") {
                        AppPickerSectionLabel(stringResource(R.string.apps_suggested))
                    }
                    when (suggested.isEmpty()) {
                        true -> item(key = "no-suggested") {
                            Text(
                                text = stringResource(R.string.apps_no_suggested),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.s),
                            )
                        }

                        else -> appRows(suggested, selected, onToggle)
                    }

                    item(key = "all-toggle") {
                        AllAppsToggle(
                            count = others.size,
                            expanded = showAll,
                            onToggle = { showAll = !showAll },
                        )
                    }
                    if (showAll) {
                        item(key = "search") { SearchField(query = query, onQueryChange = { query = it }) }
                        appRows(filtered, selected, onToggle)
                    }
                    footer()
                }
            }
        }
    )
}

private fun LazyListScope.appRows(apps: List<AppInfo>, selected: Set<String>, onToggle: (String) -> Unit) {
    items(apps, key = { it.packageName }) { app ->
        AppPickerRow(
            name = app.name,
            checked = app.packageName in selected,
            onToggle = { onToggle(app.packageName) },
            modifier = Modifier.animateItem(),
            icon = {
                AsyncImage(
                    model = app.icon,
                    contentDescription = null,
                    colorFilter = Monochrome,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                )
            },
        )
    }
}

@Composable
fun AppPickerSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .padding(horizontal = Spacing.xl, vertical = Spacing.s)
            .semantics { heading() },
    )
}

@Composable
private fun Loading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.apps_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    )
}

/** Same row as the Matches filter: checkbox left, grey icon, name. */
@Composable
fun AppPickerRow(
    name: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .toggleable(value = checked, onValueChange = { onToggle() }, role = Role.Checkbox)
            .padding(horizontal = Spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.l),
        content = {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.onSurface,
                    checkmarkColor = MaterialTheme.colorScheme.surface,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            icon()
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    )
}

@Composable
private fun AllAppsToggle(count: Int, expanded: Boolean, onToggle: () -> Unit) {
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "ExpandArrow")
    val state = stringResource(if (expanded) R.string.expanded else R.string.collapsed)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.m)
            .clickable(onClick = onToggle, role = Role.Button)
            .semantics { stateDescription = state }
            .heightIn(min = 56.dp)
            .padding(horizontal = Spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = pluralStringResource(R.plurals.apps_all, count, count),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.rotate(rotation),
            )
        }
    )
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.s),
        placeholder = { Text(stringResource(R.string.apps_search)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(Radius.m),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}
