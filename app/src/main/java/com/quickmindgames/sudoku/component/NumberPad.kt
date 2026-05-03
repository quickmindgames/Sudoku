package com.quickmindgames.sudoku.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.data.state.CellData

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun NumberPad(
    grid: List<List<CellData>>,
    enabled: Boolean = true,
    onNumberClick: (Int) -> Unit
) {
    // Count how many times each number (1–9) appears in the grid.
    val numberCounts = IntArray(10)
    for (row in grid) for (cell in row) if (cell.value in 1..9) numberCounts[cell.value]++
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..9).forEach { number ->
            val isComplete = numberCounts[number] >= 9
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                if (!isComplete) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (enabled) Modifier.clickable { onNumberClick(number) } else Modifier),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = if (enabled) 6.dp else 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.5f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}