package dev.logickoder.keyguarde.onboarding.pages

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.onboarding.domain.TelegramPackageName
import dev.logickoder.keyguarde.onboarding.domain.WhatsappPackageName
import dev.logickoder.keyguarde.onboarding.domain.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppSelectionPage(
    selected: SnapshotStateList<String>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val apps = remember { mutableStateListOf<AppInfo>() }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Default) {
            val installedApps = context.getInstalledApps()
            withContext(Dispatchers.Main) {
                apps.addAll(installedApps)
                // automatically select whatsapp and telegram if available
                selected.clear()
                apps.find { it.packageName == WhatsappPackageName }?.let {
                    selected.add(it.packageName)
                }
                apps.find { it.packageName == TelegramPackageName }?.let {
                    selected.add(it.packageName)
                }
            }
        }
    }

    Column(
        modifier = modifier.padding(24.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Text(
                text = "Select Apps to Monitor",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose which apps Keyguarde should watch for your keywords",
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
                text = "You can change these selections later in Settings",
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