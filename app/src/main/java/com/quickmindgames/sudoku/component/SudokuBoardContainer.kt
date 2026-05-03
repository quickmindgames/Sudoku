package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import kotlin.math.floor

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SudokuBoardContainer(
    modifier: Modifier = Modifier,
    content: @Composable (cellSizeDp: Dp, boardSizeDp: Dp) -> Unit
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val boardDpRaw = min(maxWidth, maxHeight)

        // Convert to px, then snap to multiples of 9 px
        val boardPxRaw = with(density) { boardDpRaw.toPx() }
        val cellPx = floor(boardPxRaw / 9f) // integer px per cell
        val boardPx = cellPx * 9f           // snapped board size px

        val cellDp = with(density) { cellPx.toDp() }
        val boardDp = with(density) { boardPx.toDp() }

        Box(
            modifier = Modifier
                .requiredSize(boardDp)   // ✅ board is exact size
                .clipToBounds()
        ) {
            content(cellDp, boardDp)
        }
    }
}