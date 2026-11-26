package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.ChannelCategory
import com.example.data.model.TvChannel
import com.example.ui.components.TvGlassCard
import com.example.ui.theme.CoralLive
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy

@Composable
fun TvChannelsScreen(
    channels: List<TvChannel>,
    selectedCategory: ChannelCategory,
    kidsLockEnabled: Boolean,
    onSelectCategory: (ChannelCategory) -> Unit,
    onPlayChannel: (TvChannel) -> Unit
) {
    val filtered = if (selectedCategory == ChannelCategory.ALL) {
        channels
    } else {
        channels.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Siaran TV Nasional & Live Makkah / Madinah",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = if (kidsLockEnabled) {
                        "Mode Anak aktif — Menampilkan kanal ramah anak & edukasi"
                    } else {
                        "Pemutar HLS (.m3u8) Media3 ExoPlayer dengan ganti kanal D-Pad"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ChannelCategory.entries) { category ->
                val isSelected = category == selectedCategory
                TvGlassCard(
                    onClick = { onSelectCategory(category) },
                    testTagName = "tv_cat_${category.name.lowercase()}",
                    shape = RoundedCornerShape(999.dp),
                    containerColor = if (isSelected) CyanPrimary else GlassSurface
                ) { _ ->
                    Text(
                        text = category.titleId,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) VoidNavy else TextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada kanal di kategori ini. Tambah URL .m3u8 di menu Pengaturan.",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 260.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filtered, key = { it.id }) { channel ->
                    TvGlassCard(
                        onClick = { onPlayChannel(channel) },
                        testTagName = "tv_channel_card_${channel.id}",
                        shape = RoundedCornerShape(20.dp)
                    ) { _ ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.LiveTv,
                                        contentDescription = null,
                                        tint = if (channel.isFeaturedHaramain) GoldAccent else CyanPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (channel.isFeaturedHaramain) CoralLive else CyanPrimary.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = channel.badgeCode,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (channel.isFeaturedHaramain) TextPrimary else CyanPrimary
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Rounded.PlayCircleFilled,
                                    contentDescription = "Putar ${channel.name}",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = channel.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = channel.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
