package com.quickmindgames.sudoku.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for storing streak game statistics
 */
@Entity(tableName = "streak_games")
data class StreakGameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "streak_day")
    val streakDay: Int,

    @ColumnInfo(name = "is_won")
    val isWon: Boolean,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "time_seconds")
    val timeSeconds: Int,

    @ColumnInfo(name = "date_completed")
    val dateCompleted: Long // timestamp in milliseconds
)

