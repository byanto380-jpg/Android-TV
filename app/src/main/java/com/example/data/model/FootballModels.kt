package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class FootballLeague(val displayName: String, val sportsDbLeagueId: String?) {
    ALL("Semua Liga", null),
    LIGA_1("BRI Liga 1", "4755"),
    TIMNAS("Timnas Indonesia", null),
    UCL("UCL", "4480"),
    PREMIER_LEAGUE("Premier League", "4328"),
    BUNDESLIGA("Bundesliga", "4331")
}

enum class MatchStatus(val labelId: String) {
    LIVE("LIVE"),
    UPCOMING("AKAN DATANG"),
    FINISHED("SELESAI")
}

data class FootballMatch(
    val id: String,
    val league: FootballLeague,
    val homeTeam: String,
    val awayTeam: String,
    val homeBadgeUrl: String? = null,
    val awayBadgeUrl: String? = null,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val kickoffWib: String,
    val dateLabel: String,
    val venue: String,
    val status: MatchStatus,
    val broadcaster: String = "Live TV / Vidio"
)

@JsonClass(generateAdapter = true)
data class SportsDbEventsResponse(
    val events: List<SportsDbEventDto>? = null
)

@JsonClass(generateAdapter = true)
data class SportsDbEventDto(
    @Json(name = "idEvent") val idEvent: String? = null,
    @Json(name = "strEvent") val strEvent: String? = null,
    @Json(name = "strLeague") val strLeague: String? = null,
    @Json(name = "strHomeTeam") val strHomeTeam: String? = null,
    @Json(name = "strAwayTeam") val strAwayTeam: String? = null,
    @Json(name = "intHomeScore") val intHomeScore: String? = null,
    @Json(name = "intAwayScore") val intAwayScore: String? = null,
    @Json(name = "dateEvent") val dateEvent: String? = null,
    @Json(name = "strTime") val strTime: String? = null,
    @Json(name = "strTimestamp") val strTimestamp: String? = null,
    @Json(name = "strVenue") val strVenue: String? = null,
    @Json(name = "strStatus") val strStatus: String? = null,
    @Json(name = "strHomeTeamBadge") val strHomeTeamBadge: String? = null,
    @Json(name = "strAwayTeamBadge") val strAwayTeamBadge: String? = null
)
