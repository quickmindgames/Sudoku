package com.gamestudio.sudo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.streakDataStore: DataStore<Preferences> by preferencesDataStore(name = "streak_state")

class StreakStateManager(private val context: Context) {

    private val STREAK_STATE_KEY = stringPreferencesKey("streak_state")

    suspend fun saveStreakState(streakState: StreakState) {
        context.streakDataStore.edit { preferences ->
            preferences[STREAK_STATE_KEY] = streakState.serialize()
        }
    }

    fun getStreakState(): Flow<StreakState?> {
        return context.streakDataStore.data.map { preferences ->
            preferences[STREAK_STATE_KEY]?.let { serialized ->
                StreakState.deserialize(serialized)
            }
        }
    }

    suspend fun clearStreakState() {
        context.streakDataStore.edit { preferences ->
            preferences.remove(STREAK_STATE_KEY)
        }
    }

    companion object {
        @Volatile
        private var instance: StreakStateManager? = null

        fun getInstance(context: Context): StreakStateManager {
            return instance ?: synchronized(this) {
                instance ?: StreakStateManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

