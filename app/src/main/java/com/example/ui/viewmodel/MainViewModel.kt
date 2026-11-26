package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DefaultChannelsCatalog
import com.example.data.local.InstalledAppsProvider
import com.example.data.local.PreferencesRepository
import com.example.data.local.UserPreferencesState
import com.example.data.model.ChannelCategory
import com.example.data.model.FootballLeague
import com.example.data.model.FootballMatch
import com.example.data.model.IndonesianCities
import com.example.data.model.InstalledAppInfo
import com.example.data.model.NetworkStatus
import com.example.data.model.PrayerScheduleState
import com.example.data.model.TvChannel
import com.example.data.model.WallpaperTheme
import com.example.data.model.WeatherState
import com.example.data.repository.FootballRepository
import com.example.data.repository.PrayerRepository
import com.example.data.repository.WeatherRepository
import com.example.util.AdzanAudioPlayer
import com.example.util.NetworkStatusMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

enum class TvTab(val titleId: String) {
    HOME("Beranda"),
    TV("TV Nasional"),
    FOOTBALL("Jadwal Bola"),
    PRAYER("Ibadah & Makkah"),
    APPS("Aplikasi"),
    SETTINGS("Pengaturan")
}

enum class PinPromptPurpose {
    UNLOCK_KIDS,
    OPEN_SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = PreferencesRepository(application)
    private val weatherRepo = WeatherRepository()
    private val footballRepo = FootballRepository()
    private val prayerRepo = PrayerRepository()
    private val appsProvider = InstalledAppsProvider(application)
    private val networkMonitor = NetworkStatusMonitor(application)
    private val adzanPlayer = AdzanAudioPlayer(application)

    val userPrefs: StateFlow<UserPreferencesState> = prefsRepo.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferencesState())

    val networkStatus: StateFlow<NetworkStatus> = networkMonitor.statusFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, NetworkStatus())

    private val _currentTab = MutableStateFlow(TvTab.HOME)
    val currentTab: StateFlow<TvTab> = _currentTab.asStateFlow()

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _footballMatches = MutableStateFlow<List<FootballMatch>>(emptyList())
    val footballMatches: StateFlow<List<FootballMatch>> = _footballMatches.asStateFlow()

    private val _selectedLeague = MutableStateFlow(FootballLeague.ALL)
    val selectedLeague: StateFlow<FootballLeague> = _selectedLeague.asStateFlow()

    private val _selectedChannelCategory = MutableStateFlow(ChannelCategory.ALL)
    val selectedChannelCategory: StateFlow<ChannelCategory> = _selectedChannelCategory.asStateFlow()

    private val _prayerState = MutableStateFlow(PrayerScheduleState())
    val prayerState: StateFlow<PrayerScheduleState> = _prayerState.asStateFlow()

    private val _clockText = MutableStateFlow("00:00:00")
    val clockText: StateFlow<String> = _clockText.asStateFlow()

    private val _dateText = MutableStateFlow("")
    val dateText: StateFlow<String> = _dateText.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _activePlayerChannel = MutableStateFlow<TvChannel?>(null)
    val activePlayerChannel: StateFlow<TvChannel?> = _activePlayerChannel.asStateFlow()

    private val _isScreensaverActive = MutableStateFlow(false)
    val isScreensaverActive: StateFlow<Boolean> = _isScreensaverActive.asStateFlow()

    private val _isWeatherModalOpen = MutableStateFlow(false)
    val isWeatherModalOpen: StateFlow<Boolean> = _isWeatherModalOpen.asStateFlow()

    private val _pinPromptPurpose = MutableStateFlow<PinPromptPurpose?>(null)
    val pinPromptPurpose: StateFlow<PinPromptPurpose?> = _pinPromptPurpose.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    private val _adzanBannerPrayer = MutableStateFlow<String?>(null)
    val adzanBannerPrayer: StateFlow<String?> = _adzanBannerPrayer.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var lastTriggeredAdzanKey: String? = null

    val allChannels: StateFlow<List<TvChannel>> = combine(
        userPrefs
    ) { (prefs) ->
        val raw = DefaultChannelsCatalog.channels + prefs.customChannels
        if (prefs.kidsLockEnabled) raw.filter { it.isKidSafe } else raw
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DefaultChannelsCatalog.channels)

    val visibleApps: StateFlow<List<InstalledAppInfo>> = combine(
        _installedApps,
        userPrefs
    ) { apps, prefs ->
        if (!prefs.kidsLockEnabled) {
            apps
        } else if (prefs.whitelistedPackages.isEmpty()) {
            emptyList()
        } else {
            apps.filter { prefs.whitelistedPackages.contains(it.packageName) }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        startClockAndPrayerTicker()
        loadInstalledApps()
        observeCityChanges()
        refreshFootball()
    }

    private fun observeCityChanges() {
        viewModelScope.launch {
            userPrefs
                .map { it.selectedCity }
                .distinctUntilChanged()
                .collect { city ->
                    refreshWeatherAndPrayer(city)
                }
        }
    }

    private fun startClockAndPrayerTicker() {
        viewModelScope.launch {
            val idLocale = Locale("id", "ID")
            val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.US)
            val dateFmt = SimpleDateFormat("EEEE, dd MMM yyyy", idLocale)

            while (true) {
                val city = IndonesianCities.findByName(userPrefs.value.selectedCity)
                val tz = TimeZone.getTimeZone(city.timezone)
                val now = Calendar.getInstance(tz).time
                timeFmt.timeZone = tz
                dateFmt.timeZone = tz

                _clockText.value = timeFmt.format(now)
                _dateText.value = dateFmt.format(now)

                val updatedPrayer = prayerRepo.computeCountdown(_prayerState.value, city.timezone)
                _prayerState.value = updatedPrayer

                if (updatedPrayer.isNowAdzanWindow && updatedPrayer.activeAdzanPrayer != null) {
                    val triggerKey = "${updatedPrayer.activeAdzanPrayer}_${updatedPrayer.nextPrayerTime}"
                    if (lastTriggeredAdzanKey != triggerKey) {
                        lastTriggeredAdzanKey = triggerKey
                        triggerAdzanNotification(updatedPrayer.activeAdzanPrayer)
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun triggerAdzanNotification(prayerName: String) {
        val prefs = userPrefs.value
        if (prefs.adzanPopupEnabled) {
            _adzanBannerPrayer.value = prayerName
        }
        if (prefs.adzanSoundEnabled) {
            adzanPlayer.playAdzan(viewModelScope)
        }
    }

    fun testAdzanAlert() {
        val nextName = _prayerState.value.nextPrayerName
        _adzanBannerPrayer.value = nextName
        if (userPrefs.value.adzanSoundEnabled) {
            adzanPlayer.playAdzan(viewModelScope)
        }
    }

    fun dismissAdzanBanner() {
        _adzanBannerPrayer.value = null
        adzanPlayer.stop()
    }

    fun toggleAdzanSound() {
        viewModelScope.launch {
            val next = !userPrefs.value.adzanSoundEnabled
            prefsRepo.setAdzanSoundEnabled(next)
            if (!next) adzanPlayer.stop()
        }
    }

    fun toggleAdzanPopup() {
        viewModelScope.launch {
            prefsRepo.setAdzanPopupEnabled(!userPrefs.value.adzanPopupEnabled)
        }
    }

    fun selectTab(tab: TvTab) {
        if (userPrefs.value.kidsLockEnabled && tab == TvTab.SETTINGS) {
            _pinError.value = null
            _pinPromptPurpose.value = PinPromptPurpose.OPEN_SETTINGS
            return
        }
        _currentTab.value = tab
    }

    fun requestToggleKidsLock() {
        if (userPrefs.value.kidsLockEnabled) {
            _pinError.value = null
            _pinPromptPurpose.value = PinPromptPurpose.UNLOCK_KIDS
        } else {
            viewModelScope.launch {
                prefsRepo.setKidsLockEnabled(true)
                if (_currentTab.value == TvTab.SETTINGS) {
                    _currentTab.value = TvTab.HOME
                }
            }
        }
    }

    fun submitPin(enteredPin: String) {
        if (enteredPin != userPrefs.value.kidsPin) {
            _pinError.value = "PIN salah. Coba lagi."
            return
        }
        val purpose = _pinPromptPurpose.value
        _pinPromptPurpose.value = null
        _pinError.value = null

        viewModelScope.launch {
            when (purpose) {
                PinPromptPurpose.UNLOCK_KIDS -> prefsRepo.setKidsLockEnabled(false)
                PinPromptPurpose.OPEN_SETTINGS -> {
                    prefsRepo.setKidsLockEnabled(false)
                    _currentTab.value = TvTab.SETTINGS
                }
                null -> Unit
            }
        }
    }

    fun dismissPinDialog() {
        _pinPromptPurpose.value = null
        _pinError.value = null
    }

    fun updateKidsPin(newPin: String) {
        viewModelScope.launch { prefsRepo.setKidsPin(newPin) }
    }

    fun toggleAppWhitelist(packageName: String) {
        viewModelScope.launch { prefsRepo.toggleWhitelistedPackage(packageName) }
    }

    fun selectCity(cityName: String) {
        viewModelScope.launch {
            prefsRepo.setCity(cityName)
        }
    }

    fun selectWallpaper(theme: WallpaperTheme) {
        viewModelScope.launch {
            prefsRepo.setWallpaperTheme(theme)
        }
    }

    fun addCustomChannel(name: String, url: String, isKidSafe: Boolean) {
        if (name.isBlank() || url.isBlank()) return
        viewModelScope.launch {
            prefsRepo.addCustomChannel(name, url, isKidSafe)
        }
    }

    fun removeCustomChannel(channelId: String) {
        viewModelScope.launch {
            prefsRepo.removeCustomChannel(channelId)
        }
    }

    fun selectLeague(league: FootballLeague) {
        _selectedLeague.value = league
    }

    fun selectChannelCategory(category: ChannelCategory) {
        _selectedChannelCategory.value = category
    }

    fun playChannel(channel: TvChannel) {
        _activePlayerChannel.value = channel
    }

    fun switchChannelRelative(delta: Int) {
        val list = allChannels.value
        if (list.isEmpty()) return
        val currentIdx = list.indexOfFirst { it.id == _activePlayerChannel.value?.id }
        val nextIdx = if (currentIdx < 0) 0 else (currentIdx + delta + list.size) % list.size
        _activePlayerChannel.value = list[nextIdx]
    }

    fun closePlayer() {
        _activePlayerChannel.value = null
    }

    fun openScreensaver() {
        _isScreensaverActive.value = true
    }

    fun closeScreensaver() {
        _isScreensaverActive.value = false
    }

    fun openWeatherModal() {
        _isWeatherModalOpen.value = true
    }

    fun closeWeatherModal() {
        _isWeatherModalOpen.value = false
    }

    fun launchExternalApp(packageName: String): Boolean {
        val intent = appsProvider.getLaunchIntent(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching {
            getApplication<Application>().startActivity(intent)
            true
        }.getOrDefault(false)
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _installedApps.value = appsProvider.loadInstalledApps()
        }
    }

    fun refreshAllData() {
        refreshWeatherAndPrayer(userPrefs.value.selectedCity)
        refreshFootball()
        loadInstalledApps()
    }

    private fun refreshWeatherAndPrayer(city: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            _weatherState.value = weatherRepo.fetchWeather(city)
            _prayerState.value = prayerRepo.fetchPrayerSchedule(city)
            _isRefreshing.value = false
        }
    }

    private fun refreshFootball() {
        viewModelScope.launch {
            _footballMatches.value = footballRepo.fetchAllLeagues()
        }
    }

    override fun onCleared() {
        adzanPlayer.stop()
        super.onCleared()
    }
}
