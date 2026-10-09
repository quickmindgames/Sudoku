package com.quickmindgames.sudoku.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom Navigation Screen Definitions
 * Defines the main bottom navigation tabs
 */
sealed class BottomScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomScreen("home", "Home", Icons.Default.Home)
    object Streak : BottomScreen("streak", "Streak", Icons.Default.Timeline)
    object Rewards : BottomScreen("rewards", "Rewards", Icons.Default.CardGiftcard)
    object Settings : BottomScreen("settings", "Settings", Icons.Default.Settings)
}
