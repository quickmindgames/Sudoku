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
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/**
 * Resuming a game (regular or streak; both use restoreFrom) must not allow double scoring
 * and must keep notes mode. Uses a throwaway DataStore and never calls initialize(), so the
 * app's real saved game and wallet are untouched.
 */
@RunWith(AndroidJUnit4::class)
class SudokuGameViewModelResumeTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var storeScope: CoroutineScope
    private lateinit var storeFile: File
    private lateinit var appPreferences: AppPreferences
    private lateinit var viewModel: SudokuGameViewModel

    @Before
    fun setUp() {
        storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        storeFile = File(context.cacheDir, "resume_test_${UUID.randomUUID()}.preferences_pb")
        appPreferences = AppPreferences(
            PreferenceDataStoreFactory.create(scope = storeScope) { storeFile }
        )
        viewModel = SudokuGameViewModel(
            context = context,
            appPreferences = appPreferences,
            mode = "resume",
            difficulty = Difficulty.Breeze,
            streakDay = 0,
            useDoublePointsBooster = false
        )
    }

    @After
    fun tearDown() {
        viewModel.pauseTimer()
        storeScope.cancel()
        storeFile.delete()
    }

    @Test
    fun oldSave_withoutScoredCells_doesNotRescoreErasedCorrectCell() {
        val (cell, state) = savedStateWithOneCorrectCell(scoredCells = null, notesMode = false)
        viewModel.restoreFrom(state)

        viewModel.selectedCell.value = cell
        viewModel.eraseSelectedCell()
        viewModel.enterNumber(solutionAt(cell), isLearningMode = false, freePlay = false)

        assertEquals(5, viewModel.score.intValue)
    }

    @Test
    fun newSave_scoredCellsRoundTrip_doesNotRescore() {
        val (cell, state) = savedStateWithOneCorrectCell(scoredCells = null, notesMode = false)
        viewModel.restoreFrom(state)
        // Save and restore again through the real serialization.
        val reloaded = GameState.deserialize(viewModel.currentGameState().serialize())!!
        assertTrue(reloaded.scoredCells!!.contains(cell.first * 9 + cell.second))
        viewModel.restoreFrom(reloaded)

        viewModel.selectedCell.value = cell
        viewModel.eraseSelectedCell()
        viewModel.enterNumber(solutionAt(cell), isLearningMode = false, freePlay = false)

        assertEquals(5, viewModel.score.intValue)
    }

    @Test
    fun hintedCell_erasedAndReentered_earnsNoPoints() = runBlocking {
        appPreferences.addHints(3)
        val (_, state) = savedStateWithOneCorrectCell(scoredCells = emptySet(), notesMode = false)
        viewModel.restoreFrom(state)
        val target = emptyCells(state).first()

        viewModel.selectedCell.value = target
        assertEquals(HintResult.APPLIED, viewModel.useHint(isLearningMode = false, freePlay = false))
        viewModel.eraseSelectedCell()
        viewModel.enterNumber(solutionAt(target), isLearningMode = false, freePlay = false)

        assertEquals(5, viewModel.score.intValue)
    }

    @Test
    fun resume_restoresNotesModeAndResetsUndo() {
        val (_, state) = savedStateWithOneCorrectCell(scoredCells = emptySet(), notesMode = true)

        viewModel.restoreFrom(state)

        assertTrue(viewModel.isNotesMode.value)
        assertFalse(viewModel.canUndo.value)
        assertEquals(1, viewModel.hintsUsed.intValue)
    }

    /** A save with score 5 in which one empty cell has been filled correctly by the player. */
    private fun savedStateWithOneCorrectCell(
        scoredCells: Set<Int>?,
        notesMode: Boolean
    ): Pair<Pair<Int, Int>, GameState> {
        // The constructor no longer generates a puzzle (#16): generate one here.
        val (puzzle, solved) = SudokuGenerator.instance.generate(Difficulty.Breeze)
        val original = puzzle.map { it.toList() }
        val solution = solved.map { it.toList() }
        val cell = (0..8).flatMap { r -> (0..8).map { c -> r to c } }
            .first { (r, c) -> original[r][c] == 0 }
        val userGrid = original.mapIndexed { r, row ->
            row.mapIndexed { c, v ->
                CellData(if (r to c == cell) solution[r][c] else v)
            }
        }
        return cell to GameState(
            difficulty = Difficulty.Breeze,
            originalGrid = original,
            solutionGrid = solution,
            userGrid = userGrid,
            wrongCells = emptySet(),
            mistakes = 0,
            score = 5,
            timeSeconds = 30,
            selectedCell = null,
            isNotesMode = notesMode,
            hintsUsed = 1,
            hasDoublePointsBooster = false,
            scoredCells = scoredCells
        )
    }

    private fun emptyCells(state: GameState) =
        (0..8).flatMap { r -> (0..8).map { c -> r to c } }
            .filter { (r, c) -> state.userGrid[r][c].value == 0 }

    private fun solutionAt(cell: Pair<Int, Int>) =
        viewModel.solutionGrid.value[cell.first][cell.second]
}
