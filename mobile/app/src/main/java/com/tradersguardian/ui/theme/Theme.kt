package com.tradersguardian.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = AccentCyan,
    onPrimary        = BgDark,
    primaryContainer = AccentCyanDim,
    background       = BgMid,
    surface          = Surface,
    surfaceVariant   = Surface2,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    onSurfaceVariant = TextMuted,
    outline          = BorderColor,
    outlineVariant   = BorderColor,
    error            = ErrorRed,
    onError          = BgDark,
    scrim            = BgDark.copy(alpha = 0.6f)
)

@Composable
fun TradersGuardianTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content
    )
}