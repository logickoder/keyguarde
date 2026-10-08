package dev.logickoder.keyguarde.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.AppPicker
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun AppsPage(
    apps: ImmutableList<AppInfo>,
    selected: ImmutableSet<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppPicker(
        apps = apps,
        selected = selected,
        onToggle = onToggle,
        modifier = modifier,
        header = { Header() },
    )
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.padding(start = Spacing.xl, end = Spacing.xl, bottom = Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            Text(
                text = stringResource(R.string.apps_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.apps_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    )
}
