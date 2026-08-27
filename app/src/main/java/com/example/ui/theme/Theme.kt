package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AdrOrange,
    onPrimary = Color.Black,
    primaryContainer = AdrOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = AdrOrangeLight,
    onSecondary = Color.Black,
    secondaryContainer = Slate700,
    onSecondaryContainer = LightTextPrimary,
    tertiary = HazardYellow,
    onTertiary = Color.Black,
    background = Slate900,
    onBackground = LightTextPrimary,
    surface = Slate800,
    onSurface = LightTextPrimary,
    surfaceVariant = Slate700,
    onSurfaceVariant = LightTextSecondary,
    error = HazardRed,
    onError = Color.White,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = AdrOrangeDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFFE65100),
    secondary = Color(0xFF37474F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFD8DC),
    onSecondaryContainer = Color(0xFF263238),
    tertiary = Color(0xFFC2410C),
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    error = HazardRed,
    onError = Color.White,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent high-visibility industrial ADR styling
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
