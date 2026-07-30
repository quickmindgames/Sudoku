package com.quickmindgames.sudoku.data.repository

import android.content.Context
import com.quickmindgames.sudoku.data.database.SudokuDatabase
import com.quickmindgames.sudoku.data.database.entity.GameStatisticEntity
import com.quickmindgames.sudoku.data.database.entity.StreakGameEntity
import com.quickmindgames.sudoku.domain.model.AllStatistics
import com.quickmindgames.sudoku.domain.model.DifficultyStatistics
import com.quickmindgames.sudoku.domain.model.GameStatistics
import com.quickmindgames.sudoku.domain.model.StreakStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Repository for managing game statistics
 * Uses lazy singleton pattern for database initialization
 */
class StatisticsRepository(context: Context) {

    private val db: SudokuDatabase by lazy {
        SudokuDatabase.getInstance(context)
    }

    private val gameStatisticDao by lazy { db.gameStatisticDao() }
    private val streakGameDao by lazy { db.streakGameDao() }

    /**
     * Record a completed regular game
     */
    suspend fun recordGameCompletion(
        difficulty: String,
        isWon: Boolean,
        mistakes: Int,
        score: Int,
        timeSeconds: Int
    ) {
        val statistic = GameStatisticEntity(
            difficulty = difficulty,
            isWon = isWon,
            mistakes = mistakes,
            score = score,
            timeSeconds = timeSeconds,
            isFlawFree = mistakes == 0,
            dateCompleted = System.currentTimeMillis()
        )
        gameStatisticDao.insertGameStatistic(statistic)
    }

    /**
     * Record a completed streak game
     */
    suspend fun recordStreakGameCompletion(
        streakDay: Int,
        isWon: Boolean,
        score: Int,
        timeSeconds: Int
    ) {
        val streakGame = StreakGameEntity(
            streakDay = streakDay,
            isWon = isWon,
            score = score,
            timeSeconds = timeSeconds,
            dateCompleted = System.currentTimeMillis()
        )
        streakGameDao.insertStreakGame(streakGame)
    }

    /**
     * Get overall statistics across all games
     */
    fun getOverallStatistics(): Flow<GameStatistics> {
        return combine(
            gameStatisticDao.getTotalGamesCount(),
            gameStatisticDao.getTotalWinsCount(),
            gameStatisticDao.getTotalLossesCount(),
            gameStatisticDao.getBestTimeOverall(),
            gameStatisticDao.getBestScoreOverall(),
            gameStatisticDao.getFlawFreeWinsCountOverall()
        ) { values ->
            val totalGames = values[0] as Int
            val wins = values[1] as Int
            val losses = values[2] as Int
            val bestTime = values[3]
            val bestScore = values[4]
            val flawFree = values[5] as Int

            val winRatio = if (totalGames > 0) (wins.toDouble() / totalGames) * 100 else 0.0
            GameStatistics(
                totalGames = totalGames,
                totalWins = wins,
                totalLosses = losses,
                winRatioPercentage = winRatio,
                bestTime = bestTime,
                bestScore = bestScore,
                flawFreeWins = flawFree
            )
        }
    }

    /**
     * Get statistics for a specific difficulty
     */
    fun getDifficultyStatistics(difficulty: String): Flow<DifficultyStatistics> {
        return combine(
            gameStatisticDao.getTotalGamesByDifficulty(difficulty),
            gameStatisticDao.getWinsCountByDifficulty(difficulty),
            gameStatisticDao.getLossesCountByDifficulty(difficulty),
            gameStatisticDao.getBestTimeByDifficulty(difficulty),
            gameStatisticDao.getBestScoreByDifficulty(difficulty),
            gameStatisticDao.getFlawFreeWinsCountByDifficulty(difficulty)
        ) { values ->
            val totalGames = values[0] as Int
            val wins = values[1] as Int
            val losses = values[2] as Int
            val bestTime = values[3]
            val bestScore = values[4]
            val flawFree = values[5] as Int

            val winRatio = if (totalGames > 0) (wins.toDouble() / totalGames) * 100 else 0.0
            DifficultyStatistics(
                difficulty = difficulty,
                totalGames = totalGames,
                wins = wins,
                losses = losses,
                winRatioPercentage = winRatio,
                bestTime = bestTime,
                bestScore = bestScore,
                flawFreeWins = flawFree
            )
        }
    }

    /**
     * Get statistics for all difficulties
     */
    fun getAllDifficultiesStatistics(): Flow<Quadruple<DifficultyStatistics, DifficultyStatistics, DifficultyStatistics, DifficultyStatistics>> {
        return combine(
            getDifficultyStatistics("Breeze"),
            getDifficultyStatistics("Pulse"),
            getDifficultyStatistics("Rage"),
            getDifficultyStatistics("Elite")
        ) { breeze, pulse, rage, elite ->
            Quadruple(breeze, pulse, rage, elite)
        }
    }

    /**
     * Get streak statistics
     */
    fun getStreakStatistics(): Flow<StreakStatistics> {
        return combine(
            streakGameDao.getTotalStreakGamesCount(),
            streakGameDao.getStreakWinsCount(),
            streakGameDao.getStreakLossesCount(),
            streakGameDao.getBestStreakTime(),
            streakGameDao.getBestStreakScore(),
            streakGameDao.getHighestStreakDay(),
            streakGameDao.getCurrentConsecutiveWins()
        ) { values ->
            val totalGames = values[0] as Int
            val wins = values[1] as Int
            val losses = values[2] as Int
            val bestTime = values[3]
            val bestScore = values[4]
            val highestDay = values[5]
            val consecutiveWins = values[6] as Int

            val winRatio = if (totalGames > 0) (wins.toDouble() / totalGames) * 100 else 0.0
            StreakStatistics(
                totalGames = totalGames,
                totalWins = wins,
                totalLosses = losses,
                winRatioPercentage = winRatio,
                bestTime = bestTime,
                bestScore = bestScore,
                highestStreakDay = highestDay ?: 0,
                currentConsecutiveWins = consecutiveWins
            )
        }
    }

    /**
     * Get all statistics combined
     */
    fun getAllStatistics(): Flow<AllStatistics> {
        return combine(
            getOverallStatistics(),
            getDifficultyStatistics("Breeze"),
            getDifficultyStatistics("Pulse"),
            getDifficultyStatistics("Rage"),
            getDifficultyStatistics("Elite"),
            getStreakStatistics()
        ) { values ->
            val overall = values[0] as GameStatistics
            val breeze = values[1] as DifficultyStatistics
            val pulse = values[2] as DifficultyStatistics
            val rage = values[3] as DifficultyStatistics
            val elite = values[4] as DifficultyStatistics
            val streak = values[5] as StreakStatistics

            AllStatistics(
                overallStats = overall,
                breeze = breeze,
                pulse = pulse,
                rage = rage,
                elite = elite,
                streak = streak
            )
        }
    }

    /**
     * Clear all statistics (for development/testing)
     */
    suspend fun clearAllStatistics() {
        gameStatisticDao.deleteAllGameStatistics()
        streakGameDao.deleteAllStreakGames()
    }
}

/**
 * Helper data class for combining 4 flows
 */
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

