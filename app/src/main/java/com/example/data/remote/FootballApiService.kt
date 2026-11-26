package com.example.data.remote

import com.example.data.model.SportsDbEventsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {
    @GET("eventsnextleague.php")
    suspend fun getNextLeagueEvents(@Query("id") leagueId: String): SportsDbEventsResponse

    @GET("eventspastleague.php")
    suspend fun getPastLeagueEvents(@Query("id") leagueId: String): SportsDbEventsResponse

    @GET("eventsnext.php")
    suspend fun getNextTeamEvents(@Query("id") teamId: String): SportsDbEventsResponse

    @GET("eventslast.php")
    suspend fun getLastTeamEvents(@Query("id") teamId: String): SportsDbEventsResponse
}
