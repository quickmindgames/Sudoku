package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun StreakNode(
    dayNumber: Int,
    isCompleted: Boolean,
    isActive: Boolean,
    isLocked: Boolean,
    isPlayable: Boolean,
    onClick: () -> Unit,
    onLockedClick: () -> Unit = {}
) {
    // 3D Gradient colors for different states
    val nodeData = when {
        isCompleted -> {
            // Completed: Blue gradient
            val primary = MaterialTheme.colorScheme.primary
            NodeColor3D(
                baseColor = primary,
                gradientStart = primary.copy(alpha = 0.9f),
                gradientEnd = primary.copy(alpha = 0.6f),
                shadowElevation = 12.dp
            )
        }

        isActive -> {
            // Active: Amber/Orange gradient with glow
            NodeColor3D(
                baseColor = Color(0xFFFF6F00),
                gradientStart = Color(0xFFFFAA00),
                gradientEnd = Color(0xFFFF6F00),
                shadowElevation = 14.dp
            )
        }

        isPlayable -> {
            // Playable: Secondary gradient
            val secondary = MaterialTheme.colorScheme.secondary
            NodeColor3D(
                baseColor = secondary,
                gradientStart = secondary.copy(alpha = 0.8f),
                gradientEnd = secondary.copy(alpha = 0.5f),
                shadowElevation = 8.dp
            )
        }

        else -> {
            // Locked: Gray gradient
            val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
            NodeColor3D(
                baseColor = surfaceVariant,
                gradientStart = surfaceVariant.copy(alpha = 0.6f),
                gradientEnd = surfaceVariant.copy(alpha = 0.3f),
                shadowElevation = 4.dp
            )
        }
    }

    val textColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        isActive -> Color.White
        isPlayable -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    }

    Column(
        modifier = Modifier
            .width(85.dp)
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D effect: Outer shadow layer
        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
                .shadow(
                    elevation = nodeData.shadowElevation,
                    shape = CircleShape,
                    clip = false
                )
        ) {
            // Gradient background for 3D depth
            ElevatedCard(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = nodeData.baseColor
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isCompleted || isActive) 8.dp
                    else if (isPlayable) 5.dp else 2.dp
                ),
                onClick = if (isPlayable || isActive) {
                    { onClick() }
                } else if (isLocked) {
                    { }
                } else {
                    { onLockedClick() }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // 3D Gradient overlay for depth
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        nodeData.gradientStart,
                                        nodeData.gradientEnd
                                    ),
                                    startY = 0f,
                                    endY = 48f
                                ),
                                shape = CircleShape
                            )
                    )

                    // Active state glow ring
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.85f)
                                .background(
                                    Color(0xFFFFCC02).copy(alpha = 0.2f),
                                    CircleShape
                                )
                        )
                    } else if (isPlayable) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.9f)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                    CircleShape
                                )
                        )
                    }

                    // Content: Icon or number
                    if (isCompleted) {
                        Text(
                            text = "✓",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    } else if (isLocked) {
                        Text(
                            text = "🔒",
                            fontSize = 24.sp
                        )
                    } else {
                        val digits = dayNumber.toString().length
                        val nodeFontSize = when {
                            digits <= 2 -> 22.sp
                            digits == 3 -> 16.sp
                            else -> 11.sp
                        }
                        Text(
                            text = dayNumber.toString(),
                            fontSize = nodeFontSize,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

// Data class for 3D node colors
data class NodeColor3D(
    val baseColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val shadowElevation: androidx.compose.ui.unit.Dp
)
