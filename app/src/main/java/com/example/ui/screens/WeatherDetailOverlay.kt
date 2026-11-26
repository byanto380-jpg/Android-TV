package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherState
import com.example.ui.components.AnimatedWeatherCanvasIcon
import com.example.ui.components.TvGlassCard
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy

@Composable
fun WeatherDetailOverlay(
    weatherState: WeatherState,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidNavy.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 920.dp)
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .background(DeepSlate)
                .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
                .padding(26.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnimatedWeatherCanvasIcon(
                        conditionType = weatherState.conditionType,
                        size = 64.dp
                    )
                    Column {
                        Text(
                            text = "Prakiraan Cuaca • ${weatherState.cityName}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "${weatherState.temperatureC}°C",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                color = CyanPrimary
                            )
                            Text(
                                text = weatherState.conditionLabel,
                                style = MaterialTheme.typography.titleLarge,
                                color = GoldAccent
                            )
                        }
                    }
                }

                TvGlassCard(
                    onClick = onClose,
                    testTagName = "weather_modal_close_btn",
                    shape = RoundedCornerShape(14.dp)
                ) { _ ->
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = TextPrimary)
                        Text("Tutup", color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherMetricPill(
                    icon = Icons.Rounded.Thermostat,
                    label = "Terasa Seperti",
                    value = "${weatherState.feelsLikeC}°C",
                    modifier = Modifier.weight(1f)
                )
                WeatherMetricPill(
                    icon = Icons.Rounded.WaterDrop,
                    label = "Kelembapan",
                    value = "${weatherState.humidityPercent}%",
                    modifier = Modifier.weight(1f)
                )
                WeatherMetricPill(
                    icon = Icons.Rounded.Air,
                    label = "Kecepatan Angin",
                    value = "${weatherState.windKmh} km/j",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Prakiraan Per Jam",
                style = MaterialTheme.typography.titleMedium,
                color = CyanPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(weatherState.hourly) { item ->
                    Column(
                        modifier = Modifier
                            .width(96.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassSurface)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.hourLabel,
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        AnimatedWeatherCanvasIcon(
                            conditionType = item.conditionType,
                            size = 30.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${item.tempCelsius}°",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${item.rainChancePercent}% hujan",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 11.sp,
                            color = CyanPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Prakiraan 5 Hari Ke Depan",
                style = MaterialTheme.typography.titleMedium,
                color = GoldAccent
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                weatherState.daily.forEach { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(GlassSurfaceElevated)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${day.dayLabel} (${day.dateLabel})",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = day.conditionLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        AnimatedWeatherCanvasIcon(
                            conditionType = day.conditionType,
                            size = 28.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "${day.rainChancePercent}% hujan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CyanPrimary
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = "${day.minTemp}° / ${day.maxTemp}°C",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = label, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextMuted)
            Text(
                text = value,
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }
    }
}
