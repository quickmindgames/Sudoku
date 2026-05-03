package com.quickmindgames.sudoku.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for storing individual game statistics
 */
@Entity(tableName = "game_statistics")
data class GameStatisticEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "difficulty")
    val difficulty: String, // "Breeze", "Pulse", "Rage", "Elite"

    @ColumnInfo(name = "is_won")
    val isWon: Boolean,

    @ColumnInfo(name = "mistakes")
    val mistakes: Int,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "time_seconds")
    val timeSeconds: Int,

    @ColumnInfo(name = "is_flaw_free")
    val isFlawFree: Boolean, // true if mistakes == 0

    @ColumnInfo(name = "date_completed")
    val dateCompleted: Long // timestamp in milliseconds
)

