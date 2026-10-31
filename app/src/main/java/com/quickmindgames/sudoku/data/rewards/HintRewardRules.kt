package com.quickmindgames.sudoku.data.rewards

data class HintMigrationGrant(
    val pointHints: Int,
    val streakHints: Int
) {
    val totalHints: Int
        get() = pointHints + streakHints
}

data class MilestoneUpdate(
    val creditedMilestone: Int,
    val hintsToAward: Int
)

object HintRewardRules {
    const val POINTS_PER_HINT = 1_000
    const val STREAK_DAYS_PER_HINT = 5

    fun initialGrant(lifetimePoints: Int, streakDays: Int): HintMigrationGrant =
        HintMigrationGrant(
            pointHints = lifetimePoints.coerceAtLeast(0) / POINTS_PER_HINT,
            streakHints = streakDays.coerceAtLeast(0) / STREAK_DAYS_PER_HINT
        )

    fun pointMilestoneUpdate(
        lifetimePoints: Int,
        previouslyCreditedMilestone: Int
    ): MilestoneUpdate {
        val reachedMilestone = lifetimePoints.coerceAtLeast(0) / POINTS_PER_HINT
        val newMilestone = maxOf(previouslyCreditedMilestone, reachedMilestone)
        return MilestoneUpdate(
            creditedMilestone = newMilestone,
            hintsToAward = (newMilestone - previouslyCreditedMilestone).coerceAtLeast(0)
        )
    }

    fun streakMilestoneUpdate(
        currentStreakDays: Int,
        previouslyObservedStreakDays: Int,
        previouslyCreditedMilestone: Int
    ): MilestoneUpdate {
        val streakDays = currentStreakDays.coerceAtLeast(0)
        val reachedMilestone = streakDays / STREAK_DAYS_PER_HINT
        if (streakDays < previouslyObservedStreakDays) {
            return MilestoneUpdate(
                creditedMilestone = reachedMilestone,
                hintsToAward = 0
            )
        }

        val newMilestone = maxOf(previouslyCreditedMilestone, reachedMilestone)
        return MilestoneUpdate(
            creditedMilestone = newMilestone,
            hintsToAward = (newMilestone - previouslyCreditedMilestone).coerceAtLeast(0)
        )
    }
}
