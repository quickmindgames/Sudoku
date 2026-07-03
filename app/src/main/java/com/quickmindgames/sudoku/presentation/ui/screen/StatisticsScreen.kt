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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.component.DifficultyStatsTab
import com.quickmindgames.sudoku.component.OverallStatsTab
import com.quickmindgames.sudoku.component.StreakStatsTab
import com.quickmindgames.sudoku.presentation.viewmodel.StatisticsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.launch

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val allStatistics by viewModel.allStatistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val tabs = listOf("Overall", "Breeze", "Pulse", "Rage", "Elite", "Streak")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val lastLoggedTabIndex = remember { mutableIntStateOf(-1) }

    // Intercept system back button and forward it to the same callback
    BackHandler {
        onBackClick()
    }

    // Refresh statistics when screen is focused
    LaunchedEffect(Unit) {
        viewModel.refreshStatistics()
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.STATISTICS,
            AnalyticsConstants.STATISTICS_SCREEN
        )
    }

    // Log tab view event when tab changes
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != lastLoggedTabIndex.intValue) {
            val tabName = tabs[pagerState.currentPage]
            AnalyticsUtils.logStatisticsViewed(context, tabName)
            lastLoggedTabIndex.intValue = pagerState.currentPage
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Statistics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Tab Row - synchronized with pager
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                        .padding(16.dp)
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
}
