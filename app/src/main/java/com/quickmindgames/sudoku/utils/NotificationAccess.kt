package com.quickmindgames.sudoku.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first

/** The Activity behind a (possibly wrapped) Compose context, if any. */
fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return currentContext as? Activity
}

/** Opens this app's notification settings page. */
fun Context.openAppNotificationSettings() {
    startActivity(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            if (this@openAppNotificationSettings !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

/**
 * True when Android 13+ will no longer show the notification permission dialog: it has
 * been requested at least twice and the system no longer offers a rationale (the user
 * denied it twice, or chose "Don't allow" permanently). Only Settings can grant it then.
 */
suspend fun Context.isNotificationPermissionPermanentlyDenied(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
    if (StreakReminderScheduler.hasNotificationPermission(this)) return false
    val attempts = AppPreferences.getInstance(this).notificationPermissionRequestAttempts.first()
    val showRationale = findActivity()?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(
            it,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } ?: false
    return attempts >= 2 && !showRationale
}
