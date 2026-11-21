package com.quickmindgames.sudoku.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.milliseconds

/**
 * Holds the current local date as a flow so screens never compare against a stale "today".
 * Call [refresh] when the app resumes and run [tickAtMidnight] in a scope that lives while
 * the screen is shown, so the date also changes at midnight while the app is in the foreground.
 */
class CurrentDateProvider(private val clock: Clock = Clock.systemDefaultZone()) {
    private val _today = MutableStateFlow(LocalDate.now(clock))
    val today: StateFlow<LocalDate> = _today.asStateFlow()

    /** Re-reads the clock. Emits only when the date actually changed. */
    fun refresh() {
        _today.value = LocalDate.now(clock)
    }

    suspend fun tickAtMidnight(): Nothing {
        while (true) {
            delay(millisUntilNextMidnight(ZonedDateTime.now(clock)).milliseconds)
            refresh()
        }
    }

    companion object {
        /** Milliseconds from [now] until the next local midnight, plus a small safety margin. */
        fun millisUntilNextMidnight(now: ZonedDateTime): Long {
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
            return Duration.between(now, nextMidnight).toMillis() + MIDNIGHT_MARGIN_MILLIS
        }

        private const val MIDNIGHT_MARGIN_MILLIS = 1_000L
    }
}
