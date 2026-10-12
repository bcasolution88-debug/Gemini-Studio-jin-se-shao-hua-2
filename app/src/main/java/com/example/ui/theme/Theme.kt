package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

private val HighContrastColorScheme = darkColorScheme(
    primary = Color(0xFFFFEA00), // Vivid electric yellow for maximum legibility
    primaryContainer = Color(0xFF2C2500),
    secondary = Color(0xFF00E676), // High contrast neon green accent
    secondaryContainer = Color(0xFF003314),
    background = Color(0xFF000000), // Pure black background for max contrast ratio
    surface = Color(0xFF000000),
    surfaceVariant = Color(0xFF1C1C1C),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF), // Pure white crisp text
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFFFEA00), // Vivid yellow text for captions/headers
    outline = Color(0xFFFFEA00)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = DarkGold,
    secondaryContainer = DarkGoldContainer,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = LightSurface,
    onSecondary = DarkBackground,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    primaryContainer = LightPrimaryContainer,
    secondary = LightGold,
    secondaryContainer = LightGoldContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = LightSurface,
    onSecondary = LightSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary
)

private val ElderColorScheme = lightColorScheme(
    primary = ElderPrimary,
    primaryContainer = ElderPrimaryContainer,
    secondary = ElderPrimary,
    secondaryContainer = ElderPrimaryContainer,
    background = ElderBackground,
    surface = ElderSurface,
    surfaceVariant = ElderSurfaceVariant,
    onPrimary = LightSurface,
    onSecondary = LightSurface,
    onBackground = ElderTextPrimary,
    onSurface = ElderTextPrimary,
    onSurfaceVariant = ElderTextSecondary
)

@Composable
fun GoldenMemoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isElderMode: Boolean = false,
    isHighContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isHighContrast -> HighContrastColorScheme
        isElderMode -> ElderColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
