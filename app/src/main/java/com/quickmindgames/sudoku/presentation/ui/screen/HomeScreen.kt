package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.DifficultyCard
import com.quickmindgames.sudoku.component.StreakSummaryCard
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.ui.isDifficultyUnlocked
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import java.time.LocalDate

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewGameClick: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onResume: () -> Unit,
    onStreakClick: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val gameStateManager = remember { GameStateManager.getInstance(context) }
    val streakStateManager = remember { StreakStateManager.getInstance(context) }
    val totalScoreManager = remember { TotalScoreManager.getInstance(context) }

    // Saved game state
    var hasSavedGame by remember { mutableStateOf(false) }
    var savedGameDifficulty by remember { mutableStateOf<Difficulty?>(null) }

    // Streak state
    var streakCount by remember { mutableIntStateOf(0) }
    var completedToday by remember { mutableStateOf(false) }
    var hasActiveToday by remember { mutableStateOf(false) }
    var showStreakBrokenDialog by remember { mutableStateOf(false) }
    var showStreakReminderDialog by remember { mutableStateOf(false) }

    // Total lifetime score
    var totalScore by remember { mutableIntStateOf(0) }

    val today = remember { LocalDate.now().toString() }

    LaunchedEffect(Unit) {
        gameStateManager.getSavedGameState().collect { savedState ->
            hasSavedGame = savedState != null
            savedGameDifficulty = savedState?.difficulty
        }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.flow.combine(
            streakStateManager.getReminderShown(),
            streakStateManager.getStreakState()
        ) { isReminderShown, saved ->
            Pair(isReminderShown, saved)
        }.collect { (isReminderShown, saved) ->
            val s = saved ?: StreakState(0, null, null, null)
            streakCount = s.streakCount
            completedToday = s.lastCompletedDate == today
            hasActiveToday = s.activeDate == today && s.activeGame?.hasStarted() == true

            if (s.streakCount > 0 && s.lastCompletedDate != null) {
                // Calculate the difference in days between today and last completed date
                val lastCompletedLocalDate = LocalDate.parse(s.lastCompletedDate)
                val todayLocalDate = LocalDate.now()
                val daysDifference = java.time.temporal.ChronoUnit.DAYS.between(
                    lastCompletedLocalDate,
                    todayLocalDate
                ).toInt()

                when {
                    daysDifference == 0 -> {
                        // Streak is fine - completed today
                    }

                    daysDifference == 1 -> {
                        // Completed yesterday - streak is still alive
                    }

                    daysDifference == 2 -> {
                        // Missed yesterday but completed the day before - show reminder if not already shown
                        if (!isReminderShown) {
                            showStreakReminderDialog = true
                        }
                    }

                    daysDifference > 2 -> {
                        // Missed more than 1 day - streak is broken
                        showStreakBrokenDialog = true
                        streakStateManager.saveStreakState(
                            StreakState(
                                streakCount = 0,
                                lastCompletedDate = null,
                                activeDate = null,
                                activeGame = null
                            )
                        )
                    }
                }
            }
        }
    }

    // Show streak reminder dialog if needed
    if (showStreakReminderDialog) {
        LaunchedEffect(Unit) {
            streakStateManager.setReminderShown(true)
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "Streak At Risk!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                Text(
                    text = "You missed playing yesterday! Play today to keep your streak alive.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(onClick = {
                    showStreakReminderDialog = false
                    onStreakClick() // Redirect to Streak Tab
                }) {
                    Text("Go to Streak")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Show streak broken dialog if needed
    if (showStreakBrokenDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "Streak Broken!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                Text(
                    text = "You missed a day and your streak has been reset. Start a new streak today!",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(onClick = {
                    showStreakBrokenDialog = false
                    onStreakClick() // Redirect to Streak Tab
                }) {
                    Text("Restart Streak")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }


    LaunchedEffect(Unit) {
        totalScoreManager.totalScore.collect { saved ->
            totalScore = saved
        }
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.HOME,
            AnalyticsConstants.HOME_SCREEN
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Header
                Text(
                    text = "Choose Difficulty",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Select your challenge level",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(20.dp))

                // Difficulty cards
                Difficulty.entries.forEach { difficulty ->
                    val (isUnlocked, lockedMessage) = isDifficultyUnlocked(difficulty, context)
                    DifficultyCard(
                        difficulty = difficulty,
                        isLocked = !isUnlocked,
                        onClick = {
                            showSheet = false
                            onDifficultySelected(difficulty)
                        },
                        onLockedClick = {
                            Toast.makeText(context, lockedMessage, Toast.LENGTH_SHORT).show()
                            /*// Show toast on main thread
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                Toast.makeText(context, lockedMessage, Toast.LENGTH_SHORT).show()
                            }*/
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 12.dp
            )
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        StreakSummaryCard(
            streakCount = streakCount,
            completedToday = completedToday,
            hasActiveToday = hasActiveToday,
            onClick = onStreakClick
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = null,
                modifier = Modifier.size(172.dp)
            )

            Text(
                text = stringResource(R.string.lbl_puzzle),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Total lifetime score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⭐",
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (totalScore == 0) "No score yet — play a game!"
                    else "Total Score: ${"%,d".format(totalScore)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalScore == 0)
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resume button
            if (hasSavedGame) {
                ElevatedButton(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Resume Game",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            savedGameDifficulty?.let { difficulty ->
                                Text(
                                    text = difficulty.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    onNewGameClick()
                    showSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "New Game", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}