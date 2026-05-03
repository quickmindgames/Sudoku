package com.gamestudio.sudo.domain.model

/**
 * Domain model for overall game statistics
 */
data class GameStatistics(
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val winRatioPercentage: Double = 0.0,
    val bestTime: Int? = null,
    val bestScore: Int? = null,
    val flawFreeWins: Int = 0
) {
    val winRatio: String = String.format("%.1f%%", winRatioPercentage)
}

/**
 * Domain model for difficulty-specific statistics
 */
data class DifficultyStatistics(
    val difficulty: String,
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val winRatioPercentage: Double = 0.0,
    val bestTime: Int? = null,
    val bestScore: Int? = null,
    val flawFreeWins: Int = 0
) {
    val winRatio: String = String.format("%.1f%%", winRatioPercentage)
}

/**
 * Domain model for streak statistics
 */
data class StreakStatistics(
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val winRatioPercentage: Double = 0.0,
    val bestTime: Int? = null,
    val bestScore: Int? = null,
    val highestStreakDay: Int = 0,
    val currentConsecutiveWins: Int = 0
) {
    val winRatio: String = String.format("%.1f%%", winRatioPercentage)
}

/**
 * Wrapper for all statistics
 */
data class AllStatistics(
    val overallStats: GameStatistics = GameStatistics(),
    val breeze: DifficultyStatistics = DifficultyStatistics("Breeze"),
    val pulse: DifficultyStatistics = DifficultyStatistics("Pulse"),
    val rage: DifficultyStatistics = DifficultyStatistics("Rage"),
    val elite: DifficultyStatistics = DifficultyStatistics("Elite"),
    val streak: StreakStatistics = StreakStatistics()
)

