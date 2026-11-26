package com.example.service

import android.service.dreams.DreamService
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.data.local.PreferencesRepository
import com.example.data.model.IndonesianCities
import com.example.data.model.PrayerScheduleState
import com.example.data.model.WallpaperTheme
import com.example.data.model.WeatherState
import com.example.data.repository.PrayerRepository
import com.example.data.repository.WeatherRepository
import com.example.ui.screens.AmbientScreensaverScreen
import com.example.ui.theme.NusantaraTVTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class NusantaraDreamService : DreamService(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isFullscreen = true

        window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
        }

        val prefsRepo = PreferencesRepository(this)
        val weatherRepo = WeatherRepository()
        val prayerRepo = PrayerRepository()

        val composeView = ComposeView(this).apply {
            setContent {
                NusantaraTVTheme {
                    var wallpaperTheme by remember { mutableStateOf(WallpaperTheme.MAKKAH_DUSK) }
                    var weatherState by remember { mutableStateOf(WeatherState()) }
                    var prayerState by remember { mutableStateOf(PrayerScheduleState()) }
                    var clockText by remember { mutableStateOf("00:00:00") }
                    var dateText by remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        val prefs = prefsRepo.preferencesFlow.first()
                        wallpaperTheme = prefs.wallpaperTheme
                        weatherState = weatherRepo.fetchWeather(prefs.selectedCity)
                        prayerState = prayerRepo.fetchPrayerSchedule(prefs.selectedCity)

                        val city = IndonesianCities.findByName(prefs.selectedCity)
                        val tz = TimeZone.getTimeZone(city.timezone)
                        val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.US).apply { timeZone = tz }
                        val dateFmt = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID")).apply { timeZone = tz }

                        while (true) {
                            val now = Calendar.getInstance(tz).time
                            clockText = timeFmt.format(now)
                            dateText = dateFmt.format(now)
                            prayerState = prayerRepo.computeCountdown(prayerState, city.timezone)
                            delay(1000L)
                        }
                    }

                    AmbientScreensaverScreen(
                        wallpaperTheme = wallpaperTheme,
                        clockText = clockText,
                        dateText = dateText,
                        weatherState = weatherState,
                        prayerState = prayerState,
                        onExitScreensaver = { finish() }
                    )
                }
            }
        }
        setContentView(composeView)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onDreamingStopped() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onDreamingStopped()
    }

    override fun onDetachedFromWindow() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDetachedFromWindow()
    }
}
