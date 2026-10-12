package com.quickmindgames.sudoku.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.quickmindgames.sudoku.data.rewards.HintMigrationGrant
import com.quickmindgames.sudoku.data.rewards.HintRewardRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences internal constructor(
    private val dataStore: DataStore<Preferences>
) {
    constructor(context: Context) : this(context.appDataStore)

    companion object {
        // 4HR = 4 * 60 * 60 * 1000L
        // 20s = 20 * 1000L
        const val WHEEL_SPIN_COOLDOWN_MILLIS = 10 * 1000L
        const val HINT_WALLET_RELEASE_BONUS = 10
        const val STREAK_SHIELD_WALLET_RELEASE_BONUS = 1

        @Volatile
        private var instance: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return instance ?: synchronized(this) {
                instance ?: AppPreferences(context.applicationContext.appDataStore)
                    .also { instance = it }
            }
        }
    }

    private val HIDE_USED_NUMBERS_KEY = booleanPreferencesKey("hide_used_numbers")
    private val HIGHLIGHT_REGION_KEY = booleanPreferencesKey("highlight_region")
    private val HIGHLIGHT_SAME_NUMBERS_KEY = booleanPreferencesKey("highlight_same_numbers")
    private val FREE_PLAY_KEY = booleanPreferencesKey("free_play")
    private val SOUND_AND_VIBRATION_KEY = booleanPreferencesKey("sound_and_vibration")
    private val LAST_SPIN_KEY = longPreferencesKey("last_spin")
    private val HINT_WALLET_KEY = intPreferencesKey("hint_wallet")
    private val DOUBLE_POINTS_BOOSTER_WALLET_KEY =
        intPreferencesKey("double_points_booster_wallet")
    private val STREAK_SHIELD_WALLET_KEY = intPreferencesKey("streak_shield_wallet")
    private val STREAK_SHIELD_RELEASE_BONUS_CREDITED_KEY =
        booleanPreferencesKey("streak_shield_release_bonus_credited")
    private val HINT_MIGRATION_COMPLETE_KEY = booleanPreferencesKey("hint_migration_complete")
    private val HINT_MIGRATION_SUMMARY_SHOWN_KEY =
        booleanPreferencesKey("hint_migration_summary_shown")
    private val HINT_RELEASE_BONUS_CREDITED_KEY = booleanPreferencesKey("hint_release_bonus_credited")
    private val INITIAL_POINT_HINTS_KEY = intPreferencesKey("initial_point_hints")
    private val INITIAL_STREAK_HINTS_KEY = intPreferencesKey("initial_streak_hints")
    private val POINT_MILESTONE_KEY = intPreferencesKey("point_hint_milestone")
    private val STREAK_MILESTONE_KEY = intPreferencesKey("streak_hint_milestone")
    private val OBSERVED_STREAK_DAYS_KEY = intPreferencesKey("observed_streak_days")
    private val LAST_HINT_SPIN_ID_KEY = stringPreferencesKey("last_hint_spin_id")
    private val LAST_BOOSTER_SPIN_ID_KEY = stringPreferencesKey("last_booster_spin_id")
    private val LAST_STREAK_SHIELD_SPIN_ID_KEY =
        stringPreferencesKey("last_streak_shield_spin_id")

    val hintWallet: Flow<Int> = dataStore.data.map { preferences ->
        preferences[HINT_WALLET_KEY] ?: 0
    }

    val doublePointsBoosterWallet: Flow<Int> = dataStore.data.map { preferences ->
        preferences[DOUBLE_POINTS_BOOSTER_WALLET_KEY] ?: 0
    }

    val streakShieldWallet: Flow<Int> = dataStore.data.map { preferences ->
        preferences[STREAK_SHIELD_WALLET_KEY] ?: 0
    }

    val initialPointHints: Flow<Int> = dataStore.data.map { preferences ->
        preferences[INITIAL_POINT_HINTS_KEY] ?: 0
    }

    val initialStreakHints: Flow<Int> = dataStore.data.map { preferences ->
        preferences[INITIAL_STREAK_HINTS_KEY] ?: 0
    }

    val lastSpin: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[LAST_SPIN_KEY]
    }

    val hideUsedNumbers: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HIDE_USED_NUMBERS_KEY] ?: false
    }

    val highlightRegion: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HIGHLIGHT_REGION_KEY] ?: false
    }

    val highlightSameNumbers: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HIGHLIGHT_SAME_NUMBERS_KEY] ?: false
    }

    val freePlay: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[FREE_PLAY_KEY] ?: false
    }

    val soundAndVibration: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SOUND_AND_VIBRATION_KEY] ?: true
    }

    suspend fun setHideUsedNumbers(enabled: Boolean): Boolean {
        var disabledFreePlay = false
        dataStore.edit { preferences ->
            preferences[HIDE_USED_NUMBERS_KEY] = enabled
            if (enabled && preferences[FREE_PLAY_KEY] == true) {
                preferences[FREE_PLAY_KEY] = false
                disabledFreePlay = true
            }
        }
        return disabledFreePlay
    }

    suspend fun setHighlightRegion(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[HIGHLIGHT_REGION_KEY] = enabled
        }
    }

    suspend fun setHighlightSameNumbers(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[HIGHLIGHT_SAME_NUMBERS_KEY] = enabled
        }
    }

    suspend fun setFreePlay(enabled: Boolean): Boolean {
        var disabledHideUsedNumbers = false
        dataStore.edit { preferences ->
            preferences[FREE_PLAY_KEY] = enabled
            if (enabled && preferences[HIDE_USED_NUMBERS_KEY] == true) {
                preferences[HIDE_USED_NUMBERS_KEY] = false
                disabledHideUsedNumbers = true
            }
        }
        return disabledHideUsedNumbers
    }

    suspend fun normalizeGameplayOptions() {
        dataStore.edit { preferences ->
            if (preferences[FREE_PLAY_KEY] == true && preferences[HIDE_USED_NUMBERS_KEY] == true) {
                preferences[HIDE_USED_NUMBERS_KEY] = false
            }
        }
    }

    suspend fun setSoundAndVibration(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SOUND_AND_VIBRATION_KEY] = enabled
        }
    }

    suspend fun setLastSpin(timestampMillis: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SPIN_KEY] = timestampMillis
        }
    }

    suspend fun migrateHintWallet(lifetimePoints: Int, currentStreakDays: Int): HintMigrationGrant {
        val grant = HintRewardRules.initialGrant(lifetimePoints, currentStreakDays)
        val preferences = dataStore.edit { preferences ->
            if (preferences[HINT_MIGRATION_COMPLETE_KEY] != true) {
                preferences[HINT_WALLET_KEY] = addWithoutOverflow(
                    preferences[HINT_WALLET_KEY] ?: 0,
                    grant.totalHints
                )
                preferences[INITIAL_POINT_HINTS_KEY] = grant.pointHints
                preferences[INITIAL_STREAK_HINTS_KEY] = grant.streakHints
                preferences[POINT_MILESTONE_KEY] = grant.pointHints
                preferences[STREAK_MILESTONE_KEY] = grant.streakHints
                preferences[OBSERVED_STREAK_DAYS_KEY] = currentStreakDays.coerceAtLeast(0)
                preferences[HINT_MIGRATION_COMPLETE_KEY] = true
            }
        }
        return HintMigrationGrant(
            pointHints = preferences[INITIAL_POINT_HINTS_KEY] ?: 0,
            streakHints = preferences[INITIAL_STREAK_HINTS_KEY] ?: 0
        )
    }

    suspend fun grantHintWalletReleaseBonusOnce(): Boolean {
        var granted = false
        dataStore.edit { preferences ->
            if (preferences[HINT_RELEASE_BONUS_CREDITED_KEY] != true) {
                preferences[HINT_WALLET_KEY] = addWithoutOverflow(
                    preferences[HINT_WALLET_KEY] ?: 0,
                    HINT_WALLET_RELEASE_BONUS
                )
                preferences[HINT_RELEASE_BONUS_CREDITED_KEY] = true
                granted = true
            }
        }
        return granted
    }

    suspend fun grantStreakShieldReleaseBonusOnce(): Boolean {
        var granted = false
        dataStore.edit { preferences ->
            if (preferences[STREAK_SHIELD_RELEASE_BONUS_CREDITED_KEY] != true) {
                preferences[STREAK_SHIELD_WALLET_KEY] = addWithoutOverflow(
                    preferences[STREAK_SHIELD_WALLET_KEY] ?: 0,
                    STREAK_SHIELD_WALLET_RELEASE_BONUS
                )
                preferences[STREAK_SHIELD_RELEASE_BONUS_CREDITED_KEY] = true
                granted = true
            }
        }
        return granted
    }

    suspend fun claimHintMigrationSummaryOnce(): Boolean {
        var shouldShow = false
        dataStore.edit { preferences ->
            if (preferences[HINT_MIGRATION_COMPLETE_KEY] == true &&
                preferences[HINT_MIGRATION_SUMMARY_SHOWN_KEY] != true
            ) {
                preferences[HINT_MIGRATION_SUMMARY_SHOWN_KEY] = true
                shouldShow = true
            }
        }
        return shouldShow
    }

    suspend fun reconcileHintMilestones(lifetimePoints: Int, currentStreakDays: Int) {
        processPointMilestones(lifetimePoints)
        processStreakMilestones(currentStreakDays)
    }

    suspend fun processPointMilestones(lifetimePoints: Int) {
        dataStore.edit { preferences ->
            if (preferences[HINT_MIGRATION_COMPLETE_KEY] == true) {
                val previous = preferences[POINT_MILESTONE_KEY] ?: 0
                val update = HintRewardRules.pointMilestoneUpdate(lifetimePoints, previous)
                if (update.hintsToAward > 0) {
                    preferences[HINT_WALLET_KEY] = addWithoutOverflow(
                        preferences[HINT_WALLET_KEY] ?: 0,
                        update.hintsToAward
                    )
                }
                preferences[POINT_MILESTONE_KEY] = update.creditedMilestone
            }
        }
    }

    suspend fun processStreakMilestones(currentStreakDays: Int) {
        dataStore.edit { preferences ->
            if (preferences[HINT_MIGRATION_COMPLETE_KEY] == true) {
                val previousStreak = preferences[OBSERVED_STREAK_DAYS_KEY] ?: 0
                val previousMilestone = preferences[STREAK_MILESTONE_KEY] ?: 0
                val update = HintRewardRules.streakMilestoneUpdate(
                    currentStreakDays = currentStreakDays,
                    previouslyObservedStreakDays = previousStreak,
                    previouslyCreditedMilestone = previousMilestone
                )
                if (update.hintsToAward > 0) {
                    preferences[HINT_WALLET_KEY] = addWithoutOverflow(
                        preferences[HINT_WALLET_KEY] ?: 0,
                        update.hintsToAward
                    )
                }
                preferences[STREAK_MILESTONE_KEY] = update.creditedMilestone
                preferences[OBSERVED_STREAK_DAYS_KEY] = currentStreakDays.coerceAtLeast(0)
            }
        }
    }

    suspend fun addHints(amount: Int) {
        require(amount >= 0) { "Hint amount cannot be negative." }
        if (amount == 0) return
        dataStore.edit { preferences ->
            preferences[HINT_WALLET_KEY] = addWithoutOverflow(
                preferences[HINT_WALLET_KEY] ?: 0,
                amount
            )
        }
    }

    suspend fun addWheelHintOnce(spinId: String, amount: Int): Boolean {
        require(spinId.isNotBlank()) { "Spin ID cannot be blank." }
        require(amount > 0) { "Hint amount must be greater than zero." }
        var added = false
        dataStore.edit { preferences ->
            if (preferences[LAST_HINT_SPIN_ID_KEY] != spinId) {
                preferences[HINT_WALLET_KEY] = addWithoutOverflow(
                    preferences[HINT_WALLET_KEY] ?: 0,
                    amount
                )
                preferences[LAST_HINT_SPIN_ID_KEY] = spinId
                added = true
            }
        }
        return added
    }

    suspend fun addWheelDoublePointsBoosterOnce(spinId: String, amount: Int): Boolean {
        require(spinId.isNotBlank()) { "Spin ID cannot be blank." }
        require(amount > 0) { "Booster amount must be greater than zero." }
        var added = false
        dataStore.edit { preferences ->
            if (preferences[LAST_BOOSTER_SPIN_ID_KEY] != spinId) {
                preferences[DOUBLE_POINTS_BOOSTER_WALLET_KEY] = addWithoutOverflow(
                    preferences[DOUBLE_POINTS_BOOSTER_WALLET_KEY] ?: 0,
                    amount
                )
                preferences[LAST_BOOSTER_SPIN_ID_KEY] = spinId
                added = true
            }
        }
        return added
    }

    suspend fun addWheelStreakShieldOnce(spinId: String, amount: Int): Boolean {
        require(spinId.isNotBlank()) { "Spin ID cannot be blank." }
        require(amount > 0) { "Streak shield amount must be greater than zero." }
        var added = false
        dataStore.edit { preferences ->
            if (preferences[LAST_STREAK_SHIELD_SPIN_ID_KEY] != spinId) {
                preferences[STREAK_SHIELD_WALLET_KEY] = addWithoutOverflow(
                    preferences[STREAK_SHIELD_WALLET_KEY] ?: 0,
                    amount
                )
                preferences[LAST_STREAK_SHIELD_SPIN_ID_KEY] = spinId
                added = true
            }
        }
        return added
    }

    suspend fun consumeDoublePointsBooster(): Boolean {
        var consumed = false
        dataStore.edit { preferences ->
            val balance =
                (preferences[DOUBLE_POINTS_BOOSTER_WALLET_KEY] ?: 0).coerceAtLeast(0)
            if (balance > 0) {
                preferences[DOUBLE_POINTS_BOOSTER_WALLET_KEY] = balance - 1
                consumed = true
            } else {
                preferences[DOUBLE_POINTS_BOOSTER_WALLET_KEY] = 0
            }
        }
        return consumed
    }

    suspend fun consumeStreakShield(): Boolean {
        var consumed = false
        dataStore.edit { preferences ->
            val balance = (preferences[STREAK_SHIELD_WALLET_KEY] ?: 0).coerceAtLeast(0)
            if (balance > 0) {
                preferences[STREAK_SHIELD_WALLET_KEY] = balance - 1
                consumed = true
            } else {
                preferences[STREAK_SHIELD_WALLET_KEY] = 0
            }
        }
        return consumed
    }

    suspend fun consumeHint(): Boolean {
        var consumed = false
        dataStore.edit { preferences ->
            val balance = (preferences[HINT_WALLET_KEY] ?: 0).coerceAtLeast(0)
            if (balance > 0) {
                preferences[HINT_WALLET_KEY] = balance - 1
                consumed = true
            } else {
                preferences[HINT_WALLET_KEY] = 0
            }
        }
        return consumed
    }

    private fun addWithoutOverflow(current: Int, amount: Int): Int =
        (current.coerceAtLeast(0).toLong() + amount.coerceAtLeast(0))
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()

}
