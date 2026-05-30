package com.quickmindgames.sudoku.presentation.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quickmindgames.sudoku.data.preferences.ThemePreferences
import com.quickmindgames.sudoku.data.repository.StatisticsRepository
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.navigation.AppNav
import com.quickmindgames.sudoku.presentation.navigation.BottomScreen
import com.quickmindgames.sudoku.presentation.ui.screen.HomeScreen
import com.quickmindgames.sudoku.presentation.ui.screen.SettingsScreen
import com.quickmindgames.sudoku.presentation.ui.screen.StreakScreen
import com.quickmindgames.sudoku.presentation.ui.theme.SudoTheme
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.RemoteConfigManager
import com.quickmindgames.sudoku.utils.StreakReminderScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Schedule streak reminder notification
        StreakReminderScheduler.schedule(this)

        // Request POST_NOTIFICATIONS permission on Android 13+
        val requestNotificationPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                // Optionally handle result
            }
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Log analytics if opened from streak notification (no navigation)
        if (intent?.getBooleanExtra("from_streak_notification", false) == true) {
            AnalyticsUtils.logNotificationClicked(this)
        }

        // Fetch remote config values on app start
        lifecycleScope.launch {
            RemoteConfigManager.fetchAndActivate()
        }

        setContent {
            val themePreferences = remember { ThemePreferences.getInstance(this) }
            var isDarkMode by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                themePreferences.isDarkMode.collect { darkMode ->
                    isDarkMode = darkMode
                    WindowCompat
                        .getInsetsController(window, window.decorView)
                        .isAppearanceLightStatusBars = !darkMode
                }
            }

            SudoTheme(darkTheme = isDarkMode) {
                AppNav()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Log analytics if opened from streak notification (no navigation)
        if (intent.getBooleanExtra("from_streak_notification", false)) {
            AnalyticsUtils.logNotificationClicked(this)
        }
    }
}

@Composable

fun MainSudokuApp(
    onOpenPlay: (String, Difficulty?, Int?) -> Unit
) {
    val navController = rememberNavController()
    val screens = listOf(BottomScreen.Home, BottomScreen.Streak, BottomScreen.Settings)


    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = currentRoute(navController)
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomScreen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomScreen.Home.route) {
                HomeScreen(
                    onNewGameClick = { /* optional analytics, etc. */ },
                    onDifficultySelected = { difficulty: Difficulty ->
                        onOpenPlay("new", difficulty, null)
                    },
                    onResume = {
                        onOpenPlay("resume", null, null)
                    },
                    onStreakClick = {
                        navController.navigate(BottomScreen.Streak.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomScreen.Streak.route) {
                StreakScreen(
                    onOpenStreak = { dayNumber ->
                        onOpenPlay(
                            "streak",
                            Difficulty.Breeze,
                            dayNumber
                        )
                    }
                )
            }
            composable(BottomScreen.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}

/**
 * Check if a difficulty level is unlocked
 * Breeze: Always unlocked
 * Pulse: Requires 5 Breeze games
 * Rage: Requires 5 Pulse games
 * Elite: Requires 5 Rage games
 */
@Composable
fun isDifficultyUnlocked(
    difficulty: Difficulty,
    context: Context
): Pair<Boolean, String> {
    var breezeWins by remember { mutableIntStateOf(0) }
    var pulseWins by remember { mutableIntStateOf(0) }
    var rageWins by remember { mutableIntStateOf(0) }

    val repository = remember { StatisticsRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val allStats = repository.getAllStatistics().first()
                breezeWins = allStats.breeze.wins
                pulseWins = allStats.pulse.wins
                rageWins = allStats.rage.wins
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return when (difficulty) {
        Difficulty.Breeze -> Pair(true, "")
        Difficulty.Pulse -> {
            if (breezeWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Breeze games to unlock Pulse")
            }
        }

        Difficulty.Rage -> {
            if (pulseWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Pulse games to unlock Rage")
            }
        }

        Difficulty.Elite -> {
            if (rageWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Rage games to unlock Elite")
            }
        }
    }
}