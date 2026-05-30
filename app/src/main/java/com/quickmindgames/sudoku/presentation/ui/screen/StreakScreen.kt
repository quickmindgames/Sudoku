package com.quickmindgames.sudoku.presentation.ui.screen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.component.StreakPath
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
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
    onOpenStreak: (Int) -> Unit
) {
    val context = LocalContext.current
    val streakStateManager = remember { StreakStateManager.getInstance(context) }

    var streakState by remember {
        mutableStateOf(
            StreakState(
                streakCount = 0,
                lastCompletedDate = null,
                activeDate = null,
                activeGame = null
            )
        )
    }

    LaunchedEffect(Unit) {
        streakStateManager.getStreakState().collect { saved ->
            streakState = saved ?: StreakState(0, null, null, null)
        }
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.STREAK,
            AnalyticsConstants.STREAK_SCREEN
        )
    }

    val today = remember { LocalDate.now().toString() }
    val streakCount = streakState.streakCount
    val hasActiveToday = streakState.activeDate == today
            && streakState.activeGame?.hasStarted() == true
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact header section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    Text(
                        text = "🔥",
                        fontSize = 32.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "My Current Streak",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = statusText,
                            fontSize = 13.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Streak counter + share icon stacked
                Box(contentAlignment = Alignment.TopEnd) {
                    // Compact streak counter
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            .then(
                                if (streakCount > 0) Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    cutter.processEvent { context.shareStreak(streakCount) }
                                } else Modifier
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
                    if (streakCount > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(22.dp)
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
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share streak",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Journey path with more space
        StreakPath(
            streakCount = streakCount,
            hasActiveToday = hasActiveToday,
            completedToday = completedToday,
            onStart = { dayNumber -> onOpenStreak(dayNumber) }
        )
    }
}