package com.example.data.model

import android.graphics.drawable.Drawable
import com.squareup.moshi.JsonClass

enum class ChannelCategory(val titleId: String) {
    ALL("Semua Kanal"),
    MAKKAH_MADINAH("Makkah & Madinah 24J"),
    TV_NASIONAL("TV Nasional"),
    ANAK_EDUKASI("Anak & Edukasi"),
    KUSTOM("Playlist Kustom")
}

@JsonClass(generateAdapter = true)
data class TvChannel(
    val id: String,
    val name: String,
    val categoryName: String,
    val streamUrl: String,
    val fallbackStreamUrl: String? = null,
    val description: String = "",
    val badgeCode: String = "HD",
    val isKidSafe: Boolean = false,
    val isFeaturedHaramain: Boolean = false
) {
    val category: ChannelCategory
        get() = ChannelCategory.entries.firstOrNull { it.name == categoryName }
            ?: ChannelCategory.TV_NASIONAL
}

enum class WallpaperTheme(val titleId: String, val subtitleId: String) {
    MAKKAH_DUSK("Masjidil Haram Senja", "Arsitektur Masjid bercahaya emas & langit indigo"),
    NUSANTARA_NATURE("Pegunungan Nusantara", "Panorama danau berkabut & mentari tropis"),
    NIGHT_STADIUM("Stadion Malam", "Sorotan lampu stadion sepak bola modern"),
    AURORA_OLED("Aurora OLED Gelap", "Gelombang cahaya cyan & emas minimalis")
}

data class InstalledAppInfo(
    val packageName: String,
    val label: String,
    val isTvLeanback: Boolean,
    val icon: Drawable? = null
)

data class NetworkStatus(
    val isConnected: Boolean = true,
    val connectionLabel: String = "Wi-Fi Terhubung"
)
