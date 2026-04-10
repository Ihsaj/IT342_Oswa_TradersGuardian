package com.tradersguardian.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tradersguardian.R

// ── Font Families ────────────────────────────────────────────────────────────
// Add sora_regular.ttf, sora_semibold.ttf, sora_bold.ttf
// and dm_sans_regular.ttf, dm_sans_medium.ttf to res/font/
// Download from: https://fonts.google.com/specimen/Sora
//                https://fonts.google.com/specimen/DM+Sans

val SoraFamily = FontFamily(
    Font(R.font.sora_regular,  FontWeight.Normal),
    Font(R.font.sora_semibold, FontWeight.SemiBold),
    Font(R.font.sora_bold,     FontWeight.Bold),
)

val DmSansFamily = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium,  FontWeight.Medium),
)

val Typography = Typography(
    displayLarge  = TextStyle(fontFamily = SoraFamily,   fontWeight = FontWeight.Bold,     fontSize = 30.sp),
    headlineLarge = TextStyle(fontFamily = SoraFamily,   fontWeight = FontWeight.Bold,     fontSize = 24.sp),
    headlineMedium= TextStyle(fontFamily = SoraFamily,   fontWeight = FontWeight.Bold,     fontSize = 20.sp),
    titleLarge    = TextStyle(fontFamily = SoraFamily,   fontWeight = FontWeight.Bold,     fontSize = 18.sp),
    titleMedium   = TextStyle(fontFamily = SoraFamily,   fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall    = TextStyle(fontFamily = SoraFamily,   fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyLarge     = TextStyle(fontFamily = DmSansFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp),
    bodyMedium    = TextStyle(fontFamily = DmSansFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp),
    bodySmall     = TextStyle(fontFamily = DmSansFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp),
    labelLarge    = TextStyle(fontFamily = DmSansFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp),
    labelSmall    = TextStyle(fontFamily = DmSansFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp),
)
