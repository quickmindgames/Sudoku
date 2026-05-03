package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.domain.model.AllStatistics
import com.quickmindgames.sudoku.domain.model.DifficultyStatistics
import com.quickmindgames.sudoku.domain.model.GameStatistics
import com.quickmindgames.sudoku.domain.model.StreakStatistics

/**
 * Data class for win ratio-based feedback with title, caption, and emoji
 */
data class WinRatioFeedback(
    val title: String,
    val caption: String,
    val emoji: String
)

/**
 * Get feedback based on win ratio percentage
 * Includes dynamic random captions for variety
 * Now with 10% interval tiers for better progression
 */
fun getWinRatioFeedback(winRatio: String): WinRatioFeedback {
    val ratio = winRatio.removeSuffix("%").toDoubleOrNull() ?: 0.0

    return when {
        ratio == 100.0 -> WinRatioFeedback(
            title = "Sudoku Legend 👑",
            caption = listOf(
                "Numbers fear you.",
                "Sudoku completed flawlessly.",
                "Perfect Mind Detected 🧠✨",
                "Puzzle Destroyer Mode: ON",
                "Are you secretly the developer?"
            ).random(),
            emoji = "🏆"
        )

        ratio >= 96 -> WinRatioFeedback(
            title = "Master",
            caption = listOf(
                "Almost illegal accuracy 😳",
                "Human or hidden AI?",
                "Numbers obey your command.",
                "Perfection is calling…",
                "One step away from legend."
            ).random(),
            emoji = "👑"
        )

        ratio >= 91 -> WinRatioFeedback(
            title = "Grandmaster",
            caption = listOf(
                "Flawless execution incoming.",
                "Your accuracy is legendary.",
                "Mistakes? Never heard of them.",
                "The puzzle gods bow to you.",
                "One mistake away from perfection."
            ).random(),
            emoji = "🎖️"
        )

        ratio >= 86 -> WinRatioFeedback(
            title = "Elite Pro",
            caption = listOf(
                "Your skills are undeniable.",
                "The puzzle solver evolution.",
                "Elite tier unlocked forever.",
                "Zero hesitation, pure skill.",
                "You are the standard now."
            ).random(),
            emoji = "⚡"
        )

        ratio >= 81 -> WinRatioFeedback(
            title = "Expert",
            caption = listOf(
                "Okay genius, we see you.",
                "Mistakes are getting rare.",
                "Sudoku sweating right now.",
                "Elite solver unlocked.",
                "You don't guess. You know."
            ).random(),
            emoji = "🚀"
        )

        ratio >= 76 -> WinRatioFeedback(
            title = "Advanced Player",
            caption = listOf(
                "Speed and accuracy combined.",
                "Patterns speak to you.",
                "You're in your element.",
                "Professional level approaching.",
                "Mastery in progress…"
            ).random(),
            emoji = "📈"
        )

        ratio >= 71 -> WinRatioFeedback(
            title = "Strong Solver",
            caption = listOf(
                "You're unstoppable now.",
                "Confidence at an all-time high.",
                "Puzzle fighter unleashed.",
                "The force is strong with you.",
                "Consistency is your superpower."
            ).random(),
            emoji = "💪"
        )

        ratio >= 66 -> WinRatioFeedback(
            title = "Solid Player",
            caption = listOf(
                "You own the puzzles now.",
                "Strategy meets execution.",
                "Winning becomes your norm.",
                "Two-thirds mastery achieved.",
                "The puzzle fears you slightly."
            ).random(),
            emoji = "🔥"
        )

        ratio >= 61 -> WinRatioFeedback(
            title = "Solver",
            caption = listOf(
                "Sudoku respects you now.",
                "Sharp mind alert!",
                "You solve more than you struggle.",
                "Brain doing gym regularly 💪",
                "Strategic thinker detected."
            ).random(),
            emoji = "🔥"
        )

        ratio >= 56 -> WinRatioFeedback(
            title = "Confident Player",
            caption = listOf(
                "Halfway to mastery.",
                "Your potential is showing.",
                "Puzzles bow to your strategy.",
                "Experience turning into skill.",
                "The momentum is real."
            ).random(),
            emoji = "✨"
        )

        ratio >= 51 -> WinRatioFeedback(
            title = "Improving Solver",
            caption = listOf(
                "Over halfway there!",
                "Growth trajectory: ↗️ Excellent",
                "You're becoming formidable.",
                "Skills upgrading rapidly.",
                "Future expert in the making."
            ).random(),
            emoji = "📊"
        )

        ratio >= 46 -> WinRatioFeedback(
            title = "Above Average",
            caption = listOf(
                "You're officially above the rest.",
                "Consistency showing up.",
                "Puzzle solver in development.",
                "Your wins outnumber losses now.",
                "The curve bends upward."
            ).random(),
            emoji = "😌"
        )

        ratio >= 41 -> WinRatioFeedback(
            title = "Thinker",
            caption = listOf(
                "Now we're talking!",
                "Puzzle fighter in training.",
                "You're officially dangerous now.",
                "Half human, half calculator.",
                "Confidence level increasing 📈"
            ).random(),
            emoji = "😎"
        )

        ratio >= 36 -> WinRatioFeedback(
            title = "Developing Player",
            caption = listOf(
                "You're getting the hang of it.",
                "Patterns starting to reveal themselves.",
                "Improvement noticed!",
                "Keep this momentum going.",
                "The puzzles don't stand a chance."
            ).random(),
            emoji = "🌱"
        )

        ratio >= 31 -> WinRatioFeedback(
            title = "Practicing Hard",
            caption = listOf(
                "Progress is beautiful.",
                "You're closer than yesterday.",
                "Muscle memory building.",
                "Consistency blooming.",
                "One foot in the victory zone."
            ).random(),
            emoji = "💭"
        )

        ratio >= 26 -> WinRatioFeedback(
            title = "Persistent Learner",
            caption = listOf(
                "The dedication is showing.",
                "Quarter victory rate achieved.",
                "Forward is all that matters.",
                "Your brain is adapting.",
                "Small wins lead to big wins."
            ).random(),
            emoji = "🎯"
        )

        ratio >= 21 -> WinRatioFeedback(
            title = "Learner",
            caption = listOf(
                "Okay okay… progress detected!",
                "You're starting to scare easy puzzles.",
                "Mistakes today, mastery tomorrow.",
                "Not bad… not bad at all.",
                "The brain cells have awakened."
            ).random(),
            emoji = "🙂"
        )

        ratio >= 16 -> WinRatioFeedback(
            title = "Beginner+",
            caption = listOf(
                "You're finding your footing.",
                "Every puzzle is a lesson.",
                "Confidence building slowly.",
                "The basics are clicking.",
                "Keep exploring, keep learning."
            ).random(),
            emoji = "🌍"
        )

        ratio >= 11 -> WinRatioFeedback(
            title = "Novice",
            caption = listOf(
                "Welcome to the journey!",
                "Numbers are becoming friends.",
                "You're on the right path.",
                "Every game makes you smarter.",
                "Patience is your strength."
            ).random(),
            emoji = "👋"
        )

        ratio >= 6 -> WinRatioFeedback(
            title = "Getting Started",
            caption = listOf(
                "Your adventure has begun.",
                "Sudoku is saying hello.",
                "First steps are the hardest.",
                "Tiny wins count too.",
                "You're braver than you think."
            ).random(),
            emoji = "🌟"
        )

        ratio > 0.0 -> WinRatioFeedback(
            title = "Rookie",
            caption = listOf(
                "Every master was once confused too 😄",
                "Numbers are warming up to you.",
                "Practice mode activated.",
                "Brain loading… please wait.",
                "Sudoku and you are getting introduced."
            ).random(),
            emoji = "😵"
        )
        // Special case: 0% win ratio with losses
        else -> WinRatioFeedback(
            title = "Keep Trying",
            caption = listOf(
                "Don't give up! Every loss teaches you something.",
                "The best time to try again is now.",
                "Failures are just setups for comebacks.",
                "You've got this! One game at a time.",
                "Persistence beats perfection every time.",
                "Champions aren't built in one day.",
                "Your breakthrough is just one puzzle away."
            ).random(),
            emoji = "💪"
        )
    }
}

@Composable
fun OverallStatsTab(allStatistics: AllStatistics) {
    Column {

        //region Get feedback based on win ratio for streak stats and use it to create a header with title, caption, and emoji
        /*val feedback = getWinRatioFeedback(allStatistics.overallStats.winRatio)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = feedback.emoji,
                fontSize = 32.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feedback.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feedback.caption,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }*/
        //endregion

        OverallStatsCard(allStatistics.overallStats)
    }
}

@Composable
fun OverallStatsCard(stats: GameStatistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatisticRow("Total Games", stats.totalGames.toString())
            StatisticRow("Wins", stats.totalWins.toString(), MaterialTheme.colorScheme.primary)
            StatisticRow("Losses", stats.totalLosses.toString(), MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            StatisticRow("Win Ratio", stats.winRatio, MaterialTheme.colorScheme.secondary)
            StatisticRow("Best Time", stats.bestTime?.let { formatTime(it) } ?: "—")
            StatisticRow("Best Score", stats.bestScore?.toString() ?: "—")
            StatisticRow(
                "Flaw-Free Wins",
                stats.flawFreeWins.toString(),
                MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun DifficultyStatsTab(difficulty: DifficultyStatistics) {
    Column {

        //region Get feedback based on win ratio for streak stats and use it to create a header with title, caption, and emoji
        /*val feedback = getWinRatioFeedback(difficulty.winRatio)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = feedback.emoji,
                fontSize = 32.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feedback.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feedback.caption,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }*/
        //endregion

        DifficultyStatsCard(difficulty)
    }
}

@Composable
fun DifficultyStatsCard(stats: DifficultyStatistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatisticRow("Total Games", stats.totalGames.toString())
            StatisticRow("Wins", stats.wins.toString(), MaterialTheme.colorScheme.primary)
            StatisticRow("Losses", stats.losses.toString(), MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            StatisticRow("Win Ratio", stats.winRatio, MaterialTheme.colorScheme.secondary)
            StatisticRow("Best Time", stats.bestTime?.let { formatTime(it) } ?: "—")
            StatisticRow("Best Score", stats.bestScore?.toString() ?: "—")
            StatisticRow(
                "Flaw-Free Wins",
                stats.flawFreeWins.toString(),
                MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun StreakStatsTab(stats: StreakStatistics) {
    Column {

        //region Get feedback based on win ratio for streak stats and use it to create a header with title, caption, and emoji
        /*val feedback = getWinRatioFeedback(stats.winRatio)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = feedback.emoji,
                fontSize = 32.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feedback.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feedback.caption,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }*/
        //endregion

        StreakStatsCard(stats)
    }
}

@Composable
fun StreakStatsCard(stats: StreakStatistics) {
    val context = LocalContext.current
    val streakStateManager = remember { StreakStateManager.getInstance(context) }
    var streakCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        streakStateManager.getStreakState().collect { saved ->
            val s = saved ?: StreakState(0, null, null, null)
            streakCount = s.streakCount
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatisticRow("Total Streak Games", stats.totalGames.toString())
            StatisticRow("Wins", stats.totalWins.toString(), MaterialTheme.colorScheme.primary)
            StatisticRow("Losses", stats.totalLosses.toString(), MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            StatisticRow("Win Ratio", stats.winRatio, MaterialTheme.colorScheme.secondary)
            StatisticRow("Best Time", stats.bestTime?.let { formatTime(it) } ?: "—")
            StatisticRow("Best Score", stats.bestScore?.toString() ?: "—")
            StatisticRow(
                "Highest Streak Day",
                stats.highestStreakDay.toString(),
                MaterialTheme.colorScheme.tertiary
            )
            StatisticRow(
                "Current Consecutive Wins",
                streakCount.toString(),
                MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StatisticRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Format time in seconds to readable format (MM:SS)
 */
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}

