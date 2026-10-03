package dev.logickoder.keyguarde.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isFilterActive: Boolean,
    onFilter: () -> Unit,
    onSelect: () -> Unit,
    onResetCounter: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSearchActive by rememberSaveable { mutableStateOf(searchQuery.isNotEmpty()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    TopAppBar(
        modifier = modifier,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
                content = {
                    AnimatedVisibility(
                        visible = !isSearchActive,
                        enter = fadeIn(),
                        exit = fadeOut(animationSpec = tween(durationMillis = 150)),
                    ) {
                        TitleContent()
                    }
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut(),
                        content = {
                            SearchInput(
                                modifier = Modifier.focusRequester(focusRequester),
                                query = searchQuery,
                                onQueryChange = onSearchQueryChange,
                                onBack = {
                                    isSearchActive = false
                                    onSearchQueryChange("")
                                },
                                onClear = { onSearchQueryChange("") },
                            )
                        }
                    )
                }
            )
        },
        actions = {
            AnimatedVisibility(
                visible = !isSearchActive,
                content = {
                    IconButton(
                        onClick = { isSearchActive = true },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    )
                }
            )
            FilterButton(isActive = isFilterActive, onClick = onFilter)
            OverflowMenu(onSelect = onSelect, onResetCounter = onResetCounter, onClearAll = onClearAll)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun FilterButton(isActive: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        content = {
            BadgedBox(
                badge = {
                    if (isActive) {
                        // Neutral, not the default error red: an active filter isn't a problem.
                        Badge(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface,
                            // The icon's description already says a filter is on.
                            content = { Text("1", modifier = Modifier.clearAndSetSemantics {}) }
                        )
                    }
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = when (isActive) {
                            true -> stringResource(R.string.filter_active)
                            else -> stringResource(R.string.filter)
                        }
                    )
                }
            )
        }
    )
}

@Composable
private fun OverflowMenu(
    onSelect: () -> Unit,
    onResetCounter: () -> Unit,
    onClearAll: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        content = {
            IconButton(
                onClick = { expanded = true },
                content = {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more_options)
                    )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                content = {
                    listOf(
                        R.string.select_matches to onSelect,
                        R.string.reset_counter to onResetCounter,
                        R.string.clear_all_matches to onClearAll,
                    ).forEach { (label, onClick) ->
                        DropdownMenuItem(
                            text = { Text(stringResource(label)) },
                            onClick = {
                                expanded = false
                                onClick()
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun TitleContent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            // Decorative: the app name right after says the same thing.
            Icon(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(text = stringResource(R.string.app_name))
        }
    )
}

@Composable
private fun SearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            IconButton(
                onClick = onBack,
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            )
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(end = 8.dp),
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        content = {
                            Placeholder(visible = query.isEmpty())
                            innerTextField()
                        }
                    )
                }
            )
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                content = {
                    IconButton(
                        onClick = onClear,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun Placeholder(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = {
            Text(
                text = stringResource(R.string.search_matches),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Preview
@Composable
private fun HomeTopAppBarPreview() = AppTheme {
    var query by remember { mutableStateOf("") }
    HomeTopAppBar(
        searchQuery = query,
        onSearchQueryChange = { query = it },
        isFilterActive = true,
        onFilter = {},
        onSelect = {},
        onResetCounter = {},
        onClearAll = {},
    )
}
