package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickmindgames.sudoku.data.repository.StatisticsRepository
import com.quickmindgames.sudoku.domain.model.AllStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for managing statistics UI state
 * Uses lazy repository initialization
 */
class StatisticsViewModel(context: Context) : ViewModel() {

    private val repository by lazy {
        StatisticsRepository(context)
    }

    private val _allStatistics = MutableStateFlow(AllStatistics())
    val allStatistics: StateFlow<AllStatistics> = _allStatistics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshStatistics()
    }

    /**
     * Refresh statistics from database
     * Called on screen focus or manually
     */
    fun refreshStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Use .first() to get one value and complete
                val stats = repository.getAllStatistics().first()
                _allStatistics.value = stats
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Record a completed regular game
     */
    fun recordGameCompletion(
        difficulty: String,
        isWon: Boolean,
        mistakes: Int,
        score: Int,
        timeSeconds: Int
    ) {
        viewModelScope.launch {
            repository.recordGameCompletion(
                difficulty = difficulty,
                isWon = isWon,
                mistakes = mistakes,
                score = score,
                timeSeconds = timeSeconds
            )
            // Refresh statistics after recording
            refreshStatistics()
        }
    }

    /**
     * Record a completed streak game
     */
    fun recordStreakGameCompletion(
        streakDay: Int,
        isWon: Boolean,
        score: Int,
        timeSeconds: Int
    ) {
        viewModelScope.launch {
            repository.recordStreakGameCompletion(
                streakDay = streakDay,
                isWon = isWon,
                score = score,
                timeSeconds = timeSeconds
            )
            // Refresh statistics after recording
            refreshStatistics()
        }
    }

    /**
     * Clear all statistics (for development/testing)
     */
    fun clearAllStatistics() {
        viewModelScope.launch {
            repository.clearAllStatistics()
            refreshStatistics()
        }
    }
}

