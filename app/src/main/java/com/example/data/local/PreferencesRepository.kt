package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.ChannelCategory
import com.example.data.model.TvChannel
import com.example.data.model.WallpaperTheme
import com.example.data.remote.NetworkModule
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nusantara_tv_prefs")

data class UserPreferencesState(
    val selectedCity: String = "Jakarta",
    val kidsLockEnabled: Boolean = false,
    val kidsPin: String = "1234",
    val whitelistedPackages: Set<String> = emptySet(),
    val adzanSoundEnabled: Boolean = true,
    val adzanPopupEnabled: Boolean = true,
    val wallpaperTheme: WallpaperTheme = WallpaperTheme.MAKKAH_DUSK,
    val customChannels: List<TvChannel> = emptyList()
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val SELECTED_CITY = stringPreferencesKey("selected_city")
        val KIDS_LOCK_ENABLED = booleanPreferencesKey("kids_lock_enabled")
        val KIDS_PIN = stringPreferencesKey("kids_pin")
        val WHITELISTED_PACKAGES = stringSetPreferencesKey("whitelisted_packages")
        val ADZAN_SOUND_ENABLED = booleanPreferencesKey("adzan_sound_enabled")
        val ADZAN_POPUP_ENABLED = booleanPreferencesKey("adzan_popup_enabled")
        val WALLPAPER_THEME = stringPreferencesKey("wallpaper_theme")
        val CUSTOM_CHANNELS_JSON = stringPreferencesKey("custom_channels_json")
    }

    private val channelListAdapter by lazy {
        val type = Types.newParameterizedType(List::class.java, TvChannel::class.java)
        NetworkModule.moshi.adapter<List<TvChannel>>(type)
    }

    val preferencesFlow: Flow<UserPreferencesState> = context.dataStore.data.map { prefs ->
        val themeName = prefs[Keys.WALLPAPER_THEME] ?: WallpaperTheme.MAKKAH_DUSK.name
        val theme = WallpaperTheme.entries.firstOrNull { it.name == themeName }
            ?: WallpaperTheme.MAKKAH_DUSK
        val customJson = prefs[Keys.CUSTOM_CHANNELS_JSON].orEmpty()
        val customChannels = if (customJson.isBlank()) {
            emptyList()
        } else {
            runCatching { channelListAdapter.fromJson(customJson) ?: emptyList() }
                .getOrDefault(emptyList())
        }
        UserPreferencesState(
            selectedCity = prefs[Keys.SELECTED_CITY] ?: "Jakarta",
            kidsLockEnabled = prefs[Keys.KIDS_LOCK_ENABLED] ?: false,
            kidsPin = prefs[Keys.KIDS_PIN] ?: "1234",
            whitelistedPackages = prefs[Keys.WHITELISTED_PACKAGES] ?: emptySet(),
            adzanSoundEnabled = prefs[Keys.ADZAN_SOUND_ENABLED] ?: true,
            adzanPopupEnabled = prefs[Keys.ADZAN_POPUP_ENABLED] ?: true,
            wallpaperTheme = theme,
            customChannels = customChannels
        )
    }

    suspend fun setCity(city: String) {
        context.dataStore.edit { it[Keys.SELECTED_CITY] = city }
    }

    suspend fun setKidsLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KIDS_LOCK_ENABLED] = enabled }
    }

    suspend fun setKidsPin(pin: String) {
        if (pin.length == 4 && pin.all { it.isDigit() }) {
            context.dataStore.edit { it[Keys.KIDS_PIN] = pin }
        }
    }

    suspend fun toggleWhitelistedPackage(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.WHITELISTED_PACKAGES]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[Keys.WHITELISTED_PACKAGES] = current
        }
    }

    suspend fun setAdzanSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ADZAN_SOUND_ENABLED] = enabled }
    }

    suspend fun setAdzanPopupEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ADZAN_POPUP_ENABLED] = enabled }
    }

    suspend fun setWallpaperTheme(theme: WallpaperTheme) {
        context.dataStore.edit { it[Keys.WALLPAPER_THEME] = theme.name }
    }

    suspend fun addCustomChannel(name: String, streamUrl: String, isKidSafe: Boolean) {
        context.dataStore.edit { prefs ->
            val currentJson = prefs[Keys.CUSTOM_CHANNELS_JSON].orEmpty()
            val list = if (currentJson.isBlank()) {
                mutableListOf()
            } else {
                runCatching { channelListAdapter.fromJson(currentJson)?.toMutableList() }
                    .getOrNull() ?: mutableListOf()
            }
            list.add(
                TvChannel(
                    id = "custom_${System.currentTimeMillis()}",
                    name = name.trim(),
                    categoryName = ChannelCategory.KUSTOM.name,
                    streamUrl = streamUrl.trim(),
                    description = "Kanal tambahan pengguna",
                    badgeCode = "KUSTOM",
                    isKidSafe = isKidSafe
                )
            )
            prefs[Keys.CUSTOM_CHANNELS_JSON] = channelListAdapter.toJson(list)
        }
    }

    suspend fun removeCustomChannel(channelId: String) {
        context.dataStore.edit { prefs ->
            val currentJson = prefs[Keys.CUSTOM_CHANNELS_JSON].orEmpty()
            if (currentJson.isBlank()) return@edit
            val list = runCatching { channelListAdapter.fromJson(currentJson)?.toMutableList() }
                .getOrNull() ?: return@edit
            list.removeAll { it.id == channelId }
            prefs[Keys.CUSTOM_CHANNELS_JSON] = channelListAdapter.toJson(list)
        }
    }
}
