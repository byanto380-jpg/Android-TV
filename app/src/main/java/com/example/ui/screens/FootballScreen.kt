package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.FootballLeague
import com.example.data.model.FootballMatch
import com.example.data.model.MatchStatus
import com.example.ui.components.TvGlassCard
import com.example.ui.theme.CoralLive
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidNavy

@Composable
fun FootballScreen(
    matches: List<FootballMatch>,
    selectedLeague: FootballLeague,
    onSelectLeague: (FootballLeague) -> Unit,
    onRefresh: () -> Unit
) {
    val filtered = if (selectedLeague == FootballLeague.ALL) {
        matches
    } else {
        matches.filter { it.league == selectedLeague }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 10.dp)
    ) {
        // Stadium Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_bg_stadium),
                contentDescription = "Stadion Sepak Bola",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(VoidNavy.copy(alpha = 0.92f), VoidNavy.copy(alpha = 0.55f))
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SportsSoccer,
                            contentDescription = null,
                            tint = CyanPrimary
                        )
                        Text(
                            text = "Pusat Jadwal Sepak Bola (WIB)",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "BRI Liga 1 Indonesia • Timnas Indonesia • UCL • Premier League • Bundesliga",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                TvGlassCard(
                    onClick = onRefresh,
                    testTagName = "football_refresh_btn",
                    shape = RoundedCornerShape(999.dp)
                ) { _ ->
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Muat ulang", tint = CyanPrimary, modifier = Modifier.size(18.dp))
                        Text("Perbarui", color = TextPrimary, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // League Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(FootballLeague.entries) { league ->
                val selected = league == selectedLeague
                TvGlassCard(
                    onClick = { onSelectLeague(league) },
                    testTagName = "league_filter_${league.name.lowercase()}",
                    shape = RoundedCornerShape(999.dp),
                    containerColor = if (selected) CyanPrimary else GlassSurface
                ) { _ ->
                    Text(
                        text = league.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) VoidNavy else TextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 330.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filtered, key = { it.id }) { match ->
                FootballMatchCard(match = match)
            }
        }
    }
}

@Composable
private fun FootballMatchCard(match: FootballMatch) {
    TvGlassCard(
        onClick = {},
        testTagName = "match_card_${match.id}",
        shape = RoundedCornerShape(20.dp)
    ) { _ ->
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.league.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = CyanPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (match.status) {
                                MatchStatus.LIVE -> CoralLive
                                MatchStatus.UPCOMING -> GoldAccent.copy(alpha = 0.22f)
                                MatchStatus.FINISHED -> GlassSurfaceElevated
                            }
                        )
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = match.status.labelId,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (match.status == MatchStatus.UPCOMING) GoldAccent else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamBadgeBlock(
                    name = match.homeTeam,
                    badgeUrl = match.homeBadgeUrl,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (match.homeScore != null && match.awayScore != null) {
                        Text(
                            text = "${match.homeScore} : ${match.awayScore}",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = GoldAccent
                        )
                    } else {
                        Text(
                            text = "VS",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = match.kickoffWib,
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        color = CyanPrimary
                    )
                }

                TeamBadgeBlock(
                    name = match.awayTeam,
                    badgeUrl = match.awayBadgeUrl,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${match.dateLabel} • ${match.venue}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = match.broadcaster,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun TeamBadgeBlock(
    name: String,
    badgeUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!badgeUrl.isNullOrBlank()) {
            AsyncImage(
                model = badgeUrl,
                contentDescription = name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GlassSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(3).uppercase(),
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CyanPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
