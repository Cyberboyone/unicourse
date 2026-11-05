package com.cyberboyone.lecturenotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Brand colours ──────────────────────────────────────────────
val NigeriaGreen      = Color(0xFF1B5E20)
val NigeriaGreenLight = Color(0xFF4CAF50)
val NigeriaGreenDark  = Color(0xFF0D3B0F)
val AccentGold        = Color(0xFFFFC107)
val AccentGoldDark    = Color(0xFFFFB300)
val SurfaceLight      = Color(0xFFF5F9F5)
val SurfaceDark       = Color(0xFF121212)
val CardLight         = Color(0xFFFFFFFF)
val CardDark          = Color(0xFF1E1E1E)
val OnSurfaceLight    = Color(0xFF1C1B1F)
val OnSurfaceDark     = Color(0xFFE6E1E5)
val ErrorRed          = Color(0xFFD32F2F)

private val LightColors = lightColorScheme(
    primary = NigeriaGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9F6CA),
    onPrimaryContainer = NigeriaGreenDark,
    secondary = AccentGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF3E2723),
    tertiary = Color(0xFF00796B),
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF49454F),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFFBDBDBD)
)

private val DarkColors = darkColorScheme(
    primary = NigeriaGreenLight,
    onPrimary = Color.Black,
    primaryContainer = NigeriaGreen,
    onPrimaryContainer = Color(0xFFB9F6CA),
    secondary = AccentGoldDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF4E342E),
    onSecondaryContainer = AccentGold,
    tertiary = Color(0xFF4DB6AC),
    onTertiary = Color.Black,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFEF5350),
    onError = Color.Black,
    outline = Color(0xFF616161)
)

// ── Typography ─────────────────────────────────────────────────
private val AppTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 0.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 0.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.15.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, letterSpacing = 0.25.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, letterSpacing = 0.4.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp)
)

// ── Theme ──────────────────────────────────────────────────────
@Composable
fun LectureNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
