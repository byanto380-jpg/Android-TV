package com.example.data.repository

import com.example.data.model.FootballLeague
import com.example.data.model.FootballMatch
import com.example.data.model.MatchStatus
import com.example.data.model.SportsDbEventDto
import com.example.data.remote.FootballApiService
import com.example.data.remote.NetworkModule
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FootballRepository(
    private val api: FootballApiService = NetworkModule.footballApi
) {
    suspend fun fetchAllLeagues(): List<FootballMatch> = coroutineScope {
        val liga1Deferred = async { fetchLeagueMatches(FootballLeague.LIGA_1) }
        val timnasDeferred = async { fetchTimnasMatches() }
        val uclDeferred = async { fetchLeagueMatches(FootballLeague.UCL) }
        val eplDeferred = async { fetchLeagueMatches(FootballLeague.PREMIER_LEAGUE) }
        val bundesDeferred = async { fetchLeagueMatches(FootballLeague.BUNDESLIGA) }

        val combined = liga1Deferred.await() +
            timnasDeferred.await() +
            uclDeferred.await() +
            eplDeferred.await() +
            bundesDeferred.await()

        combined.sortedWith(
            compareBy<FootballMatch> {
                when (it.status) {
                    MatchStatus.LIVE -> 0
                    MatchStatus.UPCOMING -> 1
                    MatchStatus.FINISHED -> 2
                }
            }.thenBy { it.dateLabel }
        )
    }

    private suspend fun fetchLeagueMatches(league: FootballLeague): List<FootballMatch> {
        val leagueId = league.sportsDbLeagueId ?: return emptyList()
        val fetched = runCatching {
            val next = api.getNextLeagueEvents(leagueId).events.orEmpty().take(4)
            val past = api.getPastLeagueEvents(leagueId).events.orEmpty().take(3)
            (next + past).mapNotNull { mapEventToMatch(it, league) }
        }.getOrDefault(emptyList())

        return if (fetched.isNotEmpty()) fetched else fallbackForLeague(league)
    }

    private suspend fun fetchTimnasMatches(): List<FootballMatch> {
        val fetched = runCatching {
            val next = api.getNextTeamEvents("136304").events.orEmpty().take(3)
            val past = api.getLastTeamEvents("136304").events.orEmpty().take(3)
            (next + past).mapNotNull { mapEventToMatch(it, FootballLeague.TIMNAS) }
        }.getOrDefault(emptyList())

        return if (fetched.isNotEmpty()) fetched else fallbackForLeague(FootballLeague.TIMNAS)
    }

    private fun mapEventToMatch(dto: SportsDbEventDto, league: FootballLeague): FootballMatch? {
        val home = dto.strHomeTeam ?: return null
        val away = dto.strAwayTeam ?: return null
        val homeScore = dto.intHomeScore?.toIntOrNull()
        val awayScore = dto.intAwayScore?.toIntOrNull()

        val rawStatus = dto.strStatus.orEmpty().lowercase(Locale.US)
        val status = when {
            rawStatus.contains("live") || rawStatus.contains("1h") || rawStatus.contains("2h") || rawStatus.contains("ht") ->
                MatchStatus.LIVE
            homeScore != null && awayScore != null && (rawStatus.contains("ft") || rawStatus.contains("match finished") || rawStatus.isBlank()) ->
                MatchStatus.FINISHED
            else -> MatchStatus.UPCOMING
        }

        val (dateLabel, wibTime) = formatWibDateTime(dto.dateEvent, dto.strTime, dto.strTimestamp)

        return FootballMatch(
            id = dto.idEvent ?: "${league.name}_${home}_$away",
            league = league,
            homeTeam = home,
            awayTeam = away,
            homeBadgeUrl = dto.strHomeTeamBadge,
            awayBadgeUrl = dto.strAwayTeamBadge,
            homeScore = homeScore,
            awayScore = awayScore,
            kickoffWib = wibTime,
            dateLabel = dateLabel,
            venue = dto.strVenue?.takeIf { it.isNotBlank() } ?: "Stadion Utama",
            status = status,
            broadcaster = when (league) {
                FootballLeague.LIGA_1 -> "Indosiar / Vidio"
                FootballLeague.TIMNAS -> "RCTI / GTV / Vision+"
                FootballLeague.UCL -> "SCTV / beIN Sports"
                FootballLeague.PREMIER_LEAGUE -> "Vidio / Champions TV"
                FootballLeague.BUNDESLIGA -> "RCTI / Vision+"
                FootballLeague.ALL -> "Live TV"
            }
        )
    }

    private fun formatWibDateTime(
        dateEvent: String?,
        strTime: String?,
        strTimestamp: String?
    ): Pair<String, String> {
        val wibZone = TimeZone.getTimeZone("Asia/Jakarta")
        val dateOutFmt = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).apply {
            timeZone = wibZone
        }
        val timeOutFmt = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID")).apply {
            timeZone = wibZone
        }

        if (!strTimestamp.isNullOrBlank()) {
            val patterns = listOf("yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss")
            for (pattern in patterns) {
                val parsed = runCatching {
                    SimpleDateFormat(pattern, Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }.parse(strTimestamp)
                }.getOrNull()
                if (parsed != null) {
                    return dateOutFmt.format(parsed) to timeOutFmt.format(parsed)
                }
            }
        }

        if (!dateEvent.isNullOrBlank() && !strTime.isNullOrBlank()) {
            val cleanTime = strTime.take(8)
            val parsed = runCatching {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse("$dateEvent $cleanTime")
            }.getOrNull()
            if (parsed != null) {
                return dateOutFmt.format(parsed) to timeOutFmt.format(parsed)
            }
        }

        return (dateEvent ?: "Jadwal Terbaru") to "${strTime?.take(5) ?: "19:30"} WIB"
    }

    private fun fallbackForLeague(league: FootballLeague): List<FootballMatch> = when (league) {
        FootballLeague.LIGA_1 -> listOf(
            FootballMatch(
                id = "l1_1",
                league = FootballLeague.LIGA_1,
                homeTeam = "Persib Bandung",
                awayTeam = "Persija Jakarta",
                homeScore = 2,
                awayScore = 1,
                kickoffWib = "19:00 WIB",
                dateLabel = "Hari Ini",
                venue = "Gelora Bandung Lautan Api",
                status = MatchStatus.LIVE,
                broadcaster = "Indosiar / Vidio"
            ),
            FootballMatch(
                id = "l1_2",
                league = FootballLeague.LIGA_1,
                homeTeam = "Persebaya Surabaya",
                awayTeam = "Bali United",
                kickoffWib = "15:30 WIB",
                dateLabel = "Besok",
                venue = "Gelora Bung Tomo",
                status = MatchStatus.UPCOMING,
                broadcaster = "Indosiar / Vidio"
            ),
            FootballMatch(
                id = "l1_3",
                league = FootballLeague.LIGA_1,
                homeTeam = "PSM Makassar",
                awayTeam = "Borneo FC",
                homeScore = 1,
                awayScore = 1,
                kickoffWib = "19:00 WIB",
                dateLabel = "Kemarin",
                venue = "Stadion Batakan",
                status = MatchStatus.FINISHED,
                broadcaster = "Vidio"
            )
        )
        FootballLeague.TIMNAS -> listOf(
            FootballMatch(
                id = "timnas_1",
                league = FootballLeague.TIMNAS,
                homeTeam = "Indonesia",
                awayTeam = "Arab Saudi",
                homeScore = 2,
                awayScore = 0,
                kickoffWib = "19:00 WIB",
                dateLabel = "Pertandingan Terakhir",
                venue = "Gelora Bung Karno, Jakarta",
                status = MatchStatus.FINISHED,
                broadcaster = "RCTI / Vision+"
            ),
            FootballMatch(
                id = "timnas_2",
                league = FootballLeague.TIMNAS,
                homeTeam = "Indonesia",
                awayTeam = "Australia",
                kickoffWib = "19:30 WIB",
                dateLabel = "Jadwal Berikutnya",
                venue = "Gelora Bung Karno, Jakarta",
                status = MatchStatus.UPCOMING,
                broadcaster = "RCTI / GTV"
            )
        )
        FootballLeague.UCL -> listOf(
            FootballMatch(
                id = "ucl_1",
                league = FootballLeague.UCL,
                homeTeam = "Real Madrid",
                awayTeam = "Bayern Munich",
                kickoffWib = "03:00 WIB",
                dateLabel = "Kamis Dini Hari",
                venue = "Santiago Bernabeu",
                status = MatchStatus.UPCOMING,
                broadcaster = "SCTV / beIN Sports"
            ),
            FootballMatch(
                id = "ucl_2",
                league = FootballLeague.UCL,
                homeTeam = "Liverpool",
                awayTeam = "AC Milan",
                homeScore = 3,
                awayScore = 1,
                kickoffWib = "03:00 WIB",
                dateLabel = "Selesai",
                venue = "Anfield",
                status = MatchStatus.FINISHED,
                broadcaster = "beIN Sports"
            )
        )
        FootballLeague.PREMIER_LEAGUE -> listOf(
            FootballMatch(
                id = "epl_1",
                league = FootballLeague.PREMIER_LEAGUE,
                homeTeam = "Arsenal",
                awayTeam = "Manchester City",
                kickoffWib = "23:30 WIB",
                dateLabel = "Sabtu Malam",
                venue = "Emirates Stadium",
                status = MatchStatus.UPCOMING,
                broadcaster = "Vidio / Champions TV"
            ),
            FootballMatch(
                id = "epl_2",
                league = FootballLeague.PREMIER_LEAGUE,
                homeTeam = "Manchester United",
                awayTeam = "Chelsea",
                homeScore = 2,
                awayScore = 2,
                kickoffWib = "22:00 WIB",
                dateLabel = "Selesai",
                venue = "Old Trafford",
                status = MatchStatus.FINISHED,
                broadcaster = "Vidio"
            )
        )
        FootballLeague.BUNDESLIGA -> listOf(
            FootballMatch(
                id = "bun_1",
                league = FootballLeague.BUNDESLIGA,
                homeTeam = "Bayer Leverkusen",
                awayTeam = "Borussia Dortmund",
                kickoffWib = "21:30 WIB",
                dateLabel = "Sabtu Malam",
                venue = "BayArena",
                status = MatchStatus.UPCOMING,
                broadcaster = "Vision+"
            ),
            FootballMatch(
                id = "bun_2",
                league = FootballLeague.BUNDESLIGA,
                homeTeam = "Bayern Munich",
                awayTeam = "RB Leipzig",
                homeScore = 4,
                awayScore = 1,
                kickoffWib = "02:30 WIB",
                dateLabel = "Selesai",
                venue = "Allianz Arena",
                status = MatchStatus.FINISHED,
                broadcaster = "RCTI / Vision+"
            )
        )
        FootballLeague.ALL -> emptyList()
    }
}
