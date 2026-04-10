package com.tradersguardian.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary       = AccentCyan,
    background    = BgDark,
    surface       = Surface,
    onPrimary     = BgDark,
    onBackground  = TextPrimary,
    onSurface     = TextPrimary,
    error         = ErrorRed
)

@Composable
fun TradersGuardianTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content
    )
}