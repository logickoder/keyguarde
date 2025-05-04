package dev.logickoder.keyguarde.onboarding.pages

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import coil3.compose.AsyncImage
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.onboarding.domain.StoreWatchedAppsInDatabaseUsecase.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.onboarding.domain.StoreWatchedAppsInDatabaseUsecase.Companion.WHATSAPP_PACKAGE_NAME
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AppSelectionPage(
    apps: ImmutableList<AppInfo>,
    selected: SnapshotStateList<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Text(
                text = stringResource(R.string.select_apps_to_monitor),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.select_apps_to_monitor_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                modifier = Modifier.weight(1f),
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
                                                isSelected = selected.contains(app.packageName),
                                                onSelectionChanged = { isSelected ->
                                                    if (isSelected) {
                                                        selected.add(app.packageName)
                                                    } else {
                                                        selected.remove(app.packageName)
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.you_can_change_selections_later),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    )
}

@Composable
private fun AppSelectionItem(
    appName: String,
    icon: Drawable,
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

@Preview(showBackground = true)
@Composable
private fun AppSelectionPagePreview() = AppTheme {
    AppSelectionPage(
        apps = persistentListOf(
            AppInfo(
                name = "WhatsApp",
                packageName = WHATSAPP_PACKAGE_NAME,
                icon = Color.TRANSPARENT.toDrawable()
            ),
            AppInfo(
                name = "Telegram",
                packageName = TELEGRAM_PACKAGE_NAME,
                icon = Color.TRANSPARENT.toDrawable()
            )
        ),
        selected = remember {
            mutableStateListOf(
                WHATSAPP_PACKAGE_NAME,
                TELEGRAM_PACKAGE_NAME
            )
        }
    )
}
