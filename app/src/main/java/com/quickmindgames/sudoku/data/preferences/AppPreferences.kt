package com.quickmindgames.sudoku.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val context: Context) {

    private val HIDE_USED_NUMBERS_KEY = booleanPreferencesKey("hide_used_numbers")
    private val FREE_PLAY_KEY = booleanPreferencesKey("free_play")
    private val SOUND_AND_VIBRATION_KEY = booleanPreferencesKey("sound_and_vibration")

    val hideUsedNumbers: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[HIDE_USED_NUMBERS_KEY] ?: false
    }

    val freePlay: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[FREE_PLAY_KEY] ?: false
    }

    val soundAndVibration: Flow<Boolean> = context.appDataStore.data.map { preferences ->
        preferences[SOUND_AND_VIBRATION_KEY] ?: true
    }

    suspend fun setHideUsedNumbers(enabled: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[HIDE_USED_NUMBERS_KEY] = enabled
        }
    }

    suspend fun setFreePlay(enabled: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[FREE_PLAY_KEY] = enabled
        }
    }

    suspend fun setSoundAndVibration(enabled: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[SOUND_AND_VIBRATION_KEY] = enabled
        }
    }

    companion object {
        @Volatile
        private var instance: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return instance ?: synchronized(this) {
                instance ?: AppPreferences(context.applicationContext).also { instance = it }
            }
        }
    }
}
