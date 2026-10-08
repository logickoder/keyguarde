package dev.logickoder.keyguarde.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Every slot is set so nothing falls back to the stock Material palette. Only `primary` (and its
// inverse) is teal; containers, secondary and tertiary are greys so colour stays reserved for
// matched keywords. Text pairs are at least 4.5:1, outlines at least 3:1, in both modes.
private val LightColors = lightColorScheme(
    primary = Teal700,
    onPrimary = Grey0,
    primaryContainer = Grey200,
    onPrimaryContainer = Grey900,
    inversePrimary = Teal400,
    secondary = Grey600,
    onSecondary = Grey0,
    secondaryContainer = Grey200,
    onSecondaryContainer = Grey900,
    tertiary = Grey700,
    onTertiary = Grey0,
    tertiaryContainer = Grey200,
    onTertiaryContainer = Grey900,
    background = Grey0,
    onBackground = Grey900,
    surface = Grey0,
    onSurface = Grey900,
    surfaceVariant = Grey100,
    onSurfaceVariant = Grey600,
    surfaceTint = Color.Transparent,
    inverseSurface = Grey800,
    inverseOnSurface = Grey100,
    error = Red700,
    onError = Grey0,
    errorContainer = Red200,
    onErrorContainer = Red800,
    outline = Grey450,
    outlineVariant = Grey200,
    scrim = Color.Black,
    surfaceBright = Grey0,
    surfaceDim = Grey150,
    surfaceContainerLowest = Grey0,
    surfaceContainerLow = Grey50,
    surfaceContainer = Grey100,
    surfaceContainerHigh = Grey150,
    surfaceContainerHighest = Grey200,
)

private val DarkColors = darkColorScheme(
    primary = Teal400,
    onPrimary = Teal950,
    primaryContainer = Grey800,
    onPrimaryContainer = Grey100,
    inversePrimary = Teal700,
    secondary = Grey400,
    onSecondary = Grey900,
    secondaryContainer = Grey700,
    onSecondaryContainer = Grey100,
    tertiary = Grey300,
    onTertiary = Grey900,
    tertiaryContainer = Grey700,
    onTertiaryContainer = Grey100,
    background = Grey925,
    onBackground = Grey100,
    surface = Grey925,
    onSurface = Grey100,
    surfaceVariant = Grey800,
    onSurfaceVariant = Grey400,
    surfaceTint = Color.Transparent,
    inverseSurface = Grey100,
    inverseOnSurface = Grey800,
    error = Red300,
    onError = Grey900,
    errorContainer = Red950,
    onErrorContainer = Red200,
    outline = Grey500,
    outlineVariant = Grey700,
    scrim = Color.Black,
    surfaceBright = Grey750,
    surfaceDim = Grey925,
    surfaceContainerLowest = Grey950,
    surfaceContainerLow = Grey900,
    surfaceContainer = Grey850,
    surfaceContainerHigh = Grey800,
    surfaceContainerHighest = Grey750,
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = KeyguardeTypography,
        shapes = Shapes(
            small = RoundedCornerShape(Radius.s),
            medium = RoundedCornerShape(Radius.m),
            large = RoundedCornerShape(Radius.l)
        ),
        content = content
    )
}
