package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun VisualLessonGrid(grid: List<List<Int>>) {
    val numColumns = grid.firstOrNull()?.size ?: 0
    val numRows = grid.size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        grid.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { digit ->
                    LessonGridCell(digit = digit, numColumns = numColumns, numRows = numRows)
                }
            }
        }
    }
}