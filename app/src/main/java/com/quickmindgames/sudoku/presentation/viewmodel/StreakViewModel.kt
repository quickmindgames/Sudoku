package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import com.quickmindgames.sudoku.utils.CurrentDateProvider
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class StreakScreenState(
    val streakCount: Int = 0,
    val activeDate: String? = null,
    val activeGameStarted: Boolean = false,
    val lastCompletedDate: String? = null,
    val hasActiveToday: Boolean = false,
    val completedToday: Boolean = false
)

class StreakViewModel(context: Context) : ViewModel() {
    private val streakStateManager =
        StreakStateManager.getInstance(context.applicationContext)
    private val currentDateProvider = CurrentDateProvider()

    private val _uiState = MutableStateFlow(StreakScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                streakStateManager.getStreakState(),
                currentDateProvider.today
            ) { savedState, currentDate ->
                val state = savedState ?: StreakState(0, null, null, null)
                val today = currentDate.toString()
                val activeGameStarted = state.activeGame?.hasStarted() == true
                StreakScreenState(
                    streakCount = state.streakCount,
                    activeDate = state.activeDate,
                    activeGameStarted = activeGameStarted,
                    lastCompletedDate = state.lastCompletedDate,
                    hasActiveToday = state.activeDate == today && activeGameStarted,
                    completedToday = state.lastCompletedDate == today
                )
            }.collect { _uiState.value = it }
        }
        viewModelScope.launch { currentDateProvider.tickAtMidnight() }
    }

    /** Re-reads the current date, e.g. when the screen resumes after midnight. */
    fun refreshDate() {
        currentDateProvider.refresh()
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StreakViewModel(context)
            }
        }
    }
}
