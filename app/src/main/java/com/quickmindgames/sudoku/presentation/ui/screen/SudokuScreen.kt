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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.LearningCompleteDialog
import com.quickmindgames.sudoku.component.NumberPad
import com.quickmindgames.sudoku.component.SudokuGridLevel
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.util.GameFeedback
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.viewmodel.HintResult
import com.quickmindgames.sudoku.presentation.viewmodel.SudokuGameViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.formatTime
import com.quickmindgames.sudoku.utils.ordinalSuffix
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val MAX_HINTS_PER_GAME = 3

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
    useDoublePointsBooster: Boolean = false,
    onExit: () -> Unit,
    onStartNormalGame: () -> Unit = onExit
) {
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val gameFeedback = remember(context) { GameFeedback(context) }
    val coroutineScope = rememberCoroutineScope()
    val gameViewModel: SudokuGameViewModel = viewModel(
        key = "$mode-${difficulty?.name}-$streakDay-$useDoublePointsBooster",
        factory = remember(appPreferences, mode, difficulty, streakDay, useDoublePointsBooster) {
            SudokuGameViewModel.factory(
                context = context,
                appPreferences = appPreferences,
                mode = mode,
                difficulty = difficulty,
                streakDay = streakDay,
                useDoublePointsBooster = useDoublePointsBooster
            )
        }
    )
    val preferences by gameViewModel.preferences.collectAsState()
    val timerState by gameViewModel.timerState.collectAsState()
    val hideUsedNumbers = preferences.hideUsedNumbers
    val highlightRegion = preferences.highlightRegion
    val highlightSameNumbers = preferences.highlightSameNumbers
    val freePlayEnabled = preferences.freePlay && mode != "streak"
    val soundAndVibrationEnabled = preferences.soundAndVibration

    DisposableEffect(gameFeedback) {
        onDispose { gameFeedback.release() }
    }

    val originalGrid by gameViewModel.originalGrid
    val userGrid = gameViewModel.userGrid
    val gameOver by gameViewModel.gameOver
    val wrongCells by gameViewModel.wrongCells
    val shakeCells by gameViewModel.shakeCells
    val gameWon by gameViewModel.gameWon
    val confettiComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.confetti)
    )
    val confettiProgress by animateLottieCompositionAsState(
        composition = confettiComposition,
        isPlaying = gameWon,
        restartOnPlay = true,
        iterations = 1
    )
    val selectedCell by gameViewModel.selectedCell
    val mistakes by gameViewModel.mistakes
    val timeSeconds = timerState.elapsedSeconds
    val isRunning = timerState.isRunning
    val score by gameViewModel.score
    val hasDoublePointsBooster by gameViewModel.hasDoublePointsBooster
    val currentDifficulty by gameViewModel.currentDifficulty

    val isNotesMode by gameViewModel.isNotesMode
    val hintsUsed by gameViewModel.hintsUsed
    val isHintProcessing by gameViewModel.isHintProcessing
    val hintWallet = preferences.hintWallet

    val isLearningMode = mode == "learn"
    val remainingGameHints = (MAX_HINTS_PER_GAME - hintsUsed).coerceAtLeast(0)
    val availableHints = minOf(hintWallet, remainingGameHints)
    val canUseHint = !isLearningMode && availableHints > 0
    val pointsToAward = gameViewModel.pointsToAward(freePlayEnabled)
    val learningHint by gameViewModel.learningHint
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(shakeCells) {
        delay(300.milliseconds)
        gameViewModel.clearShakeCells()
    }

    LaunchedEffect(preferences.freePlay, mode) {
        gameViewModel.onPreferencesChanged(freePlayEnabled)
    }

    LaunchedEffect(learningHint) {
        if (learningHint != null) {
            delay(2500.milliseconds)
            gameViewModel.clearLearningHint()
        }
    }

    LaunchedEffect(mode, difficulty) {
        if (gameViewModel.initialize()) onExit()
    }

    // Auto-save game state whenever important state changes
    LaunchedEffect(
        userGrid.hashCode(),
        mistakes,
        score,
        timeSeconds,
        selectedCell,
        isNotesMode,
        hintsUsed,
        hasDoublePointsBooster
    ) {
        gameViewModel.saveCurrentGame()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle observer for background/foreground events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    // Pause the timer and auto-save (already handled by LaunchedEffect)
                    gameViewModel.pauseTimer()
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
            .navigationBarsPadding()
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
                        tint = colors.primary
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

            // Keep the timer in the scorecard position during free play.
            if (!isLearningMode && freePlayEnabled) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 4.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(timeSeconds),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onPrimaryContainer
                        )
                        IconButton(
                            onClick = { gameViewModel.toggleTimer(!isLearningMode) },
                            enabled = !gameOver && !gameWon,
                            modifier = Modifier.size(32.dp)
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
            } else if (!isLearningMode) {
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

        if (!isLearningMode && !freePlayEnabled) {
            // Mistakes (left) + Play/Pause Icon + Timer (right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
            ) {
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
                        onClick = { gameViewModel.toggleTimer(!isLearningMode) },
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
                highlightRegion,
                highlightSameNumbers,
                onCellClick = { r, c ->
                    gameViewModel.selectCell(r, c)
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
                        .clickable { gameViewModel.resumeTimer(!isLearningMode) },
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
            grid = userGrid,
            enabled = isRunning && !gameOver && !gameWon,
            hideUsedNumbers = hideUsedNumbers
        ) { number ->
            val result = gameViewModel.enterNumber(
                number = number,
                isLearningMode = isLearningMode,
                freePlay = freePlayEnabled
            ) ?: return@NumberPad
            if (result.isCorrect) {
                gameFeedback.playCorrectNumber(soundAndVibrationEnabled)
            } else {
                gameFeedback.playWrongNumber(soundAndVibrationEnabled)
            }
            if (result.gameOver) gameFeedback.playGameOver(soundAndVibrationEnabled)
            if (result.gameWon) gameFeedback.playPuzzleCompleted(soundAndVibrationEnabled)
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
                    onClick = { gameViewModel.undoMove() },
                    enabled = gameViewModel.canUndo.value && isRunning && !gameOver && !gameWon,
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
                    onClick = { gameViewModel.eraseSelectedCell() },
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
                    onClick = { gameViewModel.toggleNotes() },
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
                        if (!isRunning || gameOver || gameWon || !canUseHint || isHintProcessing) {
                            return@FilledTonalButton
                        }
                        coroutineScope.launch {
                            when (gameViewModel.processHint(isLearningMode, freePlayEnabled)) {
                                HintResult.EMPTY_WALLET ->
                                    Toast.makeText(
                                        context,
                                        "Your Hint Wallet is empty.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                HintResult.CELL_NOT_SELECTED ->
                                    Toast.makeText(
                                        context,
                                        "Please select a cell for which you want to use a hint.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                HintResult.CELL_ALREADY_CORRECT ->
                                    Toast.makeText(
                                        context,
                                        "This cell is already correct.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                HintResult.GAME_WON ->
                                    gameFeedback.playPuzzleCompleted(soundAndVibrationEnabled)

                                HintResult.APPLIED -> Unit
                            }
                        }
                    },
                    enabled = isRunning && !gameOver && !gameWon && canUseHint &&
                            !isHintProcessing,
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
                        Text(
                            text = when {
                                hintsUsed >= MAX_HINTS_PER_GAME -> "Limit reached"
                                hintWallet == 0 -> "Wallet empty"
                                else -> "Used $hintsUsed/$MAX_HINTS_PER_GAME"
                            },
                            fontSize = 9.sp
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
                        fontSize = 18.sp,
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
                    hintCount = hintsUsed
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
                                    coroutineScope.launch { gameViewModel.retryGame() }
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
                                    gameViewModel.dismissGameOver()
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
                    gameViewModel.dismissGameWon()
                    onStartNormalGame() // Pop back to home — user can tap New Game
                },
                onBackToHome = {
                    gameViewModel.dismissGameWon()
                    onExit()
                }
            )
            AnalyticsUtils.logLearningCompleted(context)
        } else {
            // Persist score & streak state
            LaunchedEffect(Unit) {
                gameViewModel.persistWin(pointsToAward, freePlayEnabled)
                AnalyticsUtils.logGameResult(
                    context,
                    win = true,
                    time = formatTime(timeSeconds),
                    mistakes = mistakes,
                    score = pointsToAward,
                    difficulty = currentDifficulty.name,
                    isStreak = mode == "streak",
                    hintCount = hintsUsed
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
                                .height(180.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1565C0), Color(0xFF6A1B9A))
                                    ),
                                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = confettiComposition,
                                progress = { confettiProgress },
                                modifier = Modifier.fillMaxSize()
                            )
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
                                if (hasDoublePointsBooster && !freePlayEnabled) {
                                    Text(
                                        text = "You've earned $score x 2 = $pointsToAward points!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
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
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            if (!freePlayEnabled) {
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
                                        text = "$pointsToAward",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        text = "Points",
                                        fontSize = 12.sp,
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
                                        fontSize = 12.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
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
                                        gameViewModel.createNextGame()
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
                                    gameViewModel.dismissGameWon()
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
