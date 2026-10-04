package dev.logickoder.keyguarde.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet

// Below this many apps the list fits at a glance, so it keeps a stable order.
private const val GroupSelectedAbove = 8

/**
 * Picks which apps' matches to show. Ticking nothing means every app, so the list is never
 * empty without a reason.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFilterSheet(
    apps: ImmutableList<WatchedApp>,
    counts: ImmutableMap<String, Int>,
    selected: ImmutableSet<String>,
    onToggle: (String) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    // Grouped by what was ticked when the sheet opened, so rows never jump under the thumb.
    val ticked = remember { selected.toSet() }
    val (pinned, others) = remember(apps) {
        when (apps.size > GroupSelectedAbove && ticked.isNotEmpty()) {
            true -> apps.partition { it.packageName in ticked }
            else -> emptyList<WatchedApp>() to apps
        }
    }
    val matchCount = when (selected.isEmpty()) {
        true -> counts.values.sum()
        else -> selected.sumOf { counts[it] ?: 0 }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                content = {
                    FilterHeader(selectedCount = selected.size, onClear = onClear)

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        content = {
                            if (pinned.isNotEmpty()) {
                                GroupLabel(stringResource(R.string.filter_group_selected))
                                pinned.forEach { app ->
                                    FilterOption(app, counts[app.packageName] ?: 0, app.packageName in selected, onToggle)
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.s),
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                )
                            }
                            others.forEach { app ->
                                FilterOption(app, counts[app.packageName] ?: 0, app.packageName in selected, onToggle)
                            }
                        }
                    )

                    Button(
                        onClick = onApply,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .padding(start = Spacing.xl, end = Spacing.xl, top = Spacing.l, bottom = Spacing.l),
                        // Dark neutral like the reference sheet: teal is kept for matched keywords.
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface,
                        ),
                        content = {
                            Text(
                                text = when (selected.isEmpty()) {
                                    true -> stringResource(R.string.filter_show_all)
                                    else -> pluralStringResource(R.plurals.filter_show_matches, matchCount, matchCount)
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
private fun FilterHeader(selectedCount: Int, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.xl, end = Spacing.m, bottom = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                content = {
                    Text(
                        text = stringResource(R.string.filter_sheet_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = when (selectedCount) {
                            0 -> stringResource(R.string.all_apps)
                            else -> stringResource(R.string.selected_count, selectedCount)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )
            if (selectedCount > 0) {
                TextButton(
                    onClick = onClear,
                    content = {
                        Text(
                            text = stringResource(R.string.clear),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                )
            }
        }
    )
}

@Composable
private fun GroupLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.s),
    )
}

@Composable
private fun FilterOption(
    app: WatchedApp,
    count: Int,
    checked: Boolean,
    onToggle: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .toggleable(value = checked, onValueChange = { onToggle(app.packageName) }, role = Role.Checkbox)
            .padding(horizontal = Spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.l),
        content = {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                // Same neutral checks as selection mode on the list.
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.onSurface,
                    checkmarkColor = MaterialTheme.colorScheme.surface,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            AppIcon(app = app, contentDescription = null)
            Text(
                text = app.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            val countLabel = pluralStringResource(R.plurals.filter_app_matches, count, count)
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // A bare number after the app name is ambiguous when read aloud.
                modifier = Modifier.clearAndSetSemantics { contentDescription = countLabel },
            )
        }
    )
}
