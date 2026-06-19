package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.domain.model.Difficulty

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    onClick: () -> Unit,
    isLocked: Boolean = false,
    onLockedClick: () -> Unit = {}
) {
    val (emoji, description) = when (difficulty) {
        Difficulty.Breeze -> Pair("🌤️", "Perfect for beginners")
        Difficulty.Pulse -> Pair("⚡", "Moderate challenge")
        Difficulty.Rage -> Pair("🔥", "Test your skills")
        Difficulty.Elite -> Pair("💎", "For masters only")
    }

    val cardColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFFDDEAFE) // Light Blue
        Difficulty.Pulse -> Color(0xFFFEF3C7) // Light Yellow
        Difficulty.Rage -> Color(0xFFFED7D7) // Light Red/Pink
        Difficulty.Elite -> Color(0xFFE9D5FF) // Light Purple
    }

    val accentColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFF3B82F6)
        Difficulty.Pulse -> Color(0xFFF59E0B)
        Difficulty.Rage -> Color(0xFFEF4444)
        Difficulty.Elite -> Color(0xFF8B5CF6)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.5f else 1f)
            .clickable(
                enabled = true,
                onClick = {
                    if (isLocked) {
                        onLockedClick()
                    } else {
                        onClick()
                    }
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = if (isLocked) 0.dp else 8.dp
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji icon with background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 32.sp
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = difficulty.label,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Arrow indicator or lock icon
                if (isLocked) {
                    Text(text = "🔒", fontSize = 24.sp)
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Select ${difficulty.label}",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Lock overlay for locked state
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                }
            }
        }
    }
}