package com.quickmindgames.sudoku.domain.util

/**
 * When to offer Streak Reminders after a streak win.
 *
 * Counted in total streak games won (which never resets, unlike the current streak): the
 * prompt is offered at the first streak win; each time the player declines (or doesn't
 * grant notification access) it is offered again [REPROMPT_EVERY_STREAK_WINS] streak wins
 * later, until reminders are turned on. Turning them on stops it; turning them off in
 * Settings stops it for good ([NEVER]).
 */
object StreakReminderPrompt {
    const val REPROMPT_EVERY_STREAK_WINS = 5
    const val NEVER = Int.MAX_VALUE

    /** [nextPromptAtWins] is 0 until the prompt has been declined once. */
    fun shouldPrompt(remindersEnabled: Boolean, streakWins: Int, nextPromptAtWins: Int): Boolean =
        !remindersEnabled && streakWins >= nextPromptAtWins

    fun nextPromptAfterDecline(streakWins: Int): Int =
        streakWins.coerceAtLeast(0) + REPROMPT_EVERY_STREAK_WINS
}
