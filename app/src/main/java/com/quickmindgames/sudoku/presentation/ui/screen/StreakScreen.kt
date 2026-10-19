package com.quickmindgames.sudoku.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.StreakPath
import com.quickmindgames.sudoku.presentation.viewmodel.StreakViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.MultipleEventsCutter
import com.quickmindgames.sudoku.utils.shareStreak
import java.time.LocalDate

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun StreakScreen(
    modifier: Modifier = Modifier,
    onOpenStreak: (Int) -> Unit
) {
    val context = LocalContext.current
    val streakViewModel: StreakViewModel = viewModel(
        factory = remember(context) { StreakViewModel.factory(context) }
    )
    val streakState by streakViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.STREAK,
            AnalyticsConstants.STREAK_SCREEN
        )
    }

    val today = remember { LocalDate.now().toString() }
    val streakCount = streakState.streakCount
    val hasActiveToday = streakState.activeDate == today &&
            streakState.activeGameStarted
    val completedToday = streakState.lastCompletedDate == today
    val cutter = MultipleEventsCutter.rememberMultipleEventsCutter()
    val statusText = when {
        hasActiveToday -> "Streak in progress..."
        completedToday -> "Completed today ✓"
        streakCount > 0 -> "Play today to keep it alive!"
        else -> "Start your first streak!"
    }
    val statusColor = when {
        hasActiveToday -> Color(0xFFFF6F00)
        completedToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    //region Main Design
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Compact header section
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Compact header with inline streak count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painterResource(id = R.drawable.ic_flame),
                            contentDescription = "Flame icon"
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "My Current Streak",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = statusColor,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Streak counter + share icon stacked
                    if (streakCount > 0) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            // Compact streak counter
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                                    .then(
                                        Modifier.clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            cutter.processEvent { context.shareStreak(streakCount) }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$streakCount",
                                    fontSize = when (streakCount.toString().length) {
                                        1, 2 -> 24.sp
                                        3 -> 17.sp
                                        else -> 13.sp
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            // Tiny share badge pinned to top-end of the circle
                            Box(
                                modifier = Modifier
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .size(20.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    )
                                    .clickable {
                                        cutter.processEvent { context.shareStreak(streakCount) }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share streak",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Journey path with more space
        StreakPath(
            streakCount = streakCount,
            hasActiveToday = hasActiveToday,
            completedToday = completedToday,
            onStart = { dayNumber -> onOpenStreak(dayNumber) }
        )
    }
    //endregion
}