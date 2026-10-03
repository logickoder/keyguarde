package dev.logickoder.keyguarde.app.theme

import androidx.compose.ui.graphics.Color

// Primitives. Screens use the semantic ColorScheme in Theme.kt, never these directly.

// Teal is the only hue: it marks matched keywords and nothing else.
// 700 is a shade deeper than Tailwind's teal-700 so it clears 4.5:1 on every grey below.
internal val Teal300 = Color(0xFF5EEAD4)
internal val Teal400 = Color(0xFF2DD4BF)
internal val Teal700 = Color(0xFF0E716A)
internal val Teal950 = Color(0xFF042F2E)

internal val Grey0 = Color(0xFFFFFFFF)
internal val Grey50 = Color(0xFFF7F7F8)
internal val Grey100 = Color(0xFFF4F4F5)
internal val Grey150 = Color(0xFFEFEFF1)
internal val Grey200 = Color(0xFFE4E4E7)
internal val Grey300 = Color(0xFFD4D4D8)
internal val Grey400 = Color(0xFFA1A1AA)
internal val Grey450 = Color(0xFF8A8A93)
internal val Grey500 = Color(0xFF71717A)
internal val Grey600 = Color(0xFF52525B)
internal val Grey700 = Color(0xFF3F3F46)
internal val Grey750 = Color(0xFF323236)
internal val Grey800 = Color(0xFF27272A)
internal val Grey850 = Color(0xFF1C1C1F)
internal val Grey900 = Color(0xFF18181B)
internal val Grey925 = Color(0xFF121214)
internal val Grey950 = Color(0xFF0B0B0D)

internal val Red200 = Color(0xFFFEE4E2)
internal val Red300 = Color(0xFFF97066)
internal val Red700 = Color(0xFFB42318)
internal val Red800 = Color(0xFF7A271A)
internal val Red950 = Color(0xFF55160C)

// Fallback avatar backgrounds. Neutral on purpose; initials stay at least 9.5:1 on every tone.
internal val AvatarTonesLight = listOf(Grey150, Grey200, Grey300)
internal val AvatarInkLight = Grey800
internal val AvatarTonesDark = listOf(Grey800, Grey750, Grey700)
internal val AvatarInkDark = Grey100
