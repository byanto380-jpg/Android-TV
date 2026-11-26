package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.local.UserPreferencesState
import com.example.data.model.IndonesianCities
import com.example.data.model.InstalledAppInfo
import com.example.data.model.WallpaperTheme
import com.example.ui.components.TvGlassCard
import com.example.ui.theme.CoralLive
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy

@Composable
fun SettingsScreen(
    prefs: UserPreferencesState,
    installedApps: List<InstalledAppInfo>,
    onSelectCity: (String) -> Unit,
    onSelectWallpaper: (WallpaperTheme) -> Unit,
    onOpenScreensaver: () -> Unit,
    onToggleKidsLock: () -> Unit,
    onUpdatePin: (String) -> Unit,
    onToggleAppWhitelist: (String) -> Unit,
    onToggleAdzanSound: () -> Unit,
    onToggleAdzanPopup: () -> Unit,
    onAddCustomChannel: (name: String, url: String, isKidSafe: Boolean) -> Unit,
    onRemoveCustomChannel: (String) -> Unit
) {
    val context = LocalContext.current
    var newPin by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }
    var customUrl by remember { mutableStateOf("") }
    var customKidSafe by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Section 1: City & System Launcher
        item {
            SettingsCard(title = "Lokasi Cuaca & Waktu Sholat", icon = Icons.Rounded.LocationCity) {
                Text(
                    text = "Pilih kota di Indonesia untuk akurasi jadwal sholat Kemenag RI dan prakiraan cuaca:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(IndonesianCities.presets, key = { it.name }) { city ->
                        val selected = city.name.equals(prefs.selectedCity, ignoreCase = true)
                        TvGlassCard(
                            onClick = { onSelectCity(city.name) },
                            testTagName = "settings_city_${city.name.lowercase()}",
                            shape = RoundedCornerShape(999.dp),
                            containerColor = if (selected) CyanPrimary else GlassSurfaceElevated
                        ) { _ ->
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) VoidNavy else TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvGlassCard(
                        onClick = {
                            runCatching {
                                context.startActivity(
                                    Intent(Settings.ACTION_HOME_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        },
                        testTagName = "settings_set_home_launcher_btn"
                    ) { _ ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Home, contentDescription = null, tint = CyanPrimary)
                            Text("Jadikan Launcher Utama TV Box", color = TextPrimary)
                        }
                    }

                    TvGlassCard(
                        onClick = {
                            runCatching {
                                context.startActivity(
                                    Intent(Settings.ACTION_DREAM_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        },
                        testTagName = "settings_system_dream_btn"
                    ) { _ ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.NightsStay, contentDescription = null, tint = GoldAccent)
                            Text("Atur Screensaver Sistem (Daydream)", color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Section 2: Live Wallpaper & Ambient Screensaver
        item {
            SettingsCard(title = "Live Wallpaper & Ambient Screensaver", icon = Icons.Rounded.Wallpaper) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WallpaperTheme.entries.forEach { theme ->
                        val selected = theme == prefs.wallpaperTheme
                        TvGlassCard(
                            onClick = { onSelectWallpaper(theme) },
                            testTagName = "settings_wallpaper_${theme.name.lowercase()}",
                            modifier = Modifier.weight(1f),
                            containerColor = if (selected) CyanPrimary.copy(alpha = 0.25f) else GlassSurfaceElevated,
                            focusedBorderColor = GoldAccent
                        ) { _ ->
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = theme.titleId,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selected) CyanPrimary else TextPrimary
                                )
                                Text(
                                    text = theme.subtitleId,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TvGlassCard(
                    onClick = onOpenScreensaver,
                    testTagName = "settings_preview_screensaver_btn"
                ) { _ ->
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.NightsStay, contentDescription = null, tint = CyanPrimary)
                        Text("Tampilkan Screensaver Ambient Sekarang", color = TextPrimary)
                    }
                }
            }
        }

        // Section 3: Prayer & Adzan Settings
        item {
            SettingsCard(title = "Notifikasi & Audio Adzan Digital", icon = Icons.Rounded.NotificationsActive) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    TvGlassCard(
                        onClick = onToggleAdzanSound,
                        testTagName = "settings_adzan_sound_toggle",
                        containerColor = if (prefs.adzanSoundEnabled) EmeraldLive.copy(alpha = 0.22f) else GlassSurfaceElevated
                    ) { _ ->
                        Text(
                            text = if (prefs.adzanSoundEnabled) "Suara Adzan Otomatis: AKTIF" else "Suara Adzan Otomatis: MUTE",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                    TvGlassCard(
                        onClick = onToggleAdzanPopup,
                        testTagName = "settings_adzan_popup_toggle",
                        containerColor = if (prefs.adzanPopupEnabled) EmeraldLive.copy(alpha = 0.22f) else GlassSurfaceElevated
                    ) { _ ->
                        Text(
                            text = if (prefs.adzanPopupEnabled) "Pop-up Layar Adzan: AKTIF" else "Pop-up Layar Adzan: MATI",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // Section 4: Kids Lock & Whitelist
        item {
            SettingsCard(title = "Kids Lock (Mode Anak & Whitelist Aplikasi)", icon = Icons.Rounded.Lock) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TvGlassCard(
                        onClick = onToggleKidsLock,
                        testTagName = "settings_kids_lock_toggle",
                        containerColor = if (prefs.kidsLockEnabled) GoldAccent else GlassSurfaceElevated
                    ) { _ ->
                        Text(
                            text = if (prefs.kidsLockEnabled) "Mode Anak AKTIF" else "Aktifkan Mode Anak",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (prefs.kidsLockEnabled) VoidNavy else TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) newPin = it },
                        label = { Text("Ubah PIN (4 digit, saat ini ${prefs.kidsPin})") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    TvGlassCard(
                        onClick = {
                            if (newPin.length == 4) {
                                onUpdatePin(newPin)
                                newPin = ""
                            }
                        },
                        testTagName = "settings_save_pin_btn"
                    ) { _ ->
                        Text(
                            text = "Simpan PIN",
                            style = MaterialTheme.typography.labelLarge,
                            color = CyanPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Pilih Aplikasi yang Diizinkan saat Mode Anak Aktif:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (installedApps.isEmpty()) {
                    Text(
                        text = "Tidak ada aplikasi eksternal ditemukan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(installedApps, key = { it.packageName }) { app ->
                            val allowed = prefs.whitelistedPackages.contains(app.packageName)
                            TvGlassCard(
                                onClick = { onToggleAppWhitelist(app.packageName) },
                                testTagName = "whitelist_toggle_${app.packageName}",
                                containerColor = if (allowed) EmeraldLive.copy(alpha = 0.25f) else GlassSurfaceElevated
                            ) { _ ->
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (allowed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (allowed) EmeraldLive else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(app.label, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Custom M3U8 Channels
        item {
            SettingsCard(title = "Playlist TV Kustom (.m3u8 / HLS)", icon = Icons.Rounded.Add) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Nama Kanal (cth: TV Daerah)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = customUrl,
                        onValueChange = { customUrl = it },
                        label = { Text("URL Stream (.m3u8)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.6f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    TvGlassCard(
                        onClick = { customKidSafe = !customKidSafe },
                        containerColor = if (customKidSafe) EmeraldLive.copy(alpha = 0.22f) else GlassSurfaceElevated
                    ) { _ ->
                        Text(
                            text = if (customKidSafe) "Ramah Anak" else "Dewasa",
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                    TvGlassCard(
                        onClick = {
                            if (customName.isNotBlank() && customUrl.isNotBlank()) {
                                onAddCustomChannel(customName, customUrl, customKidSafe)
                                customName = ""
                                customUrl = ""
                            }
                        },
                        testTagName = "settings_add_channel_btn"
                    ) { _ ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Tambah Kanal", tint = CyanPrimary)
                            Text("Tambah", color = TextPrimary)
                        }
                    }
                }

                if (prefs.customChannels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        prefs.customChannels.forEach { ch ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GlassSurfaceElevated)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ch.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                    Text(ch.streamUrl, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                                }
                                TvGlassCard(onClick = { onRemoveCustomChannel(ch.id) }) { _ ->
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Hapus ${ch.name}",
                                        tint = CoralLive,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(GlassSurface)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
