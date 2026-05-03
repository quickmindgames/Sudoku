package com.quickmindgames.sudoku.utils

import android.content.Context
import android.content.Intent

fun Context.shareStreak(streakCount: Int, appName: String) {
    AnalyticsUtils.logStreakShare(this, streakCount)
    val shareText = buildString {
        append("\uD83D\uDD25 I'm on a $streakCount day streak in $appName!\n")
        append("Can you beat my streak? Play now:\n")
        append("https://play.google.com/store/apps/details?id=$packageName")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    startActivity(Intent.createChooser(intent, "Share your streak"))
}