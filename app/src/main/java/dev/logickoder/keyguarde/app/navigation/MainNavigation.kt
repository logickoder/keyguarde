package dev.logickoder.keyguarde.app.navigation

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.BannerAd
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.home.HomeScreen
import dev.logickoder.keyguarde.settings.KeywordsScreen

enum class MainTab(@param:StringRes val label: Int) {
    Matches(R.string.tab_matches),
    Keywords(R.string.tab_keywords),
    Settings(R.string.settings),
}

// Filled when selected, outlined otherwise: the shape change carries the state, not the pill alone.
@Composable
private fun MainTab.icon(selected: Boolean): Painter = when (this) {
    MainTab.Matches -> painterResource(if (selected) R.drawable.ic_tab_matches_filled else R.drawable.ic_tab_matches)
    MainTab.Keywords -> painterResource(if (selected) R.drawable.ic_tab_keywords_filled else R.drawable.ic_tab_keywords)
    MainTab.Settings -> rememberVectorPainter(if (selected) Icons.Filled.Settings else Icons.Outlined.Settings)
}

/**
 * The signed-in shell: Matches, Keywords and Settings tabs. Each tab keeps its own state while
 * the others are showing.
 */
@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    var tab by rememberSaveable { mutableStateOf(MainTab.Matches) }
    val tabStates = rememberSaveableStateHolder()

    // Back from another tab returns to Matches before it leaves the app.
    BackHandler(enabled = tab != MainTab.Matches) {
        tab = MainTab.Matches
    }

    Column(
        modifier = modifier,
        content = {
            Box(
                // The bar below owns the navigation-bar inset, so screens shouldn't pad for it.
                modifier = Modifier
                    .weight(1f)
                    .consumeWindowInsets(NavigationBarDefaults.windowInsets),
                content = {
                    Crossfade(
                        targetState = tab,
                        label = "MainTab",
                        content = { current ->
                            tabStates.SaveableStateProvider(current.name) {
                                when (current) {
                                    MainTab.Matches -> HomeScreen(onOpenKeywords = { tab = MainTab.Keywords })
                                    MainTab.Keywords -> KeywordsScreen()
                                    MainTab.Settings -> SettingsNavigation()
                                }
                            }
                        }
                    )
                }
            )

            // Both the list and the bar are surface-coloured, so a hairline marks where the list ends.
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            BannerAd(modifier = Modifier.padding(top = Spacing.s))

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                content = {
                    MainTab.entries.forEach { item ->
                        val selected = item == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { tab = item },
                            icon = { Icon(painter = item.icon(selected), contentDescription = null) },
                            label = { Text(stringResource(item.label)) },
                            alwaysShowLabel = true,
                            // Neutral on purpose: teal is reserved for matched keywords.
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            )
        }
    )
}
