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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.domain.model.LessonData

@Preview
@Composable
fun LessonCardColumn(lesson: LessonData, stepNumber: Int) {
    val colors = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(24.dp)
    val columnLabel = "Scan top to bottom — no repeats allowed in a column."
    val bodyText = lesson.body
        .replace(columnLabel, "")
        .trim()
        .removeSuffix("—")
        .trim()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.16f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            colors.primary.copy(alpha = 0.1f),
                            CircleShape
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

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColumnLessonIllustration(
                    grid = lesson.visualGrid,
                    modifier = Modifier.width(68.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = lesson.title,
                        color = colors.onBackground,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.1.sp
                    )

                    Text(
                        text = buildAnnotatedString {
                            val emphasized = "1–9 exactly once"
                            val start = bodyText.indexOf(emphasized, ignoreCase = true)
                            if (start >= 0) {
                                append(bodyText.substring(0, start))
                                withStyle(
                                    SpanStyle(
                                        color = colors.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append(bodyText.substring(start, start + emphasized.length))
                                }
                                append(bodyText.substring(start + emphasized.length))
                            } else {
                                append(bodyText)
                            }
                        },
                        color = colors.onSurfaceVariant,
                        fontSize = 16.sp,
                        lineHeight = 25.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                colors.primary.copy(alpha = 0.06f),
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.dp,
                                colors.primary.copy(alpha = 0.16f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Scan top to bottom before inserting a number to ensure no conflicts in that line.",
                            color = colors.onSurfaceVariant,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ColumnLessonIllustration(
    grid: List<List<Int>>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val digits = grid.mapNotNull { it.firstOrNull() }

    Column(
        modifier = modifier
            .background(
                colors.surfaceVariant.copy(alpha = 0.45f),
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                colors.primary.copy(alpha = 0.08f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SCAN",
                color = colors.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(13.dp)
            )
        }

        digits.forEachIndexed { index, digit ->
            val highlighted = index == digits.lastIndex / 2
            val cellShape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .then(
                        if (highlighted) {
                            Modifier
                                .background(colors.primary, cellShape)
                                .border(
                                    2.dp,
                                    colors.primary.copy(alpha = 0.32f),
                                    RoundedCornerShape(11.dp)
                                )
                        } else {
                            Modifier
                                .background(colors.surface, cellShape)
                                .border(1.dp, colors.primary, cellShape)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (digit == 0) "?" else digit.toString(),
                    color = if (highlighted) colors.onPrimary else colors.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
