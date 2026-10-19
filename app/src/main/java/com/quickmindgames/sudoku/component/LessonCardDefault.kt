package com.quickmindgames.sudoku.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.domain.model.LessonData

@Composable
fun LessonCardDefault(lesson: LessonData, stepNumber: Int) {
    val colors = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(22.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(
            width = 1.dp,
            color = colors.primary.copy(alpha = 0.16f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            color = colors.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(colors.primary, CircleShape)
                    )
                    Text(
                        text = "Lesson $stepNumber",
                        color = colors.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = lesson.title,
                color = colors.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            )

            Spacer(Modifier.height(16.dp))

            LessonExamplePanel(lesson = lesson)

            Spacer(Modifier.height(18.dp))

            val repeatedNumberPhrase = "can't appear again."
            val emphasizedPhraseStart = lesson.body.indexOf(
                repeatedNumberPhrase,
                ignoreCase = true
            )
            val body = buildAnnotatedString {
                if (emphasizedPhraseStart >= 0) {
                    append(lesson.body.substring(0, emphasizedPhraseStart))
                    withStyle(SpanStyle(color = colors.error, fontWeight = FontWeight.SemiBold)) {
                        append(
                            lesson.body.substring(
                                emphasizedPhraseStart,
                                emphasizedPhraseStart + repeatedNumberPhrase.length
                            )
                        )
                    }
                    append(lesson.body.substring(emphasizedPhraseStart + repeatedNumberPhrase.length))
                } else {
                    append(lesson.body)
                }
            }

            Text(
                text = body,
                color = colors.onSurfaceVariant,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun LessonExamplePanel(lesson: LessonData) {
    val colors = MaterialTheme.colorScheme
    val grid = lesson.visualGrid
    val isSingleRow = grid.size == 1 && grid.firstOrNull()?.size == 9
    val panelShape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant.copy(alpha = 0.45f), panelShape)
            .border(1.dp, colors.primary.copy(alpha = 0.08f), panelShape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "→",
                    color = colors.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when {
                        lesson.title.contains("row", ignoreCase = true) -> "Across Any Row"
                        lesson.title.contains("column", ignoreCase = true) -> "Down Any Column"
                        lesson.title.contains("box", ignoreCase = true) -> "Inside Any Box"
                        else -> "Example"
                    },
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = if (isSingleRow) "1 to 9 Unique" else "No Repeats",
                color = colors.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isSingleRow) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                grid.first().forEachIndexed { index, digit ->
                    val isHighlighted = index == grid.first().lastIndex / 2
                    val cellShape = RoundedCornerShape(10.dp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .shadow(
                                elevation = if (isHighlighted) 3.dp else 1.dp,
                                shape = cellShape
                            )
                            .background(
                                color = if (isHighlighted) colors.primary else colors.surface,
                                shape = cellShape
                            )
                            .then(
                                if (isHighlighted) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = colors.primary.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isHighlighted && lesson.id == 4) "?" else digit.toString(),
                            color = if (isHighlighted) colors.onPrimary else colors.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                VisualLessonGrid(grid = grid)
            }
        }
    }
}
