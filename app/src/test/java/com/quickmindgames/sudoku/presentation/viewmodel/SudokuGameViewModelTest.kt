package com.quickmindgames.sudoku.presentation.viewmodel

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
    fun creditedPoints_debug_doesNotOverflow() {
        assertEquals(Int.MAX_VALUE, SudokuGameViewModel.creditedPoints(Int.MAX_VALUE, debug = true))
    }
}
