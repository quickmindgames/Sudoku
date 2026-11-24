package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pencil-mark candidates for one Sudoku cell, as a 3×3 mini-grid: each digit always sits
 * in its own slot (1 top-left … 9 bottom-right), the standard Sudoku layout, so all nine
 * candidates fit and a digit never moves when others are added or removed.
 *
 * The text size is derived from [cellSize] (not a fixed sp) and is independent of the
 * system font scale, so the notes can't overflow their slots on small screens (360 dp)
 * or with large font settings.
 */
@Composable
fun NotesMiniGrid(
    notes: Set<Int>,
    cellSize: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val slotSize = cellSize / 3
    // ~70% of a slot: legible, with room for the glyph's side bearings. Converting from
    // dp (toSp divides by the font scale) keeps it the same size at any font setting.
    val noteTextStyle = with(density) {
        val size = (slotSize * NOTE_TEXT_FRACTION).toSp()
        TextStyle(
            fontSize = size,
            lineHeight = size,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = color,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (column in 0 until 3) {
                    val digit = row * 3 + column + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (digit in notes) {
                            Text(text = digit.toString(), style = noteTextStyle, maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }
    }
}

private const val NOTE_TEXT_FRACTION = 0.7f

@Preview(widthDp = 36, heightDp = 36, showBackground = true, fontScale = 2f)
@Composable
private fun NotesMiniGridAllNinePreview() {
    Box(Modifier.fillMaxSize()) {
        NotesMiniGrid(notes = (1..9).toSet(), cellSize = 36.dp, color = Color.DarkGray)
    }
}
