package com.gamestudio.sudo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sudoku_game_state")

/**
 * Manages saving and loading game state using DataStore
 */
class GameStateManager(private val context: Context) {

    private val GAME_STATE_KEY = stringPreferencesKey("current_game_state")

    /**
     * Save the current game state
     */
    suspend fun saveGameState(gameState: GameState) {
        context.dataStore.edit { preferences ->
            preferences[GAME_STATE_KEY] = gameState.serialize()
        }
    }

    /**
     * Get the saved game state as a Flow
     */
    fun getSavedGameState(): Flow<GameState?> {
        return context.dataStore.data.map { preferences ->
            preferences[GAME_STATE_KEY]?.let { serialized ->
                GameState.deserialize(serialized)
            }
        }
    }

    /**
     * Check if there's a saved game
     */
    fun hasSavedGame(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[GAME_STATE_KEY] != null
        }
    }

    /**
     * Clear the saved game state
     */
    suspend fun clearGameState() {
        context.dataStore.edit { preferences ->
            preferences.remove(GAME_STATE_KEY)
        }
    }

    companion object {
        @Volatile
        private var instance: GameStateManager? = null

        fun getInstance(context: Context): GameStateManager {
            return instance ?: synchronized(this) {
                instance ?: GameStateManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

