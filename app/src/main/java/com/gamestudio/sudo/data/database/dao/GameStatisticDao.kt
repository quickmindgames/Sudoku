package com.gamestudio.sudo.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gamestudio.sudo.data.database.entity.GameStatisticEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for GameStatisticEntity
 */
@Dao
interface GameStatisticDao {

    /**
     * Insert a new game statistic record
     */
    @Insert
    suspend fun insertGameStatistic(statistic: GameStatisticEntity)

    /**
     * Get all game statistics
     */
    @Query("SELECT * FROM game_statistics ORDER BY date_completed DESC")
    fun getAllGameStatistics(): Flow<List<GameStatisticEntity>>

    /**
     * Get game statistics for a specific difficulty
     */
    @Query("SELECT * FROM game_statistics WHERE difficulty = :difficulty ORDER BY date_completed DESC")
    fun getGameStatisticsByDifficulty(difficulty: String): Flow<List<GameStatisticEntity>>

    /**
     * Get total games count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty")
    fun getTotalGamesByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get total wins count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 1")
    fun getWinsCountByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get total losses count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 0")
    fun getLossesCountByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get best (minimum) time for a specific difficulty
     */
    @Query("SELECT MIN(time_seconds) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 1")
    fun getBestTimeByDifficulty(difficulty: String): Flow<Int?>

    /**
     * Get best (maximum) score for a specific difficulty
     */
    @Query("SELECT MAX(score) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 1")
    fun getBestScoreByDifficulty(difficulty: String): Flow<Int?>

    /**
     * Get flaw-free wins (mistakes == 0) count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty AND is_flaw_free = 1 AND is_won = 1")
    fun getFlawFreeWinsCountByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get total games count across all difficulties
     */
    @Query("SELECT COUNT(*) FROM game_statistics")
    fun getTotalGamesCount(): Flow<Int>

    /**
     * Get total wins count across all difficulties
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE is_won = 1")
    fun getTotalWinsCount(): Flow<Int>

    /**
     * Get total losses count across all difficulties
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE is_won = 0")
    fun getTotalLossesCount(): Flow<Int>

    /**
     * Get best (minimum) time across all games
     */
    @Query("SELECT MIN(time_seconds) FROM game_statistics WHERE is_won = 1")
    fun getBestTimeOverall(): Flow<Int?>

    /**
     * Get best (maximum) score across all games
     */
    @Query("SELECT MAX(score) FROM game_statistics WHERE is_won = 1")
    fun getBestScoreOverall(): Flow<Int?>

    /**
     * Get flaw-free wins count across all games
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE is_flaw_free = 1 AND is_won = 1")
    fun getFlawFreeWinsCountOverall(): Flow<Int>

    /**
     * Delete all game statistics (for testing/reset)
     */
    @Query("DELETE FROM game_statistics")
    suspend fun deleteAllGameStatistics()
}

