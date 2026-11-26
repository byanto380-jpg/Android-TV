package com.example

import com.example.data.model.PrayerScheduleState
import com.example.data.model.PrayerTimeItem
import com.example.data.model.WeatherCodeMapper
import com.example.data.model.WeatherConditionType
import com.example.data.repository.PrayerRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NusantaraTvCoreTest {

    @Test
    fun `weather code mapper maps rain and clear accurately`() {
        val (clearLabel, clearType) = WeatherCodeMapper.map(0)
        assertEquals("Cerah", clearLabel)
        assertEquals(WeatherConditionType.CLEAR, clearType)

        val (rainLabel, rainType) = WeatherCodeMapper.map(63)
        assertEquals("Hujan", rainLabel)
        assertEquals(WeatherConditionType.RAIN, rainType)
    }

    @Test
    fun `prayer countdown formats HH_MM_SS and marks one next prayer`() {
        val repo = PrayerRepository()
        val initial = PrayerScheduleState(
            cityName = "Jakarta",
            prayers = listOf(
                PrayerTimeItem("Subuh", "04:15", 4 * 60 + 15),
                PrayerTimeItem("Dzuhur", "11:45", 11 * 60 + 45),
                PrayerTimeItem("Ashar", "15:05", 15 * 60 + 5),
                PrayerTimeItem("Maghrib", "17:52", 17 * 60 + 52),
                PrayerTimeItem("Isya", "19:05", 19 * 60 + 5)
            )
        )
        val computed = repo.computeCountdown(initial, "Asia/Jakarta")
        assertTrue(computed.countdownText.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
        assertEquals(1, computed.prayers.count { it.isNext })
    }
}
