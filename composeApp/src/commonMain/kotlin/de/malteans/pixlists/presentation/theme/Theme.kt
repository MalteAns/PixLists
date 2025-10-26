package de.malteans.pixlists.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    secondary = Peach,
    tertiary = Lavender
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    secondary = Peach,
    tertiary = Lavender
)

val ColorScheme.containerColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) surfaceContainer else surfaceContainerHigh

@Composable
fun PixListsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}