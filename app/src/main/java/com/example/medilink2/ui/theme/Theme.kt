package com.example.medilink2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    secondary = TealDark,
    tertiary = TealLight,
    background = Background,
    surface = Surface,
    onPrimary = Background,
    onSecondary = Background,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = OnSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    secondary = TealLight,
    tertiary = TealDark,
    background = Color(0xFF0F1111), // Deeper black for better contrast
    surface = Color(0xFF1C1E1F),    // Darker surface for cards/inputs
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFFFFFFF), // Pure white text for max clarity
    onSurface = Color(0xFFFFFFFF),    // Pure white text on surfaces
    onSurfaceVariant = Color(0xFFB0B3B8) // Brighter secondary text
)

@Composable
fun Medilink2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}