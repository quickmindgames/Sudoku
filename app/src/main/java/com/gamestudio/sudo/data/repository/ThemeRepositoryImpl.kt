package com.gamestudio.sudo.data.repository

import com.gamestudio.sudo.ThemePreferences
import com.gamestudio.sudo.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val themePreferences: ThemePreferences
) : ThemeRepository {

    override val isDarkMode: Flow<Boolean> = themePreferences.isDarkMode

    override suspend fun setDarkMode(enabled: Boolean) {
        themePreferences.setDarkMode(enabled)
    }
}

