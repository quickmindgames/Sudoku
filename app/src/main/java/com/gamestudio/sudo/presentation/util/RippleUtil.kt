package com.gamestudio.sudo.presentation.util

import androidx.compose.foundation.Indication
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Provides ripple indication for interactive elements
 * Uses Material3 ripple implementation
 */
@Composable
fun rememberRippleIndication(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
): Indication {
    return ripple(
        bounded = bounded,
        radius = radius,
        color = color
    )
}

