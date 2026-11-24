package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.data.state.GameState
import com.quickmindgames.sudoku.domain.generator.SudokuGenerator
import com.quickmindgames.sudoku.domain.model.Difficulty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/** Issue #20: a cell holds all 9 pencil-mark candidates (no 4-note cap). */
@RunWith(AndroidJUnit4::class)
class SudokuGameViewModelNotesTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var storeScope: CoroutineScope
    private lateinit var storeFile: File
    private lateinit var viewModel: SudokuGameViewModel
    private lateinit var emptyCell: Pair<Int, Int>

    @Before
    fun setUp() {
        storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        storeFile = File(context.cacheDir, "notes_test_${UUID.randomUUID()}.preferences_pb")
        viewModel = SudokuGameViewModel(
            context = context,
            appPreferences = AppPreferences(
                PreferenceDataStoreFactory.create(scope = storeScope) { storeFile }
            ),
            mode = "new",
            difficulty = Difficulty.Breeze,
            streakDay = 0,
            useDoublePointsBooster = false
        )
        val (puzzle, solution) = SudokuGenerator.instance.generate(Difficulty.Breeze)
        val original = puzzle.map { it.toList() }
        viewModel.restoreFrom(
            GameState(
                difficulty = Difficulty.Breeze,
                originalGrid = original,
                solutionGrid = solution.map { it.toList() },
                userGrid = original.map { row -> row.map { CellData(it) } },
                wrongCells = emptySet(),
                mistakes = 0,
                score = 0,
                timeSeconds = 0,
                selectedCell = null,
                isNotesMode = true,
                scoredCells = emptySet()
            )
        )
        emptyCell = (0..8).flatMap { r -> (0..8).map { c -> r to c } }
            .first { (r, c) -> original[r][c] == 0 }
        viewModel.selectedCell.value = emptyCell
    }

    @After
    fun tearDown() {
        viewModel.pauseTimer()
        storeScope.cancel()
        storeFile.delete()
    }

    private fun notes() = viewModel.userGrid[emptyCell.first][emptyCell.second].notes

    @Test
    fun allNineCandidates_canBeNoted() {
        for (digit in 1..9) viewModel.enterNumber(digit, isLearningMode = false, freePlay = false)

        assertEquals((1..9).toSet(), notes())
        assertEquals(0, viewModel.userGrid[emptyCell.first][emptyCell.second].value)
    }

    @Test
    fun tappingANotedDigitAgain_removesOnlyThatNote() {
        for (digit in 1..9) viewModel.enterNumber(digit, isLearningMode = false, freePlay = false)
        viewModel.enterNumber(5, isLearningMode = false, freePlay = false)

        assertEquals((1..9).toSet() - 5, notes())
    }

    @Test
    fun nineNotes_surviveSaveAndRestore() {
        for (digit in 1..9) viewModel.enterNumber(digit, isLearningMode = false, freePlay = false)

        val restored = GameState.deserialize(viewModel.currentGameState().serialize())!!

        assertEquals((1..9).toSet(), restored.userGrid[emptyCell.first][emptyCell.second].notes)
    }
}
