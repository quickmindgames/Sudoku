package com.quickmindgames.sudoku.domain.util

import com.quickmindgames.sudoku.domain.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DifficultyUnlocksTest {

    @Test
    fun newPlayer_onlyBreezeIsUnlocked() {
        val unlocks = DifficultyUnlocks(breezeWins = 0, pulseWins = 0, rageWins = 0)
        assertTrue(unlocks.isUnlocked(Difficulty.Breeze))
        assertFalse(unlocks.isUnlocked(Difficulty.Pulse))
        assertFalse(unlocks.isUnlocked(Difficulty.Rage))
        assertFalse(unlocks.isUnlocked(Difficulty.Elite))
    }

    @Test
    fun eachLevel_unlocksAtExactlyFiveWinsOfThePreviousLevel() {
        assertFalse(DifficultyUnlocks(4, 0, 0).isUnlocked(Difficulty.Pulse))
        assertTrue(DifficultyUnlocks(5, 0, 0).isUnlocked(Difficulty.Pulse))
        assertFalse(DifficultyUnlocks(5, 4, 0).isUnlocked(Difficulty.Rage))
        assertTrue(DifficultyUnlocks(5, 5, 0).isUnlocked(Difficulty.Rage))
        assertFalse(DifficultyUnlocks(5, 5, 4).isUnlocked(Difficulty.Elite))
        assertTrue(DifficultyUnlocks(5, 5, 5).isUnlocked(Difficulty.Elite))
    }

    @Test
    fun eachLevel_dependsOnlyOnThePreviousLevel() {
        // Same rule as before the refactor: Rage needs Pulse wins, regardless of Breeze.
        assertTrue(DifficultyUnlocks(0, 5, 0).isUnlocked(Difficulty.Rage))
        assertTrue(DifficultyUnlocks(0, 0, 5).isUnlocked(Difficulty.Elite))
    }

    @Test
    fun lockedMessages_matchThePreviousText() {
        assertEquals("", DifficultyUnlocks.lockedMessage(Difficulty.Breeze))
        assertEquals("Win 5 Breeze games to unlock Pulse", DifficultyUnlocks.lockedMessage(Difficulty.Pulse))
        assertEquals("Win 5 Pulse games to unlock Rage", DifficultyUnlocks.lockedMessage(Difficulty.Rage))
        assertEquals("Win 5 Rage games to unlock Elite", DifficultyUnlocks.lockedMessage(Difficulty.Elite))
    }
}
