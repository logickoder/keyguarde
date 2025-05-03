package dev.logickoder.keyguarde.onboarding.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage

@Composable
fun OnboardingBottomBar(
    currentPage: OnboardingPage,
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    AnimatedContent(
                        targetState = currentPage != OnboardingPage.Welcome,
                        content = { showPrevious ->
                            when (showPrevious) {
                                true -> TextButton(
                                    onClick = onPrevious,
                                    content = {
                                        Text(
                                            text = stringResource(R.string.previous),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                )

                                else -> Spacer(modifier = Modifier.width(64.dp))
                            }
                        }
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            OnboardingPage.entries.forEachIndexed { index, page ->
                                AnimatedVisibility(
                                    visible = page != OnboardingPage.ReadyScreen,
                                    content = {
                                        Box(
                                            modifier = Modifier
                                                .padding(
                                                    end = when (index < OnboardingPage.entries.size - 2) {
                                                        true -> 8.dp
                                                        else -> 0.dp
                                                    }
                                                )
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (currentPage.ordinal >= index) {
                                                        true -> MaterialTheme.colorScheme.primary
                                                        else -> MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.3f
                                                        )
                                                    }
                                                )
                                        )
                                    }
                                )
                            }
                        }
                    )

                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        content = {
                            Text(
                                text = stringResource(R.string.next),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            )
        }
    )
}