package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.data.model.InstalledAppInfo
import com.example.ui.components.TvGlassCard
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy

@Composable
fun AppsScreen(
    apps: List<InstalledAppInfo>,
    kidsLockEnabled: Boolean,
    onLaunchApp: (String) -> Unit,
    onRefreshApps: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (kidsLockEnabled) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Mode Anak",
                            tint = GoldAccent
                        )
                    }
                    Text(
                        text = if (kidsLockEnabled) {
                            "Aplikasi Mode Anak (Diizinkan Orang Tua)"
                        } else {
                            "Semua Aplikasi Android TV Box"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                }
                Text(
                    text = if (kidsLockEnabled) {
                        "Akses aplikasi dibatasi sesuai daftar izin di Pengaturan."
                    } else {
                        "Pilih aplikasi untuk membuka langsung dari launcher."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            TvGlassCard(
                onClick = onRefreshApps,
                testTagName = "apps_refresh_btn",
                shape = RoundedCornerShape(999.dp)
            ) { _ ->
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Segarkan Aplikasi", tint = CyanPrimary, modifier = Modifier.size(18.dp))
                    Text("Segarkan", color = TextPrimary, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassSurface)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.Apps,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (kidsLockEnabled) {
                            "Belum ada aplikasi yang diizinkan pada Mode Anak.\nMatikan Mode Anak dengan PIN lalu atur daftar aplikasi di Pengaturan."
                        } else {
                            "Belum ada aplikasi lain terpasang di perangkat ini."
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 165.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(apps, key = { it.packageName }) { app ->
                    TvGlassCard(
                        onClick = { onLaunchApp(app.packageName) },
                        testTagName = "app_tile_${app.packageName}",
                        shape = RoundedCornerShape(20.dp)
                    ) { _ ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            app.icon?.let { drawable ->
                                Image(
                                    bitmap = drawable.toBitmap(128, 128).asImageBitmap(),
                                    contentDescription = app.label,
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                )
                            } ?: Icon(
                                imageVector = Icons.Rounded.Apps,
                                contentDescription = app.label,
                                tint = CyanPrimary,
                                modifier = Modifier.size(62.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = app.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (app.isTvLeanback) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyanPrimary.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tv,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "TV APP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanPrimary
                                    )
                                }
                            } else {
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.labelSmall,
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
    }
}
