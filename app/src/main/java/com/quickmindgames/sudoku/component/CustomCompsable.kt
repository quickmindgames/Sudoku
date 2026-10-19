package com.quickmindgames.sudoku.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import com.quickmindgames.sudoku.presentation.ui.theme.LightPrimary

@Composable
fun primaryGradientBrush(): Brush {
    val colorScheme = MaterialTheme.colorScheme
    return remember(colorScheme.primary) {
        Brush.horizontalGradient(
            colors = listOf(colorScheme.primary, LightPrimary)
        )
    }
}