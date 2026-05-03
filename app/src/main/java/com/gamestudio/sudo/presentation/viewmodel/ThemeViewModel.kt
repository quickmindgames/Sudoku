package com.gamestudio.sudo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamestudio.sudo.domain.usecase.SetDarkModeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    isDarkModeFlow: Flow<Boolean>,
    private val setDarkModeUseCase: SetDarkModeUseCase
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    init {
        viewModelScope.launch {
            isDarkModeFlow.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            setDarkModeUseCase(enabled)
        }
    }
}

