package com.quickmindgames.sudoku.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.ui.MainSudokuApp
import com.quickmindgames.sudoku.presentation.ui.screen.SudokuScreen

/**
 * App Navigation Setup
 * Manages navigation between Main screen and Play screen
 */
@Composable
fun AppNav() {
    val rootNavController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = rootNavController,
        startDestination = "main"
    ) {

        // MAIN SCREEN (contains bottom tabs)
        composable("main") {
            MainSudokuApp(
                onOpenPlay = { mode, difficulty, streakDay ->
                    rootNavController.navigate(
                        "play/$mode/${difficulty?.name ?: "none"}/${streakDay ?: 0}"
                    )
                }
            )
        }

        // PLAY SCREEN (outside bottom tabs)
        composable(
            route = "play/{mode}/{difficulty}/{streakDay}",
            arguments = listOf(
                navArgument("mode") { defaultValue = "new" },
                navArgument("difficulty") { defaultValue = "none" },
                navArgument("streakDay") { defaultValue = "0" }
            )
        ) { entry ->

            val mode = entry.arguments?.getString("mode") ?: "new"
            val diffStr = entry.arguments?.getString("difficulty") ?: "none"
            val streakDay = entry.arguments?.getString("streakDay")?.toIntOrNull() ?: 0
            val difficulty = diffStr
                .takeIf { it != "none" }
                ?.let { s ->
                    Difficulty.entries.firstOrNull {
                        it.name.equals(s, ignoreCase = true)
                    }
                }

            SudokuScreen(
                mode = mode,
                difficulty = difficulty,
                streakDay = streakDay,
                onExit = { rootNavController.popBackStack() }
            )
        }
    }
}
