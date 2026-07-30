package com.quickmindgames.sudoku.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.LessonCardColumn
import com.quickmindgames.sudoku.component.LessonCardDefault
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

    Column(
        modifier = Modifier
            .background(colors.background)
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding(),
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
            )
    ) {
        // ── Top bar ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Exit",
                    tint = colors.primary
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.lbl_tutorial),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
        }

        // ── Scrollable lessons ──────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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

        // ── Start Puzzle button ─────────────────────────────────
        Button(
            onClick = onLessonsComplete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.tertiary
            )
        ) {
            Text(
                text = stringResource(R.string.lbl_lets_practice),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



