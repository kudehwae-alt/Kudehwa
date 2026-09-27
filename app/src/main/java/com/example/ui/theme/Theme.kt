package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.data.AppThemeStyle

private val DarkNavyColorScheme =
  darkColorScheme(
    primary = Color(0xFF4D9CFF),
    onPrimary = Color(0xFF002F6C),
    primaryContainer = Color(0xFF004899),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF8EBBFF),
    onSecondary = Color(0xFF002E69),
    tertiary = KudehwaAccent,
    background = KudehwaDarkBg,
    onBackground = Color(0xFFE2E8F0),
    surface = KudehwaDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = KudehwaDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
  )

private val AmoledBlackColorScheme =
  darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1D4ED8),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF60A5FA),
    onSecondary = Color(0xFF000000),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF171717),
    onSurfaceVariant = Color(0xFFA3A3A3),
    outline = Color(0xFF262626)
  )

private val EmeraldGoldColorScheme =
  darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF047857),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF002013),
    tertiary = Color(0xFFD4AF37),
    background = Color(0xFF04150F),
    onBackground = Color(0xFFECFDF5),
    surface = Color(0xFF062319),
    onSurface = Color(0xFFF0FDF4),
    surfaceVariant = Color(0xFF0D3B2B),
    onSurfaceVariant = Color(0xFFA7F3D0),
    outline = Color(0xFF134E39)
  )

private val CyberpunkNeonColorScheme =
  darkColorScheme(
    primary = Color(0xFFEC4899),
    onPrimary = Color(0xFF500021),
    primaryContainer = Color(0xFFBE185D),
    onPrimaryContainer = Color(0xFFFCE7F3),
    secondary = Color(0xFF06B6D4),
    onSecondary = Color(0xFF00363D),
    tertiary = Color(0xFFA855F7),
    background = Color(0xFF090314),
    onBackground = Color(0xFFF5F3FF),
    surface = Color(0xFF130724),
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = Color(0xFF22113D),
    onSurfaceVariant = Color(0xFFDDD6FE),
    outline = Color(0xFF3B1E66)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = KudehwaBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0EFFF),
    onPrimaryContainer = Color(0xFF001D40),
    secondary = KudehwaNavy,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = KudehwaAccent,
    background = KudehwaLightBg,
    onBackground = Color(0xFF0F172A),
    surface = KudehwaLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = KudehwaLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
  )

@Composable
fun MyApplicationTheme(
  appTheme: AppThemeStyle = AppThemeStyle.DARK_NAVY,
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when (appTheme) {
    AppThemeStyle.DARK_NAVY -> DarkNavyColorScheme
    AppThemeStyle.AMOLED_BLACK -> AmoledBlackColorScheme
    AppThemeStyle.EMERALD_GOLD -> EmeraldGoldColorScheme
    AppThemeStyle.CYBERPUNK_NEON -> CyberpunkNeonColorScheme
    AppThemeStyle.LIGHT -> LightColorScheme
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
