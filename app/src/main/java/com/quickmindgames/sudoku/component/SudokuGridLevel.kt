package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.presentation.util.shakeEffect

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SudokuGridLevel(
    originalGrid: List<List<Int>>,
    grid: List<List<CellData>>,
    wrongCells: Set<Pair<Int, Int>>,
    shakeCells: Set<Pair<Int, Int>>,
    selectedCell: Pair<Int, Int>?,
    highlightRegion: Boolean,
    highlightSameNumbers: Boolean,
    onCellClick: (Int, Int) -> Unit
) {

    val colors = MaterialTheme.colorScheme

    SudokuBoardContainer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f).padding(horizontal = 8.dp)
    ) { cellSizeDp, boardSizeDp ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier.requiredSize(boardSizeDp),
            userScrollEnabled = false
        ) {
            items(81) { index ->
                val row = index / 9
                val col = index % 9
                val cell = grid[row][col]
                val value = cell.value
                val original = originalGrid[row][col]
                val isSelected = selectedCell == row to col
                val selectedValue = selectedCell?.let { grid[it.first][it.second].value }
                val isSameNumber = highlightSameNumbers &&
                    selectedValue != null && selectedValue != 0 && value == selectedValue
                val isInSelectedRegion = highlightRegion &&
                    selectedCell?.let { (selectedRow, selectedCol) ->
                        row == selectedRow ||
                            col == selectedCol ||
                            (row / 3 == selectedRow / 3 && col / 3 == selectedCol / 3)
                    } == true
                val isWrong = wrongCells.contains(row to col)
                val shouldShake = shakeCells.contains(row to col)
                val animatedBgColor = when {
                    isWrong -> colors.error.copy(alpha = 0.10f)
                    isSelected -> colors.surfaceVariant
                    isInSelectedRegion -> colors.secondary.copy(alpha = 0.15f)
                    isSameNumber -> colors.secondary.copy(alpha = 0.30f)
                    original != 0 -> colors.surfaceVariant
                    else -> colors.surface
                }
                Box(
                    modifier = Modifier
                        .requiredSize(cellSizeDp)
                        .background(animatedBgColor)
                        .clickable { onCellClick(row, col) },
                    contentAlignment = Alignment.Center
                ) {
                    val showNotes = (cell.notes.isNotEmpty() && (cell.value == 0 || isWrong))
                    if (showNotes) {
                        // All 9 candidates, each in its fixed 3×3 slot, sized to the cell.
                        NotesMiniGrid(
                            notes = cell.notes,
                            cellSize = cellSizeDp,
                            color = colors.onSurface
                        )
                    } else {
                        Text(
                            text = if (value == 0) "" else value.toString(),
                            fontSize = 26.sp,
                            fontWeight = when {
                                isWrong -> FontWeight.Bold
                                else -> FontWeight.Medium
                            },
                            color = when {
                                original != 0 -> colors.onSurface
                                isWrong -> colors.error
                                else -> colors.primary
                            },
                            modifier = Modifier.shakeEffect(shouldShake)
                        )
                    }
                }
            }
        }
        SudokuGridCanvas(modifier = Modifier.requiredSize(boardSizeDp))
    }
}