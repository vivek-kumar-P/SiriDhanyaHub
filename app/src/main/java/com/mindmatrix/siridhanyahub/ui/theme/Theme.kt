package com.mindmatrix.siridhanyahub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = ForestGreen,
    secondary = HarvestAmber,
    tertiary = TerracottaRed,
    background = CreamBackground,
    surface = WheatSurface,
    onPrimary = WheatSurface,
    onBackground = DarkEarthText,
    onSurface = DarkEarthText
)

private val DarkColors = darkColorScheme(
    primary = ForestGreen,
    secondary = HarvestAmber,
    tertiary = TerracottaRed
)

@Composable
fun SiriDhanyaHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}

