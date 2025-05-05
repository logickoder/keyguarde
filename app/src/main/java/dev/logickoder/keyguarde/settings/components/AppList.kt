package dev.logickoder.keyguarde.settings.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppList(
    apps: ImmutableList<AppInfo>,
    modifier: Modifier = Modifier,
    isSelected: (String) -> Boolean,
    addItem: (AppInfo) -> Unit,
    removeItem: (String) -> Unit,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = apps.isEmpty(),
        content = { isEmpty ->
            when (isEmpty) {
                true -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                strokeWidth = 4.dp
                            )
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(16.dp),
                        content = {
                            items(
                                count = apps.size,
                                key = { apps[it].packageName },
                                itemContent = { index ->
                                    val app = apps[index]
                                    AppSelectionItem(
                                        modifier = Modifier.animateItem(),
                                        appName = app.name,
                                        icon = app.icon,
                                        isSelected = isSelected(app.packageName),
                                        onSelectionChanged = { isSelected ->
                                            when (isSelected) {
                                                true -> addItem(app)
                                                else -> removeItem(app.packageName)
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    )
}