package com.gamestudio.sudo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gamestudio.sudo.component.Difficulty
import com.gamestudio.sudo.data.util.GameCompletionRecorder
import com.gamestudio.sudo.domain.util.clearUndoRedoHistory
import com.gamestudio.sudo.domain.util.saveUndoState
import com.gamestudio.sudo.domain.util.undo
import com.gamestudio.sudo.domain.util.undoStack
import com.gamestudio.sudo.presentation.navigation.AppNav
import com.gamestudio.sudo.presentation.util.shakeEffect
import com.gamestudio.sudo.ui.theme.SudoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.floor
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themePreferences = remember { ThemePreferences.getInstance(this) }
            var isDarkMode by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                themePreferences.isDarkMode.collect { darkMode ->
                    isDarkMode = darkMode
                    // isAppearanceLightStatusBars = true → dark icons (visible on light bg)
                    // isAppearanceLightStatusBars = false → light icons (visible on dark bg)
                    androidx.core.view.WindowCompat
                        .getInsetsController(window, window.decorView)
                        .isAppearanceLightStatusBars = !darkMode
                }
            }

            SudoTheme(darkTheme = isDarkMode) {
                AppNav()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    mode: String,
    difficulty: Difficulty?,
    streakDay: Int = 0,
    onExit: () -> Unit
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
        mutableStateListOf(*originalGrid.map { it.toMutableStateList() }.toTypedArray())
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

    LaunchedEffect(shakeCells) {
        delay(300)
        shakeCells = emptySet()
    }

    LaunchedEffect(timerKey) {
        while (true) {
            if (isRunning) {
                delay(1000)
                if (isRunning) timeSeconds++
            } else {
                delay(100) // poll until resumed
            }
        }
    }

    LaunchedEffect(mode, difficulty) {

        if (mode == "new") {
            // Generate new puzzle
            val diff = difficulty ?: Difficulty.Breeze
            val (puzzle, solution) = SudokuGenerator.instance.generate(diff)
            puzzleData = Pair(puzzle, solution)
            originalGrid = puzzle.map { it.toList() }
            solutionGrid = solution
            currentDifficulty = diff

            // Properly reset user grid
            userGrid.clear()
            originalGrid.forEach { row ->
                userGrid.add(row.toMutableStateList())
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
        }

        if (mode == "resume") {
            // Restore from DataStore
            gameStateManager.getSavedGameState().collect { savedState ->
                savedState?.let { state ->
                    currentDifficulty = state.difficulty
                    originalGrid = state.originalGrid
                    solutionGrid = state.solutionGrid.map { it.toIntArray() }.toTypedArray()

                    // Properly restore user grid by clearing and repopulating
                    userGrid.clear()
                    state.userGrid.forEach { row ->
                        userGrid.add(row.toMutableStateList())
                    }

                    wrongCells = state.wrongCells
                    mistakes = state.mistakes
                    score = state.score
                    timeSeconds = state.timeSeconds
                    selectedCell = state.selectedCell
                    gameOver = false
                    gameWon = false
                    isRunning = true
                    timerKey++ // Restart timer from saved time
                }
            }
        }

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
                // Already completed today, go back
                onExit()
            } else {
                // Start new streak game
                val (puzzle, solution) = SudokuGenerator.instance.generate(Difficulty.Breeze)
                puzzleData = Pair(puzzle, solution)
                originalGrid = puzzle.map { it.toList() }
                solutionGrid = solution
                currentDifficulty = Difficulty.Breeze

                userGrid.clear()
                originalGrid.forEach { row ->
                    userGrid.add(row.toMutableStateList())
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
    }

    // Clear saved game when starting a new game
    LaunchedEffect(mode) {
        if (mode == "new") {
            gameStateManager.clearGameState()
        }
    }

    // Auto-save game state whenever important state changes
    LaunchedEffect(userGrid.hashCode(), mistakes, score, timeSeconds, selectedCell) {
        // Only save if game has started (timer ticked at least once) and is not over/won
        if (!gameOver && !gameWon) {
            val currentState = GameState(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
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
                    bottom = 12.dp
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
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = if (mode == "streak" && streakDay > 0) "${ordinalSuffix(streakDay)} Streak"
                    else if (mode == "streak") "Streak"
                    else currentDifficulty.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(8.dp))

            // Score Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Mistakes (left) + Timer + Pause (right)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            // Mistakes anchored to the left
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mistakes: ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                repeat(3) { index ->
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (index < mistakes)
                                    MaterialTheme.colorScheme.errorContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (index < mistakes)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Timer anchored to the right
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = formatTime(timeSeconds),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            // Sudoku board — always composed but visually hidden when paused
            SudokuGridLevel2(originalGrid, userGrid, wrongCells, shakeCells, selectedCell) { r, c ->
                if (isRunning) selectedCell = r to c
            }

            // Pause overlay
            if (!isRunning && !gameOver && !gameWon) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.93f),
                            shape = RoundedCornerShape(12.dp)
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
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Game Paused",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tap to resume",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        NumberPad(grid = userGrid) { number ->
            selectedCell?.let { (r, c) ->
                if (originalGrid[r][c] != 0 || gameOver || gameWon) return@NumberPad

                saveUndoState(userGrid)
                userGrid[r][c] = number

                if (number != solutionGrid[r][c]) {
                    mistakes++
                    wrongCells = wrongCells + (r to c)
                    shakeCells = shakeCells + (r to c)
                    // Break correct streak on mistake
                    correctStreak = 0

                    if (mistakes >= 3) {
                        gameOver = true
                        isRunning = false
                        coroutineScope.launch { gameStateManager.clearGameState() }

                        // Record the game loss
                        GameCompletionRecorder.recordGameCompletion(
                            context = context,
                            scope = coroutineScope,
                            difficulty = currentDifficulty.name,
                            isWon = false,
                            mistakes = mistakes,
                            score = score,
                            timeSeconds = timeSeconds
                        )
                    }
                } else {
                    wrongCells = wrongCells - (r to c)

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

                if (isWin(userGrid, solutionGrid)) {
                    gameWon = true
                    isRunning = false

                    // Record the game completion
                    GameCompletionRecorder.recordGameCompletion(
                        context = context,
                        scope = coroutineScope,
                        difficulty = currentDifficulty.name,
                        isWon = true,
                        mistakes = mistakes,
                        score = score,
                        timeSeconds = timeSeconds
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Undo Button
            FilledTonalButton(
                onClick = {
                    undo(userGrid)
                    wrongCells = recomputeWrongCells(userGrid, solutionGrid)
                },
                enabled = undoStack.isNotEmpty(),
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
                        imageVector = Icons.Default.Undo,
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

            // Erase Button
            FilledTonalButton(
                onClick = {
                    selectedCell?.let { (r, c) ->
                        if (originalGrid[r][c] == 0) {
                            saveUndoState(userGrid)
                            userGrid[r][c] = 0
                            wrongCells = wrongCells - (r to c)
                        }
                    }
                },
                enabled = selectedCell != null && selectedCell?.let { (r, c) ->
                    wrongCells.contains(r to c)
                } == true,
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

            // Pause / Resume Button
            FilledTonalButton(
                onClick = { isRunning = !isRunning },
                enabled = !gameOver && !gameWon,
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
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Resume",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "Pause" else "Resume",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (gameOver) {
            BasicAlertDialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    originalGrid.forEach { row -> userGrid.add(row.toMutableStateList()) }
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
                                onClick = { onExit() },
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
        // Persist score & streak state
        LaunchedEffect(Unit) {
            totalScoreManager.addScore(score)
            if (mode == "streak") {
                val today = LocalDate.now().toString()
                val savedStreak = streakStateManager.getStreakState().first()
                    ?: StreakState(0, null, null, null)
                if (savedStreak.lastCompletedDate != today) {
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
            } else {
                gameStateManager.clearGameState()
            }
        }

        BasicAlertDialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) { Text("⏱️", fontSize = 22.sp) }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = formatTime(timeSeconds),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Time",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Score
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) { Text("⭐", fontSize = 22.sp) }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "$score",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Score",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Mistakes
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) { Text(if (mistakes == 0) "✅" else "❌", fontSize = 22.sp) }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "$mistakes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Mistakes",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    originalGrid.forEach { row -> userGrid.add(row.toMutableStateList()) }
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
                            onClick = { onExit() },
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


fun formatTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
}

fun ordinalSuffix(n: Int): String {
    val suffix = when {
        n % 100 in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
}

fun resetGrid(grid: List<MutableList<Int>>, original: List<List<Int>>) {
    for (r in 0..8) {
        for (c in 0..8) {
            grid[r][c] = original[r][c]
        }
    }
}

@Composable
fun NumberPad(
    grid: List<List<Int>>,
    onNumberClick: (Int) -> Unit
) {
    // Count how many times each number (1–9) appears in the grid.
    // No remember() — must recompute on every recomposition so undo/redo
    // changes are reflected immediately (SnapshotStateList mutations trigger recompose).
    val numberCounts = IntArray(10)
    for (row in grid) for (cell in row) if (cell in 1..9) numberCounts[cell]++

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..9).forEach { number ->
            val isComplete = numberCounts[number] >= 9

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                if (!isComplete) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onNumberClick(number) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuGridLevel2(
    originalGrid: List<List<Int>>,
    grid: List<List<Int>>,
    wrongCells: Set<Pair<Int, Int>>,
    shakeCells: Set<Pair<Int, Int>>,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit
) {
    SudokuBoardContainer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // responsive
    ) { cellSizeDp, boardSizeDp ->

        // ✅ Your LazyVerticalGrid uses EXACT cell size now
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier.requiredSize(boardSizeDp),
            userScrollEnabled = false
        ) {
            items(81) { index ->
                val row = index / 9
                val col = index % 9
                val value = grid[row][col]
                val original = originalGrid[row][col]

                val isSelected = selectedCell == row to col
                val selectedValue = selectedCell?.let { grid[it.first][it.second] }
                val isSameNumber =
                    selectedValue != null && selectedValue != 0 && value == selectedValue
                val isWrong = wrongCells.contains(row to col)
                val shouldShake = shakeCells.contains(row to col)

                Box(
                    modifier = Modifier
                        .requiredSize(cellSizeDp) // ✅ exact dp snapped from px
                        .background(
                            when {
                                isSelected -> Color.LightGray
                                isSameNumber -> Color.Cyan.copy(alpha = 0.3f)
                                original != 0 -> Color(0xFFEFEFEF)
                                else -> Color.White
                            }
                        )
                        .clickable { onCellClick(row, col) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (value == 0) "" else value.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            original != 0 -> Color.Black
                            isWrong -> Color.Red
                            else -> Color.Blue
                        },
                        modifier = Modifier.shakeEffect(shouldShake)
                    )
                }
            }
        }

        // If you draw Canvas lines, keep it same snapped size:
        SudokuGridCanvas(modifier = Modifier.requiredSize(boardSizeDp))
    }
}

@Composable
fun SudokuGridCanvas(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    Canvas(modifier = modifier.clipToBounds()) {
        val thin = with(density) { 1.dp.toPx() }
        val thick = with(density) { 2.dp.toPx() }

        val outerColor = Color.Black.copy(alpha = 0.9f)
        val innerColor = Color.Black.copy(alpha = 0.6f)

        // ✅ Force perfect square drawing area
        val boardSize = min(size.width, size.height)
        val cellSize = boardSize / 9f

        // Optional: center the board if canvas is larger
        val offsetX = (size.width - boardSize) / 2f
        val offsetY = (size.height - boardSize) / 2f

        fun lineStroke(i: Int): Float =
            when {
                i == 0 || i == 9 -> thick
                i % 3 == 0 -> thick
                else -> thin
            }

        for (i in 0..9) {
            val stroke = lineStroke(i)
            val half = stroke / 2f
            val isOuter = (i == 0 || i == 9)
            val color = if (isOuter) outerColor else innerColor

            val x = offsetX + i * cellSize
            val y = offsetY + i * cellSize

            // ✅ Vertical lines end at boardSize, not canvas height
            drawLine(
                color = color,
                start = Offset(x, offsetY + half),
                end = Offset(x, offsetY + boardSize - half),
                strokeWidth = stroke,
                cap = StrokeCap.Butt
            )

            // ✅ Horizontal lines end at boardSize, not canvas width
            drawLine(
                color = color,
                start = Offset(offsetX + half, y),
                end = Offset(offsetX + boardSize - half, y),
                strokeWidth = stroke,
                cap = StrokeCap.Butt
            )
        }
    }
}

@Composable
fun rememberSudokuGrid(initialGrid: List<List<Int>>): SnapshotStateList<SnapshotStateList<Int>> {
    return remember(initialGrid) {
        mutableStateListOf(*initialGrid.map { it.toMutableStateList() }.toTypedArray())
    }
}

fun isWin(userGrid: List<MutableList<Int>>, solution: Array<IntArray>): Boolean {
    for (r in 0..8) {
        for (c in 0..8) {
            if (userGrid[r][c] != solution[r][c]) {
                return false
            }
        }
    }
    return true
}

fun recomputeWrongCells(
    grid: List<MutableList<Int>>,
    solution: Array<IntArray>
): Set<Pair<Int, Int>> {

    val wrong = mutableSetOf<Pair<Int, Int>>()

    for (r in 0..8) {
        for (c in 0..8) {
            val v = grid[r][c]
            if (v != 0 && v != solution[r][c]) {
                wrong.add(r to c)
            }
        }
    }
    return wrong
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}

@Composable
fun SudokuBoardContainer(
    modifier: Modifier = Modifier,
    content: @Composable (cellSizeDp: androidx.compose.ui.unit.Dp, boardSizeDp: androidx.compose.ui.unit.Dp) -> Unit
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val boardDpRaw = min(maxWidth, maxHeight)

        // Convert to px, then snap to multiples of 9 px
        val boardPxRaw = with(density) { boardDpRaw.toPx() }
        val cellPx = floor(boardPxRaw / 9f) // integer px per cell
        val boardPx = cellPx * 9f           // snapped board size px

        val cellDp = with(density) { cellPx.toDp() }
        val boardDp = with(density) { boardPx.toDp() }

        Box(
            modifier = Modifier
                .requiredSize(boardDp)   // ✅ board is exact size
                .clipToBounds()
        ) {
            content(cellDp, boardDp)
        }
    }
}

@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    onClick: () -> Unit,
    isLocked: Boolean = false,
    onLockedClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val (emoji, description) = when (difficulty) {
        Difficulty.Breeze -> Pair("🌤️", "Perfect for beginners")
        Difficulty.Pulse -> Pair("⚡", "Moderate challenge")
        Difficulty.Rage -> Pair("🔥", "Test your skills")
        Difficulty.Elite -> Pair("💎", "For masters only")
    }

    val cardColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFFDDEAFE) // Light Blue
        Difficulty.Pulse -> Color(0xFFFEF3C7) // Light Yellow
        Difficulty.Rage -> Color(0xFFFED7D7) // Light Red/Pink
        Difficulty.Elite -> Color(0xFFE9D5FF) // Light Purple
    }

    val accentColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFF3B82F6)
        Difficulty.Pulse -> Color(0xFFF59E0B)
        Difficulty.Rage -> Color(0xFFEF4444)
        Difficulty.Elite -> Color(0xFF8B5CF6)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.5f else 1f)
            .clickable(
                enabled = true,
                onClick = {
                    if (isLocked) {
                        onLockedClick()
                    } else {
                        onClick()
                    }
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = if (isLocked) 0.dp else 8.dp
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji icon with background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 32.sp
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = difficulty.label,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Arrow indicator or lock icon
                if (isLocked) {
                    Text(text = "🔒", fontSize = 24.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Select ${difficulty.label}",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Lock overlay for locked state
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                }
            }
        }
    }
}

/**
 * Check if a difficulty level is unlocked
 * Breeze: Always unlocked
 * Pulse: Requires 5 Breeze games
 * Rage: Requires 5 Pulse games
 * Elite: Requires 5 Rage games
 */
@Composable
fun isDifficultyUnlocked(
    difficulty: Difficulty,
    context: android.content.Context
): Pair<Boolean, String> {
    var breezeWins by remember { mutableIntStateOf(0) }
    var pulseWins by remember { mutableIntStateOf(0) }
    var rageWins by remember { mutableIntStateOf(0) }

    val repository = remember { com.gamestudio.sudo.data.repository.StatisticsRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val allStats = repository.getAllStatistics().first()
                breezeWins = allStats.breeze.wins
                pulseWins = allStats.pulse.wins
                rageWins = allStats.rage.wins
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return when (difficulty) {
        Difficulty.Breeze -> Pair(true, "")
        Difficulty.Pulse -> {
            if (breezeWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Breeze games to unlock Pulse")
            }
        }

        Difficulty.Rage -> {
            if (pulseWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Pulse games to unlock Rage")
            }
        }

        Difficulty.Elite -> {
            if (rageWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Rage games to unlock Elite")
            }
        }
    }
}

