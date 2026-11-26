package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.model.WallpaperTheme
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.VoidNavy

@Composable
fun AnimatedAmbientBackground(
    theme: WallpaperTheme,
    dimAlpha: Float = 0.62f,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_bg")
    val zoom by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.09f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_zoom"
    )
    val drift by infiniteTransition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 19000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_drift"
    )
    val auroraShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aurora_shift"
    )

    Box(modifier = modifier.fillMaxSize().background(VoidNavy)) {
        val drawableRes = when (theme) {
            WallpaperTheme.MAKKAH_DUSK -> R.drawable.img_bg_makkah
            WallpaperTheme.NUSANTARA_NATURE -> R.drawable.img_bg_nature
            WallpaperTheme.NIGHT_STADIUM -> R.drawable.img_bg_stadium
            WallpaperTheme.AURORA_OLED -> null
        }

        if (drawableRes != null) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoom
                        scaleY = zoom
                        translationX = drift
                    }
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawRect(color = VoidNavy)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanPrimary.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(w * (0.25f + auroraShift * 0.3f), h * 0.3f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(w * (0.25f + auroraShift * 0.3f), h * 0.3f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(IndigoAccent.copy(alpha = 0.24f), Color.Transparent),
                        center = Offset(w * (0.8f - auroraShift * 0.25f), h * 0.65f),
                        radius = w * 0.5f
                    ),
                    radius = w * 0.5f,
                    center = Offset(w * (0.8f - auroraShift * 0.25f), h * 0.65f)
                )
            }
        }

        // Scrim & atmospheric glow for TV readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VoidNavy.copy(alpha = dimAlpha + 0.14f),
                            VoidNavy.copy(alpha = dimAlpha * 0.78f),
                            VoidNavy.copy(alpha = (dimAlpha + 0.25f).coerceAtMost(0.95f))
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GoldAccent.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(w * 0.85f, h * 0.18f),
                    radius = w * 0.32f
                ),
                radius = w * 0.32f,
                center = Offset(w * 0.85f, h * 0.18f)
            )
        }
    }
}
