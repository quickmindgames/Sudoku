package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val soundAndVibrationEnabled by appPreferences.soundAndVibration.collectAsState(initial = true)
    val lastSpinMillis by appPreferences.lastSpin.collectAsState(initial = Long.MIN_VALUE)
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val pointsCounter = remember { Animatable(0f) }
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasLoadedPoints by remember { mutableStateOf(false) }
    var animateNextPointsUpdate by remember { mutableStateOf(false) }
    var isSpinning by remember { mutableStateOf(false) }
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
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
        ) {

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.Start),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.size(24.dp))

            Text(
                text = "Spin to win!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(12.dp))

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
                                    // add 'prize.amount' hint in the hint wallet
                                    Toast.makeText(
                                        context,
                                        "You won +${prize.amount} hint!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                }
                            }

                        } finally {
                            isSpinning = false
                        }
                    }
                },
                modifier = Modifier.size(wheelSize)
            )

            Spacer(modifier = Modifier.size(16.dp))

        }
    }
}
