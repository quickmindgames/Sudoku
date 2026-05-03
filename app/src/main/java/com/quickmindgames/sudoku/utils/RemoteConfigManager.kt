package com.quickmindgames.sudoku.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

object RemoteConfigManager {
    private const val DEFAULT_BREEZE_MIN_CLUES = 46L
    private const val DEFAULT_BREEZE_MAX_CLUES = 51L
    const val KEY_BREEZE_MIN_CLUES = "breeze_min_clues"
    const val KEY_BREEZE_MAX_CLUES = "breeze_max_clues"
    private const val DEFAULT_PULSE_MIN_CLUES = 32L
    private const val DEFAULT_PULSE_MAX_CLUES = 37L
    const val KEY_PULSE_MIN_CLUES = "pulse_min_clues"
    const val KEY_PULSE_MAX_CLUES = "pulse_max_clues"
    private const val DEFAULT_RAGE_MIN_CLUES = 26L
    private const val DEFAULT_RAGE_MAX_CLUES = 31L
    const val KEY_RAGE_MIN_CLUES = "rage_min_clues"
    const val KEY_RAGE_MAX_CLUES = "rage_max_clues"
    private const val DEFAULT_ELITE_MIN_CLUES = 22L
    private const val DEFAULT_ELITE_MAX_CLUES = 26L
    const val KEY_ELITE_MIN_CLUES = "elite_min_clues"
    const val KEY_ELITE_MAX_CLUES = "elite_max_clues"
    const val KEY_BREEZE_HINTS = "breeze_hints"
    const val KEY_PULSE_HINTS = "pulse_hints"
    const val KEY_RAGE_HINTS = "rage_hints"
    const val KEY_ELITE_HINTS = "elite_hints"
    const val DEFAULT_BREEZE_HINTS = 1
    const val DEFAULT_PULSE_HINTS = 1
    const val DEFAULT_RAGE_HINTS = 1
    const val DEFAULT_ELITE_HINTS = 1

    /**
     * Initialize Remote Config with default values and settings.
     * Should be called once at app startup (e.g., in Application.onCreate()).
     */
    fun init() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60) // Fetch new values at most once per hour
            .build()
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(configSettings)
        val defaults = mapOf(
            KEY_BREEZE_MIN_CLUES to DEFAULT_BREEZE_MIN_CLUES,
            KEY_BREEZE_MAX_CLUES to DEFAULT_BREEZE_MAX_CLUES,
            KEY_PULSE_MIN_CLUES to DEFAULT_PULSE_MIN_CLUES,
            KEY_PULSE_MAX_CLUES to DEFAULT_PULSE_MAX_CLUES,
            KEY_RAGE_MIN_CLUES to DEFAULT_RAGE_MIN_CLUES,
            KEY_RAGE_MAX_CLUES to DEFAULT_RAGE_MAX_CLUES,
            KEY_ELITE_MIN_CLUES to DEFAULT_ELITE_MIN_CLUES,
            KEY_ELITE_MAX_CLUES to DEFAULT_ELITE_MAX_CLUES,
            KEY_BREEZE_HINTS to DEFAULT_BREEZE_HINTS,
            KEY_PULSE_HINTS to DEFAULT_PULSE_HINTS,
            KEY_RAGE_HINTS to DEFAULT_RAGE_HINTS,
            KEY_ELITE_HINTS to DEFAULT_ELITE_HINTS
        )
        remoteConfig.setDefaultsAsync(defaults)
    }

    /**
     * Fetch and activate the latest remote config values.
     * Should be called at app start or when you want to refresh values.
     */
    suspend fun fetchAndActivate() {
        try {
            FirebaseRemoteConfig.getInstance().fetchAndActivate().await()
        } catch (e: Exception) {
            // Log or handle error if needed
        }
    }

    /**
     * Get the min clues for Breeze difficulty.
     */
    fun getBreezeMinClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_BREEZE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Breeze difficulty.
     */
    fun getBreezeMaxClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_BREEZE_MAX_CLUES).toInt()
    }

    /**
     * Get the min clues for Pulse difficulty.
     */
    fun getPulseMinClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_PULSE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Pulse difficulty.
     */
    fun getPulseMaxClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_PULSE_MAX_CLUES).toInt()
    }

    /**
     * Get the min clues for Rage difficulty.
     */
    fun getRageMinClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_RAGE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Rage difficulty.
     */
    fun getRageMaxClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_RAGE_MAX_CLUES).toInt()
    }

    /**
     * Get the min clues for Elite difficulty.
     */
    fun getEliteMinClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_ELITE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Elite difficulty.
     */
    fun getEliteMaxClues(): Int {
        return FirebaseRemoteConfig.getInstance().getLong(KEY_ELITE_MAX_CLUES).toInt()
    }

    /**
     * Get the number of hints allowed for a given difficulty.
     */
    fun getHintsForDifficulty(difficulty: String): Int {
        return when (difficulty) {
            "breeze" -> FirebaseRemoteConfig.getInstance().getLong(KEY_BREEZE_HINTS).toInt()
            "pulse" -> FirebaseRemoteConfig.getInstance().getLong(KEY_PULSE_HINTS).toInt()
            "rage" -> FirebaseRemoteConfig.getInstance().getLong(KEY_RAGE_HINTS).toInt()
            "elite" -> FirebaseRemoteConfig.getInstance().getLong(KEY_ELITE_HINTS).toInt()
            else -> 0
        }
    }
}