package com.quickmindgames.sudoku.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakReminderPromptTest {

    @Test
    fun firstStreakWin_showsPrompt() {
        assertTrue(StreakReminderPrompt.shouldPrompt(false, streakWins = 1, nextPromptAtWins = 0))
    }

    @Test
    fun remindersAlreadyOn_neverShowsPrompt() {
        assertFalse(StreakReminderPrompt.shouldPrompt(true, streakWins = 50, nextPromptAtWins = 0))
    }

    @Test
    fun afterDecline_asksAgainFiveStreakWinsLater() {
        val next = StreakReminderPrompt.nextPromptAfterDecline(streakWins = 1)
        assertEquals(6, next)
        for (wins in 2..5) {
            assertFalse("wins $wins", StreakReminderPrompt.shouldPrompt(false, wins, next))
        }
        assertTrue(StreakReminderPrompt.shouldPrompt(false, streakWins = 6, nextPromptAtWins = next))
    }

    @Test
    fun keepsAskingEveryFiveWinsUntilTurnedOn() {
        var next = 0
        val promptedAt = mutableListOf<Int>()
        for (wins in 1..16) {
            if (StreakReminderPrompt.shouldPrompt(false, wins, next)) {
                promptedAt += wins
                next = StreakReminderPrompt.nextPromptAfterDecline(wins) // declined every time
            }
        }
        assertEquals(listOf(1, 6, 11, 16), promptedAt)
    }

    @Test
    fun turnedOffInSettings_neverShowsPromptAgain() {
        assertFalse(
            StreakReminderPrompt.shouldPrompt(
                false,
                streakWins = 1_000,
                nextPromptAtWins = StreakReminderPrompt.NEVER
            )
        )
    }
}
