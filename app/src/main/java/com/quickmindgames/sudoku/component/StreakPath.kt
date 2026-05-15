package com.quickmindgames.sudoku.component

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun StreakPath(
    streakCount: Int,
    hasActiveToday: Boolean,
    completedToday: Boolean,
    onStart: (Int) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BoxWithConstraints {
                val nodeSpacing = 85.dp
                val horizontalPadding = 16.dp
                val availableWidth = maxWidth - horizontalPadding

                val nodesPerRowCalculated = maxOf(
                    2,
                    (availableWidth.value / nodeSpacing.value).toInt()
                )

                // Always show at least 3 full rows beyond the last completed node,
                // so the path always looks like it goes on forever.
                val completedRows = if (streakCount == 0) 0
                else (streakCount + nodesPerRowCalculated - 1) / nodesPerRowCalculated
                val minRows = maxOf(10, completedRows + 3)
                // totalNodes is always an exact multiple of nodesPerRow → every row is complete
                val totalNodes = minRows * nodesPerRowCalculated

                // Group nodes by row
                val nodeRows = (0 until totalNodes).groupBy { index ->
                    index / nodesPerRowCalculated
                }

                val primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                val grayColor = Color.Gray.copy(alpha = 0.5f)

                // Auto-scroll: the active/next node is streakCount+1, in row = streakCount/nodesPerRow.
                // Scroll so that one completed row above it is visible (for context).
                val scrollState = rememberScrollState()
                val density = LocalDensity.current
                val activeRowIndex = streakCount / nodesPerRowCalculated

                LaunchedEffect(streakCount, nodesPerRowCalculated) {
                    val rowHeightPx = with(density) { nodeSpacing.toPx() }
                    // One row above the active row for context; never below 0
                    val targetPx = ((activeRowIndex - 1).coerceAtLeast(0) * rowHeightPx).toInt()
                    // Wait for layout to complete before animating
                    delay(150)
                    scrollState.animateScrollTo(targetPx)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp, horizontal = horizontalPadding)
                        .verticalScroll(scrollState)
                ) {
                    nodeRows.forEach { (rowIndex, nodeIndices) ->
                        val isEvenRow = rowIndex % 2 == 0
                        val sortedIndices = if (isEvenRow) nodeIndices else nodeIndices.reversed()

                        // Row height == nodeSpacing so vertical padding == horizontal padding
                        // → both line segments have the same length
                        val rowHeightDp = nodeSpacing  // 85.dp

                        Box(modifier = Modifier.fillMaxWidth()) {

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeightDp)
                            ) {
                                val nodeSpacingPx = 85.dp.toPx()
                                val nodeWidthPx = 48.dp.toPx()
                                val nodePaddingPx = (nodeSpacingPx - nodeWidthPx) / 2f
                                val lineY = size.height / 2f

                                // Calculate center offset: nodes are centered, so calculate padding on left
                                val totalNodesWidth = nodeIndices.size * nodeSpacingPx
                                val centerOffsetX = (size.width - totalNodesWidth) / 2f

                                val leadingX = if (isEvenRow) {
                                    centerOffsetX + nodePaddingPx + nodeWidthPx / 2f
                                } else {
                                    centerOffsetX + ((nodeIndices.size - 1) * nodeSpacingPx) + nodePaddingPx + nodeWidthPx / 2f
                                }
                                val trailingX = if (isEvenRow) {
                                    centerOffsetX + ((nodeIndices.size - 1) * nodeSpacingPx) + nodePaddingPx + nodeWidthPx / 2f
                                } else {
                                    centerOffsetX + nodePaddingPx + nodeWidthPx / 2f
                                }

                                // ── Horizontal lines: per-segment color ──
                                // Segment between sortedIndices[i] (day d1) and sortedIndices[i+1] (day d2)
                                // is colored primary only if min(d1, d2) <= streakCount.
                                if (nodeIndices.size > 1) {
                                    for (i in 0 until nodeIndices.size - 1) {
                                        val d1 = sortedIndices[i] + 1
                                        val d2 = sortedIndices[i + 1] + 1
                                        val segColor = if (minOf(
                                                d1,
                                                d2
                                            ) <= streakCount
                                        ) primaryColor else grayColor
                                        val lineStart =
                                            centerOffsetX + (i * nodeSpacingPx) + nodePaddingPx + nodeWidthPx
                                        val lineEnd =
                                            centerOffsetX + ((i + 1) * nodeSpacingPx) + nodePaddingPx
                                        drawLine(
                                            color = segColor,
                                            start = Offset(lineStart, lineY),
                                            end = Offset(lineEnd, lineY),
                                            strokeWidth = 2.dp.toPx()
                                        )
                                    }
                                }

                                // ── Top-half vertical connector (entering from previous row) ──
                                // This connector joins day (rowIndex * nodesPerRow) to day (rowIndex * nodesPerRow + 1).
                                // Color it primary only if the earlier of those two days is completed.
                                if (rowIndex > 0) {
                                    val connectingDay = rowIndex * nodesPerRowCalculated
                                    val topVertColor =
                                        if (connectingDay <= streakCount) primaryColor else grayColor
                                    drawLine(
                                        color = topVertColor,
                                        start = Offset(leadingX, 0f),
                                        end = Offset(leadingX, lineY - nodeWidthPx / 2f),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }

                                // ── Bottom-half vertical connector (exiting to next row) ──
                                // This connector joins day ((rowIndex+1) * nodesPerRow) to day ((rowIndex+1) * nodesPerRow + 1).
                                // Color it primary only if the trailing day of this row is completed.
                                if (rowIndex < nodeRows.size - 1) {
                                    val trailingDay = (rowIndex + 1) * nodesPerRowCalculated
                                    val botVertColor =
                                        if (trailingDay <= streakCount) primaryColor else grayColor
                                    drawLine(
                                        color = botVertColor,
                                        start = Offset(trailingX, lineY + nodeWidthPx / 2f),
                                        end = Offset(trailingX, size.height),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }

                            // Nodes layer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeightDp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                            ) {
                                sortedIndices.forEach { index ->
                                    val dayNumber = index + 1
                                    val isCompleted = dayNumber <= streakCount
                                    val isNext = dayNumber == streakCount + 1
                                    val isLocked = dayNumber > streakCount + 1
                                    val isPlayable = isNext && (hasActiveToday || !completedToday)
                                    val isActive = isNext && hasActiveToday
                                    val isNextButCompletedToday =
                                        isNext && completedToday && !hasActiveToday

                                    StreakNode(
                                        dayNumber = dayNumber,
                                        nodesPerRow = nodesPerRowCalculated,
                                        isCompleted = isCompleted,
                                        isActive = isActive,
                                        isLocked = isLocked,
                                        isPlayable = isPlayable,
                                        onClick = { onStart(dayNumber) },
                                        onLockedClick = {
                                            if (isNextButCompletedToday) {
                                                Toast.makeText(
                                                    context,
                                                    "Come back tomorrow to keep your streak alive!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        // No separate connector canvas needed:
                        // adjacent rows share the same vertical gap via their own top/bottom half-lines
                    } // end nodeRows.forEach
                }
            }
        }
    }
}