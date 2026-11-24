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
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/**
 * The per-game hint limit is enforced by the ViewModel, not only by the disabled button.
 * Uses a throwaway DataStore so the app's real wallet is untouched, and never calls
 * initialize(), so the app's saved game is untouched too.
 */
@RunWith(AndroidJUnit4::class)
class SudokuGameViewModelHintTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var storeScope: CoroutineScope
    private lateinit var storeFile: File
    private lateinit var appPreferences: AppPreferences
    private lateinit var viewModel: SudokuGameViewModel

    @Before
    fun setUp() {
        storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        storeFile = File(context.cacheDir, "hint_test_${UUID.randomUUID()}.preferences_pb")
        appPreferences = AppPreferences(
            PreferenceDataStoreFactory.create(scope = storeScope) { storeFile }
        )
        viewModel = SudokuGameViewModel(
            context = context,
            appPreferences = appPreferences,
            mode = "new",
            difficulty = Difficulty.Breeze,
            streakDay = 0,
            useDoublePointsBooster = false
        )
        // The constructor no longer generates a puzzle (#16): load one directly.
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
                scoredCells = emptySet()
            )
        )
    }

    @After
    fun tearDown() {
        viewModel.pauseTimer()
        storeScope.cancel()
        storeFile.delete()
    }

    @Test
    fun fourthHint_returnsLimitReached_andLeavesWalletUnchanged() = runBlocking {
        appPreferences.addHints(10)
        val emptyCells = emptyCells()

        repeat(SudokuGameViewModel.MAX_HINTS_PER_GAME) { index ->
            viewModel.selectedCell.value = emptyCells[index]
            assertEquals(HintResult.APPLIED, viewModel.useHint(isLearningMode = false, freePlay = false))
        }
        assertEquals(7, appPreferences.hintWallet.first())

        viewModel.selectedCell.value = emptyCells[SudokuGameViewModel.MAX_HINTS_PER_GAME]
        assertEquals(
            HintResult.LIMIT_REACHED,
            viewModel.useHint(isLearningMode = false, freePlay = false)
        )
        assertEquals(7, appPreferences.hintWallet.first())
        assertEquals(SudokuGameViewModel.MAX_HINTS_PER_GAME, viewModel.hintsUsed.intValue)
    }

    @Test
    fun concurrentHintTaps_applyOnlyOneHint() = runBlocking {
        appPreferences.addHints(10)
        viewModel.selectedCell.value = emptyCells().first()

        val first = async { viewModel.processHint(isLearningMode = false, freePlay = false) }
        val second = async { viewModel.processHint(isLearningMode = false, freePlay = false) }

        assertEquals(
            setOf(HintResult.APPLIED, HintResult.IN_PROGRESS),
            setOf(first.await(), second.await())
        )
        assertEquals(9, appPreferences.hintWallet.first())
        assertEquals(1, viewModel.hintsUsed.intValue)
    }

    private fun emptyCells(): List<Pair<Int, Int>> {
        val grid = viewModel.originalGrid.value
        val cells = (0..8).flatMap { r -> (0..8).map { c -> r to c } }
            .filter { (r, c) -> grid[r][c] == 0 }
        check(cells.size > SudokuGameViewModel.MAX_HINTS_PER_GAME) { "puzzle needs more empty cells" }
        return cells
    }
}
