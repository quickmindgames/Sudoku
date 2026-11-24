package com.quickmindgames.sudoku.domain.util

import com.quickmindgames.sudoku.domain.model.Difficulty

/**
 * Wins that decide which difficulties are unlocked:
 * Breeze is always open; Pulse needs [WINS_TO_UNLOCK] Breeze wins, Rage needs that many
 * Pulse wins, and Elite needs that many Rage wins.
 */
data class DifficultyUnlocks(
    val breezeWins: Int,
    val pulseWins: Int,
    val rageWins: Int
) {
    fun isUnlocked(difficulty: Difficulty): Boolean = when (difficulty) {
        Difficulty.Breeze -> true
        Difficulty.Pulse -> breezeWins >= WINS_TO_UNLOCK
        Difficulty.Rage -> pulseWins >= WINS_TO_UNLOCK
        Difficulty.Elite -> rageWins >= WINS_TO_UNLOCK
    }

    companion object {
        const val WINS_TO_UNLOCK = 5

        /** Shown when a locked difficulty is tapped. Empty for Breeze (never locked). */
        fun lockedMessage(difficulty: Difficulty): String = when (difficulty) {
            Difficulty.Breeze -> ""
            Difficulty.Pulse -> "Win $WINS_TO_UNLOCK Breeze games to unlock Pulse"
            Difficulty.Rage -> "Win $WINS_TO_UNLOCK Pulse games to unlock Rage"
            Difficulty.Elite -> "Win $WINS_TO_UNLOCK Rage games to unlock Elite"
        }
    }
}
