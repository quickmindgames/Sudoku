package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.CustomActionDialog
import com.quickmindgames.sudoku.component.DialogAction
import com.quickmindgames.sudoku.component.PrizeWheel
import com.quickmindgames.sudoku.component.PrizeWheelPrizes
import com.quickmindgames.sudoku.component.WalletInfoButton
import com.quickmindgames.sudoku.component.WalletRewardSummaryRow
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.util.GameFeedback
import com.quickmindgames.sudoku.presentation.viewmodel.PrizeCreditResult
import com.quickmindgames.sudoku.presentation.viewmodel.RewardsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class WalletInfo {
    POINTS,
    HINTS,
    BOOSTERS,
    SHIELDS
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RewardsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rewardsViewModel: RewardsViewModel = viewModel(
        factory = remember(context) { RewardsViewModel.factory(context) }
    )
    val rewardsState by rewardsViewModel.uiState.collectAsState()
    val gameFeedback = remember(context) { GameFeedback(context) }
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val pointsCounter = remember { Animatable(0f) }
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasLoadedPoints by remember { mutableStateOf(false) }
    var animateNextPointsUpdate by remember { mutableStateOf(false) }
    var selectedWalletInfo by remember { mutableStateOf<WalletInfo?>(null) }
    val totalPoints = rewardsState.totalPoints
    val hintWallet = rewardsState.hintWallet
    val doublePointsBoosterWallet = rewardsState.doublePointsBoosterWallet
    val streakShieldWallet = rewardsState.streakShieldWallet
    val initialPointHints = rewardsState.initialPointHints
    val initialStreakHints = rewardsState.initialStreakHints
    val soundAndVibrationEnabled = rewardsState.soundAndVibrationEnabled
    val lastSpinMillis = rewardsState.lastSpinMillis
    val isSpinning = rewardsState.isSpinning
    val showMigrationSummary = rewardsState.showMigrationSummary
    val cooldownRemainingMillis = lastSpinMillis?.let {
        (it + AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS - currentTimeMillis).coerceAtLeast(0L)
    }
    var showDialog by remember { mutableStateOf(true) }
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

    //region Main Design
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp
            )
    ) {
        val wheelSize = minOf(maxWidth * 0.95f, maxHeight * 0.50f)

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Rewards",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Spin to unlock power-ups and points",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        //region Total Points
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(
                                0.5.dp,
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Points".uppercase(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight =
                                                FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start
                                    )
                                    WalletInfoButton("About total points") {
                                        showDialog = true
                                        selectedWalletInfo = WalletInfo.POINTS
                                    }
                                }
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_score),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Unspecified
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "%,d".format(pointsCounter.value.roundToInt()),
                                        fontSize = walletCountFontSize(
                                            pointsCounter.value.roundToInt()
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }

                        }
                        //endregion

                        //region Hints
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                0.5.dp,
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Hint".uppercase(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight =
                                                FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start
                                    )
                                    WalletInfoButton("How to earn hints") {
                                        showDialog = true
                                        selectedWalletInfo = WalletInfo.HINTS
                                    }
                                }
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painterResource(id = R.drawable.ic_hint),
                                        contentDescription = "Hint icon",
                                        Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "%,d".format(hintWallet),
                                        fontSize = walletCountFontSize(hintWallet),
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }
                        //endregion
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        //region 2X Points Booster
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                0.5.dp,
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "2X Points Booster".uppercase(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight =
                                                FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start
                                    )
                                    WalletInfoButton("About the 2X Points Wallet") {
                                        showDialog = true
                                        selectedWalletInfo = WalletInfo.BOOSTERS
                                    }
                                }
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painterResource(id = R.drawable.ic_2x),
                                        contentDescription = "2x icon",
                                        Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "%,d".format(doublePointsBoosterWallet),
                                        fontSize = walletCountFontSize(
                                            doublePointsBoosterWallet
                                        ),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }
                        //endregion

                        //region Streak Shield
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                0.5.dp,
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.streak_shield_wallet).uppercase(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight =
                                                FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start
                                    )
                                    WalletInfoButton("About the Shield Wallet") {
                                        showDialog = true
                                        selectedWalletInfo = WalletInfo.SHIELDS
                                    }
                                }
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painterResource(id = R.drawable.ic_shield),
                                        contentDescription = "Shield icon",
                                        Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "%,d".format(streakShieldWallet),
                                        fontSize = walletCountFontSize(streakShieldWallet),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }
                        //endregion
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
                            coroutineScope.launch {
                                val spin = rewardsViewModel.beginSpin() ?: return@launch
                                try {
                                    val segmentAngle = 360f / PrizeWheelPrizes.size
                                    val targetRotation =
                                        (360f - segmentAngle / 2f -
                                                spin.prizeIndex * segmentAngle) % 360f
                                    val currentRotation = ((rotation.value % 360f) + 360f) % 360f
                                    val remainingRotation =
                                        (targetRotation - currentRotation + 360f) % 360f
                                    gameFeedback.playSpinWheel(soundAndVibrationEnabled)
                                    rotation.animateTo(
                                        targetValue = rotation.value + 10 * 360f + remainingRotation,
                                        animationSpec = tween(
                                            durationMillis = 4_000,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    when (rewardsViewModel.creditPrize(spin)) {
                                        PrizeCreditResult.POINTS -> {
                                            animateNextPointsUpdate = true
                                            Toast.makeText(
                                                context,
                                                "You won +${spin.prize.amount} points!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                        }

                                        PrizeCreditResult.HINTS_CREDITED -> {
                                            Toast.makeText(
                                                context,
                                                "You won +${spin.prize.amount} hint!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                        }

                                        PrizeCreditResult.BOOSTER_CREDITED -> {
                                            Toast.makeText(
                                                context,
                                                "You won +${spin.prize.amount} 2X points booster!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                        }

                                        PrizeCreditResult.STREAK_SHIELD_CREDITED -> {
                                            Toast.makeText(
                                                context,
                                                context.getString(
                                                    R.string.streak_shield_won,
                                                    spin.prize.amount
                                                ),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                        }

                                        PrizeCreditResult.ALREADY_CREDITED -> Unit
                                    }

                                } finally {
                                    rewardsViewModel.finishSpin()
                                }
                            }
                        },
                        modifier = Modifier.size(wheelSize)
                    )
                }
            }

            if (showMigrationSummary) {

                val hintGrantDetails = buildList {
                    if (AppPreferences.HINT_WALLET_RELEASE_BONUS > 0) {
                        add(
                            context.getString(
                                R.string.rewards_welcome_bonus_hints,
                                AppPreferences.HINT_WALLET_RELEASE_BONUS
                            )
                        )
                    }
                }.joinToString(", ")

                CustomActionDialog(
                    icon = painterResource(R.drawable.ic_reward),
                    title = stringResource(R.string.rewards_wallet_summary_title),
                    description = stringResource(R.string.rewards_welcome_subtitle),
                    content = {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            WalletRewardSummaryRow(
                                emoji = painterResource(R.drawable.ic_hint),
                                title = stringResource(R.string.hint_wallet_info_title),
                                amount = initialPointHints +
                                        initialStreakHints +
                                        AppPreferences.HINT_WALLET_RELEASE_BONUS,
                                detail = hintGrantDetails
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                            WalletRewardSummaryRow(
                                emoji = painterResource(R.drawable.ic_shield),
                                title = stringResource(R.string.shield_wallet_info_title),
                                amount = streakShieldWallet,
                                detail = stringResource(
                                    R.string.rewards_welcome_shields_detail,
                                    AppPreferences.STREAK_SHIELD_WALLET_RELEASE_BONUS
                                )
                            )
                        }
                    },
                    onDismissRequest = { rewardsViewModel.dismissMigrationSummary() },
                    positiveButton = DialogAction(
                        text = stringResource(R.string.rewards_welcome_button),
                        onClick = { rewardsViewModel.dismissMigrationSummary() }
                    ),
                    negativeButton = null
                )
            }

            if (selectedWalletInfo != null) {
                val infoTitle = when (selectedWalletInfo) {
                    WalletInfo.POINTS, null -> stringResource(R.string.points_wallet_info_title)
                    WalletInfo.HINTS -> stringResource(R.string.hint_wallet_info_title)
                    WalletInfo.BOOSTERS -> stringResource(R.string.booster_wallet_info_title)
                    WalletInfo.SHIELDS -> stringResource(R.string.shield_wallet_info_title)
                }
                val infoDescription = when (selectedWalletInfo) {
                    WalletInfo.POINTS, null -> "Track and accumulate total points from victories and prize wheel."
                    WalletInfo.HINTS -> "Hints reveal the verified number for your currently focussed grid cell."
                    WalletInfo.BOOSTERS -> "Double the points outcome of your next completed Sudoku challenge."
                    WalletInfo.SHIELDS -> "Shields protect missed days and prevent your streak counter from wiping."
                }
                val infoIcon = when (selectedWalletInfo) {
                    WalletInfo.POINTS, null -> painterResource(R.drawable.ic_score)
                    WalletInfo.HINTS -> painterResource(R.drawable.ic_hint)
                    WalletInfo.BOOSTERS -> painterResource(R.drawable.ic_2x)
                    WalletInfo.SHIELDS -> painterResource(R.drawable.ic_shield)
                }
                val content = when (selectedWalletInfo) {
                    WalletInfo.POINTS, null -> {
                        @Composable {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = "EARN",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "Earn points by completing games and spinning the prize wheel.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(all = 4.dp)
                                )
                            }
                        }
                    }

                    WalletInfo.HINTS -> {
                        @Composable {
                            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "EARN",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.hint_wallet_earn_points) + "\n" + stringResource(
                                            R.string.hint_wallet_earn_streak
                                        ) + "\n" + stringResource(R.string.hint_wallet_earn_wheel),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(all = 4.dp)
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "USE",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.hint_wallet_use),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(all = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    WalletInfo.BOOSTERS -> {
                        @Composable {
                            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "EARN",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = "Win 2X Boosters exclusively through the prize wheel.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(all = 4.dp)
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "USE",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = "Apply before launching a new game to double the points. 1 booster consumed per finished game.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(all = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    WalletInfo.SHIELDS -> {
                        @Composable {
                            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "EARN",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = "Win streak shields exclusively through the prize wheel.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(all = 4.dp)
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = "USE",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = "Use one streak shield to protect one missed day and keep your streak alive.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(all = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                if (showDialog) {
                    CustomActionDialog(
                        icon = infoIcon,
                        title = infoTitle,
                        description = infoDescription,
                        content = { content() },
                        onDismissRequest = {
                            selectedWalletInfo = null
                            showDialog = false
                        }
                    )
                }
                /*BasicAlertDialog(
                    onDismissRequest = { selectedWalletInfo = null }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 420.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF1565C0), Color(0xFF6A1B9A))
                                        ),
                                        shape = RoundedCornerShape(
                                            topStart = 28.dp,
                                            topEnd = 28.dp
                                        )
                                    )
                                    .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = infoTitle,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                verticalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                when (selectedWalletInfo) {
                                    WalletInfo.POINTS -> {
                                        WalletInfoSection(
                                            label = stringResource(R.string.wallet_info_earn_label),
                                            details = listOf(
                                                stringResource(R.string.points_wallet_earn)
                                            )
                                        )
                                    }

                                    WalletInfo.HINTS -> {
                                        WalletInfoSection(
                                            label = stringResource(R.string.wallet_info_earn_label),
                                            details = listOf(
                                                stringResource(R.string.hint_wallet_earn_points),
                                                stringResource(R.string.hint_wallet_earn_streak),
                                                stringResource(R.string.hint_wallet_earn_wheel)
                                            )
                                        )
                                        WalletInfoSection(
                                            label = stringResource(R.string.wallet_info_use_label),
                                            details = listOf(
                                                stringResource(R.string.hint_wallet_use)
                                            )
                                        )
                                    }

                                    WalletInfo.BOOSTERS -> {
                                        WalletInfoSection(
                                            label = stringResource(R.string.wallet_info_earn_label),
                                            details = listOf(
                                                stringResource(R.string.booster_wallet_earn)
                                            )
                                        )
                                        WalletInfoSection(
                                            label = stringResource(R.string.wallet_info_use_label),
                                            details = listOf(
                                                stringResource(R.string.booster_wallet_info)
                                            )
                                        )
                                    }

                                    WalletInfo.SHIELDS -> {
                                        WalletInfoSection(
                                            label = stringResource(R.string.wallet_info_earn_label),
                                            details = listOf(
                                                stringResource(R.string.shield_wallet_earn)
                                            )
                                        )
                                        WalletInfoSection(
                                            label = stringResource(R.string.wallet_info_use_label),
                                            details = listOf(
                                                stringResource(R.string.shield_wallet_info)
                                            )
                                        )
                                    }

                                    null -> Unit
                                }
                            }
                        }
                    }
                }*/
            }

        }
    }
    //endregion
}

private fun walletCountFontSize(value: Int) = when ("%,d".format(value).length) {
    in 0..4 -> 26.sp
    5 -> 24.sp
    6 -> 22.sp
    7 -> 20.sp
    else -> 18.sp
}