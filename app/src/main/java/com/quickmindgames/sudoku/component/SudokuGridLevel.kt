package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
    onCellClick: (Int, Int) -> Unit
) {

    val colors = MaterialTheme.colorScheme

    SudokuBoardContainer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
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
                val isSameNumber =
                    selectedValue != null && selectedValue != 0 && value == selectedValue
                val isWrong = wrongCells.contains(row to col)
                val shouldShake = shakeCells.contains(row to col)
                val animatedBgColor = when {
                    isSelected -> colors.surfaceVariant
                    isSameNumber -> colors.secondary.copy(alpha = 0.18f)
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
                        val notesList = cell.notes.toList().take(4)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    notesList.getOrNull(0)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    notesList.getOrNull(1)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    notesList.getOrNull(2)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    notesList.getOrNull(3)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
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