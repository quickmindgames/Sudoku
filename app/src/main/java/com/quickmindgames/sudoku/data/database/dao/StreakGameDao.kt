package com.quickmindgames.sudoku.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.quickmindgames.sudoku.data.database.entity.StreakGameEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for StreakGameEntity
 */
@Dao
interface StreakGameDao {

    /**
     * Insert a new streak game record
     */
    @Insert
    suspend fun insertStreakGame(game: StreakGameEntity)

    /**
     * Get all streak games ordered by date (descending)
     */
    @Query("SELECT * FROM streak_games ORDER BY date_completed DESC")
    fun getAllStreakGames(): Flow<List<StreakGameEntity>>

    /**
     * Get streak games for a specific day range
     */
    @Query("SELECT * FROM streak_games WHERE streak_day >= :startDay AND streak_day <= :endDay ORDER BY streak_day ASC")
    fun getStreakGamesByDayRange(startDay: Int, endDay: Int): Flow<List<StreakGameEntity>>

    /**
     * Get total streak games count
     */
    @Query("SELECT COUNT(*) FROM streak_games")
    fun getTotalStreakGamesCount(): Flow<Int>

    /**
     * Get total wins in streak mode
     */
    @Query("SELECT COUNT(*) FROM streak_games WHERE is_won = 1")
    fun getStreakWinsCount(): Flow<Int>

    /**
     * Get total losses in streak mode
     */
    @Query("SELECT COUNT(*) FROM streak_games WHERE is_won = 0")
    fun getStreakLossesCount(): Flow<Int>

    /**
     * Get total score across all completed streak games
     */
    @Query("SELECT COALESCE(SUM(score), 0) FROM streak_games")
    fun getTotalStreakScore(): Flow<Long>

    /**
     * Get average completion time across all streak games
     */
    @Query("SELECT AVG(time_seconds) FROM streak_games")
    fun getAverageStreakTime(): Flow<Double?>

    /**
     * Get best time in streak mode (across all days)
     */
    @Query("SELECT MIN(time_seconds) FROM streak_games WHERE is_won = 1")
    fun getBestStreakTime(): Flow<Int?>

    /**
     * Get best score in streak mode (across all days)
     */
    @Query("SELECT MAX(score) FROM streak_games WHERE is_won = 1")
    fun getBestStreakScore(): Flow<Int?>

    /**
     * Get the highest streak day reached by winning a game
     */
    @Query("SELECT MAX(streak_day) FROM streak_games WHERE is_won = 1")
    fun getHighestStreakDay(): Flow<Int?>

    /**
     * Get consecutive wins from most recent games
     * Returns total streak wins (user needs to track sequence in ViewModel)
     */
    @Query("""
        SELECT COUNT(*) FROM streak_games WHERE is_won = 1
    """)
    fun getCurrentConsecutiveWins(): Flow<Int>

    /**
     * Delete all streak games (for testing/reset)
     */
    @Query("DELETE FROM streak_games")
    suspend fun deleteAllStreakGames()
}
