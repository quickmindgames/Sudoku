package com.gamestudio.sudo.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable

/**
 * Composable extension to remember StatisticsViewModel with context
 * Usage: val viewModel = rememberStatisticsViewModel()
 */
@Composable
fun rememberStatisticsViewModel(context: Context): StatisticsViewModel {
    return remember { StatisticsViewModel(context) }
}

