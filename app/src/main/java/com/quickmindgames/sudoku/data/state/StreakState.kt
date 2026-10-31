package com.quickmindgames.sudoku.data.state

import android.util.Base64
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Represents the state of the Streak (daily) challenge.
 */
data class StreakState(
    val streakCount: Int,
    val lastCompletedDate: String?,
    val activeDate: String?,
    val activeGame: GameState?
) {
    fun protectMissedDay(today: LocalDate): StreakState? {
        val completedDate = lastCompletedDate?.let(LocalDate::parse) ?: return null
        if (streakCount <= 0 || ChronoUnit.DAYS.between(completedDate, today) != 2L) {
            return null
        }
        return copy(lastCompletedDate = completedDate.plusDays(1).toString())
    }

    fun serialize(): String {
        val encodedGame = activeGame
            ?.serialize()
            ?.encodeToByteArray()
            ?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
            ?: "null"

        return buildString {
            append("COUNT:$streakCount\n")
            append("LAST_COMPLETED:${lastCompletedDate ?: "null"}\n")
            append("ACTIVE_DATE:${activeDate ?: "null"}\n")
            append("ACTIVE_GAME:$encodedGame\n")
        }
    }

    companion object {
        fun deserialize(data: String): StreakState? {
            return try {
                val lines = data.split("\n")
                var index = 0

                val count = lines[index++].substringAfter("COUNT:").toInt()

                val lastCompleted = lines[index++]
                    .substringAfter("LAST_COMPLETED:")
                    .takeIf { it != "null" }

                val activeDate = lines[index++]
                    .substringAfter("ACTIVE_DATE:")
                    .takeIf { it != "null" }

                val encodedGame = lines[index++].substringAfter("ACTIVE_GAME:")
                val activeGame = if (encodedGame == "null") {
                    null
                } else {
                    val decoded = Base64.decode(encodedGame, Base64.NO_WRAP)
                    GameState.deserialize(decoded.decodeToString())
                }

                StreakState(
                    streakCount = count,
                    lastCompletedDate = lastCompleted,
                    activeDate = activeDate,
                    activeGame = activeGame
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}