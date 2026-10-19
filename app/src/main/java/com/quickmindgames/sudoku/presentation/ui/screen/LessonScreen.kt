package com.quickmindgames.sudoku.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.LessonCardColumn
import com.quickmindgames.sudoku.component.LessonCardDefault
import com.quickmindgames.sudoku.component.primaryGradientBrush
import com.quickmindgames.sudoku.domain.model.LESSONS
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils

@Composable
fun LessonScreen(
    onLessonsComplete: () -> Unit,
    onExit: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.LESSON,
            AnalyticsConstants.LESSON_SCREEN
        )
    }

    //region Main Design
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Exit",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "How to Play?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) {

            items(items = LESSONS, key = { lesson -> lesson.id }) { lesson ->
                val index = LESSONS.indexOf(lesson)

                // Special layout for Column Rule (index 1)
                if (index == 1) {
                    LessonCardColumn(lesson = lesson, stepNumber = index + 1)
                } else {
                    LessonCardDefault(lesson = lesson, stepNumber = index + 1)
                }

                // Add divider between lessons (except after last)
                if (index < LESSONS.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        thickness = 4.dp,
                        color = colors.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                }
            }
            item {
                Spacer(Modifier.height(24.dp))
            }
        }

        Button(
            onClick = onLessonsComplete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(56.dp)
                .background(
                    brush = primaryGradientBrush(),
                    shape = RoundedCornerShape(24.dp)
                ), shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.lbl_lets_practice),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    //endregion

}