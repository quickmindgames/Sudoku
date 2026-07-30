package com.quickmindgames.sudoku.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SudokuGridCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cellSize = size.width / 9f
        // Draw minor grid lines
        for (i in 1 until 9) {
            val stroke = if (i % 3 == 0) 3f else 1f
            // Vertical
            drawLine(
                color = Color.Gray,
                start = Offset(x = i * cellSize, y = 0f),
                end = Offset(x = i * cellSize, y = size.height),
                strokeWidth = stroke
            )
            // Horizontal
            drawLine(
                color = Color.Gray,
                start = Offset(x = 0f, y = i * cellSize),
                end = Offset(x = size.width, y = i * cellSize),
                strokeWidth = stroke
            )
        }
        // Draw outer border
        drawRect(
            color = Color.Black,
            topLeft = Offset.Zero,
            size = size,
            style = Stroke(width = 3f)
        )
    }
}