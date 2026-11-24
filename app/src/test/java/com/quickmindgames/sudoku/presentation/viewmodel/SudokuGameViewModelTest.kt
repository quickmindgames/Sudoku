package com.quickmindgames.sudoku.presentation.viewmodel

import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.utils.DebugOptions
import org.junit.Assert.assertEquals
import org.junit.Test

class SudokuGameViewModelTest {

    @Test
    fun finalScore_withoutBooster_isBaseScore() {
        assertEquals(42, SudokuGameViewModel.finalScore(42, boosted = false))
    }

    @Test
    fun finalScore_withBooster_isDoubled() {
        assertEquals(84, SudokuGameViewModel.finalScore(42, boosted = true))
    }

    @Test
    fun finalScore_withBooster_doesNotOverflow() {
        assertEquals(Int.MAX_VALUE, SudokuGameViewModel.finalScore(Int.MAX_VALUE, boosted = true))
    }

    @Test
    fun creditedPoints_release_equalsAward() {
        assertEquals(84, SudokuGameViewModel.creditedPoints(84, debug = false))
    }

    @Test
    fun creditedPoints_debug_multipliesOnlyTheCredit() {
        assertEquals(
            84 * DebugOptions.SCORE_MULTIPLIER,
            SudokuGameViewModel.creditedPoints(84, debug = true)
        )
    }

    @Test
    fun inferScoredCells_marksOnlyCorrectUserFilledCells() {
        val solution = List(9) { r -> List(9) { c -> (r * 3 + r / 3 + c) % 9 + 1 } }
        val original = solution.mapIndexed { r, row ->
            row.mapIndexed { c, v -> if (r == 0) 0 else v }
        }
        val user = original.mapIndexed { r, row ->
            row.mapIndexed { c, v ->
                when {
                    r == 0 && c == 0 -> CellData(solution[0][0]) // correct -> scored
                    r == 0 && c == 1 -> CellData(solution[0][1] % 9 + 1) // wrong -> not scored
                    r == 0 && c == 2 -> CellData(0, setOf(1, 2)) // empty with notes
                    else -> CellData(v) // givens are never "scored"
                }
            }
        }

        assertEquals(setOf(0), SudokuGameViewModel.inferScoredCells(original, solution, user))
    }

    @Test
    fun creditedPoints_debug_doesNotOverflow() {
        assertEquals(Int.MAX_VALUE, SudokuGameViewModel.creditedPoints(Int.MAX_VALUE, debug = true))
    }
}
