package com.quickmindgames.sudoku.presentation.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shake Effect Modifier
 * Creates a shaking animation for UI feedback on errors
 */
@Composable
fun Modifier.shakeEffect(shouldShake: Boolean): Modifier {
    var shakeOffset by remember(shouldShake) { mutableStateOf(0f) }

    if (shouldShake) {
        shakeOffset = (Math.random() * 20 - 10).toFloat()
    }

    val animatedOffset by animateFloatAsState(
        targetValue = if (shouldShake) shakeOffset else 0f,
        label = "shake"
    )

    return this.offset(x = animatedOffset.dp)
}

