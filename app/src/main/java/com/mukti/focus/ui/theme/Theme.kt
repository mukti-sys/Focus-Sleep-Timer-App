package com.mukti.focus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = TerracottaPrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SoftPeachSecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SoftPeachSecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = SlateTertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = SlateTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceContainerLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaPrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = TerracottaPrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SoftPeachSecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SoftPeachSecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = SlateTertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = SlateTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

@Composable
fun TerraFocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
