package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerScheduleState
import com.example.data.model.WallpaperTheme
import com.example.data.model.WeatherState
import com.example.ui.components.AnimatedAmbientBackground
import com.example.ui.components.AnimatedWeatherCanvasIcon
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AmbientScreensaverScreen(
    wallpaperTheme: WallpaperTheme,
    clockText: String,
    dateText: String,
    weatherState: WeatherState,
    prayerState: PrayerScheduleState,
    onExitScreensaver: () -> Unit
) {
    BackHandler { onExitScreensaver() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp) {
                    onExitScreensaver()
                    true
                } else {
                    false
                }
            }
            .clickable { onExitScreensaver() }
    ) {
        AnimatedAmbientBackground(theme = wallpaperTheme, dimAlpha = 0.42f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 38.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar: Weather + Prayer pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassSurface)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedWeatherCanvasIcon(conditionType = weatherState.conditionType, size = 38.dp)
                    Column {
                        Text(
                            text = "${weatherState.cityName} • ${weatherState.temperatureC}°C",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "${weatherState.conditionLabel} • Kelembapan ${weatherState.humidityPercent}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassSurface)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Mosque,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(26.dp)
                    )
                    Column {
                        Text(
                            text = "Sholat ${prayerState.nextPrayerName} (${prayerState.nextPrayerTime})",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldAccent
                        )
                        Text(
                            text = "Menuju adzan: -${prayerState.countdownText}",
                            fontFamily = MonoFontFamily,
                            fontSize = 13.sp,
                            color = CyanPrimary
                        )
                    }
                }
            }

            // Bottom-Left Huge Ambient Clock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = clockText.take(5),
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 96.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$dateText  •  ${prayerState.hijriDate}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "Tekan tombol apa saja untuk kembali ke Nusantara TV",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }
    }
}
