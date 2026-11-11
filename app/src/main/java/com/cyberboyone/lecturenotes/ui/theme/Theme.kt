package com.cyberboyone.lecturenotes.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ── Brand colours ──────────────────────────────────────────────
// Primary
val ElectricViolet = Color(0xFF7455F7)
val VibrantBlue = Color(0xFF398BFF)
val DeepPurple = Color(0xFF4830C9)

// Accent
val NeonLime = Color(0xFFC5F36A)
val Coral = Color(0xFFFF8066)
val SoftYellow = Color(0xFFFFD66B)

// Light
val LightBackground = Color(0xFFF6F5FC)
val LightSurface = Color(0xFFFFFFFF)
val LightPrimaryText = Color(0xFF19182B)
val LightSecondaryText = Color(0xFF77758B)
val LightBorder = Color(0xFFEBE9F3)

// Dark
val DarkBackground = Color(0xFF11111B)
val DarkSurface = Color(0xFF1C1B2B)
val DarkElevatedSurface = Color(0xFF25243A)
val DarkPrimaryText = Color(0xFFF8F7FF)
val DarkSecondaryText = Color(0xFFAAA8BF)

// Gradients (accessible as lists of Color)
val PrimaryGradient = listOf(ElectricViolet, VibrantBlue)
val AccentGradient = listOf(ElectricViolet, DeepPurple)
val HeroGradient = listOf(Color(0xFF4830C9), Color(0xFF7455F7), Color(0xFF398BFF))
val CoralGradient = listOf(Coral, Color(0xFFFF6B8A))
val LimeGradient = listOf(NeonLime, Color(0xFFB8E65C))

// Legacy aliases so existing code compiles during transition
val NigeriaGreen = ElectricViolet
val NigeriaGreenLight = VibrantBlue
val NigeriaGreenDark = DeepPurple
val AccentGold = NeonLime

// ── Color Scheme ───────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary = ElectricViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7FF),
    onPrimaryContainer = DeepPurple,
    secondary = VibrantBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F0FF),
    onSecondaryContainer = Color(0xFF1A3A6B),
    tertiary = Coral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE5E0),
    onTertiaryContainer = Color(0xFF5C1A10),
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightSurface,
    onSurface = LightPrimaryText,
    surfaceVariant = Color(0xFFF0EEF8),
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorder,
    outlineVariant = Color(0xFFDCDAE8),
    error = Color(0xFFE53935),
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C)
)

private val DarkColors = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = Color.White,
    primaryContainer = DeepPurple,
    onPrimaryContainer = Color(0xFFDDD0FF),
    secondary = VibrantBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1A3A6B),
    onSecondaryContainer = Color(0xFFD1E4FF),
    tertiary = Coral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5C1A10),
    onTertiaryContainer = Color(0xFFFFD6CC),
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkElevatedSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = Color(0xFF3D3C50),
    outlineVariant = Color(0xFF2E2D42),
    error = Color(0xFFEF5350),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// ── Typography ─────────────────────────────────────────────────
val AppTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 0.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, letterSpacing = 0.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 0.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.15.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, letterSpacing = 0.25.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, letterSpacing = 0.4.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp)
)

// ── Shapes ─────────────────────────────────────────────────────
val UniCourseShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// ── Theme ──────────────────────────────────────────────────────
@Composable
fun LectureNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = UniCourseShapes,
        content = content
    )
}
