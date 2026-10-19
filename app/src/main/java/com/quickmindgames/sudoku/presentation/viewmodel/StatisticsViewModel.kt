package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StatisticsViewModel(context)
            }
        }
    }

    private val repository by lazy {
        StatisticsRepository(context)
    }

    private val _allStatistics = MutableStateFlow(AllStatistics())
    val allStatistics: StateFlow<AllStatistics> = _allStatistics.asStateFlow()

    private val _hasLoadedStatistics = MutableStateFlow(false)
    val hasLoadedStatistics: StateFlow<Boolean> = _hasLoadedStatistics.asStateFlow()

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
        _hasLoadedStatistics.value = false
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Use .first() to get one value and complete
                val stats = repository.getAllStatistics().first()
                _allStatistics.value = stats
                _hasLoadedStatistics.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

