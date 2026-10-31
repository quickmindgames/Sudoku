package com.quickmindgames.sudoku.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom Navigation Screen Definitions
 * Defines the main bottom navigation tabs
 */
sealed class BottomScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomScreen("home", "Home", Icons.Rounded.Home)
    object Streak : BottomScreen("streak", "Streak", Icons.AutoMirrored.Rounded.TrendingUp)
    object Rewards : BottomScreen("rewards", "Rewards", Icons.Rounded.CardGiftcard)
    object Settings : BottomScreen("settings", "Settings", Icons.Rounded.Settings)
}
