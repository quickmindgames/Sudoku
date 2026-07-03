package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LessonCardDefault(lesson: com.quickmindgames.sudoku.domain.model.LessonData, stepNumber: Int) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
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
        Spacer(Modifier.height(8.dp))

        // Visual grid representation
        VisualLessonGrid(grid = lesson.visualGrid)
        Spacer(Modifier.height(12.dp))

        // Body
        Text(
            text = lesson.body,
            fontSize = 16.sp,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        )
    }
}