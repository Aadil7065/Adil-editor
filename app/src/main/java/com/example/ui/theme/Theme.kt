package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = TextWhite,
    primaryContainer = DeepViolet,
    onPrimaryContainer = TextWhite,
    secondary = NeonCyan,
    onSecondary = DeepSpace,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = NeonCyan,
    tertiary = NeonPink,
    onTertiary = TextWhite,
    background = DeepSpace,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = SurfaceBorder,
    outlineVariant = SurfaceBorderGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to futuristic dark theme
    dynamicColor: Boolean = false, // Keep consistent cyber branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
