package com.quickmindgames.sudoku.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class CurrentDateProviderTest {

    private class MutableClock(var instant: Instant, private val zone: ZoneId) : Clock() {
        override fun getZone(): ZoneId = zone
        override fun withZone(zone: ZoneId): Clock = MutableClock(instant, zone)
        override fun instant(): Instant = instant
    }

    private val zone = ZoneId.of("Asia/Kolkata")

    @Test
    fun refresh_afterMidnight_emitsNewDate() {
        val beforeMidnight = ZonedDateTime.of(2026, 11, 20, 23, 58, 0, 0, zone)
        val clock = MutableClock(beforeMidnight.toInstant(), zone)
        val provider = CurrentDateProvider(clock)
        assertEquals(LocalDate.of(2026, 11, 20), provider.today.value)

        clock.instant = beforeMidnight.plusMinutes(5).toInstant()
        provider.refresh()

        assertEquals(LocalDate.of(2026, 11, 21), provider.today.value)
    }

    @Test
    fun refresh_sameDay_keepsDate() {
        val morning = ZonedDateTime.of(2026, 11, 20, 9, 0, 0, 0, zone)
        val clock = MutableClock(morning.toInstant(), zone)
        val provider = CurrentDateProvider(clock)

        clock.instant = morning.plusHours(10).toInstant()
        provider.refresh()

        assertEquals(LocalDate.of(2026, 11, 20), provider.today.value)
    }

    @Test
    fun millisUntilNextMidnight_returnsTimeToNextDayPlusMargin() {
        val now = ZonedDateTime.of(2026, 11, 20, 23, 59, 0, 0, zone)

        assertEquals(61_000L, CurrentDateProvider.millisUntilNextMidnight(now))
    }

    @Test
    fun millisUntilNextMidnight_atMidnight_waitsFullDay() {
        val now = ZonedDateTime.of(2026, 11, 20, 0, 0, 0, 0, zone)

        assertEquals(86_401_000L, CurrentDateProvider.millisUntilNextMidnight(now))
    }
}
