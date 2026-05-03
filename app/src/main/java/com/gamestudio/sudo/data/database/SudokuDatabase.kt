package com.gamestudio.sudo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gamestudio.sudo.data.database.dao.GameStatisticDao
import com.gamestudio.sudo.data.database.dao.StreakGameDao
import com.gamestudio.sudo.data.database.entity.GameStatisticEntity
import com.gamestudio.sudo.data.database.entity.StreakGameEntity

/**
 * Room database for Sudoku game statistics
 */
@Database(
    entities = [GameStatisticEntity::class, StreakGameEntity::class],
    version = 1,
    exportSchema = false
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
                )
                    .fallbackToDestructiveMigration() // Use only for development
                    .build()
                    .also { instance = it }
            }
        }
    }
}

