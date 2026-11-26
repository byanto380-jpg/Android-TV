package com.example.ui.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralLive
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VoidNavy

@Composable
fun KidsPinDialog(
    title: String,
    errorMessage: String?,
    onSubmitPin: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pinBuffer by remember { mutableStateOf("") }

    LaunchedEffect(pinBuffer) {
        if (pinBuffer.length == 4) {
            onSubmitPin(pinBuffer)
            pinBuffer = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidNavy.copy(alpha = 0.84f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DeepSlate)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = "PIN Mode Anak",
                tint = GoldAccent,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "PIN default: 1234",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4 PIN Dots
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(4) { idx ->
                    val filled = idx < pinBuffer.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (filled) CyanPrimary else GlassSurfaceElevated)
                            .border(1.5.dp, if (filled) CyanPrimary else GlassBorder, CircleShape)
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CoralLive
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BACK", "0", "CANCEL")
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { key ->
                            TvGlassCard(
                                onClick = {
                                    when (key) {
                                        "BACK" -> if (pinBuffer.isNotEmpty()) pinBuffer = pinBuffer.dropLast(1)
                                        "CANCEL" -> onDismiss()
                                        else -> if (pinBuffer.length < 4) pinBuffer += key
                                    }
                                },
                                testTagName = "pin_key_$key",
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) { _ ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (key) {
                                        "BACK" -> Icon(
                                            imageVector = Icons.Rounded.Backspace,
                                            contentDescription = "Hapus",
                                            tint = GoldAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        "CANCEL" -> Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Batal",
                                            tint = CoralLive,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        else -> Text(
                                            text = key,
                                            fontFamily = MonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = TextPrimary
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
}
