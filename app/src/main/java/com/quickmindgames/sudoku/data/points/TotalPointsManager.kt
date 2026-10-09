// This file has been moved to data/score/TotalScoreManager.kt

package com.quickmindgames.sudoku.data.score

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.scoreDataStore: DataStore<Preferences> by preferencesDataStore(name = "total_score")

class TotalScoreManager(private val context: Context) {

    private val TOTAL_SCORE_KEY = intPreferencesKey("total_score")

    val totalScore: Flow<Int> = context.scoreDataStore.data.map { preferences ->
        preferences[TOTAL_SCORE_KEY] ?: 0
    }

    suspend fun addScore(points: Int) {
        context.scoreDataStore.edit { preferences ->
            val current = preferences[TOTAL_SCORE_KEY] ?: 0
            preferences[TOTAL_SCORE_KEY] = current + points
        }
    }

    companion object {
        @Volatile
        private var instance: TotalScoreManager? = null

        fun getInstance(context: Context): TotalScoreManager {
            return instance ?: synchronized(this) {
                instance ?: TotalScoreManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
