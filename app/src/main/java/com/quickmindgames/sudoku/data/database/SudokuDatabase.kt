package com.quickmindgames.sudoku.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quickmindgames.sudoku.data.database.dao.GameStatisticDao
import com.quickmindgames.sudoku.data.database.dao.StreakGameDao
import com.quickmindgames.sudoku.data.database.entity.GameStatisticEntity
import com.quickmindgames.sudoku.data.database.entity.StreakGameEntity

/**
 * Room database for Sudoku game statistics.
 *
 * Schema changes: bump [version], add an AutoMigration or Migration, commit the new
 * `app/schemas/.../<version>.json`, and add a test in `SudokuDatabaseMigrationTest`.
 * Never add fallbackToDestructiveMigration(): it would wipe users' statistics.
 */
@Database(
    entities = [GameStatisticEntity::class, StreakGameEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SudokuDatabase : RoomDatabase() {

    abstract fun gameStatisticDao(): GameStatisticDao
    abstract fun streakGameDao(): StreakGameDao

    companion object {
        private const val DATABASE_NAME = "sudoku_database"

        @Volatile
        private var instance: SudokuDatabase? = null

        /**
         * Get the singleton instance of SudokuDatabase
         * Uses lazy initialization pattern for thread safety
         */
        fun getInstance(context: Context): SudokuDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SudokuDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
        }
    }
}

