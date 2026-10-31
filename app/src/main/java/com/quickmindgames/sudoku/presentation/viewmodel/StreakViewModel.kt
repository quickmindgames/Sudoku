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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StreakScreenState(
    val streakCount: Int = 0,
    val activeDate: String? = null,
    val activeGameStarted: Boolean = false,
    val lastCompletedDate: String? = null
)

class StreakViewModel(context: Context) : ViewModel() {
    private val streakStateManager =
        StreakStateManager.getInstance(context.applicationContext)

    private val _uiState = MutableStateFlow(StreakScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            streakStateManager.getStreakState().collect { savedState ->
                val state = savedState ?: StreakState(0, null, null, null)
                _uiState.value = StreakScreenState(
                    streakCount = state.streakCount,
                    activeDate = state.activeDate,
                    activeGameStarted = state.activeGame?.hasStarted() == true,
                    lastCompletedDate = state.lastCompletedDate
                )
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StreakViewModel(context)
            }
        }
    }
}
