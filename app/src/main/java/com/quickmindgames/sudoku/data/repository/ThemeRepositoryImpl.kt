package com.quickmindgames.sudoku.data.repository

import com.quickmindgames.sudoku.data.preferences.ThemePreferences
import com.quickmindgames.sudoku.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val themePreferences: ThemePreferences
) : ThemeRepository {

    override val isDarkMode: Flow<Boolean> = themePreferences.isDarkMode

    override suspend fun setDarkMode(enabled: Boolean) {
        themePreferences.setDarkMode(enabled)
    }
}

