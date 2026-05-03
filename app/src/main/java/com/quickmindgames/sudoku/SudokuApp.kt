package com.quickmindgames.sudoku

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.quickmindgames.sudoku.utils.RemoteConfigManager

/**
 *
 * Created by sagar.tahelyani on 07/03/26
 *
 */
class SudokuApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (BuildConfig.DEBUG) {
            //FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = false
        }
        RemoteConfigManager.init()
    }
}