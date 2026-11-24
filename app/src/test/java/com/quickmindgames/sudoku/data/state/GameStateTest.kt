package com.quickmindgames.sudoku.data.state

import com.quickmindgames.sudoku.domain.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class GameStateTest {

    private val solution = listOf(
        listOf(5, 3, 4, 6, 7, 8, 9, 1, 2),
        listOf(6, 7, 2, 1, 9, 5, 3, 4, 8),
        listOf(1, 9, 8, 3, 4, 2, 5, 6, 7),
        listOf(8, 5, 9, 7, 6, 1, 4, 2, 3),
        listOf(4, 2, 6, 8, 5, 3, 7, 9, 1),
        listOf(7, 1, 3, 9, 2, 4, 8, 5, 6),
        listOf(9, 6, 1, 5, 3, 7, 2, 8, 4),
        listOf(2, 8, 7, 4, 1, 9, 6, 3, 5),
        listOf(3, 4, 5, 2, 8, 6, 1, 7, 9)
    )

    /** Same grid with the first row blanked out. */
    private val original = listOf(List(9) { 0 }) + solution.drop(1)

    private fun state(scoredCells: Set<Int>?) = GameState(
        difficulty = Difficulty.Pulse,
        originalGrid = original,
        solutionGrid = solution,
        userGrid = original.mapIndexed { r, row ->
            row.mapIndexed { c, v ->
                when {
                    r == 0 && c < 3 -> CellData(solution[r][c])
                    r == 0 && c == 3 -> CellData(0, setOf(1, 6))
                    else -> CellData(v)
                }
            }
        },
        wrongCells = setOf(0 to 4),
        mistakes = 1,
        score = 12,
        timeSeconds = 95,
        selectedCell = 0 to 3,
        isNotesMode = true,
        hintsUsed = 2,
        hasDoublePointsBooster = true,
        scoredCells = scoredCells
    )

    @Test
    fun roundTrip_keepsEveryField() {
        val saved = state(scoredCells = setOf(0, 1, 2))

        assertEquals(saved, GameState.deserialize(saved.serialize()))
    }

    @Test
    fun roundTrip_emptyScoredCells_staysEmptyNotNull() {
        val restored = GameState.deserialize(state(scoredCells = emptySet()).serialize())

        assertEquals(emptySet<Int>(), restored?.scoredCells)
    }

    @Test
    fun oldSave_withoutNewerLines_stillLoads() {
        // Saves written before HINTS_USED, DOUBLE_POINTS_BOOSTER and SCORED existed.
        val oldFormat = state(scoredCells = setOf(0, 1, 2)).serialize()
            .lines()
            .filterNot {
                it.startsWith("HINTS_USED:") ||
                    it.startsWith("DOUBLE_POINTS_BOOSTER:") ||
                    it.startsWith("SCORED:")
            }
            .joinToString("\n")

        val restored = GameState.deserialize(oldFormat)

        assertNotNull(restored)
        assertEquals(12, restored!!.score)
        assertEquals(1, restored.mistakes)
        assertEquals(95, restored.timeSeconds)
        assertEquals(true, restored.isNotesMode)
        assertEquals(setOf(1, 6), restored.userGrid[0][3].notes)
        assertEquals(0, restored.hintsUsed)
        assertEquals(false, restored.hasDoublePointsBooster)
        assertNull("old saves must signal 'infer from grid'", restored.scoredCells)
    }

    @Test
    fun saveWithUnknownFutureLine_stillLoads() {
        val saved = state(scoredCells = setOf(0))
        val withExtraLine = saved.serialize() + "SOME_FUTURE_FIELD:42\n"

        assertEquals(saved, GameState.deserialize(withExtraLine))
    }

    @Test
    fun scoredCells_ignoresOutOfRangeAndGarbage() {
        val saved = state(scoredCells = null).serialize() + "SCORED:3,x,81,-1,80\n"

        assertEquals(setOf(3, 80), GameState.deserialize(saved)?.scoredCells)
    }
}
