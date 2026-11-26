package com.example.data.repository

import com.example.data.model.IndonesianCities
import com.example.data.model.PrayerScheduleState
import com.example.data.model.PrayerTimeItem
import com.example.data.remote.NetworkModule
import com.example.data.remote.PrayerApiService
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class PrayerRepository(
    private val api: PrayerApiService = NetworkModule.prayerApi
) {
    suspend fun fetchPrayerSchedule(cityName: String): PrayerScheduleState {
        val city = IndonesianCities.findByName(cityName)
        return runCatching {
            val response = api.getTimingsByCity(city = city.name)
            val data = response.data ?: return@runCatching fallbackSchedule(city.name)
            val timings = data.timings ?: return@runCatching fallbackSchedule(city.name)

            val rawItems = listOf(
                "Subuh" to cleanTime(timings.fajr),
                "Dzuhur" to cleanTime(timings.dhuhr),
                "Ashar" to cleanTime(timings.asr),
                "Maghrib" to cleanTime(timings.maghrib),
                "Isya" to cleanTime(timings.isha)
            ).map { (name, timeStr) ->
                PrayerTimeItem(
                    nameId = name,
                    time24h = timeStr,
                    totalMinutes = parseMinutes(timeStr)
                )
            }

            val hijri = data.date?.hijri?.let {
                "${it.day} ${it.month?.en.orEmpty()} ${it.year} H".trim()
            } ?: "Kalender Hijriah"

            computeCountdown(
                PrayerScheduleState(
                    cityName = city.name,
                    hijriDate = hijri,
                    prayers = rawItems
                ),
                city.timezone
            )
        }.getOrElse {
            computeCountdown(fallbackSchedule(city.name), city.timezone)
        }
    }

    fun computeCountdown(
        state: PrayerScheduleState,
        timezoneId: String = IndonesianCities.findByName(state.cityName).timezone
    ): PrayerScheduleState {
        val cal = Calendar.getInstance(TimeZone.getTimeZone(timezoneId))
        val nowHour = cal.get(Calendar.HOUR_OF_DAY)
        val nowMinute = cal.get(Calendar.MINUTE)
        val nowSecond = cal.get(Calendar.SECOND)
        val nowTotalSeconds = nowHour * 3600 + nowMinute * 60 + nowSecond
        val nowTotalMinutes = nowHour * 60 + nowMinute

        val activeAdzan = state.prayers.firstOrNull { it.totalMinutes == nowTotalMinutes }

        val nextIdx = state.prayers.indexOfFirst { it.totalMinutes * 60 > nowTotalSeconds }
        val resolvedIdx = if (nextIdx >= 0) nextIdx else 0
        val nextPrayer = state.prayers[resolvedIdx]

        var diffSeconds = nextPrayer.totalMinutes * 60 - nowTotalSeconds
        if (diffSeconds < 0) {
            diffSeconds += 24 * 3600
        }

        val hours = diffSeconds / 3600
        val mins = (diffSeconds % 3600) / 60
        val secs = diffSeconds % 60
        val countdownStr = String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)

        val updatedList = state.prayers.mapIndexed { idx, item ->
            item.copy(isNext = idx == resolvedIdx)
        }

        return state.copy(
            prayers = updatedList,
            nextPrayerName = nextPrayer.nameId,
            nextPrayerTime = nextPrayer.time24h,
            countdownText = countdownStr,
            isNowAdzanWindow = activeAdzan != null && nowSecond < 50,
            activeAdzanPrayer = activeAdzan?.nameId
        )
    }

    private fun cleanTime(raw: String): String =
        raw.substringBefore(" ").trim().take(5)

    private fun parseMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }

    private fun fallbackSchedule(cityName: String): PrayerScheduleState =
        PrayerScheduleState(
            cityName = cityName,
            hijriDate = "15 Jumadil Akhir 1448 H",
            prayers = PrayerScheduleState.defaultPrayers()
        )
}
