package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LessonGridCell(digit: Int, numColumns: Int, numRows: Int = 1) {
    // Calculate cell size based on number of columns and rows
    val cellSize = when {
        numColumns >= 9 -> 36.dp    // 9 cells per row (horizontal)
        numColumns == 1 && numRows >= 9 -> 28.dp  // 9 cells per column (vertical) - smaller
        numColumns >= 3 -> 44.dp    // 3 cells per row
        else -> 52.dp               // 1 cell per row
    }

    val fontSize = when {
        numColumns >= 9 -> 14.sp
        numColumns == 1 && numRows >= 9 -> 12.sp  // Smaller font for vertical
        else -> 18.sp
    }
    val lessonCellShape = RoundedCornerShape(6.dp)
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(cellSize)
            .border(2.dp, colors.primary, lessonCellShape)
            .background(
                if (digit == 0)
                    colors.surfaceVariant
                else
                    colors.surface,
                lessonCellShape
            )
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (digit == 0) "?" else digit.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )
    }
}