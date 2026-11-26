package com.example.data.local

import com.example.data.model.ChannelCategory
import com.example.data.model.TvChannel

object DefaultChannelsCatalog {
    val channels: List<TvChannel> = listOf(
        TvChannel(
            id = "makkah_live",
            name = "Live Masjidil Haram (Mekkah)",
            categoryName = ChannelCategory.MAKKAH_MADINAH.name,
            streamUrl = "https://win.holol.com/live/quran/playlist.m3u8",
            fallbackStreamUrl = "https://cdn-globecast.akamaized.net/live/eds/saudi_quran/hls_roku/index.m3u8",
            description = "Siaran langsung 24 jam Masjidil Haram & Ka'bah (Al-Quran Al-Kareem TV)",
            badgeCode = "24J LIVE",
            isKidSafe = true,
            isFeaturedHaramain = true
        ),
        TvChannel(
            id = "madinah_live",
            name = "Live Masjid Nabawi (Madinah)",
            categoryName = ChannelCategory.MAKKAH_MADINAH.name,
            streamUrl = "https://win.holol.com/live/sunnah/playlist.m3u8",
            fallbackStreamUrl = "https://cdn-globecast.akamaized.net/live/eds/saudi_sunnah/hls_roku/index.m3u8",
            description = "Siaran langsung 24 jam Masjid Nabawi Madinah (Al-Sunnah Al-Nabawiyyah TV)",
            badgeCode = "24J LIVE",
            isKidSafe = true,
            isFeaturedHaramain = true
        ),
        TvChannel(
            id = "tvri_nasional",
            name = "TVRI Nasional",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://ott-balancer.tvri.go.id/live/eds/Nasional/hls/Nasional.m3u8",
            fallbackStreamUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
            description = "Televisi Publik Nasional Indonesia — Berita, Budaya & Edukasi",
            badgeCode = "HD",
            isKidSafe = true
        ),
        TvChannel(
            id = "tvri_sport",
            name = "TVRI Sport",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://ott-balancer.tvri.go.id/live/eds/SportHD/hls/SportHD.m3u8",
            fallbackStreamUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
            description = "Siaran olahraga nasional, bulu tangkis & pertandingan pilihan",
            badgeCode = "SPORT",
            isKidSafe = true
        ),
        TvChannel(
            id = "tvri_world",
            name = "TVRI World",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://ott-balancer.tvri.go.id/live/eds/World/hls/World.m3u8",
            description = "Kanal internasional Indonesia dengan program budaya & pariwisata",
            badgeCode = "HD",
            isKidSafe = true
        ),
        TvChannel(
            id = "cnn_id",
            name = "CNN Indonesia",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://live.cnnindonesia.com/livecnn/smil:cnntv.smil/playlist.m3u8",
            description = "Berita terkini nasional, ekonomi, dan dunia 24 jam",
            badgeCode = "NEWS",
            isKidSafe = false
        ),
        TvChannel(
            id = "cnbc_id",
            name = "CNBC Indonesia",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://live.cnbcindonesia.com/livecnbc/smil:cnbctv.smil/playlist.m3u8",
            description = "Informasi pasar modal, bisnis, dan ekonomi Indonesia",
            badgeCode = "BISNIS",
            isKidSafe = false
        ),
        TvChannel(
            id = "kompas_tv",
            name = "Kompas TV",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://live-kompas.klikhost.com/kompastv/kompastv.m3u8",
            description = "Independen Terpercaya — Berita & dokumenter Nusantara",
            badgeCode = "NEWS",
            isKidSafe = false
        ),
        TvChannel(
            id = "metro_tv",
            name = "Metro TV",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://edge.medcom.id/live-edge/smil:metro.smil/playlist.m3u8",
            description = "Kanal berita & dialog nasional terkemuka",
            badgeCode = "NEWS",
            isKidSafe = false
        ),
        TvChannel(
            id = "jaktv",
            name = "Jak TV",
            categoryName = ChannelCategory.TV_NASIONAL.name,
            streamUrl = "https://cdn-telkomsel-01.akamaized.net/Content/HLS/Live/channel(59a77e1a-f77e-48ab-9294-7605f84e1d1c)/index.m3u8",
            description = "Hiburan, gaya hidup, dan berita Jabodetabek",
            badgeCode = "HD",
            isKidSafe = true
        ),
        TvChannel(
            id = "kids_edukasi",
            name = "Edukasi & Animasi Anak",
            categoryName = ChannelCategory.ANAK_EDUKASI.name,
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            description = "Siaran aman ramah anak dengan animasi & edukasi keluarga",
            badgeCode = "KIDS",
            isKidSafe = true
        )
    )
}
