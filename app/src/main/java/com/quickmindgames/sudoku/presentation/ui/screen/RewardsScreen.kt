package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.component.PrizeType
import com.quickmindgames.sudoku.component.PrizeWheel
import com.quickmindgames.sudoku.component.PrizeWheelPrizes
import com.quickmindgames.sudoku.component.weightedPrizeIndex
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.util.GameFeedback
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Preview
@Composable
fun RewardsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val totalScoreManager = remember { TotalScoreManager.getInstance(context) }
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val gameFeedback = remember(context) { GameFeedback(context) }
    val totalPoints by totalScoreManager.totalScore.collectAsState(initial = 0)
    val hintWallet by appPreferences.hintWallet.collectAsState(initial = 0)
    val doublePointsBoosterWallet by appPreferences.doublePointsBoosterWallet
        .collectAsState(initial = 0)
    val initialPointHints by appPreferences.initialPointHints.collectAsState(initial = 0)
    val initialStreakHints by appPreferences.initialStreakHints.collectAsState(initial = 0)
    val soundAndVibrationEnabled by appPreferences.soundAndVibration.collectAsState(initial = true)
    val lastSpinMillis by appPreferences.lastSpin.collectAsState(initial = Long.MIN_VALUE)
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val pointsCounter = remember { Animatable(0f) }
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasLoadedPoints by remember { mutableStateOf(false) }
    var animateNextPointsUpdate by remember { mutableStateOf(false) }
    var isSpinning by remember { mutableStateOf(false) }
    var showMigrationSummary by remember { mutableStateOf(false) }
    var showHintEarningInfo by remember { mutableStateOf(false) }
    val cooldownRemainingMillis = lastSpinMillis?.let {
        (it + AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS - currentTimeMillis).coerceAtLeast(0L)
    }
    val canSpin = lastSpinMillis != Long.MIN_VALUE &&
            (cooldownRemainingMillis == null || cooldownRemainingMillis == 0L)
    val cooldownLabel = cooldownRemainingMillis
        ?.takeIf { !canSpin && !isSpinning }
        ?.let { remainingMillis ->
            val totalSeconds = (remainingMillis + 999L) / 1_000L
            "%02d:%02d:%02d".format(
                totalSeconds / 3_600L,
                totalSeconds % 3_600L / 60L,
                totalSeconds % 60L
            )
        }

    LaunchedEffect(lastSpinMillis) {
        while (lastSpinMillis != Long.MIN_VALUE) {
            currentTimeMillis = System.currentTimeMillis()
            val previousSpin = lastSpinMillis ?: break
            val remainingMillis =
                previousSpin + AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS - currentTimeMillis
            if (remainingMillis <= 0L) break
            delay(remainingMillis.coerceAtMost(1_000L))
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    LaunchedEffect(totalPoints) {
        if (!hasLoadedPoints) {
            pointsCounter.snapTo(totalPoints.toFloat())
            hasLoadedPoints = true
        } else if (animateNextPointsUpdate) {
            animateNextPointsUpdate = false
            pointsCounter.animateTo(
                targetValue = totalPoints.toFloat(),
                animationSpec = tween(durationMillis = 1000)
            )
        } else {
            pointsCounter.snapTo(totalPoints.toFloat())
        }
    }

    DisposableEffect(gameFeedback) {
        onDispose { gameFeedback.release() }
    }

    LaunchedEffect(Unit) {
        showMigrationSummary = appPreferences.claimHintMigrationSummaryOnce()
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.REWARDS,
            AnalyticsConstants.REWARDS_SCREEN
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val wheelSize = minOf(maxWidth * 0.9f, maxHeight * 0.46f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    bottom = 12.dp, start = 12.dp, end = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Points", fontSize = 12.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "⭐", fontSize = 18.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "%,d".format(pointsCounter.value.roundToInt()),
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hint Wallet", fontSize = 12.sp)
                            IconButton(
                                onClick = { showHintEarningInfo = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "How to earn hints",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "💡", fontSize = 18.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "%,d".format(hintWallet),
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("2X Points Wallet", fontSize = 12.sp, textAlign = TextAlign.Center)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "2X", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "%,d".format(doublePointsBoosterWallet),
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(24.dp))

            AnimatedVisibility(
                visible = showMigrationSummary,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(
                            animationSpec = tween(1000, easing = FastOutSlowInEasing),
                            initialOffsetY = { it / 3 }
                        ) +
                        expandVertically(animationSpec = tween(1000))
            ) {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉 Hint Wallet rewards added!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Hints from lifetime points: $initialPointHints\n" +
                                        "Hints from your streak: $initialStreakHints\n" +
                                        "Bonus hints: ${AppPreferences.HINT_WALLET_RELEASE_BONUS}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                PrizeWheel(
                    rotation = rotation.value,
                    isSpinning = isSpinning,
                    canSpin = canSpin,
                    cooldownLabel = cooldownLabel,
                    onSpin = {
                        if (!canSpin || isSpinning) return@PrizeWheel
                        val prizeIndex = weightedPrizeIndex(PrizeWheelPrizes, Random)
                        val prize = PrizeWheelPrizes[prizeIndex]
                        val segmentAngle = 360f / PrizeWheelPrizes.size
                        val targetRotation =
                            (360f - segmentAngle / 2f - prizeIndex * segmentAngle) % 360f
                        val currentRotation = ((rotation.value % 360f) + 360f) % 360f
                        val remainingRotation = (targetRotation - currentRotation + 360f) % 360f

                        coroutineScope.launch {
                            isSpinning = true
                            try {
                                val spinStartedAt = System.currentTimeMillis()
                                appPreferences.setLastSpin(spinStartedAt)
                                currentTimeMillis = spinStartedAt
                                gameFeedback.playSpinWheel(soundAndVibrationEnabled)
                                rotation.animateTo(
                                    targetValue = rotation.value + 10 * 360f + remainingRotation,
                                    animationSpec = tween(
                                        durationMillis = 4_000,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                                when (prize.type) {
                                    PrizeType.POINTS -> {
                                        animateNextPointsUpdate = true
                                        totalScoreManager.addScore(prize.amount)
                                        Toast.makeText(
                                            context,
                                            "You won +${prize.amount} points!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                    }

                                    PrizeType.HINTS -> {
                                        val credited = appPreferences.addWheelHintOnce(
                                            spinId = spinStartedAt.toString(),
                                            amount = prize.amount
                                        )
                                        if (credited) {
                                            Toast.makeText(
                                                context,
                                                "You won +${prize.amount} hint!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                        }
                                    }

                                    PrizeType.BOOSTER -> {
                                        val credited = appPreferences.addWheelDoublePointsBoosterOnce(
                                            spinId = spinStartedAt.toString(),
                                            amount = prize.amount
                                        )
                                        if (credited) {
                                            Toast.makeText(
                                                context,
                                                "You won +${prize.amount} 2X points booster!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                        }
                                    }
                                }

                            } finally {
                                isSpinning = false
                            }
                        }
                    },
                    modifier = Modifier.size(wheelSize)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

        }

        if (showHintEarningInfo) {
            AlertDialog(
                onDismissRequest = { showHintEarningInfo = false },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "How to earn hints",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(onClick = { showHintEarningInfo = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("• Earn 1 hint for every 1,000 points.")
                        Text("• Earn 1 hint for every 5 consecutive streak days.")
                        Text("• Spin the Prize Wheel for a chance to win +1 hint.")
                        Text(
                            text = "You can use up to 3 hints in each game.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {}
            )
        }
    }
}
