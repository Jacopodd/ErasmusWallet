package com.example.erasmuswallet.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanGlow,
    onPrimary = Color(0xFF031018),
    primaryContainer = Color(0xFF113E5D),
    onPrimaryContainer = LiquidText,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF162A67),
    onSecondaryContainer = LiquidText,
    tertiary = VividPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF33235F),
    onTertiaryContainer = LiquidText,
    background = LiquidBackground,
    onBackground = LiquidText,
    surface = LiquidSurface,
    onSurface = LiquidText,
    surfaceVariant = LiquidSurfaceSoft,
    onSurfaceVariant = LiquidTextSecondary,
    outline = LiquidBorderStrong,
    outlineVariant = LiquidBorder,
    inverseOnSurface = LiquidBackground,
    inverseSurface = LiquidText,
    inversePrimary = Aqua,
    surfaceTint = CyanGlow,
    scrim = Color.Black
)

@Composable
fun ErasmusWalletTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
