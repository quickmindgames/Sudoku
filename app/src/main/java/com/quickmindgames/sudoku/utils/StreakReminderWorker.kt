package com.quickmindgames.sudoku.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.presentation.ui.MainActivity
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class StreakReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val appPreferences = AppPreferences.getInstance(applicationContext)
        if (!appPreferences.streakRemindersEnabled.first()) return Result.success()

        val streakStateManager = StreakStateManager.getInstance(applicationContext)
        val streakState = streakStateManager.getStreakState().first()
        val today = LocalDate.now().toString()
        // A null state (fresh install, never played a streak) is treated as
        // streak 0, so these users still get the "Start your streak!" nudge
        // instead of the worker crashing with an NPE.
        when {
            streakState == null || streakState.streakCount == 0 -> {
                sendNotification(
                    title = "Start your streak!",
                    message = "Hey, you haven't started your streak yet, start your streak now!"
                )
            }

            streakState.lastCompletedDate != today -> {
                sendNotification(
                    title = "Don't lose your streak!",
                    message = "Hey, you haven't played streak for today, play now to keep it alive!"
                )
            }
        }
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        if (!StreakReminderScheduler.hasNotificationAccess(applicationContext)) return

        val channelId = StreakReminderScheduler.CHANNEL_ID
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // IMPORTANCE_DEFAULT: sound and status-bar icon, but no heads-up banner for a daily
        // reminder. For an existing channel created with IMPORTANCE_HIGH, Android applies
        // this lower importance as long as the user hasn't changed the channel themselves.
        val channel = NotificationChannel(
            channelId,
            "Streak Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        // --- Add PendingIntent for notification click ---
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("from_streak_notification", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            // Monochrome status-bar icon (the launcher icon renders as a white square).
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setColor(ContextCompat.getColor(applicationContext, R.color.notification_accent))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(1001, notification)
        AnalyticsUtils.logNotificationReceived(applicationContext)
    }
}
