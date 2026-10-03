package dev.logickoder.keyguarde.app.navigation

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
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

@Composable
private fun MainTab.icon(): Painter = when (this) {
    MainTab.Matches -> painterResource(R.drawable.ic_tab_matches)
    MainTab.Keywords -> painterResource(R.drawable.ic_tab_keywords)
    MainTab.Settings -> rememberVectorPainter(Icons.Rounded.Settings)
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
                                    MainTab.Matches -> HomeScreen()
                                    MainTab.Keywords -> KeywordsScreen()
                                    MainTab.Settings -> SettingsNavigation()
                                }
                            }
                        }
                    )
                }
            )

            BannerAd(modifier = Modifier.padding(top = Spacing.s))

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                content = {
                    MainTab.entries.forEach { item ->
                        NavigationBarItem(
                            selected = item == tab,
                            onClick = { tab = item },
                            icon = { Icon(painter = item.icon(), contentDescription = null) },
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
