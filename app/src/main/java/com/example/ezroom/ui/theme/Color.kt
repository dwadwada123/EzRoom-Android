package com.example.ezroom.ui.theme

import androidx.compose.ui.graphics.Color

// EzRoom palette
val PrimaryMain = Color(0xFF2563EB)
val PrimaryLight = Color(0xFFDBEAFE)
val PrimarySurface = Color(0xFFF0F7FF)

val AccentTeal = Color(0xFF0D9488)
val AccentAmber = Color(0xFFF59E0B)
val SuccessEmerald = Color(0xFF10B981)
val ErrorRose = Color(0xFFF43F5E)

val Neutral900 = Color(0xFF0F172A)
val Neutral700 = Color(0xFF334155)
val Neutral500 = Color(0xFF64748B)
val Neutral300 = Color(0xFFCBD5E1)
val Neutral100 = Color(0xFFF1F5F9)
val Neutral50 = Color(0xFFF8FAFC)

val White = Color(0xFFFFFFFF)

// Material3 mapping
val md_theme_light_primary = PrimaryMain
val md_theme_light_onPrimary = White
val md_theme_light_primaryContainer = PrimaryLight
val md_theme_light_onPrimaryContainer = PrimaryMain

val md_theme_light_secondary = AccentTeal
val md_theme_light_onSecondary = White
val md_theme_light_secondaryContainer = Color(0xFFF0FDFA)
val md_theme_light_onSecondaryContainer = AccentTeal

val md_theme_light_error = ErrorRose
val md_theme_light_onError = White

val md_theme_light_background = Color(0xFFFDFDFE)
val md_theme_light_onBackground = Neutral900
val md_theme_light_surface = White
val md_theme_light_onSurface = Neutral900
val md_theme_light_surfaceVariant = Neutral50
val md_theme_light_onSurfaceVariant = Neutral700
val md_theme_light_outline = Neutral300

// Compatibility
val OrangePrimary = PrimaryMain
val OrangeSecondary = AccentAmber
val TealAccent = AccentTeal
val BackgroundLight = md_theme_light_background
val SurfaceLight = White
val SurfaceGrey = Color(0xFFF3F4F6)
val OnPrimaryLight = White
val OnBackgroundLight = Neutral900
