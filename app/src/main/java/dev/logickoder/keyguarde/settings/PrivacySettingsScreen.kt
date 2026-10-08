package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.settings.components.LinkButton
import dev.logickoder.keyguarde.app.domain.openUrl
import dev.logickoder.keyguarde.settings.components.SettingsTopBar

private const val PRIVACY_POLICY_URL = "https://logickoder.dev/keyguarde/#/privacy-policy"

// Each service that sends data off the phone, named, with what it sends.
private val Services = listOf(
    R.string.privacy_analytics_title to R.string.privacy_analytics_body,
    R.string.privacy_crashes_title to R.string.privacy_crashes_body,
    R.string.privacy_performance_title to R.string.privacy_performance_body,
    R.string.privacy_ads_title to R.string.privacy_ads_body,
)

@Composable
fun PrivacySettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    PrivacyContent(
        onBack = onBack,
        onOpenPolicy = { context.openUrl(PRIVACY_POLICY_URL) },
        modifier = modifier,
    )
}

@Composable
private fun PrivacyContent(
    onBack: () -> Unit,
    onOpenPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.privacy), onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.l, vertical = Spacing.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl),
                content = {
                    Block(
                        title = stringResource(R.string.privacy_stays_title),
                        body = stringResource(R.string.privacy_stays_body),
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Block(
                        title = stringResource(R.string.privacy_leaves_title),
                        body = stringResource(R.string.privacy_leaves_body),
                    )
                    Services.forEach { (title, body) ->
                        Fact(title = stringResource(title), body = stringResource(body))
                    }
                    LinkButton(
                        text = stringResource(R.string.privacy_policy),
                        onClick = onOpenPolicy,
                    )
                }
            )
        }
    )
}

@Composable
private fun Block(title: String, body: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    )
}

@Composable
private fun Fact(title: String, body: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun PrivacyContentPreview() = AppTheme {
    PrivacyContent(onBack = {}, onOpenPolicy = {})
}
