package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.LearningCompleteDialog
import com.quickmindgames.sudoku.component.NumberPad
import com.quickmindgames.sudoku.component.SudokuGridLevel
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.data.state.GameState
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.data.util.GameCompletionRecorder
import com.quickmindgames.sudoku.domain.generator.SudokuGenerator
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.domain.util.clearUndoRedoHistory
import com.quickmindgames.sudoku.domain.util.saveUndoState
import com.quickmindgames.sudoku.domain.util.undo
import com.quickmindgames.sudoku.domain.util.undoStack
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.RemoteConfigManager
import com.quickmindgames.sudoku.utils.formatTime
import com.quickmindgames.sudoku.utils.isWin
import com.quickmindgames.sudoku.utils.ordinalSuffix
import com.quickmindgames.sudoku.utils.recomputeWrongCells
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    mode: String,
    difficulty: Difficulty?,
    streakDay: Int = 0,
    onExit: () -> Unit,
    onStartNormalGame: () -> Unit = onExit
) {
    val context = LocalContext.current
    val gameStateManager = remember { GameStateManager.getInstance(context) }
    val streakStateManager = remember { StreakStateManager.getInstance(context) }
    val totalScoreManager = remember { TotalScoreManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    // Generate puzzle and solution
    var puzzleData by remember(mode, difficulty) {
        val diff = difficulty ?: Difficulty.Breeze
        val (puzzle, solution) = SudokuGenerator.instance.generate(diff)
        mutableStateOf(Pair(puzzle, solution))
    }

    var originalGrid by remember(mode, difficulty) {
        mutableStateOf(puzzleData.first.map { it.toList() })
    }

    var solutionGrid by remember(mode, difficulty) {
        mutableStateOf(puzzleData.second)
    }

    // Don't key userGrid by originalGrid to avoid recreation during resume
    val userGrid = remember {
        mutableStateListOf(*originalGrid.map { row ->
            row.map { CellData(it) }.toMutableStateList()
        }.toTypedArray())
    }
    var gameOver by remember { mutableStateOf(false) }
    var wrongCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var shakeCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var gameWon by remember { mutableStateOf(false) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var mistakes by remember { mutableIntStateOf(0) }
    var timeSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }
    var timerKey by remember { mutableIntStateOf(0) }
    var currentDifficulty by remember { mutableStateOf(difficulty ?: Difficulty.Breeze) }
    var correctStreak by remember { mutableIntStateOf(0) }   // consecutive correct cells
    var lastCorrectTime by remember { mutableIntStateOf(0) } // timeSeconds when last correct cell placed
    val scoredCells = remember {
        Array(9) { mutableStateListOf(*BooleanArray(9) { false }.toTypedArray()) }
    }

    var isNotesMode by remember { mutableStateOf(false) }
    var availableHints by remember(mode, difficulty) { mutableIntStateOf(1) }

    val isLearningMode = mode == "learn"
    var learningHint by remember { mutableStateOf<String?>(null) }
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(shakeCells) {
        delay(300.milliseconds)
        shakeCells = emptySet()
    }

    LaunchedEffect(learningHint) {
        if (learningHint != null) {
            delay(2500.milliseconds)
            learningHint = null
        }
    }

    LaunchedEffect(timerKey) {
        while (true) {
            // Only advance the timer for normal modes — do not increment during learning/practice
            if (isRunning && !isLearningMode) {
                delay(1000.milliseconds)
                if (isRunning) timeSeconds++
            } else {
                delay(100.milliseconds) // poll until resumed or learning mode ends
            }
        }
    }

    LaunchedEffect(mode, difficulty) {

        //region Learn Mode
        if (mode == "learn") {
            val diff = Difficulty.Breeze
            currentDifficulty = diff

            userGrid.clear()
            originalGrid.forEach { row ->
                userGrid.add(row.map { CellData(it) }.toMutableStateList())
            }

            clearUndoRedoHistory()
            wrongCells = emptySet()
            mistakes = 0
            score = 0
            timeSeconds = 0
            gameOver = false
            gameWon = false
            selectedCell = null
            shakeCells = emptySet()
            isRunning = true
            timerKey++
            correctStreak = 0
            lastCorrectTime = 0
            for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false
            availableHints = 0 // hints disabled
        }
        //endregion

        //region New Game
        if (mode == "new") {
            // Generate new puzzle
            val diff = difficulty ?: Difficulty.Breeze
            //val (puzzle, solution) = SudokuGenerator.instance.generate(diff)
            //puzzleData = Pair(puzzle, solution)
            //originalGrid = puzzle.map { it.toList() }
            //solutionGrid = solution
            currentDifficulty = diff

            // Properly reset user grid
            userGrid.clear()
            originalGrid.forEach { row ->
                userGrid.add(row.map { CellData(it) }.toMutableStateList())
            }

            clearUndoRedoHistory()

            wrongCells = emptySet()
            mistakes = 0
            score = 0
            timeSeconds = 0
            gameOver = false
            gameWon = false
            selectedCell = null
            shakeCells = emptySet()
            isRunning = true
            timerKey++
            correctStreak = 0
            lastCorrectTime = 0
            for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false

            availableHints = RemoteConfigManager.getHintsForDifficulty(diff.name.lowercase())

            // Log new game started event
            val savedGameDifficulty =
                gameStateManager.getSavedGameState().first()?.difficulty // if any
            AnalyticsUtils.logNewGameStarted(context, diff.name, savedGameDifficulty?.name)
        }
        //endregion

        //region Resume Game
        if (mode == "resume") {
            // Restore from DataStore
            gameStateManager.getSavedGameState().collect { savedState ->
                savedState?.let { state ->
                    AnalyticsUtils.logResumeGame(context, state.difficulty.name)
                    currentDifficulty = state.difficulty
                    originalGrid = state.originalGrid
                    solutionGrid = state.solutionGrid.map { it.toIntArray() }.toTypedArray()
                    puzzleData = Pair(
                        originalGrid.map { it.toIntArray() }.toTypedArray(),
                        solutionGrid
                    )
                    userGrid.clear()
                    state.userGrid.forEach { row ->
                        userGrid.add(row.toMutableStateList())
                    }
                    wrongCells = state.wrongCells
                    mistakes = state.mistakes
                    score = state.score
                    timeSeconds = state.timeSeconds
                    selectedCell = state.selectedCell
                    isNotesMode = state.isNotesMode // Restore notes mode
                    availableHints = state.availableHints
                    gameOver = false
                    gameWon = false
                    isRunning = true
                    timerKey++ // Restart timer from saved time
                }
            }
        }
        //endregion

        //region Streak Mode
        if (mode == "streak") {
            val today = LocalDate.now().toString()
            val savedStreak = streakStateManager.getStreakState().first()
                ?: StreakState(0, null, null, null)

            val activeGame = savedStreak.activeGame
            if (activeGame != null && savedStreak.activeDate == today) {
                // Resume active streak game
                currentDifficulty = activeGame.difficulty
                originalGrid = activeGame.originalGrid
                solutionGrid = activeGame.solutionGrid.map { it.toIntArray() }.toTypedArray()

                userGrid.clear()
                activeGame.userGrid.forEach { row ->
                    userGrid.add(row.toMutableStateList())
                }

                wrongCells = activeGame.wrongCells
                mistakes = activeGame.mistakes
                score = activeGame.score
                timeSeconds = activeGame.timeSeconds
                selectedCell = activeGame.selectedCell
                gameOver = false
                gameWon = false
                isRunning = true
                timerKey++
            } else if (savedStreak.lastCompletedDate == today) {
                onExit()
            } else {
                // Start new streak game
                AnalyticsUtils.logStreakGameStarted(context)
                val (puzzle, solution) = SudokuGenerator.instance.generate(Difficulty.Breeze)
                puzzleData = Pair(puzzle, solution)
                originalGrid = puzzle.map { it.toList() }
                solutionGrid = solution
                currentDifficulty = Difficulty.Breeze

                userGrid.clear()
                originalGrid.forEach { row ->
                    userGrid.add(row.map { CellData(it) }.toMutableStateList())
                }

                clearUndoRedoHistory()

                wrongCells = emptySet()
                mistakes = 0
                score = 0
                timeSeconds = 0
                gameOver = false
                gameWon = false
                selectedCell = null
                shakeCells = emptySet()
                isRunning = true
                timerKey++
                correctStreak = 0
                lastCorrectTime = 0
                for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false

                // Save initial streak state
                val newGame = GameState(
                    difficulty = currentDifficulty,
                    originalGrid = originalGrid,
                    solutionGrid = solutionGrid.map { it.toList() },
                    userGrid = userGrid.map { it.toList() },
                    wrongCells = wrongCells,
                    mistakes = mistakes,
                    score = score,
                    timeSeconds = timeSeconds,
                    selectedCell = selectedCell
                )

                streakStateManager.saveStreakState(
                    savedStreak.copy(
                        activeDate = today,
                        activeGame = newGame
                    )
                )
            }
        }
        //endregion
    }

    // Clear saved game when starting a new game
    LaunchedEffect(mode) {
        if (mode == "new") {
            gameStateManager.clearGameState()
        }
    }

    // Auto-save game state whenever important state changes
    LaunchedEffect(
        userGrid.hashCode(),
        mistakes,
        score,
        timeSeconds,
        selectedCell,
        isNotesMode,
        availableHints
    ) {
        // Only save if game has started (timer ticked at least once) and is not over/won
        if (!gameOver && !gameWon && !isLearningMode) {
            val currentState = GameState(
                difficulty = currentDifficulty,
                originalGrid = originalGrid,
                solutionGrid = solutionGrid.map { it.toList() },
                userGrid = userGrid.map { it.toList() },
                wrongCells = wrongCells,
                mistakes = mistakes,
                score = score,
                timeSeconds = timeSeconds,
                selectedCell = selectedCell,
                isNotesMode = isNotesMode, // Save notes mode
                availableHints = availableHints,
            )

            // Save as soon as timer has started (timeSeconds > 0)
            if (currentState.hasStarted()) {
                if (mode == "streak") {
                    val today = LocalDate.now().toString()
                    val savedStreak = streakStateManager.getStreakState().first()
                        ?: StreakState(0, null, null, null)

                    streakStateManager.saveStreakState(
                        savedStreak.copy(
                            activeDate = today,
                            activeGame = currentState
                        )
                    )
                } else {
                    gameStateManager.saveGameState(currentState)
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle observer for background/foreground events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    // Pause the timer and auto-save (already handled by LaunchedEffect)
                    isRunning = false
                }
                // ON_RESUME/ON_START: do nothing, keep paused until user resumes
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.SUDOKU_GAME,
            AnalyticsConstants.SUDOKU_SCREEN
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(
                top = 12.dp
            )
            .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 32.dp), // Minimized margins
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    bottom = 12.dp, start = 12.dp, end = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onExit() },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = if (isLearningMode) stringResource(R.string.lbl_practice_mode)
                    else if (mode == "streak" && streakDay > 0) "${ordinalSuffix(streakDay)} Streak"
                    else if (mode == "streak") "Streak"
                    else currentDifficulty.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }

            Spacer(Modifier.width(8.dp))

            // Scorecard
            if (!isLearningMode) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 16.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$score",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer
                        )
                    }
                }
            }
        }

        if (!isLearningMode) {
            // Mistakes (left) + Play/Pause Icon + Timer (right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
            ) {
                // Mistakes anchored to the left
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mistakes: ",
                        fontSize = 14.sp,
                        color = colors.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    repeat(3) { index ->
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = if (index < mistakes)
                                        colors.errorContainer
                                    else
                                        colors.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (index < mistakes)
                                    colors.error
                                else
                                    colors.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Play/Pause icon and Timer anchored to the right
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(timeSeconds),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        onClick = { isRunning = !isRunning },
                        enabled = !gameOver && !gameWon,
                        modifier = Modifier
                            .background(
                                color = colors.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Resume",
                            modifier = Modifier.size(18.dp),
                            tint = colors.onPrimaryContainer
                        )
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            // Sudoku board — always composed but visually hidden when paused
            SudokuGridLevel(
                originalGrid,
                userGrid,
                wrongCells,
                shakeCells,
                selectedCell,
                onCellClick = { r, c ->
                    if (isRunning) selectedCell = r to c
                }
            )

            // Pause overlay
            if (!isRunning && !gameOver && !gameWon) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = colors.surfaceContainerLow.copy(alpha = 0.98f),
                        )
                        .clickable { isRunning = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    color = colors.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = colors.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Game Paused",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tap to resume",
                            fontSize = 14.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        NumberPad(
            grid = userGrid, enabled = isRunning && !gameOver && !gameWon
        ) { number ->
            selectedCell?.let { (r, c) ->
                if (originalGrid[r][c] != 0 || gameOver || gameWon || !isRunning) return@NumberPad

                if (isNotesMode) {
                    val cell = userGrid[r][c]
                    val isWrong = cell.value != 0 && cell.value != solutionGrid[r][c]
                    val isEditable = cell.value == 0 || isWrong
                    if (isEditable) {
                        val notes = cell.notes.toMutableSet()
                        if (notes.contains(number)) {
                            notes.remove(number)
                        } else if (notes.size < 4) {
                            notes.add(number)
                        }
                        saveUndoState(userGrid)
                        // If cell had a value, clear it so number pad updates
                        if (cell.value != 0) {
                            userGrid[r][c] = cell.copy(value = 0, notes = notes)
                        } else {
                            userGrid[r][c] = cell.copy(notes = notes)
                        }
                    }
                } else {
                    // Do nothing if cell already has the correct number
                    val cellCurrentValue = userGrid[r][c].value
                    if (cellCurrentValue != 0 && cellCurrentValue == solutionGrid[r][c]) {
                        return@NumberPad  // Cell is locked (already correct)
                    }
                    val isCorrect = number == solutionGrid[r][c]

                    if (!isLearningMode || isCorrect) {
                        // If notes exist, replace notes with value
                        if (userGrid[r][c].notes.isNotEmpty()) {
                            saveUndoState(userGrid)
                            userGrid[r][c] = userGrid[r][c].copy(value = number, notes = emptySet())
                            updateNotesAfterValueChange(userGrid, r, c, number)
                        } else {
                            saveUndoState(userGrid)
                            // Fill cell value and clear notes
                            userGrid[r][c] = userGrid[r][c].copy(value = number)
                            updateNotesAfterValueChange(userGrid, r, c, number)
                        }
                    } else {
                        // In learning mode and number is wrong: don't place it, just save undo state for sound
                        saveUndoState(userGrid)
                    }

                    if (!isCorrect) {
                        if (isLearningMode) {
                            // ── Learning mode: show explanation, don't count mistake ──
                            //wrongCells = wrongCells + (r to c)
                            shakeCells = shakeCells + (r to c)

                            // Determine WHY the number is wrong
                            val rowConflict = (0 until 9).any { col ->
                                col != c && userGrid[r][col].value == number
                            }
                            val colConflict = (0 until 9).any { row ->
                                row != r && userGrid[row][c].value == number
                            }
                            val boxRowStart = (r / 3) * 3
                            val boxColStart = (c / 3) * 3
                            val boxConflict = (boxRowStart until boxRowStart + 3).any { br ->
                                (boxColStart until boxColStart + 3).any { bc ->
                                    (br != r || bc != c) && userGrid[br][bc].value == number
                                }
                            }

                            learningHint = when {
                                rowConflict -> "Number already exists in this row"
                                colConflict -> "Number already exists in this column"
                                boxConflict -> "Number already exists in this 3×3 box"
                                else -> "That's not the correct number for this cell"
                            }
                            // Do NOT increment mistakes
                            // Do NOT trigger gameOver
                        } else {
                            mistakes++
                            wrongCells = wrongCells + (r to c)
                            shakeCells = shakeCells + (r to c)
                            // Break correct streak on mistake
                            correctStreak = 0

                            if (mistakes >= 3) {
                                gameOver = true
                                isRunning = false
                                coroutineScope.launch {
                                    gameStateManager.clearGameState()
                                    if (mode == "streak") {
                                        GameCompletionRecorder.recordStreakGameCompletion(
                                            context = context,
                                            scope = this,
                                            streakDay = streakDay,
                                            isWon = false,
                                            score = score,
                                            timeSeconds = timeSeconds
                                        )
                                        // Also record as a regular game for overall stats
                                        GameCompletionRecorder.recordGameCompletion(
                                            context = context,
                                            scope = this,
                                            difficulty = "streak",
                                            isWon = false,
                                            mistakes = mistakes,
                                            score = score,
                                            timeSeconds = timeSeconds,
                                            mode = "regular"
                                        )
                                        // Clear streak resume state for today (reset activeDate and activeGame)
                                        val savedStreak =
                                            streakStateManager.getStreakState().first()
                                                ?: StreakState(0, null, null, null)
                                        streakStateManager.saveStreakState(
                                            savedStreak.copy(activeDate = null, activeGame = null)
                                        )
                                    } else {
                                        GameCompletionRecorder.recordGameCompletion(
                                            context = context,
                                            scope = this,
                                            difficulty = currentDifficulty.name,
                                            isWon = false,
                                            mistakes = mistakes,
                                            score = score,
                                            timeSeconds = timeSeconds,
                                            mode = "regular"
                                        )
                                    }
                                }
                            }
                        }

                    } else {
                        wrongCells = wrongCells - (r to c)

                        if (!isLearningMode) {
                            if (!scoredCells[r][c]) {
                                // 1. Base points by difficulty
                                val basePoints = when (currentDifficulty) {
                                    Difficulty.Breeze -> 1
                                    Difficulty.Pulse -> 2
                                    Difficulty.Rage -> 3
                                    Difficulty.Elite -> 4
                                }

                                // 2. Speed bonus — seconds since last correct cell
                                val secondsSinceLast = timeSeconds - lastCorrectTime
                                val speedMultiplier = when {
                                    secondsSinceLast <= 5 -> 2.0f   // Lightning fast  → ×2
                                    secondsSinceLast <= 10 -> 1.5f   // Fast            → ×1.5
                                    secondsSinceLast <= 20 -> 1.25f  // Good pace       → ×1.25
                                    else -> 1.0f   // No bonus
                                }

                                // 3. Streak multiplier — consecutive correct cells
                                correctStreak++
                                val streakMultiplier = when {
                                    correctStreak >= 5 -> 2.0f   // On fire!  → ×2
                                    correctStreak >= 3 -> 1.5f   // Streak    → ×1.5
                                    else -> 1.0f
                                }

                                // 4. Mistake penalty — each mistake reduces points by 10%
                                val mistakePenalty = 1f - (mistakes * 0.10f)

                                val earned =
                                    (basePoints * speedMultiplier * streakMultiplier * mistakePenalty)
                                        .toInt()
                                        .coerceAtLeast(1)

                                score += earned
                                scoredCells[r][c] = true
                                lastCorrectTime = timeSeconds
                            }
                        }
                    }

                    if (isWin(userGrid, solutionGrid)) {
                        gameWon = true
                        isRunning = false

                        if (!isLearningMode) {
                            // Record the game completion
                            if (mode == "streak") {
                                GameCompletionRecorder.recordStreakGameCompletion(
                                    context = context,
                                    scope = coroutineScope,
                                    streakDay = streakDay, // ensure streakDay is available in scope
                                    isWon = true,
                                    score = score,
                                    timeSeconds = timeSeconds
                                )
                                // Also record as a regular game for overall stats
                                GameCompletionRecorder.recordGameCompletion(
                                    context = context,
                                    scope = coroutineScope,
                                    difficulty = "streak", // or use "Breeze" if you want to count as easy
                                    isWon = true,
                                    mistakes = mistakes,
                                    score = score,
                                    timeSeconds = timeSeconds,
                                    mode = "regular"
                                )
                            } else {
                                GameCompletionRecorder.recordGameCompletion(
                                    context = context,
                                    scope = coroutineScope,
                                    difficulty = currentDifficulty.name,
                                    isWon = true,
                                    mistakes = mistakes,
                                    score = score,
                                    timeSeconds = timeSeconds,
                                    mode = "regular"
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!isLearningMode) {

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //region Undo Button
                FilledTonalButton(
                    onClick = {
                        undo(userGrid)
                        wrongCells = recomputeWrongCells(userGrid, solutionGrid)
                        AnalyticsUtils.logUndoUsed(context)
                    },
                    enabled = undoStack.isNotEmpty() && isRunning && !gameOver && !gameWon,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Undo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                //endregion

                //region Erase Button
                FilledTonalButton(
                    onClick = {
                        selectedCell?.let { (r, c) ->
                            if (originalGrid[r][c] == 0) {
                                saveUndoState(userGrid)
                                // If notes exist, erase notes
                                if (userGrid[r][c].notes.isNotEmpty()) {
                                    userGrid[r][c] = userGrid[r][c].copy(notes = emptySet())
                                } else {
                                    // If value is wrong, erase value
                                    userGrid[r][c] = userGrid[r][c].copy(value = 0)
                                    wrongCells = wrongCells - (r to c)
                                }
                            }
                        }
                        AnalyticsUtils.logEraseUsed(context)
                    },
                    enabled = isRunning && selectedCell != null && selectedCell?.let { (r, c) ->
                        (wrongCells.contains(r to c) || userGrid[r][c].notes.isNotEmpty())
                    } == true && !gameOver && !gameWon,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Erase",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Erase",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                //endregion

                //region Notes Toggle Button
                FilledTonalButton(
                    onClick = {
                        isNotesMode = !isNotesMode
                        AnalyticsUtils.logNotesToggled(context, isNotesMode)
                    },
                    enabled = isRunning && !gameOver && !gameWon,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isNotesMode) colors.primary else colors.secondaryContainer,
                        contentColor = if (isNotesMode) colors.onPrimary else colors.onSecondaryContainer
                    ),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (isNotesMode) "Notes" else "Notes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                //endregion

                //region Hint Button
                FilledTonalButton(
                    onClick = {
                        if (!isRunning || gameOver || gameWon || availableHints == 0) return@FilledTonalButton
                        if (selectedCell == null) {
                            Toast.makeText(
                                context,
                                "Please select a cell for which you want to use a hint.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@FilledTonalButton
                        }
                        val (r, c) = selectedCell!!
                        val cell = userGrid[r][c]
                        val isWrong = cell.value != 0 && cell.value != solutionGrid[r][c]
                        val isEditable = cell.value == 0 || isWrong
                        if (isEditable) {
                            saveUndoState(userGrid)
                            userGrid[r][c] =
                                cell.copy(value = solutionGrid[r][c], notes = emptySet())
                            updateNotesAfterValueChange(userGrid, r, c, solutionGrid[r][c])
                            availableHints--
                            AnalyticsUtils.logHintUsed(context, currentDifficulty.name)
                            coroutineScope.launch {
                                delay(2000.milliseconds)
                            }
                            // Check for win after hint fills a cell
                            if (!gameOver && !gameWon && isWin(userGrid, solutionGrid)) {
                                gameWon = true
                                isRunning = false
                                // Record the game completion
                                if (mode == "streak") {
                                    GameCompletionRecorder.recordStreakGameCompletion(
                                        context = context,
                                        scope = coroutineScope,
                                        streakDay = streakDay,
                                        isWon = true,
                                        score = score,
                                        timeSeconds = timeSeconds
                                    )
                                    GameCompletionRecorder.recordGameCompletion(
                                        context = context,
                                        scope = coroutineScope,
                                        difficulty = "streak",
                                        isWon = true,
                                        mistakes = mistakes,
                                        score = score,
                                        timeSeconds = timeSeconds,
                                        mode = "regular"
                                    )
                                } else {
                                    GameCompletionRecorder.recordGameCompletion(
                                        context = context,
                                        scope = coroutineScope,
                                        difficulty = currentDifficulty.name,
                                        isWon = true,
                                        mistakes = mistakes,
                                        score = score,
                                        timeSeconds = timeSeconds,
                                        mode = "regular"
                                    )
                                }
                            }
                        }
                    },
                    enabled = isRunning && !gameOver && !gameWon && availableHints > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Hint ($availableHints)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                //endregion
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = learningHint != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            learningHint?.let { hint ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .background(
                            color = colors.errorContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (gameOver && !isLearningMode) {
            LaunchedEffect(Unit) {
                AnalyticsUtils.logGameResult(
                    context,
                    win = false,
                    time = formatTime(timeSeconds),
                    mistakes = mistakes,
                    score = score,
                    difficulty = currentDifficulty.name,
                    isStreak = mode == "streak",
                    hintCount = 1 - availableHints  // currently only 1 hint allowed, so this will be 0 or 1
                )
            }

            BasicAlertDialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // ── Gradient header ──────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFB71C1C), Color(0xFFE53935))
                                    ),
                                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💀", fontSize = 52.sp)
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "Game Over",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "3 mistakes — better luck next time!",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // ── Buttons ──────────────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Try Again
                            Button(
                                onClick = {
                                    userGrid.clear()
                                    originalGrid.forEach { row ->
                                        userGrid.add(row.map { CellData(it) }.toMutableStateList())
                                    }
                                    clearUndoRedoHistory()
                                    gameOver = false
                                    wrongCells = emptySet()
                                    mistakes = 0
                                    score = 0
                                    timeSeconds = 0
                                    gameWon = false
                                    selectedCell = null
                                    shakeCells = emptySet()
                                    isRunning = true
                                    timerKey++
                                    correctStreak = 0
                                    lastCorrectTime = 0
                                    availableHints =
                                        RemoteConfigManager.getHintsForDifficulty(currentDifficulty.name.lowercase())
                                    for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false
                                    coroutineScope.launch {
                                        if (mode != "streak") gameStateManager.clearGameState()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE53935)
                                )
                            ) { Text("Try Again", fontWeight = FontWeight.SemiBold) }

                            // Exit
                            FilledTonalButton(
                                onClick = {
                                    gameOver = false
                                    onExit()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Exit", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }
        }

    } // end Column

    if (gameWon) {
        if (isLearningMode) {
            // ── Learning mode: show LearningCompleteDialog ──
            LearningCompleteDialog(
                onTryNormalMode = {
                    gameWon = false
                    onStartNormalGame() // Pop back to home — user can tap New Game
                },
                onBackToHome = {
                    gameWon = false
                    onExit()
                }
            )
        } else {
            // Persist score & streak state
            LaunchedEffect(Unit) {
                totalScoreManager.addScore(score)
                if (mode == "streak") {
                    val today = LocalDate.now().toString()
                    val savedStreak = streakStateManager.getStreakState().first()
                        ?: StreakState(0, null, null, null)
                    // Increment streakCount only when this streakDay hasn't been counted yet.
                    // Using streakDay > streakCount (instead of date check) allows multiple
                    // streak days to be completed in the same calendar day during DEBUG testing,
                    // while still being correct in release (only one game per day allowed).
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
                    AnalyticsUtils.logStreakGameCompleted(context, streakDay)
                    streakStateManager.setReminderShown(false) // reset reminder shown flag for today so that reminder can be shown again tomorrow
                } else {
                    gameStateManager.clearGameState()
                }
                AnalyticsUtils.logGameResult(
                    context,
                    win = true,
                    time = formatTime(timeSeconds),
                    mistakes = mistakes,
                    score = score,
                    difficulty = currentDifficulty.name,
                    isStreak = mode == "streak",
                    hintCount = 1 - availableHints  // currently only 1 hint allowed, so this will be 0 or 1
                )
            }

            BasicAlertDialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // ── Gradient header ──────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1565C0), Color(0xFF6A1B9A))
                                    ),
                                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(vertical = 28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎉", fontSize = 48.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "You Win!",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Puzzle solved!",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // ── Stats row ────────────────────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Time
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            colors.primaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) { Text("⏱️", fontSize = 22.sp) }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = formatTime(timeSeconds),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "Time",
                                    fontSize = 11.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            // Score
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            colors.primaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) { Text("⭐", fontSize = 22.sp) }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "$score",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "Score",
                                    fontSize = 11.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            // Mistakes
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            colors.primaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) { Text(if (mistakes == 0) "✅" else "❌", fontSize = 22.sp) }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "$mistakes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "Mistakes",
                                    fontSize = 11.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }

                        // ── Buttons ──────────────────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 12.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Next Game — only for non-streak mode
                            if (mode != "streak") {
                                Button(
                                    onClick = {
                                        val (newPuzzle, newSolution) = SudokuGenerator.instance.generate(
                                            currentDifficulty
                                        )
                                        originalGrid = newPuzzle.map { it.toList() }
                                        solutionGrid = newSolution
                                        userGrid.clear()
                                        originalGrid.forEach { row ->
                                            userGrid.add(row.map { CellData(it) }
                                                .toMutableStateList())
                                        }
                                        clearUndoRedoHistory()
                                        gameOver = false
                                        wrongCells = emptySet()
                                        mistakes = 0
                                        score = 0
                                        timeSeconds = 0
                                        gameWon = false
                                        selectedCell = null
                                        shakeCells = emptySet()
                                        isRunning = true
                                        timerKey++
                                        correctStreak = 0
                                        lastCorrectTime = 0
                                        for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false
                                        availableHints =
                                            RemoteConfigManager.getHintsForDifficulty("breeze") // reset hints to default for new game
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1565C0)
                                    )
                                ) {
                                    Text("Next Game", fontWeight = FontWeight.SemiBold)
                                }
                            }

                            // Done
                            FilledTonalButton(
                                onClick = {
                                    gameWon = false
                                    onExit()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Done", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun updateNotesAfterValueChange(
    grid: SnapshotStateList<SnapshotStateList<CellData>>,
    row: Int,
    col: Int,
    value: Int
) {
    for (i in 0..8) {
        // Row
        if (grid[row][i].value == 0) {
            val updatedNotes = grid[row][i].notes.toMutableSet()
            updatedNotes.remove(value)
            grid[row][i] = grid[row][i].copy(notes = updatedNotes)
        }
        // Column
        if (grid[i][col].value == 0) {
            val updatedNotes = grid[i][col].notes.toMutableSet()
            updatedNotes.remove(value)
            grid[i][col] = grid[i][col].copy(notes = updatedNotes)
        }
    }
    // Box
    val boxRow = row / 3 * 3
    val boxCol = col / 3 * 3
    for (r in boxRow until boxRow + 3) {
        for (c in boxCol until boxCol + 3) {
            if (grid[r][c].value == 0) {
                val updatedNotes = grid[r][c].notes.toMutableSet()
                updatedNotes.remove(value)
                grid[r][c] = grid[r][c].copy(notes = updatedNotes)
            }
        }
    }
}
