package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.preferences.ThemePreferences
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsScreenState(
    val isDarkMode: Boolean = false,
    val hideUsedNumbers: Boolean = false,
    val highlightRegion: Boolean = false,
    val highlightSameNumbers: Boolean = false,
    val freePlay: Boolean = false,
    val soundAndVibration: Boolean = true
)

data class FreePlayChangeResult(
    val blockedBySavedGame: Boolean = false,
    val disabledHideUsedNumbers: Boolean = false
)

class SettingsViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext
    private val themePreferences = ThemePreferences.getInstance(appContext)
    private val appPreferences = AppPreferences.getInstance(appContext)
    private val gameStateManager = GameStateManager.getInstance(appContext)

    private val _uiState = MutableStateFlow(SettingsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appPreferences.normalizeGameplayOptions()
            combine(
                appPreferences.hideUsedNumbers,
                appPreferences.freePlay
            ) { hide, free ->
                hide to free
            }.collect { (hide, free) ->
                _uiState.update {
                    it.copy(hideUsedNumbers = hide, freePlay = free)
                }
            }
        }
        viewModelScope.launch {
            themePreferences.isDarkMode.collect { darkMode ->
                _uiState.update { it.copy(isDarkMode = darkMode) }
            }
        }
        viewModelScope.launch {
            appPreferences.highlightRegion.collect { enabled ->
                _uiState.update { it.copy(highlightRegion = enabled) }
            }
        }
        viewModelScope.launch {
            appPreferences.highlightSameNumbers.collect { enabled ->
                _uiState.update { it.copy(highlightSameNumbers = enabled) }
            }
        }
        viewModelScope.launch {
            appPreferences.soundAndVibration.collect { enabled ->
                _uiState.update { it.copy(soundAndVibration = enabled) }
            }
        }
    }

    suspend fun setHideUsedNumbers(enabled: Boolean): Boolean {
        val disabledFreePlay = appPreferences.setHideUsedNumbers(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.HIDE_USED_NUMBERS,
            enabled
        )
        if (disabledFreePlay) {
            AnalyticsUtils.logSettingsOptionChanged(
                appContext,
                AnalyticsConstants.FREE_PLAY,
                false
            )
        }
        return disabledFreePlay
    }

    suspend fun setHighlightSameNumbers(enabled: Boolean) {
        appPreferences.setHighlightSameNumbers(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.HIGHLIGHT_SAME_NUMBERS,
            enabled
        )
    }

    suspend fun setHighlightRegion(enabled: Boolean) {
        appPreferences.setHighlightRegion(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.HIGHLIGHT_REGION,
            enabled
        )
    }

    suspend fun setFreePlay(enabled: Boolean): FreePlayChangeResult {
        if (gameStateManager.getSavedGameState().first() != null) {
            return FreePlayChangeResult(blockedBySavedGame = true)
        }

        val disabledHideUsedNumbers = appPreferences.setFreePlay(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.FREE_PLAY,
            enabled
        )
        if (disabledHideUsedNumbers) {
            AnalyticsUtils.logSettingsOptionChanged(
                appContext,
                AnalyticsConstants.HIDE_USED_NUMBERS,
                false
            )
        }
        return FreePlayChangeResult(disabledHideUsedNumbers = disabledHideUsedNumbers)
    }

    suspend fun setSoundAndVibration(enabled: Boolean) {
        appPreferences.setSoundAndVibration(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.SOUND_VIBRATIONS,
            enabled
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        themePreferences.setDarkMode(enabled)
        val themeName = if (enabled) "dark_theme" else "light_theme"
        AnalyticsUtils.logThemeToggled(appContext, themeName)
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(context)
            }
        }
    }
}
