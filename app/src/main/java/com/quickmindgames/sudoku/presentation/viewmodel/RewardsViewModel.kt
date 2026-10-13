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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class RewardsScreenState(
    val totalPoints: Int = 0,
    val hintWallet: Int = 0,
    val doublePointsBoosterWallet: Int = 0,
    val streakShieldWallet: Int = 0,
    val initialPointHints: Int = 0,
    val initialStreakHints: Int = 0,
    val soundAndVibrationEnabled: Boolean = true,
    val lastSpinMillis: Long? = Long.MIN_VALUE,
    val showMigrationSummary: Boolean = false,
    val isSpinning: Boolean = false
)

data class RewardSpin(
    val prize: PrizeWheelPrize,
    val prizeIndex: Int,
    val spinId: String
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
            appPreferences.lastSpin.collect { timestamp ->
                _uiState.update { it.copy(lastSpinMillis = timestamp) }
            }
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(showMigrationSummary = appPreferences.claimHintMigrationSummaryOnce())
            }
        }
    }

    suspend fun beginSpin(): RewardSpin? {
        if (_uiState.value.isSpinning) return null
        val lastSpin = _uiState.value.lastSpinMillis
        val now = System.currentTimeMillis()
        if (
            lastSpin == Long.MIN_VALUE ||
            lastSpin != null &&
            now < lastSpin + AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS
        ) return null

        val prizeIndex = weightedPrizeIndex(PrizeWheelPrizes, Random)
        val prize = PrizeWheelPrizes[prizeIndex]
        val spinId = now.toString()
        _uiState.update { it.copy(isSpinning = true, lastSpinMillis = now) }
        try {
            appPreferences.setLastSpin(now)
        } catch (exception: Exception) {
            _uiState.update { it.copy(isSpinning = false) }
            throw exception
        }
        return RewardSpin(prize, prizeIndex, spinId)
    }

    suspend fun creditPrize(spin: RewardSpin): PrizeCreditResult =
        when (spin.prize.type) {
            PrizeType.POINTS -> {
                totalScoreManager.addScore(spin.prize.amount)
                PrizeCreditResult.POINTS
            }

            PrizeType.HINTS -> if (
                appPreferences.addWheelHintOnce(spin.spinId, spin.prize.amount)
            ) {
                PrizeCreditResult.HINTS_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }

            PrizeType.BOOSTER -> if (
                appPreferences.addWheelDoublePointsBoosterOnce(spin.spinId, spin.prize.amount)
            ) {
                PrizeCreditResult.BOOSTER_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }

            PrizeType.STREAK_SHIELD -> if (
                appPreferences.addWheelStreakShieldOnce(spin.spinId, spin.prize.amount)
            ) {
                PrizeCreditResult.STREAK_SHIELD_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }
        }

    fun finishSpin() {
        _uiState.update { it.copy(isSpinning = false) }
    }

    fun dismissMigrationSummary() {
        _uiState.update { it.copy(showMigrationSummary = false) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RewardsViewModel(context)
            }
        }
    }
}
