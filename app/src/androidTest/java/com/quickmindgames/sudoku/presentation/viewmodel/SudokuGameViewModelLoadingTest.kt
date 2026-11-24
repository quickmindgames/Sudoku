package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import android.os.Looper
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.domain.model.Difficulty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/**
 * Issue #16: the ViewModel constructor must not generate a puzzle (it runs on the main
 * thread), and a puzzle is generated on demand, off the main thread, behind isLoading.
 * Uses a throwaway DataStore and never calls initialize(), so app data is untouched.
 */
@RunWith(AndroidJUnit4::class)
class SudokuGameViewModelLoadingTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var storeScope: CoroutineScope
    private lateinit var storeFile: File
    private lateinit var viewModel: SudokuGameViewModel

    @Before
    fun setUp() {
        storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        storeFile = File(context.cacheDir, "loading_test_${UUID.randomUUID()}.preferences_pb")
        val appPreferences = AppPreferences(
            PreferenceDataStoreFactory.create(scope = storeScope) { storeFile }
        )
        // Built on the main thread, like the real screen does.
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            viewModel = SudokuGameViewModel(
                context = context,
                appPreferences = appPreferences,
                mode = "new",
                difficulty = Difficulty.Breeze,
                streakDay = 0,
                useDoublePointsBooster = false
            )
        }
    }

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { viewModel.pauseTimer() }
        storeScope.cancel()
        storeFile.delete()
    }

    @Test
    fun constructor_doesNotGenerate_boardIsEmptyAndLoading() {
        assertTrue(viewModel.isLoading.value)
        assertTrue(viewModel.originalGrid.value.all { row -> row.all { it == 0 } })
    }

    @Test
    fun inputWhileLoading_isIgnored() {
        viewModel.selectedCell.value = 0 to 0
        assertNull(viewModel.enterNumber(5, isLearningMode = false, freePlay = false))
        assertEquals(0, viewModel.userGrid[0][0].value)
    }

    @Test
    fun createNextGame_fromMainThread_producesValidPuzzle() = runBlocking {
        withContext(Dispatchers.Main) {
            assertTrue(Looper.myLooper() == Looper.getMainLooper())
            viewModel.createNextGame()
        }

        assertFalse(viewModel.isLoading.value)
        val givens = viewModel.originalGrid.value.sumOf { row -> row.count { it != 0 } }
        assertTrue("puzzle should have givens, had $givens", givens in 17..80)
        val solution = viewModel.solutionGrid.value
        for (i in 0..8) {
            assertEquals((1..9).toSet(), solution[i].toSet()) // every row 1..9
            assertEquals((1..9).toSet(), (0..8).map { solution[it][i] }.toSet()) // every column
        }
        // Every given matches the solution.
        for (r in 0..8) for (c in 0..8) {
            val given = viewModel.originalGrid.value[r][c]
            if (given != 0) assertEquals(solution[r][c], given)
        }
    }
}
