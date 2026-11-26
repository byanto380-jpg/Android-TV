package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PrayerScheduleState
import com.example.data.model.TvChannel
import com.example.ui.components.TvGlassCard
import com.example.ui.theme.CoralLive
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy

@Composable
fun PrayerAndMakkahScreen(
    prayerState: PrayerScheduleState,
    clockText: String,
    dateText: String,
    adzanSoundEnabled: Boolean,
    haramainChannels: List<TvChannel>,
    onToggleAdzanSound: () -> Unit,
    onTestAdzan: () -> Unit,
    onPlayChannel: (TvChannel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Left: Digital Prayer Clock & 5 Daily Schedules
        Column(
            modifier = Modifier
                .weight(1.15f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(GlassSurface)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Jadwal Sholat Digital • ${prayerState.cityName}",
                            style = MaterialTheme.typography.titleLarge,
                            color = GoldAccent
                        )
                        Text(
                            text = "$dateText  •  ${prayerState.hijriDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TvGlassCard(
                            onClick = onToggleAdzanSound,
                            testTagName = "prayer_toggle_mute_btn",
                            shape = RoundedCornerShape(12.dp)
                        ) { _ ->
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (adzanSoundEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                                    contentDescription = "Suara Adzan",
                                    tint = if (adzanSoundEnabled) CyanPrimary else CoralLive,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (adzanSoundEnabled) "Adzan ON" else "Mute",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextPrimary
                                )
                            }
                        }
                        TvGlassCard(
                            onClick = onTestAdzan,
                            testTagName = "prayer_test_adzan_btn",
                            shape = RoundedCornerShape(12.dp)
                        ) { _ ->
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.NotificationsActive,
                                    contentDescription = "Uji Notifikasi Adzan",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Uji Adzan",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Digital Clock + Countdown Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(VoidNavy.copy(alpha = 0.72f))
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "JAM DIGITAL SAAT INI",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = clockText,
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp,
                            color = TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "MENUJU ${prayerState.nextPrayerName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent
                        )
                        Text(
                            text = "-${prayerState.countdownText}",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = CyanPrimary
                        )
                    }
                }
            }

            // 5 Daily Prayer Times Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                prayerState.prayers.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (item.isNext) GoldAccent else GlassSurfaceElevated)
                            .padding(vertical = 14.dp, horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.nameId,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (item.isNext) VoidNavy else TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.time24h,
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (item.isNext) VoidNavy else TextPrimary
                        )
                    }
                }
            }
        }

        // Right: Live Streaming 24 Jam Masjidil Haram & Masjid Nabawi
        Column(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            haramainChannels.take(2).forEachIndexed { idx, channel ->
                TvGlassCard(
                    onClick = { onPlayChannel(channel) },
                    testTagName = "haramain_live_card_$idx",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) { _ ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_bg_makkah),
                            contentDescription = channel.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            VoidNavy.copy(alpha = 0.45f),
                                            VoidNavy.copy(alpha = 0.92f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CoralLive)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "LIVE 24 JAM",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextPrimary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.Mosque,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = channel.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = channel.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = "Putar",
                                    tint = VoidNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Putar Siaran Langsung",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = VoidNavy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
