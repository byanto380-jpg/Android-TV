package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.WeatherConditionType
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FocusRingCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TvGlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTagName: String? = null,
    shape: Shape = RoundedCornerShape(18.dp),
    containerColor: Color = GlassSurface,
    focusedContainerColor: Color = GlassSurfaceElevated,
    focusedBorderColor: Color = FocusRingCyan,
    focusScale: Float = 1.035f,
    content: @Composable BoxScope.(isFocused: Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tv_focus_scale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) focusedContainerColor else containerColor,
        label = "tv_bg_color"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) focusedBorderColor else GlassBorder,
        label = "tv_border_color"
    )

    Box(
        modifier = modifier
            .then(if (testTagName != null) Modifier.testTag(testTagName) else Modifier)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .clip(shape)
            .background(bgColor)
            .border(
                BorderStroke(if (isFocused) 2.2.dp else 1.dp, borderColor),
                shape
            )
            .onFocusChanged { isFocused = it.isFocused }
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp &&
                    (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter)
                ) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = CyanPrimary),
                onClick = onClick
            )
    ) {
        content(isFocused)
    }
}

@Composable
fun AnimatedWeatherCanvasIcon(
    conditionType: WeatherConditionType,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "weather_phase"
    )
    val bob by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_bob"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when (conditionType) {
            WeatherConditionType.CLEAR -> {
                val center = Offset(w * 0.5f, h * 0.5f)
                val sunRadius = w * 0.24f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GoldAccent, GoldAccent.copy(alpha = 0.1f)),
                        center = center,
                        radius = w * 0.45f
                    ),
                    radius = w * 0.45f,
                    center = center
                )
                drawCircle(color = GoldAccent, radius = sunRadius, center = center)
                val angleOffset = phase * 360f
                for (i in 0 until 8) {
                    val rad = Math.toRadians((i * 45.0) + angleOffset)
                    val start = Offset(
                        center.x + cos(rad).toFloat() * (sunRadius + w * 0.06f),
                        center.y + sin(rad).toFloat() * (sunRadius + w * 0.06f)
                    )
                    val end = Offset(
                        center.x + cos(rad).toFloat() * (sunRadius + w * 0.16f),
                        center.y + sin(rad).toFloat() * (sunRadius + w * 0.16f)
                    )
                    drawLine(
                        color = GoldAccent,
                        start = start,
                        end = end,
                        strokeWidth = w * 0.06f,
                        cap = StrokeCap.Round
                    )
                }
            }

            WeatherConditionType.PARTLY_CLOUDY -> {
                val sunCenter = Offset(w * 0.64f, h * 0.36f)
                drawCircle(color = GoldAccent, radius = w * 0.18f, center = sunCenter)
                val cloudY = h * 0.60f + bob
                drawCircle(color = Color.White, radius = w * 0.18f, center = Offset(w * 0.36f, cloudY))
                drawCircle(color = Color.White, radius = w * 0.22f, center = Offset(w * 0.52f, cloudY - h * 0.06f))
                drawCircle(color = TextSecondary, radius = w * 0.16f, center = Offset(w * 0.68f, cloudY))
            }

            WeatherConditionType.CLOUDY -> {
                val cloudY = h * 0.54f + bob
                drawCircle(color = TextSecondary, radius = w * 0.20f, center = Offset(w * 0.33f, cloudY))
                drawCircle(color = Color.White, radius = w * 0.25f, center = Offset(w * 0.51f, cloudY - h * 0.08f))
                drawCircle(color = TextSecondary, radius = w * 0.19f, center = Offset(w * 0.69f, cloudY))
            }

            WeatherConditionType.RAIN, WeatherConditionType.STORM -> {
                val cloudY = h * 0.38f + bob * 0.5f
                drawCircle(color = Color(0xFF94A3B8), radius = w * 0.18f, center = Offset(w * 0.34f, cloudY))
                drawCircle(color = Color(0xFFCBD5E1), radius = w * 0.22f, center = Offset(w * 0.50f, cloudY - h * 0.06f))
                drawCircle(color = Color(0xFF94A3B8), radius = w * 0.17f, center = Offset(w * 0.66f, cloudY))

                val dropOffsets = listOf(0.32f, 0.50f, 0.68f)
                dropOffsets.forEachIndexed { idx, xFrac ->
                    val dropProgress = (phase + idx * 0.33f) % 1f
                    val startY = h * (0.56f + dropProgress * 0.22f)
                    drawLine(
                        color = CyanPrimary.copy(alpha = 1f - dropProgress * 0.5f),
                        start = Offset(w * xFrac, startY),
                        end = Offset(w * (xFrac - 0.04f), startY + h * 0.13f),
                        strokeWidth = w * 0.055f,
                        cap = StrokeCap.Round
                    )
                }
                if (conditionType == WeatherConditionType.STORM && phase < 0.25f) {
                    drawLine(
                        color = GoldAccent,
                        start = Offset(w * 0.52f, h * 0.52f),
                        end = Offset(w * 0.45f, h * 0.72f),
                        strokeWidth = w * 0.06f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
