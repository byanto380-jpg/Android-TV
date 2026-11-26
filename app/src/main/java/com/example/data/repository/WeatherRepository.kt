package com.example.data.repository

import com.example.data.model.CityOption
import com.example.data.model.DailyForecastItem
import com.example.data.model.DailyWeatherDto
import com.example.data.model.HourlyForecastItem
import com.example.data.model.HourlyWeatherDto
import com.example.data.model.IndonesianCities
import com.example.data.model.WeatherCodeMapper
import com.example.data.model.WeatherConditionType
import com.example.data.model.WeatherState
import com.example.data.remote.NetworkModule
import com.example.data.remote.WeatherApiService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class WeatherRepository(
    private val api: WeatherApiService = NetworkModule.weatherApi
) {
    suspend fun fetchWeather(cityName: String): WeatherState {
        val city = IndonesianCities.findByName(cityName)
        return runCatching {
            val response = api.getForecast(
                latitude = city.latitude,
                longitude = city.longitude
            )
            val current = response.current ?: return@runCatching fallbackWeather(city)
            val (condLabel, condType) = WeatherCodeMapper.map(current.weatherCode)

            val hourlyList = buildHourlyList(response.hourly, city.timezone)
            val dailyList = buildDailyList(response.daily)

            WeatherState(
                cityName = city.name,
                temperatureC = current.temperature2m.roundToInt(),
                feelsLikeC = current.apparentTemperature.roundToInt(),
                humidityPercent = current.relativeHumidity2m,
                windKmh = current.windSpeed10m.roundToInt(),
                conditionLabel = condLabel,
                conditionType = condType,
                isDay = current.isDay == 1,
                hourly = hourlyList,
                daily = dailyList,
                isFromCacheOrFallback = false
            )
        }.getOrElse {
            fallbackWeather(city)
        }
    }

    private fun buildHourlyList(
        dto: HourlyWeatherDto?,
        timezone: String
    ): List<HourlyForecastItem> {
        if (dto == null || dto.time.isEmpty()) return emptyList()
        val cal = Calendar.getInstance(TimeZone.getTimeZone(timezone))
        val currentHourStr = SimpleDateFormat("yyyy-MM-dd'T'HH", Locale.US).apply {
            timeZone = TimeZone.getTimeZone(timezone)
        }.format(cal.time)

        val startIdx = dto.time.indexOfFirst { it.startsWith(currentHourStr) }.coerceAtLeast(0)
        val endIdx = (startIdx + 12).coerceAtMost(dto.time.size)

        return (startIdx until endIdx).map { idx ->
            val rawTime = dto.time[idx]
            val hourLabel = rawTime.substringAfter("T").take(5)
            val temp = dto.temperature2m.getOrNull(idx)?.roundToInt() ?: 28
            val code = dto.weatherCode.getOrNull(idx) ?: 1
            val rain = dto.precipitationProbability.getOrNull(idx) ?: 10
            HourlyForecastItem(
                hourLabel = hourLabel,
                tempCelsius = temp,
                rainChancePercent = rain,
                conditionType = WeatherCodeMapper.map(code).second
            )
        }
    }

    private fun buildDailyList(dto: DailyWeatherDto?): List<DailyForecastItem> {
        if (dto == null || dto.time.isEmpty()) return emptyList()
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFmt = SimpleDateFormat("EEE", Locale("id", "ID"))
        val dateFmt = SimpleDateFormat("dd MMM", Locale("id", "ID"))

        return dto.time.take(5).mapIndexed { idx, dateStr ->
            val parsed = runCatching { parser.parse(dateStr) }.getOrNull()
            val dayLabel = if (idx == 0) "Hari Ini" else parsed?.let { dayFmt.format(it) } ?: "H+$idx"
            val dateLabel = parsed?.let { dateFmt.format(it) } ?: dateStr
            val code = dto.weatherCode.getOrNull(idx) ?: 1
            val (label, type) = WeatherCodeMapper.map(code)
            DailyForecastItem(
                dayLabel = dayLabel,
                dateLabel = dateLabel,
                minTemp = dto.temperature2mMin.getOrNull(idx)?.roundToInt() ?: 24,
                maxTemp = dto.temperature2mMax.getOrNull(idx)?.roundToInt() ?: 32,
                rainChancePercent = dto.precipitationProbabilityMax.getOrNull(idx) ?: 20,
                conditionLabel = label,
                conditionType = type
            )
        }
    }

    private fun fallbackWeather(city: CityOption): WeatherState {
        val hourly = (0..8).map { offset ->
            val hour = (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + offset) % 24
            HourlyForecastItem(
                hourLabel = String.format(Locale.US, "%02d:00", hour),
                tempCelsius = 28 + (if (hour in 11..15) 4 else 0),
                rainChancePercent = if (hour >= 16) 45 else 15,
                conditionType = if (hour >= 16) WeatherConditionType.RAIN else WeatherConditionType.PARTLY_CLOUDY
            )
        }
        val daily = listOf(
            DailyForecastItem("Hari Ini", "Hari Ini", 25, 32, 30, "Cerah Berawan", WeatherConditionType.PARTLY_CLOUDY),
            DailyForecastItem("Besok", "Besok", 24, 31, 55, "Hujan", WeatherConditionType.RAIN),
            DailyForecastItem("Lusa", "H+2", 25, 33, 20, "Cerah", WeatherConditionType.CLEAR),
            DailyForecastItem("H+3", "H+3", 24, 31, 40, "Berawan", WeatherConditionType.CLOUDY),
            DailyForecastItem("H+4", "H+4", 24, 30, 65, "Hujan Petir", WeatherConditionType.STORM)
        )
        return WeatherState(
            cityName = city.name,
            temperatureC = 30,
            feelsLikeC = 33,
            humidityPercent = 76,
            windKmh = 11,
            conditionLabel = "Cerah Berawan",
            conditionType = WeatherConditionType.PARTLY_CLOUDY,
            isDay = true,
            hourly = hourly,
            daily = daily,
            isFromCacheOrFallback = true
        )
    }
}
