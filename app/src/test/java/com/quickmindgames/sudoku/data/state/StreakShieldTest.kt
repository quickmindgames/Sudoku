package com.quickmindgames.sudoku.data.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class StreakShieldTest {
    @Test
    fun protectingMissedDayAdvancesDateWithoutIncreasingStreak() {
        val state = StreakState(
            streakCount = 12,
            lastCompletedDate = "2026-10-10",
            activeDate = null,
            activeGame = null
        )

        val protected = state.protectMissedDay(LocalDate.parse("2026-10-12"))

        assertEquals("2026-10-11", protected?.lastCompletedDate)
        assertEquals(12, protected?.streakCount)
    }

    @Test
    fun anotherShieldCanProtectTheNextMissedDay() {
        val state = StreakState(
            streakCount = 12,
            lastCompletedDate = "2026-10-10",
            activeDate = null,
            activeGame = null
        )

        val firstProtection = state.protectMissedDay(LocalDate.parse("2026-10-12"))
        val secondProtection = firstProtection?.protectMissedDay(LocalDate.parse("2026-10-13"))

        assertEquals("2026-10-12", secondProtection?.lastCompletedDate)
        assertEquals(12, secondProtection?.streakCount)
    }

    @Test
    fun shieldCannotProtectWhenStreakIsMoreThanOneMissedDayOld() {
        val state = StreakState(
            streakCount = 12,
            lastCompletedDate = "2026-10-10",
            activeDate = null,
            activeGame = null
        )

        assertNull(state.protectMissedDay(LocalDate.parse("2026-10-13")))
    }
}
