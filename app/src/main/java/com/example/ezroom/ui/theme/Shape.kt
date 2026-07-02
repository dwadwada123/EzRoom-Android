package com.example.ezroom.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * EzRoom 2026 "Squircle-inspired" Shape System
 * Focus: Large radii, soft edges, and organic feel
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),    // Inputs, Small Buttons
    medium = RoundedCornerShape(16.dp),   // Cards, Major Buttons
    large = RoundedCornerShape(24.dp),    // Bottom Sheets, Feature Cards
    extraLarge = RoundedCornerShape(32.dp) // Page-level containers
)

