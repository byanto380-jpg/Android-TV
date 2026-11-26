package com.example.data.remote

import com.example.data.model.AladhanResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerApiService {
    @GET("v1/timingsByCity")
    suspend fun getTimingsByCity(
        @Query("city") city: String,
        @Query("country") country: String = "Indonesia",
        @Query("method") method: Int = 20 // Kemenag RI
    ): AladhanResponse
}
