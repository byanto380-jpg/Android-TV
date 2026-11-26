package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class AdzanAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var audioJob: Job? = null

    fun playAdzan(scope: CoroutineScope, onFinished: () -> Unit = {}) {
        stop()
        audioJob = scope.launch(Dispatchers.IO) {
            val streamStarted = tryPlayRemoteAdzan(onFinished)
            if (!streamStarted) {
                playPeacefulChime()
                onFinished()
            }
        }
    }

    private fun tryPlayRemoteAdzan(onFinished: () -> Unit): Boolean {
        return runCatching {
            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource("https://www.islamcan.com/audio/adhan/azan1.mp3")
                setOnCompletionListener {
                    stop()
                    onFinished()
                }
                setOnErrorListener { _, _, _ ->
                    stop()
                    false
                }
                prepare()
                start()
            }
            mediaPlayer = mp
            true
        }.getOrDefault(false)
    }

    private fun playPeacefulChime() {
        runCatching {
            val sampleRate = 22050
            val notes = listOf(440.0, 554.37, 659.25, 880.0)
            val noteSamples = (sampleRate * 0.45).toInt()
            val totalSamples = noteSamples * notes.size
            val buffer = ShortArray(totalSamples)

            notes.forEachIndexed { idx, freq ->
                val offset = idx * noteSamples
                for (i in 0 until noteSamples) {
                    val envelope = 1.0 - (i.toDouble() / noteSamples)
                    val sample = sin(2.0 * PI * freq * i / sampleRate) * envelope * 0.35
                    buffer[offset + i] = (sample * Short.MAX_VALUE).toInt().toShort()
                }
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(1900)
            track.stop()
            track.release()
        }
    }

    fun stop() {
        audioJob?.cancel()
        audioJob = null
        runCatching {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = null
    }
}
