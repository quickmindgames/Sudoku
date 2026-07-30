package com.quickmindgames.sudoku.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsUtils {
    /**
     * Logs a screen view event to Firebase Analytics.
     *
     * @param context The context from which the event is logged.
     * @param screenName The name of the screen being viewed (e.g., "Home", "Statistics").
     * @param screenClass The class name of the screen being viewed.
     */
    fun logScreenView(
        context: Context,
        screenName: String,
        screenClass: String
    ) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /**
     * Logs a "new_game_started" event to Firebase Analytics with the specified difficulty level and resume state.
     *
     * @param context The context from which the event is logged.
     * @param difficulty The difficulty level of the new game (e.g., "Breeze", "Pulse", "Rage", "Elite").
     * @param hasSavedGame Whether a resume game existed before starting a new game.
     */
    fun logNewGameStarted(context: Context, difficulty: String, hasSavedGame: String?) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.DIFFICULTY, difficulty)
            putString(AnalyticsConstants.SAVED_GAME, hasSavedGame)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.NEW_GAME, bundle)
    }

    /**
     * Logs a "resume_game" event to Firebase Analytics with the specified difficulty level.
     *
     * @param context The context from which the event is logged.
     * @param difficulty The difficulty level of the resumed game (e.g., "Breeze", "Pulse", "Rage", "Elite").
     */
    fun logResumeGame(context: Context, difficulty: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.DIFFICULTY, difficulty)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.RESUME_GAME, bundle)
    }

    /**
     * Logs a "streak_game_started" event to Firebase Analytics.
     *
     * @param context The context from which the event is logged.
     */
    fun logStreakGameStarted(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putBoolean(AnalyticsConstants.STREAK_STARTED, true)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.STREAK_GAME, bundle)
    }

    /**
     * Logs a "streak_game_completed" event to Firebase Analytics with the specified streak count.
     *
     * @param context The context from which the event is logged.
     * @param streakCount The current streak count when the game was completed.
     */
    fun logStreakGameCompleted(context: Context, streakCount: Int) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putInt(AnalyticsConstants.STREAK_COUNT, streakCount)
            putBoolean(AnalyticsConstants.STREAK_COMPLETED, true)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.STREAK_GAME, bundle)
    }

    /**
     * Logs a "learning_mode" event to Firebase Analytics when a lesson is started.
     *
     * @param context The context from which the event is logged.
     */
    fun logLearningStarted(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putBoolean(AnalyticsConstants.LESSON_STARTED, true)
        }
        firebaseAnalytics.logEvent("learning_mode", bundle)
    }

    /**
     * Logs a "learning_mode" event to Firebase Analytics when a lesson is completed.
     *
     * @param context The context from which the event is logged.
     */
    fun logLearningCompleted(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putBoolean(AnalyticsConstants.LESSON_COMPLETED, true)
        }
        firebaseAnalytics.logEvent("learning_mode", bundle)
    }

    /**
     * Logs a "game_result" event to Firebase Analytics with game outcome and stats.
     *
     * @param context The context from which the event is logged.
     * @param win Whether the game was won (true) or lost (false).
     * @param time Time taken to complete the game.
     * @param mistakes Number of mistakes made in the game.
     * @param score Score achieved in the game.
     * @param difficulty Difficulty level of the game.
     * @param isStreak Whether the game was a streak game.
     * @param hintCount Number of hints used in the game.
     */
    fun logGameResult(
        context: Context,
        win: Boolean,
        time: String,
        mistakes: Int,
        score: Int,
        difficulty: String,
        isStreak: Boolean = false,
        hintCount: Int = 0
    ) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(
                AnalyticsConstants.RESULT,
                if (win) AnalyticsConstants.WIN else AnalyticsConstants.LOSS
            )
            putString(AnalyticsConstants.TIME, time)
            putInt(AnalyticsConstants.MISTAKES, mistakes)
            putInt(AnalyticsConstants.SCORE, score)
            putString(AnalyticsConstants.DIFFICULTY, difficulty)
            putBoolean(AnalyticsConstants.IS_STREAK, isStreak)
            putInt(AnalyticsConstants.HINT_COUNT, hintCount)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_RESULT, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when a hint is used.
     *
     * @param context The context from which the event is logged.
     * @param difficulty The difficulty level of the game.
     */
    fun logHintUsed(context: Context, difficulty: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.ACTION, AnalyticsConstants.HINT)
            putString(AnalyticsConstants.DIFFICULTY, difficulty)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when notes mode is toggled.
     *
     * @param context The context from which the event is logged.
     * @param enabled Whether notes mode was turned on (true) or off (false).
     */
    fun logNotesToggled(context: Context, enabled: Boolean) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.ACTION, AnalyticsConstants.NOTES)
            putString(
                AnalyticsConstants.STATE,
                if (enabled) AnalyticsConstants.ON else AnalyticsConstants.OFF
            )
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when undo is used.
     *
     * @param context The context from which the event is logged.
     */
    fun logUndoUsed(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.ACTION, AnalyticsConstants.UNDO)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when erase is used.
     *
     * @param context The context from which the event is logged.
     */
    fun logEraseUsed(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.ACTION, AnalyticsConstants.ERASE)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "theme_toggled" event to Firebase Analytics when the theme is changed.
     *
     * @param context The context from which the event is logged.
     * @param theme The theme selected by the user (e.g., "light_theme", "dark_theme").
     */
    fun logThemeToggled(context: Context, theme: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.THEME, theme)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.THEME_TOGGLED, params)
    }

    /**
     * Logs a "notification_received" event to Firebase Analytics when a notification is received.
     *
     * @param context The context from which the event is logged.
     */
    fun logNotificationReceived(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.logEvent(AnalyticsConstants.NOTIFICATION_RECEIVED, null)
    }

    /**
     * Logs a "notification_clicked" event to Firebase Analytics when a notification is clicked.
     *
     * @param context The context from which the event is logged.
     */
    fun logNotificationClicked(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.logEvent(AnalyticsConstants.NOTIFICATION_CLICKED, null)
    }

    /**
     * Logs a "streak_share" event to Firebase Analytics when the user shares their streak.
     *
     * @param context The context from which the event is logged.
     * @param streakCount The current streak count when the share action was triggered.
     */
    fun logStreakShare(context: Context, streakCount: Int) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putInt(AnalyticsConstants.STREAK_COUNT_SHARE, streakCount)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.STREAK_SHARE, bundle)
    }

    /**
     * Logs a "statistics_event" event to Firebase Analytics when a statistics tab is viewed.
     *
     * @param context The context from which the event is logged.
     * @param tab The tab viewed in the statistics screen (e.g., "Overall", "Breeze", "Pulse", "Rage", "Elite", "Streak").
     */
    fun logStatisticsViewed(context: Context, tab: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.STREAK_TAB, tab)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.STATISTICS_EVENT, params)
    }


}
