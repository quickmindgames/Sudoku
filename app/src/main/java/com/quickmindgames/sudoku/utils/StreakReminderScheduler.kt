package com.quickmindgames.sudoku.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object StreakReminderScheduler {
    fun schedule(context: Context) {
        val now = LocalDateTime.now()
        val targetTime = now.with(LocalTime.of(22, 0)) // 10pm
        val initialDelay = if (now.isBefore(targetTime)) {
            Duration.between(now, targetTime)
        } else {
            Duration.between(now, targetTime.plusDays(1))
        }
        val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "streak_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
