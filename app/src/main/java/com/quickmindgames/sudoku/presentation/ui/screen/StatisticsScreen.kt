package com.quickmindgames.sudoku.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.component.DifficultyStatsTab
import com.quickmindgames.sudoku.component.OverallStatsTab
import com.quickmindgames.sudoku.component.StreakStatsTab
import com.quickmindgames.sudoku.presentation.viewmodel.StatisticsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Preview
@Composable
fun StatisticsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = remember(context) { StatisticsViewModel.factory(context) }
    )
    val allStatistics by statisticsViewModel.allStatistics.collectAsState()
    val isLoading by statisticsViewModel.isLoading.collectAsState()

    val tabs = listOf("Overall", "Breeze", "Pulse", "Rage", "Elite", "Streak")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    // Intercept system back button and forward it to the same callback
    BackHandler {
        onBackClick()
    }

    // Refresh statistics when screen is focused
    LaunchedEffect(Unit) {
        statisticsViewModel.refreshStatistics()
        statisticsViewModel.hasLoadedStatistics.first { it }
        AnalyticsUtils.logStatisticsViewed(
            context = context,
            statistics = statisticsViewModel.allStatistics.value
        )
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.STATISTICS,
            AnalyticsConstants.STATISTICS_SCREEN
        )
    }

    //region Main Design
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBackClick() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Statistics",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Scrollable pill tabs - synchronized with pager
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = 10.dp,
            indicator = {},
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .background(
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            },
                            shape = CircleShape
                        ),
                    selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Content - Swipeable with HorizontalPager
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading statistics...",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                ) {
                    when (pageIndex) {
                        0 -> OverallStatsTab(allStatistics)
                        1 -> DifficultyStatsTab(allStatistics.breeze)
                        2 -> DifficultyStatsTab(allStatistics.pulse)
                        3 -> DifficultyStatsTab(allStatistics.rage)
                        4 -> DifficultyStatsTab(allStatistics.elite)
                        5 -> StreakStatsTab(allStatistics.streak)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    //endregion
}
