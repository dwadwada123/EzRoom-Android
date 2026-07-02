package com.example.ezroom.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * EzRoom 2026 "Pro Max" Color System
 * Focus: Depth, Softness, and Modern Contrast
 */

// Primary Palette
val PrimaryMain = Color(0xFF2563EB)       // Modern Royal Blue
val PrimaryDark = Color(0xFF1E293B)       // Deep Slate
val PrimaryLight = Color(0xFFDBEAFE)      // Soft Blue Tint
val PrimarySurface = Color(0xFFF0F7FF)    // Ultra-light surface blue

// Accent & Status
val AccentTeal = Color(0xFF0D9488)        // Professional Teal
val AccentAmber = Color(0xFFF59E0B)       // Warm Amber
val SuccessEmerald = Color(0xFF10B981)    // Success Green
val ErrorRose = Color(0xFFF43F5E)         // Error Rose

// Neutral System (Depth Focused)
val Neutral900 = Color(0xFF0F172A)        // Primary Headings
val Neutral700 = Color(0xFF334155)        // Body text
val Neutral500 = Color(0xFF64748B)        // Secondary text
val Neutral300 = Color(0xFFCBD5E1)        // Disabled/Dividers
val Neutral100 = Color(0xFFF1F5F9)        // Surface backgrounds
val Neutral50 = Color(0xFFF8FAFC)         // Subtle backgrounds

val White = Color(0xFFFFFFFF)
val GlassWhite = Color(0xCCFFFFFF)        // 80% Opacity for Glassmorphism
val GlassWhiteDark = Color(0x99FFFFFF)    // 60% Opacity

// Functional Mappings for Material3 (2026 Profile)
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

val md_theme_light_background = Color(0xFFFDFDFE) // Minimal tint
val md_theme_light_onBackground = Neutral900
val md_theme_light_surface = White
val md_theme_light_onSurface = Neutral900
val md_theme_light_surfaceVariant = Neutral50
val md_theme_light_onSurfaceVariant = Neutral700
val md_theme_light_outline = Neutral300

// Legacy Compatibility
val OrangePrimary = PrimaryMain
val OrangeSecondary = AccentAmber
val OrangeTertiary = PrimaryLight
val TealAccent = AccentTeal
val BackgroundLight = md_theme_light_background
val SurfaceLight = White
val SurfaceGrey = Color(0xFFF3F4F6)
val OnPrimaryLight = White
val OnBackgroundLight = Neutral900
val OnSurfaceVariantLight = Neutral700

