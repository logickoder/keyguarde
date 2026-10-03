package dev.logickoder.keyguarde.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.logickoder.keyguarde.R

val interProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val InterFont = GoogleFont("Inter")

val interFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = interProvider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = interProvider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = interProvider, weight = FontWeight.SemiBold),
    Font(googleFont = InterFont, fontProvider = interProvider, weight = FontWeight.Bold)
)

private val OswaldFont = GoogleFont("Oswald")

// Oswald is reserved for keyword pills and the Keywords tab. Downloaded, not bundled.
val oswaldFontFamily = FontFamily(
    Font(googleFont = OswaldFont, fontProvider = interProvider, weight = FontWeight.Medium),
    Font(googleFont = OswaldFont, fontProvider = interProvider, weight = FontWeight.SemiBold),
)

/** Keyword pill text. Callers uppercase the string; TextStyle has no text transform. */
val KeywordPillStyle = TextStyle(
    fontFamily = oswaldFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.08.em,
)

private val base = Typography()

// M3 base tracking is tuned for Roboto and loosens Inter, so it's reset to 0. Line heights stay.
private fun TextStyle.inter(weight: FontWeight? = null) = copy(
    fontFamily = interFontFamily,
    fontWeight = weight ?: fontWeight,
    letterSpacing = 0.sp,
)

val KeyguardeTypography = Typography(
    displayLarge = base.displayLarge.inter(FontWeight.Bold).copy(fontSize = 36.sp),
    displayMedium = base.displayMedium.inter(),
    displaySmall = base.displaySmall.inter(),
    headlineLarge = base.headlineLarge.inter(),
    headlineMedium = base.headlineMedium.inter(FontWeight.SemiBold).copy(fontSize = 24.sp),
    headlineSmall = base.headlineSmall.inter(),
    titleLarge = base.titleLarge.inter(FontWeight.Medium).copy(fontSize = 20.sp),
    titleMedium = base.titleMedium.inter(FontWeight.SemiBold),
    titleSmall = base.titleSmall.inter(FontWeight.Medium),
    bodyLarge = base.bodyLarge.inter(FontWeight.Normal).copy(fontSize = 16.sp),
    bodyMedium = base.bodyMedium.inter(FontWeight.Normal),
    bodySmall = base.bodySmall.inter(FontWeight.Normal).copy(fontSize = 12.sp),
    labelLarge = base.labelLarge.inter(FontWeight.Medium).copy(fontSize = 14.sp),
    labelMedium = base.labelMedium.inter(FontWeight.Medium),
    labelSmall = base.labelSmall.inter(FontWeight.Medium),
)
