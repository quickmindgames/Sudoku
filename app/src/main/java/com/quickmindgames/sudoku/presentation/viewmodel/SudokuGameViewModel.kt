package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.BuildConfig
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.data.state.GameState
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.data.util.GameCompletionRecorder
import com.quickmindgames.sudoku.domain.generator.SudokuGenerator
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.isWin
import com.quickmindgames.sudoku.utils.recomputeWrongCells
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SudokuTimerState(
    val elapsedSeconds: Int = 0,
    val isRunning: Boolean = true
)

data class SudokuPreferencesState(
    val hideUsedNumbers: Boolean = false,
    val highlightRegion: Boolean = false,
    val highlightSameNumbers: Boolean = false,
    val freePlay: Boolean = false,
    val soundAndVibration: Boolean = true,
    val hintWallet: Int = 0
)

data class SudokuMoveResult(
    val isCorrect: Boolean,
    val gameOver: Boolean = false,
    val gameWon: Boolean = false,
    val learningHint: String? = null
)

enum class HintResult {
    APPLIED,
    EMPTY_WALLET,
    CELL_NOT_SELECTED,
    CELL_ALREADY_CORRECT,
    GAME_WON
}

class SudokuGameViewModel(
    context: Context,
    private val appPreferences: AppPreferences,
    private val mode: String,
    difficulty: Difficulty?,
    private val streakDay: Int,
    useDoublePointsBooster: Boolean
) : ViewModel() {
    private val appContext = context.applicationContext
    private val gameStateManager = GameStateManager.getInstance(appContext)
    private val streakStateManager = StreakStateManager.getInstance(appContext)
    private val totalScoreManager = TotalScoreManager.getInstance(appContext)

    private val generatedPuzzle =
        SudokuGenerator.instance.generate(difficulty ?: Difficulty.Breeze)
    val puzzleData = mutableStateOf(generatedPuzzle)
    val originalGrid: MutableState<List<List<Int>>> =
        mutableStateOf(generatedPuzzle.first.map { it.toList() })
    val solutionGrid = mutableStateOf(generatedPuzzle.second)
    val userGrid = mutableStateListOf<SnapshotStateList<CellData>>()
    val gameOver = mutableStateOf(false)
    val wrongCells = mutableStateOf(setOf<Pair<Int, Int>>())
    val shakeCells = mutableStateOf(setOf<Pair<Int, Int>>())
    val gameWon = mutableStateOf(false)
    val selectedCell = mutableStateOf<Pair<Int, Int>?>(null)
    val mistakes = mutableIntStateOf(0)
    val score = mutableIntStateOf(0)
    val hasDoublePointsBooster = mutableStateOf(useDoublePointsBooster)
    val currentDifficulty = mutableStateOf(difficulty ?: Difficulty.Breeze)
    val correctStreak = mutableIntStateOf(0)
    val lastCorrectTime = mutableIntStateOf(0)
    val scoredCells = Array(9) {
        mutableStateListOf(*BooleanArray(9) { false }.toTypedArray())
    }
    val isNotesMode = mutableStateOf(false)
    val hintsUsed = mutableIntStateOf(0)
    val learningHint = mutableStateOf<String?>(null)
    val isHintProcessing = mutableStateOf(false)
    val canUndo = mutableStateOf(false)
    private val undoHistory = mutableListOf<List<List<CellData>>>()

    private val _timerState = MutableStateFlow(SudokuTimerState())
    val timerState = _timerState.asStateFlow()
    private var timerJob: Job? = null
    private var initialized = false
    private var initializationStarted = false
    private var winPersisted = false

    private val gameplayPreferences = combine(
        appPreferences.hideUsedNumbers,
        appPreferences.highlightRegion,
        appPreferences.highlightSameNumbers,
        appPreferences.freePlay,
        appPreferences.soundAndVibration
    ) { hideUsedNumbers, highlightRegion, highlightSameNumbers, freePlay, soundAndVibration ->
        SudokuPreferencesState(
            hideUsedNumbers = hideUsedNumbers,
            highlightRegion = highlightRegion,
            highlightSameNumbers = highlightSameNumbers,
            freePlay = freePlay,
            soundAndVibration = soundAndVibration
        )
    }

    val preferences = combine(
        gameplayPreferences,
        appPreferences.hintWallet
    ) { gameplay, hintWallet ->
        gameplay.copy(hintWallet = hintWallet)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SudokuPreferencesState()
    )

    init {
        replaceUserGrid(originalGrid.value)
    }

    suspend fun initialize(): Boolean {
        if (initialized || initializationStarted) return false
        initializationStarted = true
        try {
            val shouldExit = when (mode) {
                "learn" -> {
                    currentDifficulty.value = Difficulty.Breeze
                    resetGameState(startTimer = true)
                    AnalyticsUtils.logLearningStarted(appContext)
                    false
                }

                "new" -> {
                    val savedDifficulty = gameStateManager.getSavedGameState().first()?.difficulty
                    resetGameState(startTimer = true)
                    gameStateManager.clearGameState()
                    AnalyticsUtils.logNewGameStarted(
                        appContext,
                        currentDifficulty.value.name,
                        savedDifficulty?.name
                    )
                    false
                }

                "resume" -> {
                    restoreRegularGame()
                    false
                }

                "streak" -> initializeStreakGame()
                else -> false
            }
            initialized = true
            return shouldExit
        } finally {
            initializationStarted = false
        }
    }

    private suspend fun restoreRegularGame() {
        val savedState = gameStateManager.getSavedGameState().first()
        if (savedState == null) {
            startTimer()
            return
        }
        savedState.let { state ->
            AnalyticsUtils.logResumeGame(appContext, state.difficulty.name)
            currentDifficulty.value = state.difficulty
            originalGrid.value = state.originalGrid
            solutionGrid.value = state.solutionGrid.map { it.toIntArray() }.toTypedArray()
            puzzleData.value = Pair(
                state.originalGrid.map { it.toIntArray() }.toTypedArray(),
                solutionGrid.value
            )
            restoreUserGrid(state.userGrid)
            wrongCells.value = state.wrongCells
            mistakes.intValue = state.mistakes
            score.intValue = state.score
            startTimer(state.timeSeconds)
            selectedCell.value = state.selectedCell
            isNotesMode.value = state.isNotesMode
            hintsUsed.intValue = state.hintsUsed
            hasDoublePointsBooster.value = state.hasDoublePointsBooster
            gameOver.value = false
            gameWon.value = false
        }
    }

    private suspend fun initializeStreakGame(): Boolean {
        val today = LocalDate.now().toString()
        val savedStreak = streakStateManager.getStreakState().first()
            ?: StreakState(0, null, null, null)
        val activeGame = savedStreak.activeGame
        if (activeGame != null && savedStreak.activeDate == today) {
            currentDifficulty.value = activeGame.difficulty
            originalGrid.value = activeGame.originalGrid
            solutionGrid.value = activeGame.solutionGrid.map { it.toIntArray() }.toTypedArray()
            restoreUserGrid(activeGame.userGrid)
            wrongCells.value = activeGame.wrongCells
            mistakes.intValue = activeGame.mistakes
            score.intValue = activeGame.score
            startTimer(activeGame.timeSeconds)
            selectedCell.value = activeGame.selectedCell
            hintsUsed.intValue = activeGame.hintsUsed
            gameOver.value = false
            gameWon.value = false
        } else if (savedStreak.lastCompletedDate == today && !BuildConfig.DEBUG) {
            return true
        } else {
            AnalyticsUtils.logStreakGameStarted(appContext)
            val (puzzle, solution) = SudokuGenerator.instance.generate(Difficulty.Breeze)
            puzzleData.value = Pair(puzzle, solution)
            originalGrid.value = puzzle.map { it.toList() }
            solutionGrid.value = solution
            currentDifficulty.value = Difficulty.Breeze
            resetGameState(startTimer = true)
            saveCurrentGame()
            streakStateManager.saveStreakState(
                savedStreak.copy(
                    activeDate = today,
                    activeGame = currentGameState()
                )
            )
        }
        return false
    }

    suspend fun saveCurrentGame() {
        if (!initialized || mode == "learn" || gameOver.value || gameWon.value) return
        val state = currentGameState()
        if (!state.hasStarted() && hintsUsed.intValue == 0 &&
            !hasDoublePointsBooster.value
        ) return
        if (mode == "streak") {
            val today = LocalDate.now().toString()
            val savedStreak = streakStateManager.getStreakState().first()
                ?: StreakState(0, null, null, null)
            streakStateManager.saveStreakState(
                savedStreak.copy(activeDate = today, activeGame = state)
            )
        } else {
            gameStateManager.saveGameState(state)
        }
    }

    fun currentGameState() = GameState(
        difficulty = currentDifficulty.value,
        originalGrid = originalGrid.value,
        solutionGrid = solutionGrid.value.map { it.toList() },
        userGrid = userGrid.map { it.toList() },
        wrongCells = wrongCells.value,
        mistakes = mistakes.intValue,
        score = score.intValue,
        timeSeconds = _timerState.value.elapsedSeconds,
        selectedCell = selectedCell.value,
        isNotesMode = isNotesMode.value,
        hintsUsed = hintsUsed.intValue,
        hasDoublePointsBooster = hasDoublePointsBooster.value
    )

    fun resetGameState(startTimer: Boolean) {
        replaceUserGrid(originalGrid.value)
        undoHistory.clear()
        canUndo.value = false
        wrongCells.value = emptySet()
        mistakes.intValue = 0
        score.intValue = 0
        startTimer(shouldCount = startTimer)
        gameOver.value = false
        gameWon.value = false
        selectedCell.value = null
        shakeCells.value = emptySet()
        learningHint.value = null
        correctStreak.intValue = 0
        lastCorrectTime.intValue = 0
        resetScoredCells()
        hintsUsed.intValue = 0
        winPersisted = false
    }

    fun createNextGame() {
        val (puzzle, solution) = SudokuGenerator.instance.generate(currentDifficulty.value)
        puzzleData.value = Pair(puzzle, solution)
        originalGrid.value = puzzle.map { it.toList() }
        solutionGrid.value = solution
        hasDoublePointsBooster.value = false
        resetGameState(startTimer = true)
    }

    suspend fun retryGame() {
        resetGameState(startTimer = true)
        if (mode != "streak") gameStateManager.clearGameState()
    }

    fun onPreferencesChanged(freePlay: Boolean) {
        if (freePlay && mode != "streak") {
            mistakes.intValue = 0
            score.intValue = 0
        }
    }

    fun clearShakeCells() {
        shakeCells.value = emptySet()
    }

    fun clearLearningHint() {
        learningHint.value = null
    }

    suspend fun processHint(isLearningMode: Boolean, freePlay: Boolean): HintResult {
        isHintProcessing.value = true
        return try {
            useHint(isLearningMode, freePlay)
        } finally {
            isHintProcessing.value = false
        }
    }

    fun pointsToAward(freePlay: Boolean): Int =
        if (hasDoublePointsBooster.value && !freePlay) {
            (score.intValue.toLong() * 2L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        } else {
            score.intValue
        }

    fun dismissGameOver() {
        gameOver.value = false
    }

    fun dismissGameWon() {
        gameWon.value = false
    }

    fun selectCell(row: Int, column: Int) {
        if (_timerState.value.isRunning) selectedCell.value = row to column
    }

    fun toggleNotes() {
        isNotesMode.value = !isNotesMode.value
        AnalyticsUtils.logNotesToggled(appContext, isNotesMode.value)
    }

    fun eraseSelectedCell() {
        selectedCell.value?.let { (row, column) ->
            if (originalGrid.value[row][column] == 0) {
                saveUndoSnapshot()
                if (userGrid[row][column].notes.isNotEmpty()) {
                    userGrid[row][column] =
                        userGrid[row][column].copy(notes = emptySet())
                } else {
                    userGrid[row][column] = userGrid[row][column].copy(value = 0)
                    wrongCells.value = wrongCells.value - (row to column)
                }
            }
        }
        AnalyticsUtils.logEraseUsed(appContext)
    }

    fun undoMove() {
        val previous = undoHistory.removeLastOrNull() ?: return
        for (row in 0..8) for (column in 0..8) {
            userGrid[row][column] = previous[row][column]
        }
        canUndo.value = undoHistory.isNotEmpty()
        wrongCells.value = recomputeWrongCells(userGrid, solutionGrid.value)
        AnalyticsUtils.logUndoUsed(appContext)
    }

    fun enterNumber(number: Int, isLearningMode: Boolean, freePlay: Boolean): SudokuMoveResult? {
        val cellPosition = selectedCell.value ?: return null
        val (row, column) = cellPosition
        if (originalGrid.value[row][column] != 0 || gameOver.value ||
            gameWon.value || !_timerState.value.isRunning
        ) return null

        val cell = userGrid[row][column]
        val solution = solutionGrid.value
        if (isNotesMode.value) {
            val isWrong = cell.value != 0 && cell.value != solution[row][column]
            if (cell.value == 0 || isWrong) {
                val notes = cell.notes.toMutableSet()
                if (number in notes) notes.remove(number)
                else if (notes.size < 4) notes.add(number)
                saveUndoSnapshot()
                userGrid[row][column] = cell.copy(
                    value = 0,
                    notes = notes
                )
            }
            return null
        }

        if (cell.value != 0 && cell.value == solution[row][column]) return null
        val isCorrect = number == solution[row][column]
        if (!isLearningMode || isCorrect) {
            saveUndoSnapshot()
            userGrid[row][column] = cell.copy(
                value = number,
                notes = emptySet()
            )
            updateNotesAfterValueChange(userGrid, row, column, number)
        } else {
            saveUndoSnapshot()
        }

        var learningExplanation: String? = null
        if (!isCorrect) {
            if (isLearningMode) {
                shakeCells.value = shakeCells.value + cellPosition
                learningExplanation = learningHintFor(row, column, number)
                learningHint.value = learningExplanation
            } else if (freePlay) {
                wrongCells.value = wrongCells.value + cellPosition
                shakeCells.value = shakeCells.value + cellPosition
            } else {
                mistakes.intValue++
                wrongCells.value = wrongCells.value + cellPosition
                shakeCells.value = shakeCells.value + cellPosition
                correctStreak.intValue = 0
                if (mistakes.intValue >= 3) {
                    gameOver.value = true
                    pauseTimer()
                    recordCompletion(isWon = false)
                }
            }
        } else {
            wrongCells.value = wrongCells.value - cellPosition
            if (!isLearningMode && !freePlay && !scoredCells[row][column]) {
                val basePoints = when (currentDifficulty.value) {
                    Difficulty.Breeze -> 1
                    Difficulty.Pulse -> 2
                    Difficulty.Rage -> 3
                    Difficulty.Elite -> 4
                }
                val secondsSinceLast = _timerState.value.elapsedSeconds - lastCorrectTime.intValue
                val speedMultiplier = when {
                    secondsSinceLast <= 5 -> 2.0f
                    secondsSinceLast <= 10 -> 1.5f
                    secondsSinceLast <= 20 -> 1.25f
                    else -> 1.0f
                }
                correctStreak.intValue++
                val streakMultiplier = when {
                    correctStreak.intValue >= 5 -> 2.0f
                    correctStreak.intValue >= 3 -> 1.5f
                    else -> 1.0f
                }
                val mistakePenalty = 1f - (mistakes.intValue * 0.10f)
                score.intValue += (basePoints * speedMultiplier * streakMultiplier * mistakePenalty)
                    .toInt()
                    .coerceAtLeast(1)
                scoredCells[row][column] = true
                lastCorrectTime.intValue = _timerState.value.elapsedSeconds
            }
        }

        val didWin = isWin(userGrid, solution)
        if (didWin) {
            gameWon.value = true
            pauseTimer()
            winPersisted = false
            if (!isLearningMode) recordCompletion(isWon = true)
        }
        return SudokuMoveResult(
            isCorrect = isCorrect,
            gameOver = gameOver.value,
            gameWon = didWin,
            learningHint = learningExplanation
        )
    }

    suspend fun useHint(isLearningMode: Boolean, freePlay: Boolean): HintResult {
        if (isLearningMode || gameOver.value || gameWon.value || !_timerState.value.isRunning) {
            return HintResult.CELL_ALREADY_CORRECT
        }
        val position = selectedCell.value ?: return HintResult.CELL_NOT_SELECTED
        val (row, column) = position
        val cell = userGrid[row][column]
        if (cell.value != 0 && cell.value == solutionGrid.value[row][column]) {
            return HintResult.CELL_ALREADY_CORRECT
        }
        if (!appPreferences.consumeHint()) return HintResult.EMPTY_WALLET
        saveUndoSnapshot()
        val value = solutionGrid.value[row][column]
        userGrid[row][column] = cell.copy(value = value, notes = emptySet())
        updateNotesAfterValueChange(userGrid, row, column, value)
        hintsUsed.intValue++
        AnalyticsUtils.logHintUsed(appContext, currentDifficulty.value.name)

        if (isWin(userGrid, solutionGrid.value)) {
            gameWon.value = true
            pauseTimer()
            recordCompletion(isWon = true, useHintScore = true)
            return HintResult.GAME_WON
        }
        return HintResult.APPLIED
    }

    suspend fun persistWin(pointsToAward: Int, freePlay: Boolean) {
        if (winPersisted || mode == "learn") return
        winPersisted = true
        if (!freePlay) {
            if (hasDoublePointsBooster.value) appPreferences.consumeDoublePointsBooster()
            totalScoreManager.addScore(pointsToAward)
        }
        if (mode == "streak") {
            val today = LocalDate.now().toString()
            val savedStreak = streakStateManager.getStreakState().first()
                ?: StreakState(0, null, null, null)
            if (streakDay > savedStreak.streakCount) {
                streakStateManager.saveStreakState(
                    savedStreak.copy(
                        streakCount = savedStreak.streakCount + 1,
                        lastCompletedDate = today,
                        activeDate = null,
                        activeGame = null
                    )
                )
            } else {
                streakStateManager.saveStreakState(
                    savedStreak.copy(activeDate = null, activeGame = null)
                )
            }
            AnalyticsUtils.logStreakGameCompleted(appContext, streakDay)
            streakStateManager.setReminderShown(false)
        } else {
            gameStateManager.clearGameState()
        }
    }

    fun startTimer(elapsedSeconds: Int = 0, shouldCount: Boolean = true) {
        timerJob?.cancel()
        _timerState.value = SudokuTimerState(
            elapsedSeconds = elapsedSeconds,
            isRunning = shouldCount
        )
        if (shouldCount && mode != "learn") runTimer()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resumeTimer(shouldCount: Boolean = true) {
        if (_timerState.value.isRunning) return
        _timerState.value = _timerState.value.copy(isRunning = true)
        if (shouldCount && mode != "learn") runTimer()
    }

    fun toggleTimer(shouldCount: Boolean = true) {
        if (_timerState.value.isRunning) pauseTimer() else resumeTimer(shouldCount)
    }

    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning) {
                delay(1_000)
                if (_timerState.value.isRunning) {
                    _timerState.value = _timerState.value.copy(
                        elapsedSeconds = _timerState.value.elapsedSeconds + 1
                    )
                }
            }
        }
    }

    private fun recordCompletion(isWon: Boolean, useHintScore: Boolean = false) {
        val elapsed = _timerState.value.elapsedSeconds
        if (mode == "streak") {
            GameCompletionRecorder.recordStreakGameCompletion(
                context = appContext,
                scope = viewModelScope,
                streakDay = streakDay,
                isWon = isWon,
                score = score.intValue,
                timeSeconds = elapsed
            )
            GameCompletionRecorder.recordGameCompletion(
                context = appContext,
                scope = viewModelScope,
                difficulty = "streak",
                isWon = isWon,
                mistakes = mistakes.intValue,
                score = score.intValue,
                timeSeconds = elapsed,
                mode = "regular"
            )
            if (!isWon) {
                viewModelScope.launch {
                    val saved = streakStateManager.getStreakState().first()
                        ?: StreakState(0, null, null, null)
                    streakStateManager.saveStreakState(
                        saved.copy(activeDate = null, activeGame = null)
                    )
                }
            }
        } else {
            val resultScore = if (isWon && !useHintScore &&
                hasDoublePointsBooster.value && preferences.value.freePlay.not()
            ) {
                (score.intValue.toLong() * 2L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            } else score.intValue
            GameCompletionRecorder.recordGameCompletion(
                context = appContext,
                scope = viewModelScope,
                difficulty = currentDifficulty.value.name,
                isWon = isWon,
                mistakes = mistakes.intValue,
                score = resultScore,
                timeSeconds = elapsed,
                mode = "regular"
            )
        }
        if (!isWon) viewModelScope.launch { gameStateManager.clearGameState() }
    }

    private fun replaceUserGrid(grid: List<List<Int>>) {
        userGrid.clear()
        grid.forEach { row ->
            userGrid.add(mutableStateListOf(*row.map(::CellData).toTypedArray()))
        }
    }

    private fun saveUndoSnapshot() {
        undoHistory.add(userGrid.map { row -> row.toList() })
        canUndo.value = true
    }

    private fun restoreUserGrid(grid: List<List<CellData>>) {
        userGrid.clear()
        grid.forEach { row ->
            userGrid.add(mutableStateListOf(*row.toTypedArray()))
        }
    }

    private fun resetScoredCells() {
        for (row in 0..8) for (column in 0..8) scoredCells[row][column] = false
    }

    private fun learningHintFor(row: Int, column: Int, number: Int): String {
        val rowConflict = (0 until 9).any { col ->
            col != column && userGrid[row][col].value == number
        }
        val columnConflict = (0 until 9).any { otherRow ->
            otherRow != row && userGrid[otherRow][column].value == number
        }
        val boxRow = row / 3 * 3
        val boxColumn = column / 3 * 3
        val boxConflict = (boxRow until boxRow + 3).any { otherRow ->
            (boxColumn until boxColumn + 3).any { otherColumn ->
                (otherRow != row || otherColumn != column) &&
                    userGrid[otherRow][otherColumn].value == number
            }
        }
        return when {
            rowConflict -> "Number already exists in this row"
            columnConflict -> "Number already exists in this column"
            boxConflict -> "Number already exists in this 3×3 box"
            else -> "That's not the correct number for this cell"
        }
    }

    private fun updateNotesAfterValueChange(
        grid: SnapshotStateList<SnapshotStateList<CellData>>,
        row: Int,
        column: Int,
        value: Int
    ) {
        for (index in 0..8) {
            if (grid[row][index].value == 0) {
                grid[row][index] = grid[row][index].copy(
                    notes = grid[row][index].notes - value
                )
            }
            if (grid[index][column].value == 0) {
                grid[index][column] = grid[index][column].copy(
                    notes = grid[index][column].notes - value
                )
            }
        }
        val boxRow = row / 3 * 3
        val boxColumn = column / 3 * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxColumn until boxColumn + 3) {
                if (grid[r][c].value == 0) {
                    grid[r][c] = grid[r][c].copy(notes = grid[r][c].notes - value)
                }
            }
        }
    }

    companion object {
        fun factory(
            context: Context,
            appPreferences: AppPreferences,
            mode: String,
            difficulty: Difficulty?,
            streakDay: Int,
            useDoublePointsBooster: Boolean
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SudokuGameViewModel(
                    context = context,
                    appPreferences = appPreferences,
                    mode = mode,
                    difficulty = difficulty,
                    streakDay = streakDay,
                    useDoublePointsBooster = useDoublePointsBooster
                )
            }
        }
    }
}
