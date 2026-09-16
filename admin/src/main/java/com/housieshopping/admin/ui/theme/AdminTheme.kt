package com.housieshopping.admin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AmberAccent,
    onPrimary = NavyPrimary,
    primaryContainer = NavyVariant,
    onPrimaryContainer = GoldSecondary,
    secondary = GoldSecondary,
    onSecondary = NavyPrimary,
    surface = DarkSurface,
    onSurface = LightSurface,
    surfaceVariant = NavyVariant
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = LightSurface,
    primaryContainer = NavyVariant,
    onPrimaryContainer = GoldSecondary,
    secondary = AmberAccent,
    onSecondary = LightSurface,
    surface = LightSurface,
    onSurface = NavyPrimary,
    surfaceVariant = LightSurface
)

@Composable
fun HousieAdminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
