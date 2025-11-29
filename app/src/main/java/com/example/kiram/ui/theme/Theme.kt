package com.example.kiram.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = White,
    primaryContainer = DarkBlueVariant,
    onPrimaryContainer = White,
    secondary = Orange,
    onSecondary = White,
    secondaryContainer = OrangeVariant,
    onSecondaryContainer = White,
    tertiary = LightOrange,
    background = DarkBackground,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Gray200,
    error = Error,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = White,
    secondary = Orange,
    onSecondary = White,
    secondaryContainer = LightOrange,
    onSecondaryContainer = Black,
    tertiary = OrangeVariant,
    background = Gray100,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,
    error = Error,
    onError = White
)

@Composable
fun KiramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use KİRÂM brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
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