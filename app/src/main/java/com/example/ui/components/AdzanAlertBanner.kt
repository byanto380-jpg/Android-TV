package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdzanAlertBanner(
    prayerName: String,
    cityName: String,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DeepSlate.copy(alpha = 0.96f))
            .border(1.5.dp, GoldAccent, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Mosque,
            contentDescription = "Waktu Sholat",
            tint = GoldAccent,
            modifier = Modifier.size(32.dp)
        )
        Column {
            Text(
                text = "Telah Masuk Waktu Sholat $prayerName",
                style = MaterialTheme.typography.titleMedium,
                color = GoldAccent
            )
            Text(
                text = "Wilayah $cityName & sekitarnya • Mari tunaikan sholat",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        TvGlassCard(
            onClick = onToggleMute,
            testTagName = "adzan_banner_mute_btn",
            shape = RoundedCornerShape(12.dp)
        ) { _ ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = if (isMuted) "Unmute Adzan" else "Mute Adzan",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isMuted) "Unmute" else "Mute",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
            }
        }
        TvGlassCard(
            onClick = onDismiss,
            testTagName = "adzan_banner_dismiss_btn",
            shape = RoundedCornerShape(12.dp)
        ) { _ ->
            Box(modifier = Modifier.padding(9.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Tutup pengingat adzan",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
