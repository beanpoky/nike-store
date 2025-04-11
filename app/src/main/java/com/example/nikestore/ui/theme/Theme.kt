package com.example.nikestore.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark
)

private val LightNikeColors = NikeColors(
    background = backgroundLight,
    backgroundOverlay = backgroundOverlayLight,
    appBar = appBarLight,
    appBarTitle = appBarTitleLight,
    appBarIcon = appBarIconLight,
    textPrimary = textPrimaryLight,
    textSecondary = textSecondaryLight,
    textTertiary = textTertiaryLight,
    textButtonContent = textButtonContentLight,
    loadingProgress = loadingProgressLight,
    error = errorLight,
    navigationSelected = navigationSelectedLight,
    navigationUnselected = navigationUnselectedLight,
    inputHint = inputHintLight,
    inputFocused = inputFocusedLight,
    inputUnfocused = inputUnfocusedLight,
    divider = dividerLight,
    radioButtonSelected = radioButtonSelectedLight,
    radioButtonUnselected = radioButtonUnselectedLight,
    actionButton = actionButtonLight,
    actionButtonContent = actionButtonContentLight,
    button = buttonLight,
    buttonContent = buttonContentLight,
    badge = badgeLight,
    badgeContent = badgeContentLight
)

private val DarkNikeColors = NikeColors(
    background = backgroundDark,
    backgroundOverlay = backgroundOverlayDark,
    appBar = appBarDark,
    appBarTitle = appBarTitleDark,
    appBarIcon = appBarIconDark,
    textPrimary = textPrimaryDark,
    textSecondary = textSecondaryDark,
    textTertiary = textTertiaryDark,
    textButtonContent = textButtonContentDark,
    loadingProgress = loadingProgressDark,
    error = errorDark,
    navigationSelected = navigationSelectedDark,
    navigationUnselected = navigationUnselectedDark,
    inputHint = inputHintDark,
    inputFocused = inputFocusedDark,
    inputUnfocused = inputUnfocusedDark,
    divider = dividerDark,
    radioButtonSelected = radioButtonSelectedDark,
    radioButtonUnselected = radioButtonUnselectedDark,
    actionButton = actionButtonDark,
    actionButtonContent = actionButtonContentDark,
    button = buttonDark,
    buttonContent = buttonContentDark,
    badge = badgeDark,
    badgeContent = badgeContentDark
)

@Composable
fun NikeStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    val nikeColors = if (darkTheme) DarkNikeColors else LightNikeColors

    CompositionLocalProvider(LocalNikeColors provides nikeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Immutable
data class NikeColors(
    val background: Color,
    val backgroundOverlay: Color,
    val appBar: Color,
    val appBarTitle: Color,
    val appBarIcon: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textButtonContent: Color,
    val loadingProgress: Color,
    val error: Color,
    val navigationSelected: Color,
    val navigationUnselected: Color,
    val inputHint: Color,
    val inputFocused: Color,
    val inputUnfocused: Color,
    val divider: Color,
    val radioButtonSelected: Color,
    val radioButtonUnselected: Color,
    val actionButton: Color,
    val actionButtonContent: Color,
    val button: Color,
    val buttonContent: Color,
    val badge: Color,
    val badgeContent: Color
)

private val LocalNikeColors = staticCompositionLocalOf { LightNikeColors }

object NikeTheme {
    val colors: NikeColors
        @Composable
        get() = LocalNikeColors.current
}