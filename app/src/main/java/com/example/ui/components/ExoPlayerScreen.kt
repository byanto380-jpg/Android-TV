package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import com.example.data.model.TvChannel
import com.example.ui.theme.CoralLive
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun FullscreenTvPlayerScreen(
    channel: TvChannel,
    allChannels: List<TvChannel>,
    onSelectChannel: (TvChannel) -> Unit,
    onSwitchRelative: (Int) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var usingFallbackUrl by remember(channel.id) { mutableStateOf(false) }

    BackHandler { onClose() }

    val activeUrl = if (usingFallbackUrl && !channel.fallbackStreamUrl.isNullOrBlank()) {
        channel.fallbackStreamUrl
    } else {
        channel.streamUrl
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(6000L)
            showControls = false
        }
    }

    LaunchedEffect(activeUrl) {
        isBuffering = true
        errorMessage = null

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("NusantaraTV/1.0 (Android TV)")
            .setAllowCrossProtocolRedirects(true)

        val mediaItemBuilder = MediaItem.Builder().setUri(activeUrl)
        if (activeUrl.contains(".m3u8", ignoreCase = true)) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }
        val mediaItem = mediaItemBuilder.build()

        if (activeUrl.contains(".m3u8", ignoreCase = true)) {
            val hlsSource = HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(mediaItem)
            exoPlayer.setMediaSource(hlsSource)
        } else {
            exoPlayer.setMediaItem(mediaItem)
        }
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    errorMessage = null
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (!usingFallbackUrl && !channel.fallbackStreamUrl.isNullOrBlank()) {
                    usingFallbackUrl = true
                } else {
                    isBuffering = false
                    errorMessage = "Siaran sedang gangguan atau diblokir jaringan (${error.errorCodeName})."
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp) {
                    showControls = true
                    when (event.key) {
                        Key.ChannelUp, Key.PageUp -> {
                            onSwitchRelative(1)
                            true
                        }
                        Key.ChannelDown, Key.PageDown -> {
                            onSwitchRelative(-1)
                            true
                        }
                        Key.Back, Key.Escape -> {
                            onClose()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    keepScreenOn = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isBuffering && errorMessage == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = CyanPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Memuat siaran ${channel.name}...",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }
        }

        if (errorMessage != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DeepSlate.copy(alpha = 0.92f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Stream Tidak Dapat Diputar",
                    style = MaterialTheme.typography.titleLarge,
                    color = CoralLive
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvGlassCard(
                        onClick = {
                            errorMessage = null
                            isBuffering = true
                            exoPlayer.prepare()
                            exoPlayer.play()
                        },
                        testTagName = "player_retry_btn"
                    ) { _ ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Coba Lagi", tint = CyanPrimary)
                            Text("Coba Lagi", color = TextPrimary)
                        }
                    }
                    TvGlassCard(
                        onClick = { onSwitchRelative(1) },
                        testTagName = "player_next_channel_on_err"
                    ) { _ ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.SkipNext, contentDescription = "Kanal Berikutnya", tint = GoldAccent)
                            Text("Ganti Kanal", color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Top & Bottom OSD Controls
        AnimatedVisibility(
            visible = showControls || errorMessage != null,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(VoidNavy.copy(alpha = 0.88f), Color.Transparent)
                            )
                        )
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TvGlassCard(
                            onClick = onClose,
                            testTagName = "player_back_btn"
                        ) { _ ->
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = TextPrimary
                                )
                                Text("Kembali", color = TextPrimary)
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CoralLive)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = channel.badgeCode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = channel.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = channel.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TvGlassCard(onClick = { onSwitchRelative(-1) }) { _ ->
                            Box(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Rounded.SkipPrevious, contentDescription = "Kanal Sebelumnya", tint = TextPrimary)
                            }
                        }
                        TvGlassCard(onClick = {
                            isMuted = !isMuted
                            exoPlayer.volume = if (isMuted) 0f else 1f
                        }) { _ ->
                            Box(modifier = Modifier.padding(10.dp)) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = "Mute TV",
                                    tint = if (isMuted) CoralLive else CyanPrimary
                                )
                            }
                        }
                        TvGlassCard(onClick = { onSwitchRelative(1) }) { _ ->
                            Box(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Rounded.SkipNext, contentDescription = "Kanal Berikutnya", tint = TextPrimary)
                            }
                        }
                    }
                }

                // Bottom Channel Switcher Strip
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, VoidNavy.copy(alpha = 0.92f))
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = "Daftar Kanal Cepat",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(allChannels, key = { it.id }) { item ->
                            val selected = item.id == channel.id
                            TvGlassCard(
                                onClick = {
                                    showControls = true
                                    onSelectChannel(item)
                                },
                                containerColor = if (selected) CyanPrimary else GlassSurface,
                                focusedContainerColor = if (selected) CyanPrimary else GlassSurfaceElevated
                            ) { _ ->
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selected) VoidNavy else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.badgeCode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) VoidNavy.copy(alpha = 0.7f) else GoldAccent
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
