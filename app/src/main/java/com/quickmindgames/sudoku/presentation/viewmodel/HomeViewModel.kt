package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.domain.model.Difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class HomeScreenState(
    val hasSavedGame: Boolean = false,
    val savedGameDifficulty: Difficulty? = null,
    val savedGameTimeSeconds: Int = 0,
    val doublePointsBoosterWallet: Int = 0,
    val streakShieldWallet: Int = 0,
    val streakCount: Int = 0,
    val completedToday: Boolean = false,
    val hasActiveToday: Boolean = false,
    val streakDialog: HomeStreakDialog? = null,
    val totalScore: Int = 0
)

enum class HomeStreakDialog {
    BROKEN,
    REMINDER,
    SHIELD
}

enum class ProtectStreakResult {
    PROTECTED,
    SHIELD_UNAVAILABLE,
    NO_LONGER_ELIGIBLE
}

class HomeViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext
    private val gameStateManager = GameStateManager.getInstance(appContext)
    private val streakStateManager = StreakStateManager.getInstance(appContext)
    private val totalScoreManager = TotalScoreManager.getInstance(appContext)
    private val appPreferences = AppPreferences.getInstance(appContext)
    private val today = LocalDate.now().toString()

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameStateManager.getSavedGameState().collect { savedGame ->
                _uiState.update {
                    it.copy(
                        hasSavedGame = savedGame != null,
                        savedGameDifficulty = savedGame?.difficulty,
                        savedGameTimeSeconds = savedGame?.timeSeconds ?: 0
                    )
                }
            }
        }
        viewModelScope.launch {
            totalScoreManager.totalScore.collect { totalScore ->
                _uiState.update { it.copy(totalScore = totalScore) }
            }
        }
        viewModelScope.launch {
            appPreferences.doublePointsBoosterWallet.collect { wallet ->
                _uiState.update { it.copy(doublePointsBoosterWallet = wallet) }
            }
        }
        viewModelScope.launch {
            combine(
                streakStateManager.getReminderShown(),
                streakStateManager.getStreakState(),
                appPreferences.streakShieldWallet
            ) { reminderShown, savedStreak, shieldWallet ->
                Triple(reminderShown, savedStreak, shieldWallet)
            }.collect { (reminderShown, savedStreak, shieldWallet) ->
                updateStreakState(reminderShown, savedStreak, shieldWallet)
            }
        }
    }

    private suspend fun updateStreakState(
        reminderShown: Boolean,
        savedStreak: StreakState?,
        shieldWallet: Int
    ) {
        val streak = savedStreak ?: StreakState(0, null, null, null)
        var streakDialog = _uiState.value.streakDialog
        var resetBrokenStreak = false

        if (streak.streakCount > 0 && streak.lastCompletedDate != null) {
            val lastCompletedDate = LocalDate.parse(streak.lastCompletedDate)
            val daysDifference = ChronoUnit.DAYS.between(
                lastCompletedDate,
                LocalDate.now()
            ).toInt()

            when {
                daysDifference == 0 || daysDifference == 1 -> {
                    if (streakDialog == HomeStreakDialog.SHIELD) streakDialog = null
                }

                daysDifference == 2 -> {
                    if (!reminderShown) {
                        streakDialog = if (shieldWallet > 0) {
                            HomeStreakDialog.SHIELD
                        } else {
                            HomeStreakDialog.REMINDER
                        }
                    }
                }

                daysDifference > 2 -> {
                    streakDialog = HomeStreakDialog.BROKEN
                    resetBrokenStreak = true
                }
            }
        }

        _uiState.update {
            it.copy(
                streakShieldWallet = shieldWallet,
                streakCount = streak.streakCount,
                completedToday = streak.lastCompletedDate == today,
                hasActiveToday = streak.activeDate == today &&
                    streak.activeGame?.hasStarted() == true,
                streakDialog = streakDialog
            )
        }
        if (resetBrokenStreak) {
            streakStateManager.saveStreakState(StreakState(0, null, null, null))
        }
    }

    fun onStreakReminderDialogShown() {
        viewModelScope.launch {
            streakStateManager.setReminderShown(true)
        }
    }

    fun onStreakShieldDialogShown() {
        viewModelScope.launch {
            streakStateManager.setReminderShown(true)
        }
    }

    suspend fun protectStreak(): ProtectStreakResult {
        val savedStreak = streakStateManager.getStreakState().first()
        val currentDate = LocalDate.now()
        val protectedStreak = savedStreak?.protectMissedDay(currentDate)
        if (protectedStreak == null) {
            _uiState.update { state ->
                val lastCompletedDate = savedStreak?.lastCompletedDate?.let(LocalDate::parse)
                val streakWasBroken = savedStreak != null &&
                    lastCompletedDate != null &&
                    ChronoUnit.DAYS.between(lastCompletedDate, currentDate) > 2
                state.copy(
                    streakDialog = when {
                        streakWasBroken -> HomeStreakDialog.BROKEN
                        (savedStreak?.streakCount ?: 0) > 0 -> HomeStreakDialog.REMINDER
                        else -> null
                    }
                )
            }
            if (
                savedStreak != null &&
                savedStreak.lastCompletedDate?.let(LocalDate::parse)?.let {
                    ChronoUnit.DAYS.between(it, currentDate) > 2
                } == true
            ) {
                streakStateManager.saveStreakState(StreakState(0, null, null, null))
            }
            return ProtectStreakResult.NO_LONGER_ELIGIBLE
        }

        if (!appPreferences.consumeStreakShield()) {
            _uiState.update {
                it.copy(streakDialog = HomeStreakDialog.REMINDER)
            }
            return ProtectStreakResult.SHIELD_UNAVAILABLE
        }

        streakStateManager.saveStreakState(protectedStreak)
        streakStateManager.setReminderShown(false)
        _uiState.update { it.copy(streakDialog = null) }
        return ProtectStreakResult.PROTECTED
    }

    suspend fun restartStreak() {
        streakStateManager.saveStreakState(StreakState(0, null, null, null))
        streakStateManager.setReminderShown(false)
        _uiState.update { it.copy(streakDialog = null) }
    }

    fun dismissStreakDialog() {
        _uiState.update { it.copy(streakDialog = null) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(context)
            }
        }
    }
}
