package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkStatus
import com.example.data.model.WeatherState
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy
import com.example.ui.viewmodel.TvTab

@Composable
fun TopTvHeaderBar(
    currentTab: TvTab,
    kidsLockEnabled: Boolean,
    weatherState: WeatherState,
    networkStatus: NetworkStatus,
    clockText: String,
    onSelectTab: (TvTab) -> Unit,
    onOpenWeather: () -> Unit,
    onToggleKidsLock: () -> Unit,
    onOpenScreensaver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Brand + Navigation Pills
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NUSANTARA TV",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
            }

            TvTab.entries.forEach { tab ->
                val isLockedTab = kidsLockEnabled && (tab == TvTab.SETTINGS || tab == TvTab.FOOTBALL)
                if (!(kidsLockEnabled && tab == TvTab.SETTINGS)) {
                    val isSelected = currentTab == tab
                    TvGlassCard(
                        onClick = { onSelectTab(tab) },
                        testTagName = "nav_tab_${tab.name.lowercase()}",
                        shape = RoundedCornerShape(999.dp),
                        containerColor = if (isSelected) CyanPrimary else GlassSurface.copy(alpha = 0.68f),
                        focusedContainerColor = if (isSelected) CyanPrimary else GlassSurfaceElevated,
                        focusScale = 1.06f
                    ) { _ ->
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = tabIcon(tab),
                                contentDescription = tab.titleId,
                                tint = if (isSelected) VoidNavy else TextPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = tab.titleId,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) VoidNavy else TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isLockedTab) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Terkunci",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Quick Settings Cluster (Top-Right)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Wi-Fi Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(GlassSurface)
                    .padding(horizontal = 11.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (networkStatus.isConnected) Icons.Rounded.Wifi else Icons.Rounded.WifiOff,
                        contentDescription = networkStatus.connectionLabel,
                        tint = if (networkStatus.isConnected) EmeraldLive else Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = networkStatus.connectionLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Weather Quick Pill
            TvGlassCard(
                onClick = onOpenWeather,
                testTagName = "quick_weather_button",
                shape = RoundedCornerShape(999.dp)
            ) { _ ->
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AnimatedWeatherCanvasIcon(
                        conditionType = weatherState.conditionType,
                        size = 22.dp
                    )
                    Text(
                        text = "${weatherState.temperatureC}°C • ${weatherState.cityName}",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }
            }

            // Kids Lock Toggle
            TvGlassCard(
                onClick = onToggleKidsLock,
                testTagName = "quick_kids_lock_button",
                shape = RoundedCornerShape(999.dp),
                containerColor = if (kidsLockEnabled) GoldAccent else GlassSurface,
                focusedContainerColor = if (kidsLockEnabled) GoldAccent else GlassSurfaceElevated
            ) { _ ->
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (kidsLockEnabled) Icons.Rounded.Lock else Icons.Rounded.ChildCare,
                        contentDescription = "Mode Anak",
                        tint = if (kidsLockEnabled) VoidNavy else GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (kidsLockEnabled) "Anak ON" else "Mode Anak",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp,
                        color = if (kidsLockEnabled) VoidNavy else TextPrimary,
                        maxLines = 1
                    )
                }
            }

            // Ambient Screensaver Trigger
            TvGlassCard(
                onClick = onOpenScreensaver,
                testTagName = "quick_screensaver_button",
                shape = CircleShape
            ) { _ ->
                Box(
                    modifier = Modifier.padding(9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NightsStay,
                        contentDescription = "Layar Ambient Screensaver",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Digital Clock
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(GlassSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = clockText.take(5),
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

private fun tabIcon(tab: TvTab): ImageVector = when (tab) {
    TvTab.HOME -> Icons.Rounded.Home
    TvTab.TV -> Icons.Rounded.LiveTv
    TvTab.FOOTBALL -> Icons.Rounded.SportsSoccer
    TvTab.PRAYER -> Icons.Rounded.Mosque
    TvTab.APPS -> Icons.Rounded.Apps
    TvTab.SETTINGS -> Icons.Rounded.Settings
}
