package com.quickmindgames.sudoku.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quickmindgames.sudoku.data.database.SudokuDatabase
import com.quickmindgames.sudoku.data.database.entity.GameStatisticEntity
import com.quickmindgames.sudoku.data.database.entity.StreakGameEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Seeded-data checks for the statistics queries, on an in-memory database. */
@RunWith(AndroidJUnit4::class)
class StatisticsDaoTest {

    private lateinit var db: SudokuDatabase
    private lateinit var streakDao: StreakGameDao
    private lateinit var statsDao: GameStatisticDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, SudokuDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        streakDao = db.streakGameDao()
        statsDao = db.gameStatisticDao()
    }

    @After
    fun tearDown() = db.close()

    private suspend fun streak(day: Int, won: Boolean, time: Int, at: Long) =
        streakDao.insertStreakGame(
            StreakGameEntity(streakDay = day, isWon = won, score = 10, timeSeconds = time, dateCompleted = at)
        )

    private suspend fun regular(difficulty: String, won: Boolean, time: Int, at: Long) =
        statsDao.insertGameStatistic(
            GameStatisticEntity(
                difficulty = difficulty, isWon = won, mistakes = if (won) 0 else 3,
                score = 10, timeSeconds = time, isFlawFree = won, dateCompleted = at
            )
        )

    @Test
    fun consecutiveWins_countsOnlyWinsAfterTheLatestLoss() = runBlocking {
        // Oldest -> newest: W W L W W W  => 3 consecutive, although there are 5 wins.
        streak(1, won = true, time = 60, at = 1_000)
        streak(2, won = true, time = 60, at = 2_000)
        streak(3, won = false, time = 60, at = 3_000)
        streak(3, won = true, time = 60, at = 4_000)
        streak(4, won = true, time = 60, at = 5_000)
        streak(5, won = true, time = 60, at = 6_000)

        assertEquals(3, streakDao.getCurrentConsecutiveWins().first())
        assertEquals(5, streakDao.getStreakWinsCount().first())
    }

    @Test
    fun consecutiveWins_isZeroWhenTheLatestGameIsALoss() = runBlocking {
        streak(1, won = true, time = 60, at = 1_000)
        streak(2, won = false, time = 60, at = 2_000)

        assertEquals(0, streakDao.getCurrentConsecutiveWins().first())
    }

    @Test
    fun consecutiveWins_countsAllWinsWhenThereIsNoLoss() = runBlocking {
        streak(1, won = true, time = 60, at = 1_000)
        streak(2, won = true, time = 60, at = 2_000)

        assertEquals(2, streakDao.getCurrentConsecutiveWins().first())
    }

    @Test
    fun consecutiveWins_isZeroWithNoGames() = runBlocking {
        assertEquals(0, streakDao.getCurrentConsecutiveWins().first())
    }

    @Test
    fun averageTimes_ignoreLostGames() = runBlocking {
        regular("Breeze", won = true, time = 100, at = 1_000)
        regular("Breeze", won = true, time = 200, at = 2_000)
        regular("Breeze", won = false, time = 10, at = 3_000) // quick loss must not pull it down
        regular("Pulse", won = true, time = 400, at = 4_000)
        streak(1, won = true, time = 90, at = 1_000)
        streak(2, won = false, time = 5, at = 2_000)

        assertEquals(150.0, statsDao.getAverageTimeByDifficulty("Breeze").first()!!, 0.001)
        assertEquals(700.0 / 3, statsDao.getAverageTimeOverall().first()!!, 0.001)
        assertEquals(90.0, streakDao.getAverageStreakTime().first()!!, 0.001)
    }

    @Test
    fun averageTime_isNullWhenThereAreOnlyLosses() = runBlocking {
        regular("Rage", won = false, time = 50, at = 1_000)

        assertNull(statsDao.getAverageTimeByDifficulty("Rage").first())
    }
}
