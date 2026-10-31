package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.CustomActionDialog
import com.quickmindgames.sudoku.component.DialogAction
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
            .systemBarsPadding()
    ) {

        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onExit() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = if (isLearningMode) stringResource(R.string.lbl_practice_mode)
                    else if (mode == "streak" && streakDay > 0) "${ordinalSuffix(streakDay)} Streak"
                    else if (mode == "streak") "Streak"
                    else currentDifficulty.label,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.width(8.dp))

            // Keep the timer in the scorecard position during free play.
            if (!isLearningMode && freePlayEnabled) {
                //region Timer
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
                            onClick = { gameViewModel.toggleTimer(true) },
                            enabled = !gameOver && !gameWon,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isRunning) "Pause" else "Resume",
                                modifier = Modifier.size(20.dp),
                                tint = colors.onPrimaryContainer
                            )
                        }
                    }
                }
                //endregion

            } else if (!isLearningMode) {
                //region Points
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_score),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$score",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer
                        )
                    }
                }
                //endregion
            }
        }

        if (!isLearningMode && !freePlayEnabled) {
            // Mistakes (left) + Play/Pause Icon + Timer (right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mistakes",
                        fontSize = 14.sp,
                        color = colors.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    repeat(3) { index ->
                        Spacer(Modifier.width(4.dp))
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
                        fontSize = 14.sp,
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
                            imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Resume",
                            modifier = Modifier.size(20.dp),
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
                                .size(64.dp)
                                .background(
                                    color = colors.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Resume",
                                tint = colors.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Game Paused",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onBackground
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tap to resume",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //region Undo Button
                FilledTonalButton(
                    onClick = { gameViewModel.undoMove() },
                    enabled = gameViewModel.canUndo.value && isRunning && !gameOver && !gameWon,
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(24.dp)
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
                        .weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Erase",
                            modifier = Modifier.size(24.dp)
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
                        .weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isNotesMode) colors.primary else colors.secondaryContainer,
                        contentColor = if (isNotesMode) colors.onPrimary else colors.onSecondaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EditNote,
                            contentDescription = "Notes",
                            modifier = Modifier.size(24.dp)
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
                            when (gameViewModel.processHint(false, freePlayEnabled)) {
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
                        .weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painterResource(id = R.drawable.ic_hint),
                            contentDescription = "Hint",
                            Modifier.size(24.dp),
                            colorFilter = tint(
                                if (canUseHint) Color(0xFFf2ca00) else colors.onSurfaceVariant.copy(
                                    alpha = 0.5f
                                )
                            )
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
            CustomActionDialog(
                icon = painterResource(R.drawable.ic_skull),
                title = "Game Over",
                description = "You've reached 3 mistakes - take a bath and try again! Practice makes perfection.",
                content = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mistakes Limit Reached",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = colors.errorContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "3 / 3",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.error
                            )
                        }
                    }
                },
                onDismissRequest = { },
                positiveButton = DialogAction(
                    text = "Try Again",
                    onClick = {
                        coroutineScope.launch { gameViewModel.retryGame() }
                    }
                ),
                negativeButton = DialogAction(
                    text = "Exit",
                    onClick = {
                        gameViewModel.dismissGameOver()
                        onExit()
                    }
                )
            )
        }

    } // end Column

    if (gameWon) {
        if (isLearningMode) {
            AnalyticsUtils.logLearningCompleted(context)
            CustomActionDialog(
                icon = painterResource(R.drawable.ic_party),
                title = "Excellent Work!",
                description = "You've completed your learning puzzle and mastered cell intersections!",
                content = {
                    Text(
                        text = "You've got the skills. Let's jump into a real rated challenge! \uD83D\uDE80",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                    )
                },
                onDismissRequest = { },
                positiveButton = DialogAction(
                    text = "Start Game",
                    onClick = {
                        gameViewModel.dismissGameWon()
                        onStartNormalGame() // Pop back to home — user can tap New Game
                    }
                ),
                negativeButton = DialogAction(
                    text = "Back to Home",
                    onClick = {
                        gameViewModel.dismissGameWon()
                        onExit()
                    }
                )
            )

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
            val doneAction = DialogAction(
                text = "Done",
                onClick = {
                    gameViewModel.dismissGameWon()
                    onExit()
                }
            )
            val nextAction = DialogAction(
                text = "Next Game",
                onClick = {
                    gameViewModel.createNextGame()
                }
            )
            CustomActionDialog(
                icon = painterResource(R.drawable.ic_party),
                title = "You Win!",
                description = "Congratulations! You've solved the puzzle.",
                content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (hasDoublePointsBooster && !freePlayEnabled) {
                            Text(
                                text = "You've earned $score x 2 = $pointsToAward points!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Time
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            colors.primaryContainer,
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_clock),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formatTime(timeSeconds),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "TIME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            if (!freePlayEnabled) {
                                // Score
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(
                                                colors.primaryContainer,
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_score),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Unspecified
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "$pointsToAward",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        text = "POINTS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                                // Mistakes
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(
                                                if (mistakes == 0) colors.primaryContainer else colors.errorContainer,
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (mistakes == 0) Icons.Rounded.Check else Icons.Rounded.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = if (mistakes == 0) colors.primary else colors.error
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "$mistakes",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        text = "MISTAKES",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                positiveButton = if (mode == "streak") {
                    doneAction
                } else nextAction,
                negativeButton = if (mode == "streak") {
                    null
                } else {
                    doneAction
                }
            )
        }
    }
}
