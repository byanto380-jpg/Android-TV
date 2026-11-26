package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ChannelCategory
import com.example.ui.components.AdzanAlertBanner
import com.example.ui.components.AnimatedAmbientBackground
import com.example.ui.components.FullscreenTvPlayerScreen
import com.example.ui.components.KidsPinDialog
import com.example.ui.components.TopTvHeaderBar
import com.example.ui.screens.AmbientScreensaverScreen
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.FootballScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrayerAndMakkahScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TvChannelsScreen
import com.example.ui.screens.WeatherDetailOverlay
import com.example.ui.theme.NusantaraTVTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PinPromptPurpose
import com.example.ui.viewmodel.TvTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NusantaraTVTheme {
                NusantaraTvLauncherApp()
            }
        }
    }
}

@Composable
fun NusantaraTvLauncherApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val userPrefs by viewModel.userPrefs.collectAsStateWithLifecycle()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val footballMatches by viewModel.footballMatches.collectAsStateWithLifecycle()
    val selectedLeague by viewModel.selectedLeague.collectAsStateWithLifecycle()
    val selectedChannelCategory by viewModel.selectedChannelCategory.collectAsStateWithLifecycle()
    val prayerState by viewModel.prayerState.collectAsStateWithLifecycle()
    val clockText by viewModel.clockText.collectAsStateWithLifecycle()
    val dateText by viewModel.dateText.collectAsStateWithLifecycle()
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val visibleApps by viewModel.visibleApps.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val activePlayerChannel by viewModel.activePlayerChannel.collectAsStateWithLifecycle()
    val isScreensaverActive by viewModel.isScreensaverActive.collectAsStateWithLifecycle()
    val isWeatherModalOpen by viewModel.isWeatherModalOpen.collectAsStateWithLifecycle()
    val pinPromptPurpose by viewModel.pinPromptPurpose.collectAsStateWithLifecycle()
    val pinError by viewModel.pinError.collectAsStateWithLifecycle()
    val adzanBannerPrayer by viewModel.adzanBannerPrayer.collectAsStateWithLifecycle()

    // Return to Home tab on Back press from secondary tabs (Launcher never exits accidentally)
    BackHandler(enabled = currentTab != TvTab.HOME && activePlayerChannel == null && !isScreensaverActive && !isWeatherModalOpen && pinPromptPurpose == null) {
        viewModel.selectTab(TvTab.HOME)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Live animated wallpaper layer
        AnimatedAmbientBackground(theme = userPrefs.wallpaperTheme)

        // Main Launcher Chrome
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            TopTvHeaderBar(
                currentTab = currentTab,
                kidsLockEnabled = userPrefs.kidsLockEnabled,
                weatherState = weatherState,
                networkStatus = networkStatus,
                clockText = clockText,
                onSelectTab = viewModel::selectTab,
                onOpenWeather = viewModel::openWeatherModal,
                onToggleKidsLock = viewModel::requestToggleKidsLock,
                onOpenScreensaver = viewModel::openScreensaver
            )

            AnimatedContent(
                targetState = currentTab,
                modifier = Modifier.weight(1f),
                label = "tv_tab_content"
            ) { tab ->
                when (tab) {
                    TvTab.HOME -> HomeScreen(
                        weatherState = weatherState,
                        prayerState = prayerState,
                        clockText = clockText,
                        dateText = dateText,
                        channels = allChannels,
                        matches = footballMatches,
                        apps = visibleApps,
                        kidsLockEnabled = userPrefs.kidsLockEnabled,
                        onPlayChannel = viewModel::playChannel,
                        onOpenWeather = viewModel::openWeatherModal,
                        onNavigateTab = viewModel::selectTab,
                        onLaunchApp = { pkg -> viewModel.launchExternalApp(pkg) }
                    )

                    TvTab.TV -> TvChannelsScreen(
                        channels = allChannels,
                        selectedCategory = selectedChannelCategory,
                        kidsLockEnabled = userPrefs.kidsLockEnabled,
                        onSelectCategory = viewModel::selectChannelCategory,
                        onPlayChannel = viewModel::playChannel
                    )

                    TvTab.FOOTBALL -> FootballScreen(
                        matches = footballMatches,
                        selectedLeague = selectedLeague,
                        onSelectLeague = viewModel::selectLeague,
                        onRefresh = viewModel::refreshAllData
                    )

                    TvTab.PRAYER -> PrayerAndMakkahScreen(
                        prayerState = prayerState,
                        clockText = clockText,
                        dateText = dateText,
                        adzanSoundEnabled = userPrefs.adzanSoundEnabled,
                        haramainChannels = allChannels.filter {
                            it.category == ChannelCategory.MAKKAH_MADINAH
                        },
                        onToggleAdzanSound = viewModel::toggleAdzanSound,
                        onTestAdzan = viewModel::testAdzanAlert,
                        onPlayChannel = viewModel::playChannel
                    )

                    TvTab.APPS -> AppsScreen(
                        apps = visibleApps,
                        kidsLockEnabled = userPrefs.kidsLockEnabled,
                        onLaunchApp = { pkg -> viewModel.launchExternalApp(pkg) },
                        onRefreshApps = viewModel::loadInstalledApps
                    )

                    TvTab.SETTINGS -> SettingsScreen(
                        prefs = userPrefs,
                        installedApps = installedApps,
                        onSelectCity = viewModel::selectCity,
                        onSelectWallpaper = viewModel::selectWallpaper,
                        onOpenScreensaver = viewModel::openScreensaver,
                        onToggleKidsLock = viewModel::requestToggleKidsLock,
                        onUpdatePin = viewModel::updateKidsPin,
                        onToggleAppWhitelist = viewModel::toggleAppWhitelist,
                        onToggleAdzanSound = viewModel::toggleAdzanSound,
                        onToggleAdzanPopup = viewModel::toggleAdzanPopup,
                        onAddCustomChannel = viewModel::addCustomChannel,
                        onRemoveCustomChannel = viewModel::removeCustomChannel
                    )
                }
            }
        }

        // Adzan Pop-up Banner Overlay
        AnimatedVisibility(
            visible = adzanBannerPrayer != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 74.dp)
        ) {
            adzanBannerPrayer?.let { prayerName ->
                AdzanAlertBanner(
                    prayerName = prayerName,
                    cityName = weatherState.cityName,
                    isMuted = !userPrefs.adzanSoundEnabled,
                    onToggleMute = viewModel::toggleAdzanSound,
                    onDismiss = viewModel::dismissAdzanBanner
                )
            }
        }

        // Detailed Weather Modal Overlay
        if (isWeatherModalOpen) {
            WeatherDetailOverlay(
                weatherState = weatherState,
                onClose = viewModel::closeWeatherModal
            )
        }

        // Fullscreen Media3 ExoPlayer Overlay
        activePlayerChannel?.let { channel ->
            FullscreenTvPlayerScreen(
                channel = channel,
                allChannels = allChannels,
                onSelectChannel = viewModel::playChannel,
                onSwitchRelative = viewModel::switchChannelRelative,
                onClose = viewModel::closePlayer
            )
        }

        // In-App Ambient Screensaver Overlay
        if (isScreensaverActive) {
            AmbientScreensaverScreen(
                wallpaperTheme = userPrefs.wallpaperTheme,
                clockText = clockText,
                dateText = dateText,
                weatherState = weatherState,
                prayerState = prayerState,
                onExitScreensaver = viewModel::closeScreensaver
            )
        }

        // Kids Lock 4-Digit PIN Prompt
        pinPromptPurpose?.let { purpose ->
            val title = when (purpose) {
                PinPromptPurpose.UNLOCK_KIDS -> "Nonaktifkan Mode Anak"
                PinPromptPurpose.OPEN_SETTINGS -> "Masukkan PIN untuk Membuka Pengaturan"
            }
            KidsPinDialog(
                title = title,
                errorMessage = pinError,
                onSubmitPin = viewModel::submitPin,
                onDismiss = viewModel::dismissPinDialog
            )
        }
    }
}
