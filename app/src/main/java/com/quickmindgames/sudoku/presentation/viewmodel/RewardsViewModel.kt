package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.component.PrizeType
import com.quickmindgames.sudoku.component.PrizeWheelPrize
import com.quickmindgames.sudoku.component.PrizeWheelPrizes
import com.quickmindgames.sudoku.component.weightedPrizeIndex
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.domain.util.SpinCooldown
import com.quickmindgames.sudoku.domain.util.SpinRecord
import com.quickmindgames.sudoku.utils.DeviceClock
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class RewardsScreenState(
    val totalPoints: Int = 0,
    val hintWallet: Int = 0,
    val doublePointsBoosterWallet: Int = 0,
    val streakShieldWallet: Int = 0,
    val initialPointHints: Int = 0,
    val initialStreakHints: Int = 0,
    val soundAndVibrationEnabled: Boolean = true,
    /** The last spin; `null` if the wheel was never spun. Only meaningful once [spinLoaded]. */
    val lastSpin: SpinRecord? = null,
    val spinLoaded: Boolean = false,
    val showWelcomeRewardsSummary: Boolean = false,
    val isSpinning: Boolean = false
)

data class RewardSpin(
    val prize: PrizeWheelPrize,
    val prizeIndex: Int,
    val spinId: String
)


data class RewardWalletBalances(
    val totalPoints: Int,
    val hints: Int,
    val boosters: Int,
    val streakShields: Int
)

enum class PrizeCreditResult {
    POINTS,
    HINTS_CREDITED,
    BOOSTER_CREDITED,
    STREAK_SHIELD_CREDITED,
    ALREADY_CREDITED
}

class RewardsViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext
    private val totalScoreManager = TotalScoreManager.getInstance(appContext)
    private val appPreferences = AppPreferences.getInstance(appContext)

    private val _uiState = MutableStateFlow(RewardsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            totalScoreManager.totalScore.collect { points ->
                _uiState.update { it.copy(totalPoints = points) }
            }
        }
        viewModelScope.launch {
            appPreferences.hintWallet.collect { balance ->
                _uiState.update { it.copy(hintWallet = balance) }
            }
        }
        viewModelScope.launch {
            appPreferences.doublePointsBoosterWallet.collect { balance ->
                _uiState.update { it.copy(doublePointsBoosterWallet = balance) }
            }
        }
        viewModelScope.launch {
            appPreferences.streakShieldWallet.collect { balance ->
                _uiState.update { it.copy(streakShieldWallet = balance) }
            }
        }
        viewModelScope.launch {
            appPreferences.initialPointHints.collect { count ->
                _uiState.update { it.copy(initialPointHints = count) }
            }
        }
        viewModelScope.launch {
            appPreferences.initialStreakHints.collect { count ->
                _uiState.update { it.copy(initialStreakHints = count) }
            }
        }
        viewModelScope.launch {
            appPreferences.soundAndVibration.collect { enabled ->
                _uiState.update { it.copy(soundAndVibrationEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            appPreferences.lastSpin.collect { record ->
                _uiState.update { it.copy(lastSpin = record, spinLoaded = true) }
            }
        }
        viewModelScope.launch {
            val summaryShown = appPreferences.welcomeRewardsSummaryShown.first()
            _uiState.update {
                it.copy(showWelcomeRewardsSummary = !summaryShown)
            }
        }
        recoverPendingSpin()
    }

    /**
     * Credits a spin that started its cooldown but was never credited, because
     * the user left the Rewards screen (or the app was killed) while the wheel
     * was still animating.
     *
     * This must be called every time the Rewards screen is (re)entered, not only
     * when the ViewModel is created: switching bottom-nav tabs retains the
     * ViewModel, so [init] alone would never recover a spin dropped by a tab
     * switch. It no-ops while a spin is in progress (the active spin is credited
     * normally when the wheel stops), and crediting is idempotent per spin id, so
     * a prize is never paid twice even if this races with the stop-path credit.
     */
    fun recoverPendingSpin() {
        viewModelScope.launch {
            if (_uiState.value.isSpinning) return@launch
            val pending = appPreferences.pendingWheelSpin.first() ?: return@launch
            val prizeType = runCatching { PrizeType.valueOf(pending.prizeType) }.getOrNull()
            if (prizeType == null) {
                appPreferences.clearPendingSpin(pending.spinId)
                return@launch
            }
            creditPrize(prizeType, pending.amount, pending.spinId)
            appPreferences.clearPendingSpin(pending.spinId)
        }
    }

    /**
     * Selects a prize and starts the cooldown, recording the prize as *pending*
     * (not yet credited) in the same atomic write as the cooldown. The caller
     * then plays the wheel animation and credits the prize via [creditSpin] when
     * the wheel stops — preserving the original "reveal on stop" behaviour.
     *
     * Because the pending prize and the cooldown are persisted together, leaving
     * the screen or killing the app mid-spin can no longer start the cooldown
     * without a payout: the pending prize is recovered on the next Rewards visit
     * (see [recoverPendingSpin]). The persistence runs in `withContext(NonCancellable)`
     * so it survives cancellation of the caller's coroutine.
     */
    /**
     * Time left before the next spin, measured with the boot clock where possible so that
     * changing the device date/time can't skip or extend the cooldown (see [SpinCooldown]).
     */
    fun cooldownRemainingMillis(coolDownTotalMillis: Long): Long =
        SpinCooldown.remainingMillis(
            _uiState.value.lastSpin,
            DeviceClock.now(appContext),
            coolDownTotalMillis
        )

    suspend fun beginSpin(coolDownTotalMillis: Long): RewardSpin? {
        val state = _uiState.value
        if (state.isSpinning || !state.spinLoaded) return null
        val now = DeviceClock.now(appContext)
        if (SpinCooldown.remainingMillis(state.lastSpin, now, coolDownTotalMillis) > 0L) {
            return null
        }

        val prizeIndex = weightedPrizeIndex(PrizeWheelPrizes, Random)
        val prize = PrizeWheelPrizes[prizeIndex]
        val spinId = now.wallMillis.toString()
        val spin = RewardSpin(prize, prizeIndex, spinId)
        val record = SpinRecord(now.wallMillis, now.elapsedMillis, now.bootCount)
        _uiState.update { it.copy(isSpinning = true, lastSpin = record) }
        return try {
            withContext(NonCancellable) {
                appPreferences.setPendingSpin(now, prize.type.name, prize.amount, spinId)
            }
            spin
        } catch (exception: Exception) {
            _uiState.update { it.copy(isSpinning = false) }
            throw exception
        }
    }

    /**
     * Credits the prize once the wheel has stopped (the happy path), then clears
     * the pending marker. Runs under [NonCancellable] so a credit in progress
     * completes even if the caller is cancelled at that instant; crediting is
     * idempotent per spin id regardless.
     */
    suspend fun creditSpin(spin: RewardSpin): PrizeCreditResult =
        withContext(NonCancellable) {
            val result = creditPrize(spin.prize.type, spin.prize.amount, spin.spinId)
            appPreferences.clearPendingSpin(spin.spinId)
            result
        }

    private suspend fun creditPrize(
        prizeType: PrizeType,
        amount: Int,
        spinId: String
    ): PrizeCreditResult =
        when (prizeType) {
            PrizeType.POINTS -> if (
                appPreferences.recordWheelPointsSpinOnce(spinId)
            ) {
                totalScoreManager.addScore(amount)
                PrizeCreditResult.POINTS
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }

            PrizeType.HINTS -> if (
                appPreferences.addWheelHintOnce(spinId, amount)
            ) {
                PrizeCreditResult.HINTS_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }

            PrizeType.BOOSTER -> if (
                appPreferences.addWheelDoublePointsBoosterOnce(spinId, amount)
            ) {
                PrizeCreditResult.BOOSTER_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }

            PrizeType.STREAK_SHIELD -> if (
                appPreferences.addWheelStreakShieldOnce(spinId, amount)
            ) {
                PrizeCreditResult.STREAK_SHIELD_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }
        }

    suspend fun currentWalletBalances(): RewardWalletBalances = RewardWalletBalances(
        totalPoints = totalScoreManager.totalScore.first(),
        hints = appPreferences.hintWallet.first(),
        boosters = appPreferences.doublePointsBoosterWallet.first(),
        streakShields = appPreferences.streakShieldWallet.first()
    )

    fun finishSpin() {
        _uiState.update { it.copy(isSpinning = false) }
    }

    fun dismissWelcomeRewardsSummary() {
        _uiState.update { it.copy(showWelcomeRewardsSummary = false) }
        viewModelScope.launch {
            appPreferences.markWelcomeRewardsSummaryShown()
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RewardsViewModel(context)
            }
        }
    }
}
