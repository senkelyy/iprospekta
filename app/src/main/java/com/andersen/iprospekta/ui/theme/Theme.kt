package com.andersen.iprospekta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = IpGreen500,
    onPrimary = IpWhite,
    primaryContainer = IpGreen700,
    onPrimaryContainer = IpWhite,
    secondary = IpOrange500,
    onSecondary = IpGrey900,
    secondaryContainer = IpOrange300,
    onSecondaryContainer = IpGrey900,
    tertiary = IpOrange500,
    background = IpGreen900,
    onBackground = IpGrey100,
    surface = IpSurfaceDark,
    onSurface = IpGrey100,
    surfaceVariant = IpGrey700,
    onSurfaceVariant = IpGrey300,
    outline = IpGrey300
)

private val LightColors = lightColorScheme(
    primary = IpGreen700,
    onPrimary = IpWhite,
    primaryContainer = IpGreen500,
    onPrimaryContainer = IpWhite,
    secondary = IpOrange500,
    onSecondary = IpWhite,
    tertiary = IpOrange500,
    background = IpGrey100,
    onBackground = IpGrey900,
    surface = IpWhite,
    onSurface = IpGrey900,
    surfaceVariant = IpGrey300,
    onSurfaceVariant = IpGrey700,
    outline = IpGrey700
)

@Composable
fun IProspektaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = IpTypography,
        content = content
    )
}
