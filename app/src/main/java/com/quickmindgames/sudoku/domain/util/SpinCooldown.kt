package com.quickmindgames.sudoku.domain.util

/**
 * A moment read from the device clocks.
 *
 * @param wallMillis wall-clock time ([System.currentTimeMillis]); the user can change it.
 * @param elapsedMillis time since boot (`SystemClock.elapsedRealtime()`); the user can't
 *   change it, but it restarts at every reboot.
 * @param bootCount the device boot counter, or `null` if it couldn't be read.
 */
data class ClockReading(
    val wallMillis: Long,
    val elapsedMillis: Long,
    val bootCount: Int?
)

/**
 * When the last prize-wheel spin happened. [elapsedMillis] and [bootCount] are `null` for
 * spins saved before they were recorded (older app versions).
 */
data class SpinRecord(
    val wallMillis: Long,
    val elapsedMillis: Long?,
    val bootCount: Int?
)

/**
 * Prize-wheel cooldown that doesn't trust the wall clock.
 *
 * - Same boot as the last spin: the time passed is measured with the boot clock, so moving
 *   the wall clock forward can't unlock a spin, and moving it back can't lock the wheel.
 * - After a reboot (or for an old spin record): the boot clock has restarted, so the wall
 *   clock is the only reference. The result is always clamped to `0..cooldown`, so a clock
 *   moved backwards never leaves more than one full cooldown.
 */
object SpinCooldown {

    fun remainingMillis(last: SpinRecord?, now: ClockReading, cooldownMillis: Long): Long {
        if (last == null) return 0L
        val sameBoot = last.elapsedMillis != null &&
            last.bootCount != null &&
            last.bootCount == now.bootCount &&
            now.elapsedMillis >= last.elapsedMillis
        val passedMillis = if (sameBoot) {
            now.elapsedMillis - last.elapsedMillis!!
        } else {
            now.wallMillis - last.wallMillis
        }
        return (cooldownMillis - passedMillis).coerceIn(0L, cooldownMillis)
    }
}
