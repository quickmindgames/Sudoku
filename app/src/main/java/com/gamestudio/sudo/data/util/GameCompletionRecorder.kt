package com.gamestudio.sudo.data.util

import android.content.Context
import com.gamestudio.sudo.data.repository.StatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Utility class to record game completions to statistics database
 */
object GameCompletionRecorder {

    /**
     * Record a completed regular game
     */
    fun recordGameCompletion(
        context: Context,
        scope: CoroutineScope,
        difficulty: String,
        isWon: Boolean,
        mistakes: Int,
        score: Int,
        timeSeconds: Int
    ) {
        val repository = StatisticsRepository(context)
        scope.launch {
            repository.recordGameCompletion(
                difficulty = difficulty,
                isWon = isWon,
                mistakes = mistakes,
                score = score,
                timeSeconds = timeSeconds
            )
        }
    }

    /**
     * Record a completed streak game
     */
    fun recordStreakGameCompletion(
        context: Context,
        scope: CoroutineScope,
        streakDay: Int,
        isWon: Boolean,
        score: Int,
        timeSeconds: Int
    ) {
        val repository = StatisticsRepository(context)
        scope.launch {
            repository.recordStreakGameCompletion(
                streakDay = streakDay,
                isWon = isWon,
                score = score,
                timeSeconds = timeSeconds
            )
        }
    }
}

