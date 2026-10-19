package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.DifficultyCard
import com.quickmindgames.sudoku.component.StreakSummaryCard
import com.quickmindgames.sudoku.component.primaryGradientBrush
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.ui.isDifficultyUnlocked
import com.quickmindgames.sudoku.presentation.viewmodel.HomeStreakDialog
import com.quickmindgames.sudoku.presentation.viewmodel.HomeViewModel
import com.quickmindgames.sudoku.presentation.viewmodel.ProtectStreakResult
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.formatTime
import kotlinx.coroutines.launch

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNewGameClick: () -> Unit,
    onDifficultySelected: (Difficulty, Boolean) -> Unit,
    onResume: () -> Unit,
    onStreakClick: () -> Unit,
    onLearningModeClick: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var showNewGameConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = remember(context) { HomeViewModel.factory(context) }
    )
    val homeState by homeViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var useDoublePointsBooster by remember { mutableStateOf(false) }

    // Show streak reminder dialog if needed
    if (homeState.streakDialog == HomeStreakDialog.REMINDER) {
        LaunchedEffect(homeState.streakDialog) {
            homeViewModel.onStreakReminderDialogShown()
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
                    homeViewModel.dismissStreakDialog()
                    onStreakClick() // Redirect to Streak Tab
                }) {
                    Text("Go to Streak")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (homeState.streakDialog == HomeStreakDialog.SHIELD) {
        LaunchedEffect(homeState.streakDialog) {
            homeViewModel.onStreakShieldDialogShown()
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = stringResource(R.string.streak_shield_prompt_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                val shieldsAvailable = pluralStringResource(
                    R.plurals.streak_shields_available,
                    homeState.streakShieldWallet,
                    homeState.streakShieldWallet
                )
                Text(
                    text = stringResource(
                        R.string.streak_shield_prompt_description,
                        homeState.streakCount,
                        shieldsAvailable
                    ),
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        when (homeViewModel.protectStreak()) {
                            ProtectStreakResult.SHIELD_UNAVAILABLE -> Toast.makeText(
                                context,
                                context.getString(R.string.streak_shield_unavailable),
                                Toast.LENGTH_LONG
                            ).show()

                            ProtectStreakResult.PROTECTED,
                            ProtectStreakResult.NO_LONGER_ELIGIBLE -> Unit
                        }
                    }
                }) {
                    Text(stringResource(R.string.streak_shield_use_one))
                }
            },
            dismissButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        homeViewModel.restartStreak()
                        onStreakClick()
                    }
                }) {
                    Text(stringResource(R.string.streak_shield_restart))
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Show streak broken dialog if needed
    if (homeState.streakDialog == HomeStreakDialog.BROKEN) {
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
                    homeViewModel.dismissStreakDialog()
                    onStreakClick() // Redirect to Streak Tab
                }) {
                    Text("Restart Streak")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.HOME,
            AnalyticsConstants.HOME_SCREEN
        )
    }

    if (showNewGameConfirmation) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNewGameConfirmation = false },
            title = {
                Text(
                    text = "Start a New Game?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Your current game progress will be lost. Are you sure you want to start a new game?")
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewGameConfirmation = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNewGameConfirmation = false
                        onNewGameClick()
                        showSheet = true
                    }
                ) {
                    Text("Start a New Game")
                }
            },
            shape = RoundedCornerShape(20.dp)
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
                            onDifficultySelected(difficulty, useDoublePointsBooster)
                            useDoublePointsBooster = false
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2X Points Booster",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Available: ${homeState.doublePointsBoosterWallet} Boosters",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Double the points earned from this game.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = useDoublePointsBooster,
                        onCheckedChange = { useDoublePointsBooster = it },
                        enabled = homeState.doublePointsBoosterWallet > 0
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    //region Main Design
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp
            )
    ) {
        StreakSummaryCard(
            streakCount = homeState.streakCount,
            completedToday = homeState.completedToday,
            hasActiveToday = homeState.hasActiveToday,
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
                modifier = Modifier.size(196.dp)
            )

            // Tag Line
            Text(
                text = stringResource(R.string.lbl_puzzle).uppercase(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            //region Total Points
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    0.dp,
                    MaterialTheme.colorScheme.tertiary
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_score),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(4.dp))
                    if (homeState.totalScore == 0) {
                        Text(
                            text = "No points yet — play a game!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    } else {
                        Text(
                            text = "Total Points: ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "%,d".format(homeState.totalScore),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            //endregion

            Spacer(modifier = Modifier.height(16.dp))

            //region Resume Button
            if (homeState.hasSavedGame) {
                ElevatedButton(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Resume Game",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "In progress".uppercase(),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.7f
                                    )
                                )
                                homeState.savedGameDifficulty?.let { difficulty ->
                                    Text(
                                        text = " • ${difficulty.label}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.5f
                                        )
                                    )
                                }
                                Text(
                                    text = " • ${formatTime(homeState.savedGameTimeSeconds)}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.5f
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            //endregion

            //region New Game
            Button(
                onClick = {
                    if (homeState.hasSavedGame) {
                        showNewGameConfirmation = true
                    } else {
                        onNewGameClick()
                        showSheet = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(
                        brush = primaryGradientBrush(),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                    }
                    Spacer(Modifier.width(8.dp))
                    Text(text = "New Game", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            //endregion

            Spacer(modifier = Modifier.height(16.dp))

            //region Learning Mode Button
            OutlinedButton(
                onClick = onLearningModeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                border = BorderStroke(
                    0.dp,
                    MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Help,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.lbl_how_to_play),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,

                        )
                }
            }
            //endregion
        }
    }
    //endregion
}