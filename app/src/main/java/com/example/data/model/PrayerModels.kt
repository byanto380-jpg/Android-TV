package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AladhanResponse(
    val code: Int = 200,
    val data: AladhanDataDto? = null
)

@JsonClass(generateAdapter = true)
data class AladhanDataDto(
    val timings: AladhanTimingsDto? = null,
    val date: AladhanDateDto? = null
)

@JsonClass(generateAdapter = true)
data class AladhanTimingsDto(
    @Json(name = "Fajr") val fajr: String = "04:20",
    @Json(name = "Sunrise") val sunrise: String = "05:35",
    @Json(name = "Dhuhr") val dhuhr: String = "11:45",
    @Json(name = "Asr") val asr: String = "15:05",
    @Json(name = "Maghrib") val maghrib: String = "17:52",
    @Json(name = "Isha") val isha: String = "19:05"
)

@JsonClass(generateAdapter = true)
data class AladhanDateDto(
    val readable: String = "",
    val hijri: AladhanHijriDto? = null
)

@JsonClass(generateAdapter = true)
data class AladhanHijriDto(
    val date: String = "",
    val day: String = "",
    val month: AladhanHijriMonthDto? = null,
    val year: String = ""
)

@JsonClass(generateAdapter = true)
data class AladhanHijriMonthDto(
    val en: String = ""
)

data class PrayerTimeItem(
    val nameId: String,
    val time24h: String,
    val totalMinutes: Int,
    val isNext: Boolean = false
)

data class PrayerScheduleState(
    val cityName: String = "Jakarta",
    val hijriDate: String = "Jumadil Akhir 1448 H",
    val prayers: List<PrayerTimeItem> = defaultPrayers(),
    val nextPrayerName: String = "Ashar",
    val nextPrayerTime: String = "15:05",
    val countdownText: String = "01:24:10",
    val isNowAdzanWindow: Boolean = false,
    val activeAdzanPrayer: String? = null
) {
    companion object {
        fun defaultPrayers(): List<PrayerTimeItem> = listOf(
            PrayerTimeItem("Subuh", "04:18", 4 * 60 + 18),
            PrayerTimeItem("Dzuhur", "11:46", 11 * 60 + 46),
            PrayerTimeItem("Ashar", "15:08", 15 * 60 + 8, isNext = true),
            PrayerTimeItem("Maghrib", "17:54", 17 * 60 + 54),
            PrayerTimeItem("Isya", "19:07", 19 * 60 + 7)
        )
    }
}
