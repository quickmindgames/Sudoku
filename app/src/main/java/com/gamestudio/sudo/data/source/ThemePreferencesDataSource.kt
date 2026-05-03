package com.gamestudio.sudo.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Theme Preferences Data Source
 * Manages dark mode preference persistence
 */
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferencesDataSource(private val context: Context) {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    val isDarkMode: Flow<Boolean> = context.themeDataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    companion object {
        @Volatile
        private var instance: ThemePreferencesDataSource? = null

        fun getInstance(context: Context): ThemePreferencesDataSource {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferencesDataSource(context.applicationContext).also { instance = it }
            }
        }
    }
}

