package dev.logickoder.keyguarde.onboarding.pages

import android.graphics.Color
import androidx.compose.foundation.layout.*
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
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.settings.components.AppList
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

            AppList(
                modifier = Modifier.weight(1f),
                apps = apps,
                isSelected = { selected.contains(it) },
                addItem = { selected.add(it.packageName) },
                removeItem = { selected.remove(it) }
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
