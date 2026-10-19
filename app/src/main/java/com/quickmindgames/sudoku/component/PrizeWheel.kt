package com.quickmindgames.sudoku.component

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.quickmindgames.sudoku.R

enum class PrizeType {
    POINTS,
    HINTS,
    BOOSTER,
    STREAK_SHIELD
}

// Add custom prizes with their own wheel text, type, and award amount.
data class PrizeWheelPrize(
    val label: String,
    val type: PrizeType,
    val amount: Int,
    val weight: Int = 1
) {
    init {
        require(weight > 0) { "Prize weight must be greater than zero." }
    }
}

val PrizeWheelPrizes = listOf(
    PrizeWheelPrize(label = "+10", type = PrizeType.POINTS, amount = 10, weight = 4),
    PrizeWheelPrize(label = "+20", type = PrizeType.POINTS, amount = 20, 3),
    PrizeWheelPrize(label = "+50", type = PrizeType.POINTS, amount = 50, weight = 2),
    PrizeWheelPrize(label = "+100", type = PrizeType.POINTS, amount = 100),
    PrizeWheelPrize(label = "💡", type = PrizeType.HINTS, amount = 1),
    PrizeWheelPrize(label = "2X", type = PrizeType.BOOSTER, amount = 1),
    PrizeWheelPrize(label = "🔥", type = PrizeType.STREAK_SHIELD, amount = 1)
)

fun weightedPrizeIndex(
    prizes: List<PrizeWheelPrize>,
    random: kotlin.random.Random = kotlin.random.Random.Default
): Int {
    require(prizes.isNotEmpty()) { "At least one prize is required." }
    val totalWeight = prizes.sumOf { it.weight }
    var ticket = random.nextInt(totalWeight)
    return prizes.indexOfFirst { prize ->
        ticket -= prize.weight
        ticket < 0
    }
}

@Preview
@Composable
fun PrizeWheel(
    rotation: Float = 0f,
    isSpinning: Boolean = false,
    canSpin: Boolean = true,
    cooldownLabel: String? = null,
    onSpin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val prizes = PrizeWheelPrizes
    val resources = LocalResources.current
    val boldTypeface = remember(resources) { resources.getFont(R.font.bold) }
    val hintDrawable = remember(resources) {
        requireNotNull(ResourcesCompat.getDrawable(resources, R.drawable.ic_hint, null)) {
            "Hint drawable not found"
        }.mutate()
    }
    val streakShieldDrawable = remember(resources) {
        requireNotNull(ResourcesCompat.getDrawable(resources, R.drawable.ic_flame_raw, null)) {
            "Shield drawable not found"
        }.mutate()
    }
    val xDrawable = remember(resources) {
        requireNotNull(ResourcesCompat.getDrawable(resources, R.drawable.ic_2x, null)) {
            "2x drawable not found"
        }.mutate().apply { setTint(android.graphics.Color.WHITE) }
    }

    val segmentColors = listOf(
        Color(0xFF263238), // blue grey
        Color(0xFF4A148C), // deep purple
        Color(0xFF006064), // dark teal
        Color(0xFF1B5E20), // dark green
        Color(0xFF880E4F)  // deep magenta
    )
    val pointerColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)

    BoxWithConstraints(
        modifier = modifier
            // These layers form the raised gold rim around the colored wheel.
            .shadow(16.dp, CircleShape)
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFFE082), Color(0xFFFFA000), Color(0xFFFFF3C4))
                ),
                shape = CircleShape
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            val radius = size.minDimension / 2f
            // Equal slices are centered from the top, then advance clockwise.
            val segmentAngle = 360f / prizes.size
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                // Adjust these relative sizes to scale the labels with the wheel.
                textSize = radius * 0.16f
                typeface = boldTypeface
                setShadowLayer(radius * 0.025f, 0f, radius * 0.018f, 0x99000000.toInt())
            }
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = radius * 0.07f
                typeface = boldTypeface
            }

            prizes.forEachIndexed { index, prize ->
                val startAngle = -90f + index * segmentAngle
                val baseColor = segmentColors[index % segmentColors.size]
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            tintToward(baseColor, Color.White, 0.32f),
                            baseColor,
                            tintToward(baseColor, Color.Black, 0.18f)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    startAngle = startAngle,
                    sweepAngle = segmentAngle,
                    useCenter = true
                )

                val labelAngle = Math.toRadians((startAngle + segmentAngle / 2f).toDouble())
                // Move labels toward the rim by increasing this ratio; decrease it to pull them inward.
                val labelRadius = radius * 0.65f
                val labelX = center.x + labelRadius * kotlin.math.cos(labelAngle).toFloat()
                val labelY = center.y + labelRadius * kotlin.math.sin(labelAngle).toFloat()
                val prizeCaption = when (prize.type) {
                    PrizeType.POINTS, PrizeType.BOOSTER -> "POINTS"
                    PrizeType.HINTS -> "HINT"
                    PrizeType.STREAK_SHIELD -> "SHIELD"
                }
                drawIntoCanvas { canvas ->
                    val nativeCanvas = canvas.nativeCanvas

                    when (prize.type) {
                        PrizeType.HINTS -> {
                            val iconSize = radius * 0.22f
                            hintDrawable.setBounds(
                                (labelX - iconSize / 2f).toInt(),
                                (labelY - iconSize / 2f).toInt(),
                                (labelX + iconSize / 2f).toInt(),
                                (labelY + iconSize / 2f).toInt()
                            )
                            hintDrawable.draw(nativeCanvas)

                        }

                        PrizeType.STREAK_SHIELD -> {
                            val iconSize = radius * 0.22f
                            streakShieldDrawable.setBounds(
                                (labelX - iconSize / 2.5f).toInt(),
                                (labelY - iconSize / 2f).toInt(),
                                (labelX + iconSize / 2.5f).toInt(),
                                (labelY + iconSize / 2f).toInt()
                            )
                            streakShieldDrawable.draw(nativeCanvas)

                        }

                        PrizeType.BOOSTER -> {
                            val iconSize = radius * 0.22f
                            xDrawable.setBounds(
                                (labelX - iconSize / 3f).toInt(),
                                (labelY - iconSize / 3f).toInt(),
                                (labelX + iconSize / 3f).toInt(),
                                (labelY + iconSize / 3f).toInt()
                            )
                            xDrawable.draw(nativeCanvas)

                        }

                        else -> {
                            nativeCanvas.drawText(
                                prize.label,
                                labelX,
                                labelY - (labelPaint.ascent() + labelPaint.descent()) / 2f,
                                labelPaint
                            )
                        }
                    }
                    canvas.nativeCanvas.drawText(
                        prizeCaption,
                        labelX,
                        // This vertical offset controls the gap beneath the prize amount.
                        labelY + radius * 0.19f,
                        subtitlePaint
                    )
                }
            }

            // Slice dividers and concentric strokes define the wheel face and inner rim.
            for (index in prizes.indices) {
                val angle = Math.toRadians((-90f + index * segmentAngle).toDouble())
                val end = Offset(
                    center.x + radius * kotlin.math.cos(angle).toFloat(),
                    center.y + radius * kotlin.math.sin(angle).toFloat()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.82f),
                    start = center,
                    end = end,
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Soft radial overlay gives the flat segments a glossy highlight.
            drawCircle(
                color = outlineColor,
                radius = radius,
                style = Stroke(width = 1.5.dp.toPx())
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.23f),
                        Color.White.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(radius * 0.38f, radius * 0.28f),
                    radius = radius * 1.15f
                ),
                radius = radius
            )

            // Layered circles and centered text make the raised center hub.
            val hubRadius = radius * 0.19f
            drawCircle(
                color = Color.Black.copy(alpha = 0.22f),
                radius = hubRadius * 1.12f,
                center = center.copy(y = center.y + 2.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFFD54F), Color(0xFFEF6C00)),
                    radius = hubRadius * 1.8f
                ),
                radius = hubRadius
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = hubRadius * 0.82f,
                style = Stroke(width = 1.5.dp.toPx())
            )
            labelPaint.clearShadowLayer()
        }

        // The center hub is the spin action; it remains stationary as the wheel face rotates.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(if (cooldownLabel == null) maxWidth * 0.38f else maxWidth)
                .clip(CircleShape)
                .then(
                    if (cooldownLabel != null) {
                        Modifier.background(
                            MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.90f),
                            CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable(
                    enabled = canSpin && !isSpinning,
                    role = Role.Button,
                    onClick = onSpin
                )
                .semantics {
                    contentDescription = when {
                        isSpinning -> "Wheel is spinning"
                        cooldownLabel != null -> "Next spin in $cooldownLabel"
                        !canSpin -> "Wheel spin is on cooldown"
                        else -> "Spin the wheel"
                    }
                    role = Role.Button
                },
            contentAlignment = Alignment.Center
        ) {
            if (cooldownLabel != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Next Spin in",
                        color = Color(0xFF5D3100),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = cooldownLabel,
                        color = Color(0xFF5D3100),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            } else {
                Text(
                    text = "SPIN",
                    color = Color(0xFF5D3100),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily(Font(R.font.bold))
                    ),
                )
            }
        }

        // The pointer sits outside the rotating Canvas so it stays fixed while the wheel spins.
        Canvas(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = 42.dp, height = 38.dp)
                .semantics { contentDescription = "Wheel pointer" }
        ) {
            val pointer = Path().apply {
                moveTo(size.width * 0.12f, 0f)
                lineTo(size.width * 0.88f, 0f)
                lineTo(size.width * 0.78f, size.height * 0.58f)
                lineTo(size.width * 0.5f, size.height)
                lineTo(size.width * 0.22f, size.height * 0.58f)
                close()
            }
            val pointerShadow = Path().apply {
                moveTo(size.width * 0.12f, 2.dp.toPx())
                lineTo(size.width * 0.88f, 2.dp.toPx())
                lineTo(size.width * 0.78f, size.height * 0.58f + 2.dp.toPx())
                lineTo(size.width * 0.5f, size.height + 2.dp.toPx())
                lineTo(size.width * 0.22f, size.height * 0.58f + 2.dp.toPx())
                close()
            }
            drawPath(pointerShadow, color = Color.Black.copy(alpha = 0.3f))
            drawPath(
                pointer,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFFF3B0), Color(0xFFFFB300), Color(0xFFFF6F00))
                )
            )
            drawPath(
                pointer,
                color = pointerColor.copy(alpha = 0.7f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

// Lighten or darken a segment color to create its gradient without hard-coded palette variants.
private fun tintToward(color: Color, target: Color, amount: Float): Color =
    Color(
        red = color.red + (target.red - color.red) * amount,
        green = color.green + (target.green - color.green) * amount,
        blue = color.blue + (target.blue - color.blue) * amount,
        alpha = color.alpha + (target.alpha - color.alpha) * amount
    )
