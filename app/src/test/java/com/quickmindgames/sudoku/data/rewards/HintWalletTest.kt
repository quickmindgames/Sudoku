package com.quickmindgames.sudoku.data.rewards

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.data.state.GameState
import com.quickmindgames.sudoku.domain.model.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HintWalletTest {
    @Test
    fun migrationCalculatesPointAndStreakHintsAndRunsOnlyOnce() = runBlocking {
        val dataStore = InMemoryPreferencesDataStore()
        val preferences = AppPreferences(dataStore)
        preferences.addHints(2)
        val firstGrant = preferences.migrateHintWallet(16_436, 36)

        assertEquals(HintMigrationGrant(pointHints = 16, streakHints = 7), firstGrant)
        assertEquals(25, preferences.hintWallet.first())

        val reopenedPreferences = AppPreferences(dataStore)
        val repeatedGrant = reopenedPreferences.migrateHintWallet(50_000, 50)

        assertEquals(firstGrant, repeatedGrant)
        assertEquals(25, reopenedPreferences.hintWallet.first())
        assertEquals(16, reopenedPreferences.initialPointHints.first())
        assertEquals(7, reopenedPreferences.initialStreakHints.first())
    }

    @Test
    fun releaseBonusAddsTenHintsToAnExistingWalletOnlyOnce() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())
        preferences.addHints(1)

        assertTrue(preferences.grantHintWalletReleaseBonusOnce())
        assertEquals(11, preferences.hintWallet.first())
        assertFalse(preferences.grantHintWalletReleaseBonusOnce())
        assertEquals(11, preferences.hintWallet.first())
    }

    @Test
    fun zeroPointsAndStreakMigrateNoHints() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())
        val grant = preferences.migrateHintWallet(0, 0)

        assertEquals(HintMigrationGrant(pointHints = 0, streakHints = 0), grant)
        assertEquals(0, preferences.hintWallet.first())
    }

    @Test
    fun migrationSummaryCanBeClaimedOnlyOnceAfterMigration() = runBlocking {
        val dataStore = InMemoryPreferencesDataStore()
        val preferences = AppPreferences(dataStore)

        assertFalse(preferences.claimHintMigrationSummaryOnce())
        preferences.migrateHintWallet(1_000, 5)

        assertTrue(preferences.claimHintMigrationSummaryOnce())
        assertFalse(AppPreferences(dataStore).claimHintMigrationSummaryOnce())
    }

    @Test
    fun pointMilestonesAwardEveryNewThresholdExactlyOnce() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())
        preferences.migrateHintWallet(16_436, 0)

        preferences.processPointMilestones(17_000)
        assertEquals(17, preferences.hintWallet.first())
        preferences.processPointMilestones(17_000)
        assertEquals(17, preferences.hintWallet.first())

        preferences.processPointMilestones(20_500)
        assertEquals(20, preferences.hintWallet.first())
        preferences.processPointMilestones(20_500)
        assertEquals(20, preferences.hintWallet.first())
    }

    @Test
    fun crossingMultiplePointMilestonesAwardsEachOne() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())
        preferences.migrateHintWallet(950, 0)

        preferences.processPointMilestones(2_050)

        assertEquals(2, preferences.hintWallet.first())
    }

    @Test
    fun streakMilestonesResumeAfterMigrationAndCanBeEarnedOnANewStreak() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())
        preferences.migrateHintWallet(0, 36)

        preferences.processStreakMilestones(40)
        assertEquals(8, preferences.hintWallet.first())
        preferences.processStreakMilestones(40)
        assertEquals(8, preferences.hintWallet.first())

        preferences.processStreakMilestones(0)
        preferences.processStreakMilestones(5)
        assertEquals(9, preferences.hintWallet.first())
    }

    @Test
    fun wheelHintIsCreditedOnceAndWalletCannotBeSpentBelowZero() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())
        assertTrue(preferences.addWheelHintOnce("spin-1", 1))
        assertFalse(preferences.addWheelHintOnce("spin-1", 1))
        assertEquals(1, preferences.hintWallet.first())

        assertTrue(preferences.consumeHint())
        assertFalse(preferences.consumeHint())
        assertEquals(0, preferences.hintWallet.first())
    }

    @Test
    fun wheelBoostersAccumulateOncePerSpinAndCannotBeSpentBelowZero() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())

        assertTrue(preferences.addWheelDoublePointsBoosterOnce("spin-1", 1))
        assertFalse(preferences.addWheelDoublePointsBoosterOnce("spin-1", 1))
        assertTrue(preferences.addWheelDoublePointsBoosterOnce("spin-2", 2))
        assertEquals(3, preferences.doublePointsBoosterWallet.first())

        assertTrue(preferences.consumeDoublePointsBooster())
        assertTrue(preferences.consumeDoublePointsBooster())
        assertTrue(preferences.consumeDoublePointsBooster())
        assertFalse(preferences.consumeDoublePointsBooster())
        assertEquals(0, preferences.doublePointsBoosterWallet.first())
    }

    @Test
    fun wheelShieldsAccumulateOncePerSpinAndCannotBeSpentBelowZero() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())

        assertTrue(preferences.addWheelStreakShieldOnce("spin-1", 1))
        assertFalse(preferences.addWheelStreakShieldOnce("spin-1", 1))
        assertTrue(preferences.addWheelStreakShieldOnce("spin-2", 2))
        assertEquals(3, preferences.streakShieldWallet.first())

        assertTrue(preferences.consumeStreakShield())
        assertTrue(preferences.consumeStreakShield())
        assertTrue(preferences.consumeStreakShield())
        assertFalse(preferences.consumeStreakShield())
        assertEquals(0, preferences.streakShieldWallet.first())
    }

    @Test
    fun streakShieldLaunchBonusIsGrantedOnlyOnceAndAddsToExistingBalance() = runBlocking {
        val preferences = AppPreferences(InMemoryPreferencesDataStore())
        preferences.addWheelStreakShieldOnce("spin-1", 2)

        assertTrue(preferences.grantStreakShieldReleaseBonusOnce())
        assertEquals(3, preferences.streakShieldWallet.first())
        assertFalse(preferences.grantStreakShieldReleaseBonusOnce())
        assertEquals(3, preferences.streakShieldWallet.first())
    }

    @Test
    fun gameStatePersistsHintUsageAndLegacySavesDefaultToZero() {
        val state = sampleGameState(hintsUsed = 2, hasDoublePointsBooster = true)
        val serialized = state.serialize()

        assertEquals(2, GameState.deserialize(serialized)?.hintsUsed)
        assertTrue(GameState.deserialize(serialized)?.hasDoublePointsBooster == true)

        val legacySerialized = serialized
            .replace("HINTS_USED:2\n", "")
            .replace("NOTES_MODE:false\n", "NOTES_MODE:false\nHINTS:1\n")
            .replace("DOUBLE_POINTS_BOOSTER:true\n", "")

        assertEquals(0, GameState.deserialize(legacySerialized)?.hintsUsed)
        assertFalse(GameState.deserialize(legacySerialized)?.hasDoublePointsBooster ?: true)
    }

    private fun sampleGameState(
        hintsUsed: Int,
        hasDoublePointsBooster: Boolean = false
    ): GameState {
        val emptyGrid = List(9) { List(9) { 0 } }
        val emptyUserGrid = List(9) { List(9) { CellData(0) } }
        return GameState(
            difficulty = Difficulty.Breeze,
            originalGrid = emptyGrid,
            solutionGrid = emptyGrid,
            userGrid = emptyUserGrid,
            wrongCells = emptySet(),
            mistakes = 0,
            score = 0,
            timeSeconds = 10,
            selectedCell = null,
            hintsUsed = hintsUsed,
            hasDoublePointsBooster = hasDoublePointsBooster
        )
    }
}

private class InMemoryPreferencesDataStore : DataStore<Preferences> {
    private val mutex = Mutex()
    private val preferences = MutableStateFlow(emptyPreferences())

    override val data: Flow<Preferences> = preferences

    override suspend fun updateData(
        transform: suspend (t: Preferences) -> Preferences
    ): Preferences = mutex.withLock {
        transform(preferences.value).also { preferences.value = it }
    }
}
