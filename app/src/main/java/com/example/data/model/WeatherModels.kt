package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class CityOption(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String = "Asia/Jakarta"
)

object IndonesianCities {
    val presets = listOf(
        CityOption("Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
        CityOption("Surabaya", "Indonesia", -7.2575, 112.7521, "Asia/Jakarta"),
        CityOption("Bandung", "Indonesia", -6.9175, 107.6191, "Asia/Jakarta"),
        CityOption("Medan", "Indonesia", 3.5952, 98.6722, "Asia/Jakarta"),
        CityOption("Semarang", "Indonesia", -6.9667, 110.4167, "Asia/Jakarta"),
        CityOption("Yogyakarta", "Indonesia", -7.7956, 110.3695, "Asia/Jakarta"),
        CityOption("Makassar", "Indonesia", -5.1477, 119.4327, "Asia/Makassar"),
        CityOption("Denpasar", "Indonesia", -8.6705, 115.2126, "Asia/Makassar"),
        CityOption("Balikpapan", "Indonesia", -1.2379, 116.8529, "Asia/Makassar"),
        CityOption("Banda Aceh", "Indonesia", 5.5483, 95.3238, "Asia/Jakarta"),
        CityOption("Palembang", "Indonesia", -2.9761, 104.7754, "Asia/Jakarta"),
        CityOption("Jayapura", "Indonesia", -2.5916, 140.6690, "Asia/Jayapura")
    )

    fun findByName(name: String): CityOption =
        presets.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: presets.first()
}

@JsonClass(generateAdapter = true)
data class OpenMeteoResponse(
    val current: CurrentWeatherDto? = null,
    val hourly: HourlyWeatherDto? = null,
    val daily: DailyWeatherDto? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "temperature_2m") val temperature2m: Double = 29.0,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Int = 75,
    @Json(name = "apparent_temperature") val apparentTemperature: Double = 32.0,
    @Json(name = "is_day") val isDay: Int = 1,
    @Json(name = "weather_code") val weatherCode: Int = 1,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double = 11.0
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    val time: List<String> = emptyList(),
    @Json(name = "temperature_2m") val temperature2m: List<Double> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    val time: List<String> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double> = emptyList(),
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double> = emptyList(),
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int> = emptyList()
)

enum class WeatherConditionType {
    CLEAR, PARTLY_CLOUDY, CLOUDY, RAIN, STORM
}

data class HourlyForecastItem(
    val hourLabel: String,
    val tempCelsius: Int,
    val rainChancePercent: Int,
    val conditionType: WeatherConditionType
)

data class DailyForecastItem(
    val dayLabel: String,
    val dateLabel: String,
    val minTemp: Int,
    val maxTemp: Int,
    val rainChancePercent: Int,
    val conditionLabel: String,
    val conditionType: WeatherConditionType
)

data class WeatherState(
    val cityName: String = "Jakarta",
    val temperatureC: Int = 30,
    val feelsLikeC: Int = 33,
    val humidityPercent: Int = 74,
    val windKmh: Int = 12,
    val conditionLabel: String = "Cerah Berawan",
    val conditionType: WeatherConditionType = WeatherConditionType.PARTLY_CLOUDY,
    val isDay: Boolean = true,
    val hourly: List<HourlyForecastItem> = emptyList(),
    val daily: List<DailyForecastItem> = emptyList(),
    val isFromCacheOrFallback: Boolean = false
)

object WeatherCodeMapper {
    fun map(code: Int): Pair<String, WeatherConditionType> = when (code) {
        0 -> "Cerah" to WeatherConditionType.CLEAR
        1, 2 -> "Cerah Berawan" to WeatherConditionType.PARTLY_CLOUDY
        3, 45, 48 -> "Berawan" to WeatherConditionType.CLOUDY
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Hujan" to WeatherConditionType.RAIN
        95, 96, 99 -> "Hujan Petir" to WeatherConditionType.STORM
        else -> "Berawan" to WeatherConditionType.PARTLY_CLOUDY
    }
}
