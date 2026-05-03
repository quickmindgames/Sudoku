package com.quickmindgames.sudoku.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.presentation.ui.MainActivity
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class StreakReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val streakStateManager = StreakStateManager.getInstance(applicationContext)
        val streakState = streakStateManager.getStreakState().first()
        val today = LocalDate.now().toString()
        if (streakState?.lastCompletedDate != today) {
            sendNotification()
        }
        return Result.success()
    }

    private fun sendNotification() {
        val channelId = "streak_reminder"
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Streak Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
        // Android 13+ (API 33): Check POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, do not post notification
                return
            }
        }
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
            .setSmallIcon(R.mipmap.ic_launcher) // Use app launcher icon
            .setContentTitle("Don't lose your streak!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Hey, you haven't played streak for today, play now otherwise your streak will be lost.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(1001, notification)
        AnalyticsUtils.logNotificationReceived(applicationContext)
    }
}
