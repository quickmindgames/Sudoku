package com.quickmindgames.sudoku.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SpinCooldownTest {

    private val cooldown = 4 * 60 * 60 * 1000L // 4 hours
    private val hour = 60 * 60 * 1000L

    /** A spin at wall time 100 h, 10 h after boot #7. */
    private val spin = SpinRecord(wallMillis = 100 * hour, elapsedMillis = 10 * hour, bootCount = 7)

    private fun now(wall: Long, elapsed: Long, boot: Int? = 7) = ClockReading(wall, elapsed, boot)

    @Test
    fun neverSpun_canSpin() {
        assertEquals(0L, SpinCooldown.remainingMillis(null, now(0, 0), cooldown))
    }

    @Test
    fun sameBoot_normalPassageOfTime() {
        val oneHourLater = now(wall = 101 * hour, elapsed = 11 * hour)
        assertEquals(3 * hour, SpinCooldown.remainingMillis(spin, oneHourLater, cooldown))

        val fiveHoursLater = now(wall = 105 * hour, elapsed = 15 * hour)
        assertEquals(0L, SpinCooldown.remainingMillis(spin, fiveHoursLater, cooldown))
    }

    @Test
    fun sameBoot_clockMovedForward_doesNotUnlock() {
        // 1 real hour passed, but the wall clock was pushed 2 days ahead.
        val cheated = now(wall = 149 * hour, elapsed = 11 * hour)
        assertEquals(3 * hour, SpinCooldown.remainingMillis(spin, cheated, cooldown))
    }

    @Test
    fun sameBoot_clockMovedBackward_doesNotExtendCooldown() {
        // 1 real hour passed, but the wall clock was set a year back.
        val rewound = now(wall = 100 * hour - 365 * 24 * hour, elapsed = 11 * hour)
        assertEquals(3 * hour, SpinCooldown.remainingMillis(spin, rewound, cooldown))
    }

    @Test
    fun afterReboot_clockMovedBackward_neverMoreThanOneCooldown() {
        val rebootedAndRewound = now(wall = 100 * hour - 365 * 24 * hour, elapsed = 1 * hour, boot = 8)
        assertEquals(cooldown, SpinCooldown.remainingMillis(spin, rebootedAndRewound, cooldown))
    }

    @Test
    fun afterReboot_usesWallClock() {
        val rebootedTwoHoursLater = now(wall = 102 * hour, elapsed = 1 * hour, boot = 8)
        assertEquals(2 * hour, SpinCooldown.remainingMillis(spin, rebootedTwoHoursLater, cooldown))
    }

    @Test
    fun bootClockWentBackwards_treatedAsReboot() {
        // Same boot count reported but uptime is smaller: can't be the same boot.
        val weird = now(wall = 102 * hour, elapsed = 1 * hour, boot = 7)
        assertEquals(2 * hour, SpinCooldown.remainingMillis(spin, weird, cooldown))
    }

    @Test
    fun unknownBootCount_fallsBackToClampedWallClock() {
        val noBoot = SpinRecord(wallMillis = 100 * hour, elapsedMillis = 10 * hour, bootCount = null)
        val cheated = now(wall = 149 * hour, elapsed = 11 * hour, boot = null)
        assertEquals(0L, SpinCooldown.remainingMillis(noBoot, cheated, cooldown))

        val rewound = now(wall = 50 * hour, elapsed = 11 * hour, boot = null)
        assertEquals(cooldown, SpinCooldown.remainingMillis(noBoot, rewound, cooldown))
    }

    @Test
    fun oldSpinRecord_withoutBootClock_usesClampedWallClock() {
        // Saved by an older app version: only the wall time is known.
        val old = SpinRecord(wallMillis = 100 * hour, elapsedMillis = null, bootCount = null)
        assertEquals(3 * hour, SpinCooldown.remainingMillis(old, now(101 * hour, 11 * hour), cooldown))
        assertEquals(cooldown, SpinCooldown.remainingMillis(old, now(10 * hour, 11 * hour), cooldown))
    }
}
