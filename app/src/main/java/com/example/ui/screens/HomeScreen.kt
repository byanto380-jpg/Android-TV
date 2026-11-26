package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.R
import com.example.data.model.FootballMatch
import com.example.data.model.InstalledAppInfo
import com.example.data.model.MatchStatus
import com.example.data.model.PrayerScheduleState
import com.example.data.model.TvChannel
import com.example.data.model.WeatherState
import com.example.ui.components.AnimatedWeatherCanvasIcon
import com.example.ui.components.TvGlassCard
import com.example.ui.theme.CoralLive
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy
import com.example.ui.viewmodel.TvTab

@Composable
fun HomeScreen(
    weatherState: WeatherState,
    prayerState: PrayerScheduleState,
    clockText: String,
    dateText: String,
    channels: List<TvChannel>,
    matches: List<FootballMatch>,
    apps: List<InstalledAppInfo>,
    kidsLockEnabled: Boolean,
    onPlayChannel: (TvChannel) -> Unit,
    onOpenWeather: () -> Unit,
    onNavigateTab: (TvTab) -> Unit,
    onLaunchApp: (String) -> Unit
) {
    val featuredMakkah = channels.firstOrNull { it.id == "makkah_live" } ?: channels.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Hero Row (Asymmetric Google TV 14 Split)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Featured Hero Card
                TvGlassCard(
                    onClick = { featuredMakkah?.let(onPlayChannel) },
                    testTagName = "home_hero_featured_card",
                    modifier = Modifier
                        .weight(1.75f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp)
                ) { _ ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_bg_makkah),
                            contentDescription = "Siaran Utama",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            VoidNavy.copy(alpha = 0.92f),
                                            VoidNavy.copy(alpha = 0.55f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CoralLive)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "LIVE 24 JAM",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = if (kidsLockEnabled) "MODE ANAK AKTIF" else "PILIHAN UTAMA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = featuredMakkah?.name ?: "Nusantara TV Hub",
                                style = MaterialTheme.typography.headlineLarge,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = featuredMakkah?.description ?: "Siaran langsung HD bebas gangguan.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(CyanPrimary)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = "Putar",
                                    tint = VoidNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Tonton Siaran Langsung",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = VoidNavy
                                )
                            }
                        }
                    }
                }

                // Right Column: Weather + Prayer Countdown Widgets
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Weather Card
                    TvGlassCard(
                        onClick = onOpenWeather,
                        testTagName = "home_weather_card",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) { _ ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = weatherState.cityName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = CyanPrimary
                                )
                                Text(
                                    text = "${weatherState.temperatureC}°C",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = TextPrimary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = weatherState.conditionLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.WaterDrop,
                                        contentDescription = "Kelembapan",
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${weatherState.humidityPercent}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted
                                    )
                                }
                            }
                            AnimatedWeatherCanvasIcon(
                                conditionType = weatherState.conditionType,
                                size = 56.dp
                            )
                        }
                    }

                    // Prayer & Clock Card
                    TvGlassCard(
                        onClick = { onNavigateTab(TvTab.PRAYER) },
                        testTagName = "home_prayer_card",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) { _ ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Mosque,
                                        contentDescription = "Sholat Berikutnya",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Menuju ${prayerState.nextPrayerName} (${prayerState.nextPrayerTime})",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = GoldAccent
                                    )
                                }
                                Text(
                                    text = prayerState.countdownText,
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "$clockText • $dateText",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Row 1: TV Nasional & Live Makkah/Madinah
        item {
            SectionHeader(
                icon = Icons.Rounded.LiveTv,
                title = "Siaran TV Nasional & Live Haramain",
                actionLabel = "Lihat Semua",
                onAction = { onNavigateTab(TvTab.TV) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(channels.take(10), key = { it.id }) { channel ->
                    TvGlassCard(
                        onClick = { onPlayChannel(channel) },
                        testTagName = "home_channel_${channel.id}",
                        modifier = Modifier.width(230.dp)
                    ) { _ ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (channel.isFeaturedHaramain) GoldAccent else CyanPrimary.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = channel.badgeCode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (channel.isFeaturedHaramain) VoidNavy else CyanPrimary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = "Putar ${channel.name}",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = channel.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = channel.description,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Row 2: Football Highlights
        if (!kidsLockEnabled && matches.isNotEmpty()) {
            item {
                SectionHeader(
                    icon = Icons.Rounded.SportsSoccer,
                    title = "Jadwal Bola (Liga 1, Timnas, UCL, EPL, Bundesliga)",
                    actionLabel = "Jadwal Lengkap",
                    onAction = { onNavigateTab(TvTab.FOOTBALL) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(matches.take(8), key = { it.id }) { match ->
                        TvGlassCard(
                            onClick = { onNavigateTab(TvTab.FOOTBALL) },
                            testTagName = "home_match_${match.id}",
                            modifier = Modifier.width(275.dp)
                        ) { _ ->
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = match.league.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when (match.status) {
                                                    MatchStatus.LIVE -> CoralLive
                                                    MatchStatus.UPCOMING -> GoldAccent.copy(alpha = 0.2f)
                                                    MatchStatus.FINISHED -> GlassSurface
                                                }
                                            )
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = match.status.labelId,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (match.status == MatchStatus.UPCOMING) GoldAccent else TextPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${match.homeTeam} vs ${match.awayTeam}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${match.dateLabel} • ${match.kickoffWib}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    if (match.homeScore != null && match.awayScore != null) {
                                        Text(
                                            text = "${match.homeScore} - ${match.awayScore}",
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
            }
        }

        // Row 3: Installed Apps Strip
        item {
            SectionHeader(
                icon = Icons.Rounded.Apps,
                title = if (kidsLockEnabled) "Aplikasi Mode Anak (Diizinkan)" else "Aplikasi Android TV",
                actionLabel = "Semua Aplikasi",
                onAction = { onNavigateTab(TvTab.APPS) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassSurface)
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (kidsLockEnabled) {
                            "Belum ada aplikasi yang diizinkan untuk Mode Anak. Buka Pengaturan untuk memilih aplikasi."
                        } else {
                            "Tidak ada aplikasi tambahan terdeteksi."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(apps.take(10), key = { it.packageName }) { app ->
                        TvGlassCard(
                            onClick = { onLaunchApp(app.packageName) },
                            testTagName = "home_app_${app.packageName}",
                            modifier = Modifier.width(150.dp)
                        ) { _ ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                app.icon?.let { drawable ->
                                    Image(
                                        bitmap = drawable.toBitmap(96, 96).asImageBitmap(),
                                        contentDescription = app.label,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                } ?: Icon(
                                    imageVector = Icons.Rounded.Apps,
                                    contentDescription = app.label,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = app.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        }
        TvGlassCard(onClick = onAction, shape = RoundedCornerShape(999.dp)) { _ ->
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = CyanPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}
