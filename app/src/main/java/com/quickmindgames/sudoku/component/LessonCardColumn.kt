package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.domain.model.LessonData

@Composable
fun LessonCardColumn(lesson: LessonData, stepNumber: Int) {

    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left side: Column grid - fixed width
        Box(
            modifier = Modifier.width(140.dp),
            contentAlignment = Alignment.Center
        ) {
            VisualLessonGrid(grid = lesson.visualGrid)
        }

        // Right side: Content - takes remaining space
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Step badge
            Text(
                text = "Lesson $stepNumber",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primary,
                modifier = Modifier
                    .background(
                        color = colors.primary.copy(alpha = 0.1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = lesson.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.onBackground,
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp
            )
            Spacer(Modifier.height(12.dp))

            // Body
            Text(
                text = lesson.body,
                fontSize = 16.sp,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Start,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}