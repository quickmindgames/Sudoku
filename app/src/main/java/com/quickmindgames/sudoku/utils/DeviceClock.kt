package com.quickmindgames.sudoku.utils

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import com.quickmindgames.sudoku.domain.util.ClockReading

/** Reads the device clocks for [com.quickmindgames.sudoku.domain.util.SpinCooldown]. */
object DeviceClock {

    fun now(context: Context): ClockReading = ClockReading(
        wallMillis = System.currentTimeMillis(),
        elapsedMillis = SystemClock.elapsedRealtime(),
        bootCount = bootCount(context)
    )

    private fun bootCount(context: Context): Int? =
        runCatching {
            Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
        }.getOrNull()?.takeIf { it >= 0 }
}
