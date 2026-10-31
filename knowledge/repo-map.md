This file is a merged representation of a subset of the codebase, containing specifically included files and files not matching ignore patterns, combined into a single document by Repomix.
The content has been processed where content has been compressed (code blocks are separated by ⋮---- delimiter).

# File Summary

## Purpose
This file contains a packed representation of a subset of the repository's contents that is considered the most important context.
It is designed to be easily consumable by AI systems for analysis, code review,
or other automated processes.

## File Format
The content is organized as follows:
1. This summary section
2. Repository information
3. Directory structure
4. Repository files (if enabled)
5. Multiple file entries, each consisting of:
  a. A header with the file path (## File: path/to/file)
  b. The full contents of the file in a code block

## Usage Guidelines
- This file should be treated as read-only. Any changes should be made to the
  original repository files, not this packed version.
- When processing this file, use the file path to distinguish
  between different files in the repository.
- Be aware that this file may contain sensitive information. Handle it with
  the same level of security as you would the original repository.

## Notes
- Some files may have been excluded based on .gitignore rules and Repomix's configuration
- Binary files are not included in this packed representation. Please refer to the Repository Structure section for a complete list of file paths, including binary files
- Only files matching these patterns are included: app/src/main/java/**, app/src/main/kotlin/**, app/src/main/res/**, app/src/main/AndroidManifest.xml, app/build.gradle*, build.gradle*, settings.gradle*, gradle.properties, gradle/libs.versions.toml, README.md
- Files matching these patterns are excluded: build/**, **/build/**, .gradle/**, .idea/**, local.properties, *.jks, *.keystore, **/*.png, **/*.jpg, **/*.jpeg, **/*.webp, **/*.gif, **/*.ttf, **/*.otf, **/*.mp3, **/*.mp4
- Files matching patterns in .gitignore are excluded
- Files matching default ignore patterns are excluded
- Content has been compressed - code blocks are separated by ⋮---- delimiter
- Files are sorted by Git change count (files with more changes are at the bottom)

# Directory Structure
```
app/
  src/
    main/
      java/
        com/
          quickmindgames/
            sudoku/
              component/
                DifficultyCard.kt
                LearningCompleteDialog.kt
                LessonCardColumn.kt
                LessonCardDefault.kt
                LessonGridCell.kt
                NumberPad.kt
                PrizeWheel.kt
                StatisticsComponents.kt
                StreakNode.kt
                StreakPath.kt
                StreakSummaryCard.kt
                SudokuBoardContainer.kt
                SudokuGridCanvas.kt
                SudokuGridLevel.kt
                VisualLessonGrid.kt
                WalletInfoButton.kt
                WalletInfoSection.kt
                WalletRewardSummaryRow.kt
              data/
                database/
                  dao/
                    GameStatisticDao.kt
                    StreakGameDao.kt
                  entity/
                    GameStatisticEntity.kt
                    StreakGameEntity.kt
                  SudokuDatabase.kt
                preferences/
                  AppPreferences.kt
                  ThemePreferences.kt
                repository/
                  GameRepositoryImpl.kt
                  StatisticsRepository.kt
                  ThemeRepositoryImpl.kt
                rewards/
                  HintRewardRules.kt
                score/
                  TotalScoreManager.kt
                source/
                  ThemePreferencesDataSource.kt
                state/
                  GameState.kt
                  GameStateManager.kt
                  StreakState.kt
                  StreakStateManager.kt
                util/
                  GameCompletionRecorder.kt
                  GameFeedback.kt
              domain/
                generator/
                  SudokuGenerator.kt
                model/
                  Difficulty.kt
                  GameData.kt
                  LessonData.kt
                  Statistics.kt
                  SudokuPuzzle.kt
                repository/
                  GameRepository.kt
                  ThemeRepository.kt
                util/
                  SudokuValidator.kt
                  UndoManager.kt
              presentation/
                navigation/
                  AppNav.kt
                  BottomScreen.kt
                ui/
                  screen/
                    HomeScreen.kt
                    LessonScreen.kt
                    RewardsScreen.kt
                    SettingsScreen.kt
                    StatisticsScreen.kt
                    StreakScreen.kt
                    SudokuScreen.kt
                  theme/
                    Color.kt
                    Theme.kt
                    Type.kt
                  MainActivity.kt
                util/
                  ShakeModifier.kt
                viewmodel/
                  HomeViewModel.kt
                  RewardsViewModel.kt
                  SettingsViewModel.kt
                  StatisticsViewModel.kt
                  StreakViewModel.kt
                  SudokuGameViewModel.kt
              utils/
                AnalyticsConstants.kt
                AnalyticsUtils.kt
                MultipleEventsCutter.kt
                RemoteConfigManager.kt
                ShareUtils.kt
                StreakReminderScheduler.kt
                StreakReminderWorker.kt
                SudokuRules.kt
                TimeFormatters.kt
              SudokuApp.kt
      res/
        drawable/
          ic_launcher_background.xml
          ic_launcher_foreground.xml
        mipmap-anydpi-v26/
          ic_launcher_round.xml
          ic_launcher.xml
        raw/
          confetti.json
          game_over.ogg
          number_correct.ogg
          number_wrong.ogg
          puzzle_complete.ogg
          spin_wheel.ogg
        values/
          colors.xml
          ic_launcher_background.xml
          strings.xml
          themes.xml
        xml/
          backup_rules.xml
          data_extraction_rules.xml
      AndroidManifest.xml
  build.gradle.kts
gradle/
  libs.versions.toml
build.gradle.kts
gradle.properties
settings.gradle.kts
```

# Files

## File: app/src/main/java/com/quickmindgames/sudoku/component/SudokuBoardContainer.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import kotlin.math.floor

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SudokuBoardContainer(
    modifier: Modifier = Modifier,
    content: @Composable (cellSizeDp: Dp, boardSizeDp: Dp) -> Unit
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val boardDpRaw = min(maxWidth, maxHeight)

        // Convert to px, then snap to multiples of 9 px
        val boardPxRaw = with(density) { boardDpRaw.toPx() }
        val cellPx = floor(boardPxRaw / 9f) // integer px per cell
        val boardPx = cellPx * 9f           // snapped board size px

        val cellDp = with(density) { cellPx.toDp() }
        val boardDp = with(density) { boardPx.toDp() }

        Box(
            modifier = Modifier
                .requiredSize(boardDp)   // ✅ board is exact size
                .clipToBounds()
        ) {
            content(cellDp, boardDp)
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/WalletInfoButton.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WalletInfoButton(
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/WalletInfoSection.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WalletInfoSection(
    label: String,
    details: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        details.forEach { detail ->
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/WalletRewardSummaryRow.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WalletRewardSummaryRow(
    emoji: String,
    title: String,
    amount: Int,
    detail: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "%,d".format(amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/database/dao/GameStatisticDao.kt
```kotlin
package com.quickmindgames.sudoku.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.quickmindgames.sudoku.data.database.entity.GameStatisticEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for GameStatisticEntity
 */
@Dao
interface GameStatisticDao {

    /**
     * Insert a new game statistic record
     */
    @Insert
    suspend fun insertGameStatistic(statistic: GameStatisticEntity)

    /**
     * Get all game statistics
     */
    @Query("SELECT * FROM game_statistics ORDER BY date_completed DESC")
    fun getAllGameStatistics(): Flow<List<GameStatisticEntity>>

    /**
     * Get game statistics for a specific difficulty
     */
    @Query("SELECT * FROM game_statistics WHERE difficulty = :difficulty ORDER BY date_completed DESC")
    fun getGameStatisticsByDifficulty(difficulty: String): Flow<List<GameStatisticEntity>>

    /**
     * Get total games count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty")
    fun getTotalGamesByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get total wins count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 1")
    fun getWinsCountByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get total losses count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 0")
    fun getLossesCountByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get best (minimum) time for a specific difficulty
     */
    @Query("SELECT MIN(time_seconds) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 1")
    fun getBestTimeByDifficulty(difficulty: String): Flow<Int?>

    /**
     * Get best (maximum) score for a specific difficulty
     */
    @Query("SELECT MAX(score) FROM game_statistics WHERE difficulty = :difficulty AND is_won = 1")
    fun getBestScoreByDifficulty(difficulty: String): Flow<Int?>

    /**
     * Get flaw-free wins (mistakes == 0) count for a specific difficulty
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE difficulty = :difficulty AND is_flaw_free = 1 AND is_won = 1")
    fun getFlawFreeWinsCountByDifficulty(difficulty: String): Flow<Int>

    /**
     * Get total games count across all difficulties
     */
    @Query("SELECT COUNT(*) FROM game_statistics")
    fun getTotalGamesCount(): Flow<Int>

    /**
     * Get total wins count across all difficulties
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE is_won = 1")
    fun getTotalWinsCount(): Flow<Int>

    /**
     * Get total losses count across all difficulties
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE is_won = 0")
    fun getTotalLossesCount(): Flow<Int>

    /**
     * Get best (minimum) time across all games
     */
    @Query("SELECT MIN(time_seconds) FROM game_statistics WHERE is_won = 1")
    fun getBestTimeOverall(): Flow<Int?>

    /**
     * Get best (maximum) score across all games
     */
    @Query("SELECT MAX(score) FROM game_statistics WHERE is_won = 1")
    fun getBestScoreOverall(): Flow<Int?>

    /**
     * Get flaw-free wins count across all games
     */
    @Query("SELECT COUNT(*) FROM game_statistics WHERE is_flaw_free = 1 AND is_won = 1")
    fun getFlawFreeWinsCountOverall(): Flow<Int>

    /**
     * Delete all game statistics (for testing/reset)
     */
    @Query("DELETE FROM game_statistics")
    suspend fun deleteAllGameStatistics()
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/database/entity/GameStatisticEntity.kt
```kotlin
package com.quickmindgames.sudoku.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for storing individual game statistics
 */
@Entity(tableName = "game_statistics")
data class GameStatisticEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "difficulty")
    val difficulty: String, // "Breeze", "Pulse", "Rage", "Elite"

    @ColumnInfo(name = "is_won")
    val isWon: Boolean,

    @ColumnInfo(name = "mistakes")
    val mistakes: Int,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "time_seconds")
    val timeSeconds: Int,

    @ColumnInfo(name = "is_flaw_free")
    val isFlawFree: Boolean, // true if mistakes == 0

    @ColumnInfo(name = "date_completed")
    val dateCompleted: Long // timestamp in milliseconds
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/database/entity/StreakGameEntity.kt
```kotlin
package com.quickmindgames.sudoku.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for storing streak game statistics
 */
@Entity(tableName = "streak_games")
data class StreakGameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "streak_day")
    val streakDay: Int,

    @ColumnInfo(name = "is_won")
    val isWon: Boolean,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "time_seconds")
    val timeSeconds: Int,

    @ColumnInfo(name = "date_completed")
    val dateCompleted: Long // timestamp in milliseconds
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/database/SudokuDatabase.kt
```kotlin
package com.quickmindgames.sudoku.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quickmindgames.sudoku.data.database.dao.GameStatisticDao
import com.quickmindgames.sudoku.data.database.dao.StreakGameDao
import com.quickmindgames.sudoku.data.database.entity.GameStatisticEntity
import com.quickmindgames.sudoku.data.database.entity.StreakGameEntity

/**
 * Room database for Sudoku game statistics
 */
@Database(
    entities = [GameStatisticEntity::class, StreakGameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SudokuDatabase : RoomDatabase() {

    abstract fun gameStatisticDao(): GameStatisticDao
    abstract fun streakGameDao(): StreakGameDao

    companion object {
        private const val DATABASE_NAME = "sudoku_database"

        @Volatile
        private var instance: SudokuDatabase? = null

        /**
         * Get the singleton instance of SudokuDatabase
         * Uses lazy initialization pattern for thread safety
         */
        fun getInstance(context: Context): SudokuDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SudokuDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/preferences/ThemePreferences.kt
```kotlin
// This file has been moved to data/preferences/ThemePreferences.kt

package com.quickmindgames.sudoku.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    val isDarkMode: Flow<Boolean> = context.themeDataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    companion object {
        @Volatile
        private var instance: ThemePreferences? = null

        fun getInstance(context: Context): ThemePreferences {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferences(context.applicationContext).also { instance = it }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/repository/GameRepositoryImpl.kt
```kotlin
package com.quickmindgames.sudoku.data.repository

import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.domain.model.GameData
import com.quickmindgames.sudoku.domain.repository.GameRepository

class GameRepositoryImpl(
    private val gameStateManager: GameStateManager
) : GameRepository {

    override suspend fun saveGame(gameData: GameData) {
        // GameData is typealias of GameState, so we can pass it directly
        gameStateManager.saveGameState(gameData)
    }

    override suspend fun loadGame(): GameData? {
        var result: GameData? = null
        gameStateManager.getSavedGameState().collect { gameState ->
            result = gameState
        }
        return result
    }

    override suspend fun deleteGame() {
        gameStateManager.clearGameState()
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/repository/ThemeRepositoryImpl.kt
```kotlin
package com.quickmindgames.sudoku.data.repository

import com.quickmindgames.sudoku.data.preferences.ThemePreferences
import com.quickmindgames.sudoku.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val themePreferences: ThemePreferences
) : ThemeRepository {

    override val isDarkMode: Flow<Boolean> = themePreferences.isDarkMode

    override suspend fun setDarkMode(enabled: Boolean) {
        themePreferences.setDarkMode(enabled)
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/rewards/HintRewardRules.kt
```kotlin
package com.quickmindgames.sudoku.data.rewards

data class HintMigrationGrant(
    val pointHints: Int,
    val streakHints: Int
) {
    val totalHints: Int
        get() = pointHints + streakHints
}

data class MilestoneUpdate(
    val creditedMilestone: Int,
    val hintsToAward: Int
)

object HintRewardRules {
    const val POINTS_PER_HINT = 1_000
    const val STREAK_DAYS_PER_HINT = 5

    fun initialGrant(lifetimePoints: Int, streakDays: Int): HintMigrationGrant =
        HintMigrationGrant(
            pointHints = lifetimePoints.coerceAtLeast(0) / POINTS_PER_HINT,
            streakHints = streakDays.coerceAtLeast(0) / STREAK_DAYS_PER_HINT
        )

    fun pointMilestoneUpdate(
        lifetimePoints: Int,
        previouslyCreditedMilestone: Int
    ): MilestoneUpdate {
        val reachedMilestone = lifetimePoints.coerceAtLeast(0) / POINTS_PER_HINT
        val newMilestone = maxOf(previouslyCreditedMilestone, reachedMilestone)
        return MilestoneUpdate(
            creditedMilestone = newMilestone,
            hintsToAward = (newMilestone - previouslyCreditedMilestone).coerceAtLeast(0)
        )
    }

    fun streakMilestoneUpdate(
        currentStreakDays: Int,
        previouslyObservedStreakDays: Int,
        previouslyCreditedMilestone: Int
    ): MilestoneUpdate {
        val streakDays = currentStreakDays.coerceAtLeast(0)
        val reachedMilestone = streakDays / STREAK_DAYS_PER_HINT
        if (streakDays < previouslyObservedStreakDays) {
            return MilestoneUpdate(
                creditedMilestone = reachedMilestone,
                hintsToAward = 0
            )
        }

        val newMilestone = maxOf(previouslyCreditedMilestone, reachedMilestone)
        return MilestoneUpdate(
            creditedMilestone = newMilestone,
            hintsToAward = (newMilestone - previouslyCreditedMilestone).coerceAtLeast(0)
        )
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/source/ThemePreferencesDataSource.kt
```kotlin
package com.quickmindgames.sudoku.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Theme Preferences Data Source
 * Manages dark mode preference persistence
 */
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferencesDataSource(private val context: Context) {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    val isDarkMode: Flow<Boolean> = context.themeDataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    companion object {
        @Volatile
        private var instance: ThemePreferencesDataSource? = null

        fun getInstance(context: Context): ThemePreferencesDataSource {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferencesDataSource(context.applicationContext).also { instance = it }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/state/GameStateManager.kt
```kotlin
package com.quickmindgames.sudoku.data.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sudoku_game_state")

/**
 * Manages saving and loading game state using DataStore
 */
class GameStateManager(private val context: Context) {

    private val GAME_STATE_KEY = stringPreferencesKey("current_game_state")

    /**
     * Save the current game state
     */
    suspend fun saveGameState(gameState: GameState) {
        context.dataStore.edit { preferences ->
            preferences[GAME_STATE_KEY] = gameState.serialize()
        }
    }

    /**
     * Get the saved game state as a Flow
     */
    fun getSavedGameState(): Flow<GameState?> {
        return context.dataStore.data.map { preferences ->
            preferences[GAME_STATE_KEY]?.let { serialized ->
                GameState.deserialize(serialized)
            }
        }
    }

    /**
     * Check if there's a saved game
     */
    fun hasSavedGame(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[GAME_STATE_KEY] != null
        }
    }

    /**
     * Clear the saved game state
     */
    suspend fun clearGameState() {
        context.dataStore.edit { preferences ->
            preferences.remove(GAME_STATE_KEY)
        }
    }

    companion object {
        @Volatile
        private var instance: GameStateManager? = null

        fun getInstance(context: Context): GameStateManager {
            return instance ?: synchronized(this) {
                instance ?: GameStateManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

// This file has been moved to data/state/GameStateManager.kt
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/util/GameCompletionRecorder.kt
```kotlin
package com.quickmindgames.sudoku.data.util

import android.content.Context
import com.quickmindgames.sudoku.data.repository.StatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Utility class to record game completions to statistics database
 */
object GameCompletionRecorder {

    /**
     * Record a completed regular game
     */
    fun recordGameCompletion(
        context: Context,
        scope: CoroutineScope,
        difficulty: String,
        isWon: Boolean,
        mistakes: Int,
        score: Int,
        timeSeconds: Int,
        mode: String = "regular"
    ) {
        val key = if (mode == "streak") "streak" else difficulty
        val repository = StatisticsRepository(context)
        scope.launch {
            repository.recordGameCompletion(
                difficulty = key,
                isWon = isWon,
                mistakes = mistakes,
                score = score,
                timeSeconds = timeSeconds
            )
        }
    }

    /**
     * Record a completed streak game
     */
    fun recordStreakGameCompletion(
        context: Context,
        scope: CoroutineScope,
        streakDay: Int,
        isWon: Boolean,
        score: Int,
        timeSeconds: Int
    ) {
        val repository = StatisticsRepository(context)
        scope.launch {
            repository.recordStreakGameCompletion(
                streakDay = streakDay,
                isWon = isWon,
                score = score,
                timeSeconds = timeSeconds
            )
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/generator/SudokuGenerator.kt
```kotlin
package com.quickmindgames.sudoku.domain.generator

import android.util.Log
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.utils.RemoteConfigManager
import kotlin.random.Random

/**
 * Sudoku Generator
 * Generates random valid Sudoku puzzles based on difficulty
 * Created by sagar.tahelyani on 22/02/26
 */
class SudokuGenerator {

    private val size = 9
    private val boxSize = 3
    private val TAG = "SudokuGenerator"

    /**
     * Generate a Sudoku puzzle with solution
     * @param difficulty The difficulty level
     * @return Pair of (puzzle grid, solution grid)
     */
    fun generate(difficulty: Difficulty): Pair<Array<IntArray>, Array<IntArray>> {
        // Generate a complete valid Sudoku grid
        val solution = generateCompleteGrid()

        // Create puzzle by removing numbers based on difficulty
        val puzzle = createPuzzle(solution, difficulty)

        return Pair(puzzle, solution)
    }

    /**
     * Generate a complete valid Sudoku grid
     */
    private fun generateCompleteGrid(): Array<IntArray> {
        val grid = Array(size) { IntArray(size) { 0 } }
        fillGrid(grid)
        return grid
    }

    /**
     * Fill the grid using backtracking
     */
    private fun fillGrid(grid: Array<IntArray>): Boolean {
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (grid[row][col] == 0) {
                    // Create a shuffled list of numbers 1-9
                    val numbers = (1..9).shuffled(Random.Default)

                    for (num in numbers) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num

                            if (fillGrid(grid)) {
                                return true
                            }

                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    /**
     * Check if placing num at position (row, col) is valid
     */
    private fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Check row
        for (c in 0 until size) {
            if (grid[row][c] == num) return false
        }

        // Check column
        for (r in 0 until size) {
            if (grid[r][col] == num) return false
        }

        // Check 3x3 box
        val boxRow = (row / boxSize) * boxSize
        val boxCol = (col / boxSize) * boxSize
        for (r in boxRow until boxRow + boxSize) {
            for (c in boxCol until boxCol + boxSize) {
                if (grid[r][c] == num) return false
            }
        }

        return true
    }

    /**
     * Create a puzzle by removing numbers from the complete grid
     */
    private fun createPuzzle(solution: Array<IntArray>, difficulty: Difficulty): Array<IntArray> {
        val puzzle = solution.map { it.clone() }.toTypedArray()

        // Determine number of clues based on difficulty
        val clues = when (difficulty) {
            Difficulty.Breeze -> {
                val minBreezeClues = RemoteConfigManager.getBreezeMinClues()
                val maxBreezeClues = RemoteConfigManager.getBreezeMaxClues()
                (minBreezeClues..maxBreezeClues).random(Random.Default)
            }

            Difficulty.Pulse -> {
                val minPulseClues = RemoteConfigManager.getPulseMinClues()
                val maxPulseClues = RemoteConfigManager.getPulseMaxClues()
                (minPulseClues..maxPulseClues).random(Random.Default)
            }

            Difficulty.Rage -> {
                val minRageClues = RemoteConfigManager.getRageMinClues()
                val maxRageClues = RemoteConfigManager.getRageMaxClues()
                (minRageClues..maxRageClues).random(Random.Default)
            }

            Difficulty.Elite -> {
                val minEliteClues = RemoteConfigManager.getEliteMinClues()
                val maxEliteClues = RemoteConfigManager.getEliteMaxClues()
                (minEliteClues..maxEliteClues).random(Random.Default)
            }
        }
        Log.d(TAG, "Creating puzzle with difficulty: ${difficulty.label}, clues: $clues")

        // Calculate how many cells to remove
        val cellsToRemove = 81 - clues

        // Get all cell positions
        val positions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                positions.add(Pair(r, c))
            }
        }

        // Shuffle and remove cells
        positions.shuffle(Random.Default)
        for (i in 0 until cellsToRemove) {
            val (r, c) = positions[i]
            puzzle[r][c] = 0
        }

        return puzzle
    }

    companion object {
        /**
         * Singleton instance
         */
        val instance = SudokuGenerator()
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/model/Difficulty.kt
```kotlin
package com.quickmindgames.sudoku.domain.model

enum class Difficulty(val label: String) {
    Breeze("Breeze"), // 46-51 clues
    Pulse("Pulse"), // 32-37 clues
    Rage("Rage"), // 26-31 clues
    Elite("Elite") // 22-26 clues
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/model/GameData.kt
```kotlin
package com.quickmindgames.sudoku.domain.model

import com.quickmindgames.sudoku.data.state.GameState

// GameData is an alias for GameState for domain layer compatibility
typealias GameData = GameState
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/model/Statistics.kt
```kotlin
package com.quickmindgames.sudoku.domain.model

/**
 * Domain model for overall game statistics
 */
data class GameStatistics(
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val winRatioPercentage: Double = 0.0,
    val bestTime: Int? = null,
    val bestScore: Int? = null,
    val flawFreeWins: Int = 0
) {
    val winRatio: String = String.format("%.1f%%", winRatioPercentage)
}

/**
 * Domain model for difficulty-specific statistics
 */
data class DifficultyStatistics(
    val difficulty: String,
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val winRatioPercentage: Double = 0.0,
    val bestTime: Int? = null,
    val bestScore: Int? = null,
    val flawFreeWins: Int = 0
) {
    val winRatio: String = String.format("%.1f%%", winRatioPercentage)
}

/**
 * Domain model for streak statistics
 */
data class StreakStatistics(
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val winRatioPercentage: Double = 0.0,
    val bestTime: Int? = null,
    val bestScore: Int? = null,
    val highestStreakDay: Int = 0,
    val currentConsecutiveWins: Int = 0
) {
    val winRatio: String = String.format("%.1f%%", winRatioPercentage)
}

/**
 * Wrapper for all statistics
 */
data class AllStatistics(
    val overallStats: GameStatistics = GameStatistics(),
    val breeze: DifficultyStatistics = DifficultyStatistics("Breeze"),
    val pulse: DifficultyStatistics = DifficultyStatistics("Pulse"),
    val rage: DifficultyStatistics = DifficultyStatistics("Rage"),
    val elite: DifficultyStatistics = DifficultyStatistics("Elite"),
    val streak: StreakStatistics = StreakStatistics()
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/model/SudokuPuzzle.kt
```kotlin
package com.quickmindgames.sudoku.domain.model

import com.quickmindgames.sudoku.domain.model.Difficulty

data class SudokuPuzzle(
    val originalGrid: List<List<Int>>,
    val solutionGrid: List<List<Int>>,
    val difficulty: Difficulty
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/repository/GameRepository.kt
```kotlin
package com.quickmindgames.sudoku.domain.repository

import com.quickmindgames.sudoku.domain.model.GameData

interface GameRepository {
    suspend fun saveGame(gameData: GameData)
    suspend fun loadGame(): GameData?
    suspend fun deleteGame()
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/repository/ThemeRepository.kt
```kotlin
package com.quickmindgames.sudoku.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val isDarkMode: Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/util/SudokuValidator.kt
```kotlin
package com.quickmindgames.sudoku.domain.util

/**
 * Sudoku Validator
 * Validates moves and puzzle rules
 */

/**
 * Check if a number placement is valid according to Sudoku rules
 * Rules: Number must not appear in same row, column, or 3x3 box
 */
fun isValidMove(grid: List<List<Int>>, row: Int, col: Int, num: Int): Boolean {
    if (num == 0) return true

    // Check row
    for (c in 0 until 9) {
        if (c != col && grid[row][c] == num) return false
    }

    // Check column
    for (r in 0 until 9) {
        if (r != row && grid[r][col] == num) return false
    }

    // Check 3x3 box
    val boxRowStart = (row / 3) * 3
    val boxColStart = (col / 3) * 3

    for (r in boxRowStart until boxRowStart + 3) {
        for (c in boxColStart until boxColStart + 3) {
            if ((r != row || c != col) && grid[r][c] == num) return false
        }
    }

    return true
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/util/UndoManager.kt
```kotlin
package com.quickmindgames.sudoku.domain.util

import com.quickmindgames.sudoku.data.state.CellData
import java.util.Stack

/**
 * Undo/Redo Manager
 * Manages undo and redo operations for game state
 */
val undoStack = Stack<List<List<CellData>>>()
val redoStack = Stack<List<List<CellData>>>()

/**
 * Save current grid state to undo stack
 * Clears redo stack when new move is made
 */
fun saveUndoState(grid: androidx.compose.runtime.snapshots.SnapshotStateList<androidx.compose.runtime.snapshots.SnapshotStateList<CellData>>) {
    undoStack.push(grid.map { it.toList() })
    redoStack.clear()
}

/**
 * Undo last action
 */
fun undo(grid: androidx.compose.runtime.snapshots.SnapshotStateList<androidx.compose.runtime.snapshots.SnapshotStateList<CellData>>) {
    if (undoStack.isNotEmpty()) {
        redoStack.push(grid.map { it.toList() })
        val last = undoStack.pop()
        for (r in 0..8) for (c in 0..8) grid[r][c] = last[r][c]
    }
}

/**
 * Redo last undone action
 */
fun redo(grid: androidx.compose.runtime.snapshots.SnapshotStateList<androidx.compose.runtime.snapshots.SnapshotStateList<CellData>>) {
    if (redoStack.isNotEmpty()) {
        undoStack.push(grid.map { it.toList() })
        val next = redoStack.pop()
        for (r in 0..8) for (c in 0..8) grid[r][c] = next[r][c]
    }
}

/**
 * Clear all undo/redo history
 */
fun clearUndoRedoHistory() {
    undoStack.clear()
    redoStack.clear()
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/theme/Color.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors - Modern Blue/Purple Gradient
val Primary = Color(0xFF6366F1) // Indigo
val PrimaryVariant = Color(0xFF4F46E5)
val Secondary = Color(0xFF8B5CF6) // Purple
val SecondaryVariant = Color(0xFF7C3AED)

// Background & Surface
val Background = Color(0xFFF8FAFC)
val Surface = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFF1F5F9)

// Difficulty Colors
val BreezeColor = Color(0xFF3B82F6) // Blue
val PulseColor = Color(0xFFF59E0B) // Amber
val RageColor = Color(0xFFEF4444) // Red
val EliteColor = Color(0xFF8B5CF6) // Purple

// Game Colors
val SelectedCell = Color(0xFFDDD6FE) // Light Purple
val SameNumberHighlight = Color(0xFFBFDBFE) // Light Blue
val OriginalCell = Color(0xFFF8FAFC) // Very Light Gray
val ErrorCell = Color(0xFFFEE2E2) // Light Red
val CorrectCell = Color(0xFFDCFCE7) // Light Green

// Text Colors
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)
val TextTertiary = Color(0xFF94A3B8)

// Status Colors
val Success = Color(0xFF10B981)
val Error = Color(0xFFEF4444)
val Warning = Color(0xFFF59E0B)
val Info = Color(0xFF3B82F6)
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/theme/Theme.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = SecondaryVariant,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = SecondaryVariant,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Error,
    onError = Color.White
)

@Composable
fun SudoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/util/ShakeModifier.kt
```kotlin
package com.quickmindgames.sudoku.presentation.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shake Effect Modifier
 * Creates a shaking animation for UI feedback on errors
 */
@Composable
fun Modifier.shakeEffect(shouldShake: Boolean): Modifier {
    var shakeOffset by remember(shouldShake) { mutableStateOf(0f) }

    if (shouldShake) {
        shakeOffset = (Math.random() * 20 - 10).toFloat()
    }

    val animatedOffset by animateFloatAsState(
        targetValue = if (shouldShake) shakeOffset else 0f,
        label = "shake"
    )

    return this.offset(x = animatedOffset.dp)
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/viewmodel/HomeViewModel.kt
```kotlin
package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.domain.model.Difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class HomeScreenState(
    val hasSavedGame: Boolean = false,
    val savedGameDifficulty: Difficulty? = null,
    val doublePointsBoosterWallet: Int = 0,
    val streakShieldWallet: Int = 0,
    val streakCount: Int = 0,
    val completedToday: Boolean = false,
    val hasActiveToday: Boolean = false,
    val streakDialog: HomeStreakDialog? = null,
    val totalScore: Int = 0
)

enum class HomeStreakDialog {
    BROKEN,
    REMINDER,
    SHIELD
}

enum class ProtectStreakResult {
    PROTECTED,
    SHIELD_UNAVAILABLE,
    NO_LONGER_ELIGIBLE
}

class HomeViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext
    private val gameStateManager = GameStateManager.getInstance(appContext)
    private val streakStateManager = StreakStateManager.getInstance(appContext)
    private val totalScoreManager = TotalScoreManager.getInstance(appContext)
    private val appPreferences = AppPreferences.getInstance(appContext)
    private val today = LocalDate.now().toString()

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameStateManager.getSavedGameState().collect { savedGame ->
                _uiState.update {
                    it.copy(
                        hasSavedGame = savedGame != null,
                        savedGameDifficulty = savedGame?.difficulty
                    )
                }
            }
        }
        viewModelScope.launch {
            totalScoreManager.totalScore.collect { totalScore ->
                _uiState.update { it.copy(totalScore = totalScore) }
            }
        }
        viewModelScope.launch {
            appPreferences.doublePointsBoosterWallet.collect { wallet ->
                _uiState.update { it.copy(doublePointsBoosterWallet = wallet) }
            }
        }
        viewModelScope.launch {
            combine(
                streakStateManager.getReminderShown(),
                streakStateManager.getStreakState(),
                appPreferences.streakShieldWallet
            ) { reminderShown, savedStreak, shieldWallet ->
                Triple(reminderShown, savedStreak, shieldWallet)
            }.collect { (reminderShown, savedStreak, shieldWallet) ->
                updateStreakState(reminderShown, savedStreak, shieldWallet)
            }
        }
    }

    private suspend fun updateStreakState(
        reminderShown: Boolean,
        savedStreak: StreakState?,
        shieldWallet: Int
    ) {
        val streak = savedStreak ?: StreakState(0, null, null, null)
        var streakDialog = _uiState.value.streakDialog
        var resetBrokenStreak = false

        if (streak.streakCount > 0 && streak.lastCompletedDate != null) {
            val lastCompletedDate = LocalDate.parse(streak.lastCompletedDate)
            val daysDifference = ChronoUnit.DAYS.between(
                lastCompletedDate,
                LocalDate.now()
            ).toInt()

            when {
                daysDifference == 0 || daysDifference == 1 -> {
                    if (streakDialog == HomeStreakDialog.SHIELD) streakDialog = null
                }

                daysDifference == 2 -> {
                    if (!reminderShown) {
                        streakDialog = if (shieldWallet > 0) {
                            HomeStreakDialog.SHIELD
                        } else {
                            HomeStreakDialog.REMINDER
                        }
                    }
                }

                daysDifference > 2 -> {
                    streakDialog = HomeStreakDialog.BROKEN
                    resetBrokenStreak = true
                }
            }
        }

        _uiState.update {
            it.copy(
                streakShieldWallet = shieldWallet,
                streakCount = streak.streakCount,
                completedToday = streak.lastCompletedDate == today,
                hasActiveToday = streak.activeDate == today &&
                    streak.activeGame?.hasStarted() == true,
                streakDialog = streakDialog
            )
        }
        if (resetBrokenStreak) {
            streakStateManager.saveStreakState(StreakState(0, null, null, null))
        }
    }

    fun onStreakReminderDialogShown() {
        viewModelScope.launch {
            streakStateManager.setReminderShown(true)
        }
    }

    fun onStreakShieldDialogShown() {
        viewModelScope.launch {
            streakStateManager.setReminderShown(true)
        }
    }

    suspend fun protectStreak(): ProtectStreakResult {
        val savedStreak = streakStateManager.getStreakState().first()
        val currentDate = LocalDate.now()
        val protectedStreak = savedStreak?.protectMissedDay(currentDate)
        if (protectedStreak == null) {
            _uiState.update { state ->
                val lastCompletedDate = savedStreak?.lastCompletedDate?.let(LocalDate::parse)
                val streakWasBroken = savedStreak != null &&
                    lastCompletedDate != null &&
                    ChronoUnit.DAYS.between(lastCompletedDate, currentDate) > 2
                state.copy(
                    streakDialog = when {
                        streakWasBroken -> HomeStreakDialog.BROKEN
                        (savedStreak?.streakCount ?: 0) > 0 -> HomeStreakDialog.REMINDER
                        else -> null
                    }
                )
            }
            if (
                savedStreak != null &&
                savedStreak.lastCompletedDate?.let(LocalDate::parse)?.let {
                    ChronoUnit.DAYS.between(it, currentDate) > 2
                } == true
            ) {
                streakStateManager.saveStreakState(StreakState(0, null, null, null))
            }
            return ProtectStreakResult.NO_LONGER_ELIGIBLE
        }

        if (!appPreferences.consumeStreakShield()) {
            _uiState.update {
                it.copy(streakDialog = HomeStreakDialog.REMINDER)
            }
            return ProtectStreakResult.SHIELD_UNAVAILABLE
        }

        streakStateManager.saveStreakState(protectedStreak)
        streakStateManager.setReminderShown(false)
        _uiState.update { it.copy(streakDialog = null) }
        return ProtectStreakResult.PROTECTED
    }

    suspend fun restartStreak() {
        streakStateManager.saveStreakState(StreakState(0, null, null, null))
        streakStateManager.setReminderShown(false)
        _uiState.update { it.copy(streakDialog = null) }
    }

    fun dismissStreakDialog() {
        _uiState.update { it.copy(streakDialog = null) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(context)
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/viewmodel/RewardsViewModel.kt
```kotlin
package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.component.PrizeType
import com.quickmindgames.sudoku.component.PrizeWheelPrize
import com.quickmindgames.sudoku.component.PrizeWheelPrizes
import com.quickmindgames.sudoku.component.weightedPrizeIndex
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class RewardsScreenState(
    val totalPoints: Int = 0,
    val hintWallet: Int = 0,
    val doublePointsBoosterWallet: Int = 0,
    val streakShieldWallet: Int = 0,
    val initialPointHints: Int = 0,
    val initialStreakHints: Int = 0,
    val soundAndVibrationEnabled: Boolean = true,
    val lastSpinMillis: Long? = Long.MIN_VALUE,
    val showMigrationSummary: Boolean = false,
    val isSpinning: Boolean = false
)

data class RewardSpin(
    val prize: PrizeWheelPrize,
    val prizeIndex: Int,
    val spinId: String
)

enum class PrizeCreditResult {
    POINTS,
    HINTS_CREDITED,
    BOOSTER_CREDITED,
    STREAK_SHIELD_CREDITED,
    ALREADY_CREDITED
}

class RewardsViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext
    private val totalScoreManager = TotalScoreManager.getInstance(appContext)
    private val appPreferences = AppPreferences.getInstance(appContext)

    private val _uiState = MutableStateFlow(RewardsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            totalScoreManager.totalScore.collect { points ->
                _uiState.update { it.copy(totalPoints = points) }
            }
        }
        viewModelScope.launch {
            appPreferences.hintWallet.collect { balance ->
                _uiState.update { it.copy(hintWallet = balance) }
            }
        }
        viewModelScope.launch {
            appPreferences.doublePointsBoosterWallet.collect { balance ->
                _uiState.update { it.copy(doublePointsBoosterWallet = balance) }
            }
        }
        viewModelScope.launch {
            appPreferences.streakShieldWallet.collect { balance ->
                _uiState.update { it.copy(streakShieldWallet = balance) }
            }
        }
        viewModelScope.launch {
            appPreferences.initialPointHints.collect { count ->
                _uiState.update { it.copy(initialPointHints = count) }
            }
        }
        viewModelScope.launch {
            appPreferences.initialStreakHints.collect { count ->
                _uiState.update { it.copy(initialStreakHints = count) }
            }
        }
        viewModelScope.launch {
            appPreferences.soundAndVibration.collect { enabled ->
                _uiState.update { it.copy(soundAndVibrationEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            appPreferences.lastSpin.collect { timestamp ->
                _uiState.update { it.copy(lastSpinMillis = timestamp) }
            }
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(showMigrationSummary = appPreferences.claimHintMigrationSummaryOnce())
            }
        }
    }

    suspend fun beginSpin(): RewardSpin? {
        if (_uiState.value.isSpinning) return null
        val lastSpin = _uiState.value.lastSpinMillis
        val now = System.currentTimeMillis()
        if (
            lastSpin == Long.MIN_VALUE ||
            lastSpin != null &&
            now < lastSpin + AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS
        ) return null

        val prizeIndex = weightedPrizeIndex(PrizeWheelPrizes, Random)
        val prize = PrizeWheelPrizes[prizeIndex]
        val spinId = now.toString()
        _uiState.update { it.copy(isSpinning = true, lastSpinMillis = now) }
        try {
            appPreferences.setLastSpin(now)
        } catch (exception: Exception) {
            _uiState.update { it.copy(isSpinning = false) }
            throw exception
        }
        return RewardSpin(prize, prizeIndex, spinId)
    }

    suspend fun creditPrize(spin: RewardSpin): PrizeCreditResult =
        when (spin.prize.type) {
            PrizeType.POINTS -> {
                totalScoreManager.addScore(spin.prize.amount)
                PrizeCreditResult.POINTS
            }

            PrizeType.HINTS -> if (
                appPreferences.addWheelHintOnce(spin.spinId, spin.prize.amount)
            ) {
                PrizeCreditResult.HINTS_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }

            PrizeType.BOOSTER -> if (
                appPreferences.addWheelDoublePointsBoosterOnce(spin.spinId, spin.prize.amount)
            ) {
                PrizeCreditResult.BOOSTER_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }

            PrizeType.STREAK_SHIELD -> if (
                appPreferences.addWheelStreakShieldOnce(spin.spinId, spin.prize.amount)
            ) {
                PrizeCreditResult.STREAK_SHIELD_CREDITED
            } else {
                PrizeCreditResult.ALREADY_CREDITED
            }
        }

    fun finishSpin() {
        _uiState.update { it.copy(isSpinning = false) }
    }

    fun dismissMigrationSummary() {
        _uiState.update { it.copy(showMigrationSummary = false) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RewardsViewModel(context)
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/viewmodel/SettingsViewModel.kt
```kotlin
package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.preferences.ThemePreferences
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsScreenState(
    val isDarkMode: Boolean = false,
    val hideUsedNumbers: Boolean = false,
    val highlightRegion: Boolean = false,
    val highlightSameNumbers: Boolean = false,
    val freePlay: Boolean = false,
    val soundAndVibration: Boolean = true
)

data class FreePlayChangeResult(
    val blockedBySavedGame: Boolean = false,
    val disabledHideUsedNumbers: Boolean = false
)

class SettingsViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext
    private val themePreferences = ThemePreferences.getInstance(appContext)
    private val appPreferences = AppPreferences.getInstance(appContext)
    private val gameStateManager = GameStateManager.getInstance(appContext)

    private val _uiState = MutableStateFlow(SettingsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appPreferences.normalizeGameplayOptions()
            combine(
                appPreferences.hideUsedNumbers,
                appPreferences.freePlay
            ) { hide, free ->
                hide to free
            }.collect { (hide, free) ->
                _uiState.update {
                    it.copy(hideUsedNumbers = hide, freePlay = free)
                }
            }
        }
        viewModelScope.launch {
            themePreferences.isDarkMode.collect { darkMode ->
                _uiState.update { it.copy(isDarkMode = darkMode) }
            }
        }
        viewModelScope.launch {
            appPreferences.highlightRegion.collect { enabled ->
                _uiState.update { it.copy(highlightRegion = enabled) }
            }
        }
        viewModelScope.launch {
            appPreferences.highlightSameNumbers.collect { enabled ->
                _uiState.update { it.copy(highlightSameNumbers = enabled) }
            }
        }
        viewModelScope.launch {
            appPreferences.soundAndVibration.collect { enabled ->
                _uiState.update { it.copy(soundAndVibration = enabled) }
            }
        }
    }

    suspend fun setHideUsedNumbers(enabled: Boolean): Boolean {
        val disabledFreePlay = appPreferences.setHideUsedNumbers(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.HIDE_USED_NUMBERS,
            enabled
        )
        if (disabledFreePlay) {
            AnalyticsUtils.logSettingsOptionChanged(
                appContext,
                AnalyticsConstants.FREE_PLAY,
                false
            )
        }
        return disabledFreePlay
    }

    suspend fun setHighlightSameNumbers(enabled: Boolean) {
        appPreferences.setHighlightSameNumbers(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.HIGHLIGHT_SAME_NUMBERS,
            enabled
        )
    }

    suspend fun setHighlightRegion(enabled: Boolean) {
        appPreferences.setHighlightRegion(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.HIGHLIGHT_REGION,
            enabled
        )
    }

    suspend fun setFreePlay(enabled: Boolean): FreePlayChangeResult {
        if (gameStateManager.getSavedGameState().first() != null) {
            return FreePlayChangeResult(blockedBySavedGame = true)
        }

        val disabledHideUsedNumbers = appPreferences.setFreePlay(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.FREE_PLAY,
            enabled
        )
        if (disabledHideUsedNumbers) {
            AnalyticsUtils.logSettingsOptionChanged(
                appContext,
                AnalyticsConstants.HIDE_USED_NUMBERS,
                false
            )
        }
        return FreePlayChangeResult(disabledHideUsedNumbers = disabledHideUsedNumbers)
    }

    suspend fun setSoundAndVibration(enabled: Boolean) {
        appPreferences.setSoundAndVibration(enabled)
        AnalyticsUtils.logSettingsOptionChanged(
            appContext,
            AnalyticsConstants.SOUND_VIBRATIONS,
            enabled
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        themePreferences.setDarkMode(enabled)
        val themeName = if (enabled) "dark_theme" else "light_theme"
        AnalyticsUtils.logThemeToggled(appContext, themeName)
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(context)
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/viewmodel/StreakViewModel.kt
```kotlin
package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StreakScreenState(
    val streakCount: Int = 0,
    val activeDate: String? = null,
    val activeGameStarted: Boolean = false,
    val lastCompletedDate: String? = null
)

class StreakViewModel(context: Context) : ViewModel() {
    private val streakStateManager =
        StreakStateManager.getInstance(context.applicationContext)

    private val _uiState = MutableStateFlow(StreakScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            streakStateManager.getStreakState().collect { savedState ->
                val state = savedState ?: StreakState(0, null, null, null)
                _uiState.value = StreakScreenState(
                    streakCount = state.streakCount,
                    activeDate = state.activeDate,
                    activeGameStarted = state.activeGame?.hasStarted() == true,
                    lastCompletedDate = state.lastCompletedDate
                )
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StreakViewModel(context)
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/viewmodel/SudokuGameViewModel.kt
```kotlin
package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.quickmindgames.sudoku.BuildConfig
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.data.state.GameState
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.data.util.GameCompletionRecorder
import com.quickmindgames.sudoku.domain.generator.SudokuGenerator
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.isWin
import com.quickmindgames.sudoku.utils.recomputeWrongCells
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SudokuTimerState(
    val elapsedSeconds: Int = 0,
    val isRunning: Boolean = true
)

data class SudokuPreferencesState(
    val hideUsedNumbers: Boolean = false,
    val highlightRegion: Boolean = false,
    val highlightSameNumbers: Boolean = false,
    val freePlay: Boolean = false,
    val soundAndVibration: Boolean = true,
    val hintWallet: Int = 0
)

data class SudokuMoveResult(
    val isCorrect: Boolean,
    val gameOver: Boolean = false,
    val gameWon: Boolean = false,
    val learningHint: String? = null
)

enum class HintResult {
    APPLIED,
    EMPTY_WALLET,
    CELL_NOT_SELECTED,
    CELL_ALREADY_CORRECT,
    GAME_WON
}

class SudokuGameViewModel(
    context: Context,
    private val appPreferences: AppPreferences,
    private val mode: String,
    difficulty: Difficulty?,
    private val streakDay: Int,
    useDoublePointsBooster: Boolean
) : ViewModel() {
    private val appContext = context.applicationContext
    private val gameStateManager = GameStateManager.getInstance(appContext)
    private val streakStateManager = StreakStateManager.getInstance(appContext)
    private val totalScoreManager = TotalScoreManager.getInstance(appContext)

    private val generatedPuzzle =
        SudokuGenerator.instance.generate(difficulty ?: Difficulty.Breeze)
    val puzzleData = mutableStateOf(generatedPuzzle)
    val originalGrid: MutableState<List<List<Int>>> =
        mutableStateOf(generatedPuzzle.first.map { it.toList() })
    val solutionGrid = mutableStateOf(generatedPuzzle.second)
    val userGrid = mutableStateListOf<SnapshotStateList<CellData>>()
    val gameOver = mutableStateOf(false)
    val wrongCells = mutableStateOf(setOf<Pair<Int, Int>>())
    val shakeCells = mutableStateOf(setOf<Pair<Int, Int>>())
    val gameWon = mutableStateOf(false)
    val selectedCell = mutableStateOf<Pair<Int, Int>?>(null)
    val mistakes = mutableIntStateOf(0)
    val score = mutableIntStateOf(0)
    val hasDoublePointsBooster = mutableStateOf(useDoublePointsBooster)
    val currentDifficulty = mutableStateOf(difficulty ?: Difficulty.Breeze)
    val correctStreak = mutableIntStateOf(0)
    val lastCorrectTime = mutableIntStateOf(0)
    val scoredCells = Array(9) {
        mutableStateListOf(*BooleanArray(9) { false }.toTypedArray())
    }
    val isNotesMode = mutableStateOf(false)
    val hintsUsed = mutableIntStateOf(0)
    val learningHint = mutableStateOf<String?>(null)
    val isHintProcessing = mutableStateOf(false)
    val canUndo = mutableStateOf(false)
    private val undoHistory = mutableListOf<List<List<CellData>>>()

    private val _timerState = MutableStateFlow(SudokuTimerState())
    val timerState = _timerState.asStateFlow()
    private var timerJob: Job? = null
    private var initialized = false
    private var initializationStarted = false
    private var winPersisted = false

    private val gameplayPreferences = combine(
        appPreferences.hideUsedNumbers,
        appPreferences.highlightRegion,
        appPreferences.highlightSameNumbers,
        appPreferences.freePlay,
        appPreferences.soundAndVibration
    ) { hideUsedNumbers, highlightRegion, highlightSameNumbers, freePlay, soundAndVibration ->
        SudokuPreferencesState(
            hideUsedNumbers = hideUsedNumbers,
            highlightRegion = highlightRegion,
            highlightSameNumbers = highlightSameNumbers,
            freePlay = freePlay,
            soundAndVibration = soundAndVibration
        )
    }

    val preferences = combine(
        gameplayPreferences,
        appPreferences.hintWallet
    ) { gameplay, hintWallet ->
        gameplay.copy(hintWallet = hintWallet)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SudokuPreferencesState()
    )

    init {
        replaceUserGrid(originalGrid.value)
    }

    suspend fun initialize(): Boolean {
        if (initialized || initializationStarted) return false
        initializationStarted = true
        try {
            val shouldExit = when (mode) {
                "learn" -> {
                    currentDifficulty.value = Difficulty.Breeze
                    resetGameState(startTimer = true)
                    AnalyticsUtils.logLearningStarted(appContext)
                    false
                }

                "new" -> {
                    val savedDifficulty = gameStateManager.getSavedGameState().first()?.difficulty
                    resetGameState(startTimer = true)
                    gameStateManager.clearGameState()
                    AnalyticsUtils.logNewGameStarted(
                        appContext,
                        currentDifficulty.value.name,
                        savedDifficulty?.name
                    )
                    false
                }

                "resume" -> {
                    restoreRegularGame()
                    false
                }

                "streak" -> initializeStreakGame()
                else -> false
            }
            initialized = true
            return shouldExit
        } finally {
            initializationStarted = false
        }
    }

    private suspend fun restoreRegularGame() {
        val savedState = gameStateManager.getSavedGameState().first()
        if (savedState == null) {
            startTimer()
            return
        }
        savedState.let { state ->
            AnalyticsUtils.logResumeGame(appContext, state.difficulty.name)
            currentDifficulty.value = state.difficulty
            originalGrid.value = state.originalGrid
            solutionGrid.value = state.solutionGrid.map { it.toIntArray() }.toTypedArray()
            puzzleData.value = Pair(
                state.originalGrid.map { it.toIntArray() }.toTypedArray(),
                solutionGrid.value
            )
            restoreUserGrid(state.userGrid)
            wrongCells.value = state.wrongCells
            mistakes.intValue = state.mistakes
            score.intValue = state.score
            startTimer(state.timeSeconds)
            selectedCell.value = state.selectedCell
            isNotesMode.value = state.isNotesMode
            hintsUsed.intValue = state.hintsUsed
            hasDoublePointsBooster.value = state.hasDoublePointsBooster
            gameOver.value = false
            gameWon.value = false
        }
    }

    private suspend fun initializeStreakGame(): Boolean {
        val today = LocalDate.now().toString()
        val savedStreak = streakStateManager.getStreakState().first()
            ?: StreakState(0, null, null, null)
        val activeGame = savedStreak.activeGame
        if (activeGame != null && savedStreak.activeDate == today) {
            currentDifficulty.value = activeGame.difficulty
            originalGrid.value = activeGame.originalGrid
            solutionGrid.value = activeGame.solutionGrid.map { it.toIntArray() }.toTypedArray()
            restoreUserGrid(activeGame.userGrid)
            wrongCells.value = activeGame.wrongCells
            mistakes.intValue = activeGame.mistakes
            score.intValue = activeGame.score
            startTimer(activeGame.timeSeconds)
            selectedCell.value = activeGame.selectedCell
            hintsUsed.intValue = activeGame.hintsUsed
            gameOver.value = false
            gameWon.value = false
        } else if (savedStreak.lastCompletedDate == today && !BuildConfig.DEBUG) {
            return true
        } else {
            AnalyticsUtils.logStreakGameStarted(appContext)
            val (puzzle, solution) = SudokuGenerator.instance.generate(Difficulty.Breeze)
            puzzleData.value = Pair(puzzle, solution)
            originalGrid.value = puzzle.map { it.toList() }
            solutionGrid.value = solution
            currentDifficulty.value = Difficulty.Breeze
            resetGameState(startTimer = true)
            saveCurrentGame()
            streakStateManager.saveStreakState(
                savedStreak.copy(
                    activeDate = today,
                    activeGame = currentGameState()
                )
            )
        }
        return false
    }

    suspend fun saveCurrentGame() {
        if (!initialized || mode == "learn" || gameOver.value || gameWon.value) return
        val state = currentGameState()
        if (!state.hasStarted() && hintsUsed.intValue == 0 &&
            !hasDoublePointsBooster.value
        ) return
        if (mode == "streak") {
            val today = LocalDate.now().toString()
            val savedStreak = streakStateManager.getStreakState().first()
                ?: StreakState(0, null, null, null)
            streakStateManager.saveStreakState(
                savedStreak.copy(activeDate = today, activeGame = state)
            )
        } else {
            gameStateManager.saveGameState(state)
        }
    }

    fun currentGameState() = GameState(
        difficulty = currentDifficulty.value,
        originalGrid = originalGrid.value,
        solutionGrid = solutionGrid.value.map { it.toList() },
        userGrid = userGrid.map { it.toList() },
        wrongCells = wrongCells.value,
        mistakes = mistakes.intValue,
        score = score.intValue,
        timeSeconds = _timerState.value.elapsedSeconds,
        selectedCell = selectedCell.value,
        isNotesMode = isNotesMode.value,
        hintsUsed = hintsUsed.intValue,
        hasDoublePointsBooster = hasDoublePointsBooster.value
    )

    fun resetGameState(startTimer: Boolean) {
        replaceUserGrid(originalGrid.value)
        undoHistory.clear()
        canUndo.value = false
        wrongCells.value = emptySet()
        mistakes.intValue = 0
        score.intValue = 0
        startTimer(shouldCount = startTimer)
        gameOver.value = false
        gameWon.value = false
        selectedCell.value = null
        shakeCells.value = emptySet()
        learningHint.value = null
        correctStreak.intValue = 0
        lastCorrectTime.intValue = 0
        resetScoredCells()
        hintsUsed.intValue = 0
        winPersisted = false
    }

    fun createNextGame() {
        val (puzzle, solution) = SudokuGenerator.instance.generate(currentDifficulty.value)
        puzzleData.value = Pair(puzzle, solution)
        originalGrid.value = puzzle.map { it.toList() }
        solutionGrid.value = solution
        hasDoublePointsBooster.value = false
        resetGameState(startTimer = true)
    }

    suspend fun retryGame() {
        resetGameState(startTimer = true)
        if (mode != "streak") gameStateManager.clearGameState()
    }

    fun onPreferencesChanged(freePlay: Boolean) {
        if (freePlay && mode != "streak") {
            mistakes.intValue = 0
            score.intValue = 0
        }
    }

    fun clearShakeCells() {
        shakeCells.value = emptySet()
    }

    fun clearLearningHint() {
        learningHint.value = null
    }

    suspend fun processHint(isLearningMode: Boolean, freePlay: Boolean): HintResult {
        isHintProcessing.value = true
        return try {
            useHint(isLearningMode, freePlay)
        } finally {
            isHintProcessing.value = false
        }
    }

    fun pointsToAward(freePlay: Boolean): Int =
        if (hasDoublePointsBooster.value && !freePlay) {
            (score.intValue.toLong() * 2L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        } else {
            score.intValue
        }

    fun dismissGameOver() {
        gameOver.value = false
    }

    fun dismissGameWon() {
        gameWon.value = false
    }

    fun selectCell(row: Int, column: Int) {
        if (_timerState.value.isRunning) selectedCell.value = row to column
    }

    fun toggleNotes() {
        isNotesMode.value = !isNotesMode.value
        AnalyticsUtils.logNotesToggled(appContext, isNotesMode.value)
    }

    fun eraseSelectedCell() {
        selectedCell.value?.let { (row, column) ->
            if (originalGrid.value[row][column] == 0) {
                saveUndoSnapshot()
                if (userGrid[row][column].notes.isNotEmpty()) {
                    userGrid[row][column] =
                        userGrid[row][column].copy(notes = emptySet())
                } else {
                    userGrid[row][column] = userGrid[row][column].copy(value = 0)
                    wrongCells.value = wrongCells.value - (row to column)
                }
            }
        }
        AnalyticsUtils.logEraseUsed(appContext)
    }

    fun undoMove() {
        val previous = undoHistory.removeLastOrNull() ?: return
        for (row in 0..8) for (column in 0..8) {
            userGrid[row][column] = previous[row][column]
        }
        canUndo.value = undoHistory.isNotEmpty()
        wrongCells.value = recomputeWrongCells(userGrid, solutionGrid.value)
        AnalyticsUtils.logUndoUsed(appContext)
    }

    fun enterNumber(number: Int, isLearningMode: Boolean, freePlay: Boolean): SudokuMoveResult? {
        val cellPosition = selectedCell.value ?: return null
        val (row, column) = cellPosition
        if (originalGrid.value[row][column] != 0 || gameOver.value ||
            gameWon.value || !_timerState.value.isRunning
        ) return null

        val cell = userGrid[row][column]
        val solution = solutionGrid.value
        if (isNotesMode.value) {
            val isWrong = cell.value != 0 && cell.value != solution[row][column]
            if (cell.value == 0 || isWrong) {
                val notes = cell.notes.toMutableSet()
                if (number in notes) notes.remove(number)
                else if (notes.size < 4) notes.add(number)
                saveUndoSnapshot()
                userGrid[row][column] = cell.copy(
                    value = 0,
                    notes = notes
                )
            }
            return null
        }

        if (cell.value != 0 && cell.value == solution[row][column]) return null
        val isCorrect = number == solution[row][column]
        if (!isLearningMode || isCorrect) {
            saveUndoSnapshot()
            userGrid[row][column] = cell.copy(
                value = number,
                notes = emptySet()
            )
            updateNotesAfterValueChange(userGrid, row, column, number)
        } else {
            saveUndoSnapshot()
        }

        var learningExplanation: String? = null
        if (!isCorrect) {
            if (isLearningMode) {
                shakeCells.value = shakeCells.value + cellPosition
                learningExplanation = learningHintFor(row, column, number)
                learningHint.value = learningExplanation
            } else if (freePlay) {
                wrongCells.value = wrongCells.value + cellPosition
                shakeCells.value = shakeCells.value + cellPosition
            } else {
                mistakes.intValue++
                wrongCells.value = wrongCells.value + cellPosition
                shakeCells.value = shakeCells.value + cellPosition
                correctStreak.intValue = 0
                if (mistakes.intValue >= 3) {
                    gameOver.value = true
                    pauseTimer()
                    recordCompletion(isWon = false)
                }
            }
        } else {
            wrongCells.value = wrongCells.value - cellPosition
            if (!isLearningMode && !freePlay && !scoredCells[row][column]) {
                val basePoints = when (currentDifficulty.value) {
                    Difficulty.Breeze -> 1
                    Difficulty.Pulse -> 2
                    Difficulty.Rage -> 3
                    Difficulty.Elite -> 4
                }
                val secondsSinceLast = _timerState.value.elapsedSeconds - lastCorrectTime.intValue
                val speedMultiplier = when {
                    secondsSinceLast <= 5 -> 2.0f
                    secondsSinceLast <= 10 -> 1.5f
                    secondsSinceLast <= 20 -> 1.25f
                    else -> 1.0f
                }
                correctStreak.intValue++
                val streakMultiplier = when {
                    correctStreak.intValue >= 5 -> 2.0f
                    correctStreak.intValue >= 3 -> 1.5f
                    else -> 1.0f
                }
                val mistakePenalty = 1f - (mistakes.intValue * 0.10f)
                score.intValue += (basePoints * speedMultiplier * streakMultiplier * mistakePenalty)
                    .toInt()
                    .coerceAtLeast(1)
                scoredCells[row][column] = true
                lastCorrectTime.intValue = _timerState.value.elapsedSeconds
            }
        }

        val didWin = isWin(userGrid, solution)
        if (didWin) {
            gameWon.value = true
            pauseTimer()
            winPersisted = false
            if (!isLearningMode) recordCompletion(isWon = true)
        }
        return SudokuMoveResult(
            isCorrect = isCorrect,
            gameOver = gameOver.value,
            gameWon = didWin,
            learningHint = learningExplanation
        )
    }

    suspend fun useHint(isLearningMode: Boolean, freePlay: Boolean): HintResult {
        if (isLearningMode || gameOver.value || gameWon.value || !_timerState.value.isRunning) {
            return HintResult.CELL_ALREADY_CORRECT
        }
        val position = selectedCell.value ?: return HintResult.CELL_NOT_SELECTED
        val (row, column) = position
        val cell = userGrid[row][column]
        if (cell.value != 0 && cell.value == solutionGrid.value[row][column]) {
            return HintResult.CELL_ALREADY_CORRECT
        }
        if (!appPreferences.consumeHint()) return HintResult.EMPTY_WALLET
        saveUndoSnapshot()
        val value = solutionGrid.value[row][column]
        userGrid[row][column] = cell.copy(value = value, notes = emptySet())
        updateNotesAfterValueChange(userGrid, row, column, value)
        hintsUsed.intValue++
        AnalyticsUtils.logHintUsed(appContext, currentDifficulty.value.name)

        if (isWin(userGrid, solutionGrid.value)) {
            gameWon.value = true
            pauseTimer()
            recordCompletion(isWon = true, useHintScore = true)
            return HintResult.GAME_WON
        }
        return HintResult.APPLIED
    }

    suspend fun persistWin(pointsToAward: Int, freePlay: Boolean) {
        if (winPersisted || mode == "learn") return
        winPersisted = true
        if (!freePlay) {
            if (hasDoublePointsBooster.value) appPreferences.consumeDoublePointsBooster()
            totalScoreManager.addScore(pointsToAward)
        }
        if (mode == "streak") {
            val today = LocalDate.now().toString()
            val savedStreak = streakStateManager.getStreakState().first()
                ?: StreakState(0, null, null, null)
            if (streakDay > savedStreak.streakCount) {
                streakStateManager.saveStreakState(
                    savedStreak.copy(
                        streakCount = savedStreak.streakCount + 1,
                        lastCompletedDate = today,
                        activeDate = null,
                        activeGame = null
                    )
                )
            } else {
                streakStateManager.saveStreakState(
                    savedStreak.copy(activeDate = null, activeGame = null)
                )
            }
            AnalyticsUtils.logStreakGameCompleted(appContext, streakDay)
            streakStateManager.setReminderShown(false)
        } else {
            gameStateManager.clearGameState()
        }
    }

    fun startTimer(elapsedSeconds: Int = 0, shouldCount: Boolean = true) {
        timerJob?.cancel()
        _timerState.value = SudokuTimerState(
            elapsedSeconds = elapsedSeconds,
            isRunning = shouldCount
        )
        if (shouldCount && mode != "learn") runTimer()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resumeTimer(shouldCount: Boolean = true) {
        if (_timerState.value.isRunning) return
        _timerState.value = _timerState.value.copy(isRunning = true)
        if (shouldCount && mode != "learn") runTimer()
    }

    fun toggleTimer(shouldCount: Boolean = true) {
        if (_timerState.value.isRunning) pauseTimer() else resumeTimer(shouldCount)
    }

    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning) {
                delay(1_000)
                if (_timerState.value.isRunning) {
                    _timerState.value = _timerState.value.copy(
                        elapsedSeconds = _timerState.value.elapsedSeconds + 1
                    )
                }
            }
        }
    }

    private fun recordCompletion(isWon: Boolean, useHintScore: Boolean = false) {
        val elapsed = _timerState.value.elapsedSeconds
        if (mode == "streak") {
            GameCompletionRecorder.recordStreakGameCompletion(
                context = appContext,
                scope = viewModelScope,
                streakDay = streakDay,
                isWon = isWon,
                score = score.intValue,
                timeSeconds = elapsed
            )
            GameCompletionRecorder.recordGameCompletion(
                context = appContext,
                scope = viewModelScope,
                difficulty = "streak",
                isWon = isWon,
                mistakes = mistakes.intValue,
                score = score.intValue,
                timeSeconds = elapsed,
                mode = "regular"
            )
            if (!isWon) {
                viewModelScope.launch {
                    val saved = streakStateManager.getStreakState().first()
                        ?: StreakState(0, null, null, null)
                    streakStateManager.saveStreakState(
                        saved.copy(activeDate = null, activeGame = null)
                    )
                }
            }
        } else {
            val resultScore = if (isWon && !useHintScore &&
                hasDoublePointsBooster.value && preferences.value.freePlay.not()
            ) {
                (score.intValue.toLong() * 2L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            } else score.intValue
            GameCompletionRecorder.recordGameCompletion(
                context = appContext,
                scope = viewModelScope,
                difficulty = currentDifficulty.value.name,
                isWon = isWon,
                mistakes = mistakes.intValue,
                score = resultScore,
                timeSeconds = elapsed,
                mode = "regular"
            )
        }
        if (!isWon) viewModelScope.launch { gameStateManager.clearGameState() }
    }

    private fun replaceUserGrid(grid: List<List<Int>>) {
        userGrid.clear()
        grid.forEach { row ->
            userGrid.add(mutableStateListOf(*row.map(::CellData).toTypedArray()))
        }
    }

    private fun saveUndoSnapshot() {
        undoHistory.add(userGrid.map { row -> row.toList() })
        canUndo.value = true
    }

    private fun restoreUserGrid(grid: List<List<CellData>>) {
        userGrid.clear()
        grid.forEach { row ->
            userGrid.add(mutableStateListOf(*row.toTypedArray()))
        }
    }

    private fun resetScoredCells() {
        for (row in 0..8) for (column in 0..8) scoredCells[row][column] = false
    }

    private fun learningHintFor(row: Int, column: Int, number: Int): String {
        val rowConflict = (0 until 9).any { col ->
            col != column && userGrid[row][col].value == number
        }
        val columnConflict = (0 until 9).any { otherRow ->
            otherRow != row && userGrid[otherRow][column].value == number
        }
        val boxRow = row / 3 * 3
        val boxColumn = column / 3 * 3
        val boxConflict = (boxRow until boxRow + 3).any { otherRow ->
            (boxColumn until boxColumn + 3).any { otherColumn ->
                (otherRow != row || otherColumn != column) &&
                    userGrid[otherRow][otherColumn].value == number
            }
        }
        return when {
            rowConflict -> "Number already exists in this row"
            columnConflict -> "Number already exists in this column"
            boxConflict -> "Number already exists in this 3×3 box"
            else -> "That's not the correct number for this cell"
        }
    }

    private fun updateNotesAfterValueChange(
        grid: SnapshotStateList<SnapshotStateList<CellData>>,
        row: Int,
        column: Int,
        value: Int
    ) {
        for (index in 0..8) {
            if (grid[row][index].value == 0) {
                grid[row][index] = grid[row][index].copy(
                    notes = grid[row][index].notes - value
                )
            }
            if (grid[index][column].value == 0) {
                grid[index][column] = grid[index][column].copy(
                    notes = grid[index][column].notes - value
                )
            }
        }
        val boxRow = row / 3 * 3
        val boxColumn = column / 3 * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxColumn until boxColumn + 3) {
                if (grid[r][c].value == 0) {
                    grid[r][c] = grid[r][c].copy(notes = grid[r][c].notes - value)
                }
            }
        }
    }

    companion object {
        fun factory(
            context: Context,
            appPreferences: AppPreferences,
            mode: String,
            difficulty: Difficulty?,
            streakDay: Int,
            useDoublePointsBooster: Boolean
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SudokuGameViewModel(
                    context = context,
                    appPreferences = appPreferences,
                    mode = mode,
                    difficulty = difficulty,
                    streakDay = streakDay,
                    useDoublePointsBooster = useDoublePointsBooster
                )
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/MultipleEventsCutter.kt
```kotlin
package com.quickmindgames.sudoku.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class MultipleEventsCutter private constructor() {
    private var lastClickTime = 0L

    fun processEvent(delayMillis: Long = 500L, event: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= delayMillis) {
            lastClickTime = currentTime
            event()
        }
    }

    companion object {
        @Composable
        fun rememberMultipleEventsCutter(): MultipleEventsCutter {
            return remember { MultipleEventsCutter() }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/StreakReminderScheduler.kt
```kotlin
package com.quickmindgames.sudoku.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object StreakReminderScheduler {
    fun schedule(context: Context) {
        val now = LocalDateTime.now()
        val targetTime = now.with(LocalTime.of(22, 0)) // 10pm
        val initialDelay = if (now.isBefore(targetTime)) {
            Duration.between(now, targetTime)
        } else {
            Duration.between(now, targetTime.plusDays(1))
        }
        val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "streak_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/StreakReminderWorker.kt
```kotlin
package com.quickmindgames.sudoku.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.presentation.ui.MainActivity
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class StreakReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val streakStateManager = StreakStateManager.getInstance(applicationContext)
        val streakState = streakStateManager.getStreakState().first()
        val today = LocalDate.now().toString()
        if (streakState?.lastCompletedDate != today) {
            sendNotification()
        }
        return Result.success()
    }

    private fun sendNotification() {
        val channelId = "streak_reminder"
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Streak Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
        // Android 13+ (API 33): Check POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, do not post notification
                return
            }
        }
        // --- Add PendingIntent for notification click ---
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("from_streak_notification", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Use app launcher icon
            .setContentTitle("Don't lose your streak!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Hey, you haven't played streak for today, play now otherwise your streak will be lost.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(1001, notification)
        AnalyticsUtils.logNotificationReceived(applicationContext)
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/SudokuRules.kt
```kotlin
package com.quickmindgames.sudoku.utils

import com.quickmindgames.sudoku.data.state.CellData

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
fun isWin(userGrid: List<List<CellData>>, solution: Array<IntArray>): Boolean {
    for (r in 0..8) {
        for (c in 0..8) {
            if (userGrid[r][c].value != solution[r][c]) {
                return false
            }
        }
    }
    return true
}

fun recomputeWrongCells(
    grid: List<List<CellData>>,
    solution: Array<IntArray>
): Set<Pair<Int, Int>> {
    val wrong = mutableSetOf<Pair<Int, Int>>()
    for (r in 0..8) {
        for (c in 0..8) {
            val v = grid[r][c].value
            if (v != 0 && v != solution[r][c]) {
                wrong.add(r to c)
            }
        }
    }
    return wrong
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/TimeFormatters.kt
```kotlin
package com.quickmindgames.sudoku.utils

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */

fun formatTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
}

fun ordinalSuffix(n: Int): String {
    val suffix = when {
        n % 100 in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
}
```

## File: app/src/main/res/drawable/ic_launcher_background.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    android:height="108dp"
    android:width="108dp"
    android:viewportHeight="108"
    android:viewportWidth="108"
    xmlns:android="http://schemas.android.com/apk/res/android">
    <path android:fillColor="#3DDC84"
          android:pathData="M0,0h108v108h-108z"/>
    <path android:fillColor="#00000000" android:pathData="M9,0L9,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M19,0L19,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M29,0L29,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M39,0L39,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M49,0L49,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M59,0L59,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M69,0L69,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M79,0L79,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M89,0L89,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M99,0L99,108"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,9L108,9"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,19L108,19"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,29L108,29"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,39L108,39"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,49L108,49"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,59L108,59"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,69L108,69"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,79L108,79"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,89L108,89"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M0,99L108,99"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M19,29L89,29"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M19,39L89,39"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M19,49L89,49"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M19,59L89,59"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M19,69L89,69"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M19,79L89,79"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M29,19L29,89"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M39,19L39,89"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M49,19L49,89"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M59,19L59,89"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M69,19L69,89"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
    <path android:fillColor="#00000000" android:pathData="M79,19L79,89"
          android:strokeColor="#33FFFFFF" android:strokeWidth="0.8"/>
</vector>
```

## File: app/src/main/res/drawable/ic_launcher_foreground.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:pathData="M31,63.928c0,0 6.4,-11 12.1,-13.1c7.2,-2.6 26,-1.4 26,-1.4l38.1,38.1L107,108.928l-32,-1L31,63.928z">
        <aapt:attr name="android:fillColor">
            <gradient
                android:endX="85.84757"
                android:endY="92.4963"
                android:startX="42.9492"
                android:startY="49.59793"
                android:type="linear">
                <item
                    android:color="#44000000"
                    android:offset="0.0" />
                <item
                    android:color="#00000000"
                    android:offset="1.0" />
            </gradient>
        </aapt:attr>
    </path>
    <path
        android:fillColor="#FFFFFF"
        android:fillType="nonZero"
        android:pathData="M65.3,45.828l3.8,-6.6c0.2,-0.4 0.1,-0.9 -0.3,-1.1c-0.4,-0.2 -0.9,-0.1 -1.1,0.3l-3.9,6.7c-6.3,-2.8 -13.4,-2.8 -19.7,0l-3.9,-6.7c-0.2,-0.4 -0.7,-0.5 -1.1,-0.3C38.8,38.328 38.7,38.828 38.9,39.228l3.8,6.6C36.2,49.428 31.7,56.028 31,63.928h46C76.3,56.028 71.8,49.428 65.3,45.828zM43.4,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2c-0.3,-0.7 -0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C45.3,56.528 44.5,57.328 43.4,57.328L43.4,57.328zM64.6,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2s-0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C66.5,56.528 65.6,57.328 64.6,57.328L64.6,57.328z"
        android:strokeWidth="1"
        android:strokeColor="#00000000" />
</vector>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

## File: app/src/main/res/raw/confetti.json
```json
{"v":"5.6.3","fr":30,"ip":0,"op":112,"w":1242,"h":2688,"nm":"Comp 1","ddd":1,"assets":[],"layers":[{"ddd":0,"ind":1,"ty":4,"nm":"Shape Layer 14","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[692,1012,0],"ix":2},"a":{"a":0,"k":[71,-332,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[-48,204],[72.555,109.763],[-9.41,53.993],[11.376,60.133],[-20.778,48.214],[-43.71,34.149],[-28,80]],"o":[[48,-204],[-46.308,-70.056],[6.44,-36.951],[-18.438,-97.46],[10.775,-25.002],[128,-100],[28,-80]],"v":[[71,-332],[151,-620],[209.484,-736.613],[141,-856],[199.096,-959.097],[193,-1114],[371,-1280]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":14.077,"s":[0]},{"t":46.384765625,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":11.923,"s":[0]},{"t":42.0771484375,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":2,"op":112,"st":2,"bm":0},{"ddd":0,"ind":2,"ty":4,"nm":"Shape Layer 13","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[648,1040,0],"ix":2},"a":{"a":0,"k":[27,-304,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[28,112],[154,-50],[122,52],[60,164]],"o":[[0,0],[-24,-96],[-150.386,48.827],[-141.68,-60.388],[-60,-164]],"v":[[27,-304],[-107,-530],[-249,-720],[-335,-908],[-537,-1284]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.6078431372549019,0.6078431372549019,0.6078431372549019,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":3.308,"s":[0]},{"t":35.615234375,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-1,"s":[0]},{"t":27,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":0,"op":112,"st":-5,"bm":0},{"ddd":0,"ind":3,"ty":4,"nm":"Shape Layer 12","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[674,1228,0],"ix":2},"a":{"a":0,"k":[53,-116,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[-34,-98],[21,-81],[-20,-38],[-18,-48],[-8,-34],[-62,-38],[-24,-36],[-62,-30],[-84,-22]],"o":[[34,98],[-11.008,42.46],[12.721,24.17],[18,48],[9.222,39.192],[62,38],[24,36],[62,30],[84,22]],"v":[[53,-116],[134,25],[94,107],[181,157],[150,265],[237,323],[250,463],[383,540],[480,694]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.2901960784313726,0.7647058823529411,0.2980392156862745,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":22.692,"s":[0]},{"t":55,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":20.538,"s":[0]},{"t":50.6923828125,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":6,"op":112,"st":6,"bm":0},{"ddd":0,"ind":4,"ty":4,"nm":"Shape Layer 11","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[704,1180,0],"ix":2},"a":{"a":0,"k":[83,-164,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[-38,-60],[-40.126,23.279],[42.5,-0.5],[-35,2.5],[-47.28,-26.863],[-20,-30],[-21.113,-24.13],[-34,-42],[-16,-40]],"o":[[38,60],[65.5,-38],[-20.024,0.236],[41.06,-2.933],[44,25],[20,30],[14,16],[34,42],[16,40]],"v":[[83,-164],[166.5,-75],[217.5,-41.5],[222.5,-81.5],[377,-77],[378,1],[458,17],[478,91],[616,178]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.9411764705882353,0.35294117647058826,0.17647058823529413,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":7.615,"s":[0]},{"t":39.9228515625,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":3.308,"s":[0]},{"t":31.3076171875,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":0,"op":112,"st":-3,"bm":0},{"ddd":0,"ind":5,"ty":4,"nm":"Shape Layer 10","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[730,1100,0],"ix":2},"a":{"a":0,"k":[109,-244,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[-72,-10],[-51,10],[13,-16],[-24.905,-7.729],[-10,10]],"o":[[72,10],[51,-10],[-10.168,12.514],[29,9],[10,-10]],"v":[[109,-244],[483,-178],[533,-129],[556,-83],[618,-93]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":16.23,"s":[0]},{"t":48.5380859375,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":14.077,"s":[0]},{"t":44.23046875,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":3,"op":112,"st":3,"bm":0},{"ddd":0,"ind":6,"ty":4,"nm":"Shape Layer 9","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[678,1086,0],"ix":2},"a":{"a":0,"k":[57,-258,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[-106,94],[-78,82],[-2,37],[-15,15],[-27.82,25.594],[-23,24],[-38.877,17.279],[-24,-14]],"o":[[106,-94],[78,-82],[2.405,-44.488],[18.028,-18.028],[25,-23],[19.594,-20.446],[36,-16],[24,14]],"v":[[57,-258],[204,-404],[337,-493],[346,-597],[426,-624],[454,-699],[542,-728],[611,-805]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":7.615,"s":[0]},{"t":39.9228515625,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":3.308,"s":[0]},{"t":31.3076171875,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":0,"op":112,"st":-3,"bm":0},{"ddd":0,"ind":7,"ty":4,"nm":"Shape Layer 8","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[640,1170,0],"ix":2},"a":{"a":0,"k":[19,-174,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[92,-22],[38,-38],[78,-8],[38,-10],[10,-20]],"o":[[-92,22],[-38,38],[-78,8],[-38,10],[-10,20]],"v":[[19,-174],[-115,-120],[-283,-46],[-423,-50],[-517,32]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":7.615,"s":[0]},{"t":39.9228515625,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":5.462,"s":[0]},{"t":35.615234375,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":0,"ind":8,"ty":4,"nm":"Shape Layer 7","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[661,1191,0],"ix":2},"a":{"a":0,"k":[40,-153,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[16.398,-37.618],[29.992,-37.49],[41,-30],[60.195,-37.77],[80.591,1.465],[-31.635,-5.678],[109.189,24.816],[22,-30]],"o":[[-34,78],[-44,55],[-59.359,43.434],[-51,32],[-55,-1],[39,7],[-132,-30],[-22,30]],"v":[[40,-153],[-41,-58],[-104,84],[-253,140],[-359,228],[-381,175],[-459,204],[-652,321]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":18.385,"s":[0]},{"t":50.6923828125,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":14.077,"s":[0]},{"t":42.0771484375,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":2,"op":112,"st":2,"bm":0},{"ddd":0,"ind":9,"ty":4,"nm":"Shape Layer 6","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[646,1126,0],"ix":2},"a":{"a":0,"k":[25,-218,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[90,52],[20,-18],[44,46],[60,-36],[34,64],[-46,-10],[34,-50],[52,12],[28,10]],"o":[[-90,-52],[-20,18],[-44,-46],[-60,36],[-34,-64],[46,10],[-34,50],[-52,-12],[-28,-10]],"v":[[25,-218],[-175,-248],[-271,-286],[-379,-328],[-487,-310],[-535,-444],[-469,-396],[-547,-342],[-627,-366]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false},{"ty":"tm","s":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":5.462,"s":[0]},{"t":37.76953125,"s":[100]}],"ix":1},"e":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":3.308,"s":[0]},{"t":35.615234375,"s":[100]}],"ix":2},"o":{"a":0,"k":0,"ix":3},"m":1,"ix":2,"nm":"Trim In Out Path","mn":"ADBE Vector Filter - Trim","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":0,"ind":10,"ty":3,"nm":"Null 1","sr":1,"ks":{"o":{"a":0,"k":0,"ix":11},"r":{"a":0,"k":184,"ix":10},"p":{"a":0,"k":[633,1188,0],"ix":2},"a":{"a":0,"k":[12,-198,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"ip":0,"op":112,"st":0,"bm":0},{"ddd":1,"ind":11,"ty":4,"nm":"Shape Layer 41","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":87.9189453125,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10,"x":"var $bm_rt;\n$bm_rt = transform.rotation;"},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0.179,"s":[6.395,-188.007,0],"to":[3.99,-626.075,396.962],"ti":[-3.99,626.075,-396.962]},{"t":34.7802734375,"s":[30.337,-3944.458,2381.774]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[7,-27],[35,7],[-41.906,32.862],[0,0]],"o":[[-7,27],[32,27],[23.167,-18.167],[0,0]],"v":[[72,-317],[4,-279],[91,-263.167],[107.167,-289.667]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":12,"ty":4,"nm":"Shape Layer 40","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":80.50390625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0.179,"s":[6.395,-188.007,0],"to":[172.235,-647.46,153.434],"ti":[-172.235,647.46,-153.434]},{"t":80,"s":[1039.806,-4072.764,920.605]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":13,"ty":4,"nm":"Shape Layer 39","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0.179,"s":[0]},{"t":91.6259765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0.179,"s":[6.395,-188.007,0],"to":[140.553,-166.199,-42.398],"ti":[-140.553,166.199,42.398]},{"t":15.0078125,"s":[849.715,-1185.203,-254.39]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":0,"ind":14,"ty":3,"nm":"Null 2","sr":1,"ks":{"o":{"a":0,"k":0,"ix":11},"r":{"a":0,"k":-49,"ix":10},"p":{"a":0,"k":[621,1344,0],"ix":2},"a":{"a":0,"k":[0,0,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"ip":0,"op":112,"st":-4,"bm":0},{"ddd":0,"ind":15,"ty":3,"nm":"Null 3","sr":1,"ks":{"o":{"a":0,"k":0,"ix":11},"r":{"a":0,"k":82,"ix":10},"p":{"a":0,"k":[621,1344,0],"ix":2},"a":{"a":0,"k":[0,0,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"ip":0,"op":112,"st":-4,"bm":0},{"ddd":1,"ind":16,"ty":4,"nm":"Shape Layer 105","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":87.6826171875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10,"x":"var $bm_rt;\n$bm_rt = transform.rotation;"},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[727.321,51.433,435.455],"ti":[-727.321,-51.433,-435.455]},{"t":34.544921875,"s":[4250.962,186.167,2612.729]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[7,-27],[35,7],[-41.906,32.862],[0,0]],"o":[[-7,27],[32,27],[23.167,-18.167],[0,0]],"v":[[72,-317],[4,-279],[91,-263.167],[107.167,-289.667]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":17,"ty":4,"nm":"Shape Layer 104","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":80.50390625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[527.848,55.985,153.434],"ti":[-527.848,-55.985,-153.434]},{"t":54.552734375,"s":[3054.124,213.477,920.605]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":18,"ty":4,"nm":"Shape Layer 103","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":91.6259765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[4.145,138.496,-42.398],"ti":[-4.145,-138.496,42.398]},{"t":15.0078125,"s":[-88.093,708.543,-254.39]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":19,"ty":4,"nm":"Shape Layer 102","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":94.09765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":-0.179,"s":[-112.965,-122.431,0],"to":[-61.615,187.567,26.907],"ti":[61.615,-187.567,-26.907]},{"t":31.0732421875,"s":[-482.654,1002.974,161.442]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":20,"ty":4,"nm":"Shape Layer 101","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-1.057,"s":[0]},{"t":84.2109375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.824},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-90.863,90.863,0],"ti":[88.083,-357.903,-768.642]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.164},"t":17.479,"s":[-658.144,422.748,0],"to":[-84.157,341.951,734.383],"ti":[-714.89,-84.078,-197.259]},{"t":86.6826171875,"s":[3631.196,927.217,1183.551]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":21,"ty":4,"nm":"Shape Layer 100","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":100.2763671875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.796},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-134.704,42.073,0],"ti":[7.203,-205.415,0]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.157},"t":28,"s":[-921.188,130.006,0],"to":[-19.567,558.035,0],"ti":[-540.328,-229.432,0]},{"t":116,"s":[2320.778,1506.598,0]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":22,"ty":4,"nm":"Shape Layer 99","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.775},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-115.257,9.245,0],"ti":[52.068,-163.912,-53.249]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.155},"t":19.951,"s":[-804.504,-66.963,0],"to":[-48.777,153.551,49.883],"ti":[-156.371,-109.917,-25.485]},{"t":91,"s":[2733.865,1051.661,319.492]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":23,"ty":4,"nm":"Shape Layer 98","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":146,"s":[1440]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":146,"s":[1440]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.855},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-138.996,-53.461,0],"ti":[-421.763,15.031,16.673]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.174},"t":21.187,"s":[-946.942,-443.198,0],"to":[421.763,-15.031,-16.673],"ti":[-560.76,-38.43,16.673]},{"t":100.2763671875,"s":[2417.616,-212.616,-100.037]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":24,"ty":4,"nm":"Shape Layer 97","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":144.7646484375,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":144.7646484375,"s":[59]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.673},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[-87.16,25.736,-45.544],"ti":[-273.542,-218.754,122.377]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.14},"t":31.073,"s":[-660.653,26.246,-273.265],"to":[129.994,103.957,-58.156],"ti":[-320.287,-255.677,-112.981]},{"t":111.3984375,"s":[3683.402,989.215,1150.251]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[0,0],[0,0],[3.444,9.757]],"o":[[0,0],[0,0],[0,0],[-4.941,-14]],"v":[[72,-317],[4,-279],[24.151,-217.936],[88.307,-269.617]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":25,"ty":4,"nm":"Shape Layer 96","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-9,"s":[0]},{"t":89.154296875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.853},"o":{"x":0.167,"y":0.167},"t":0,"s":[-89.038,-0.153,0],"to":[-552.568,-84.135,-9.152],"ti":[-1514.035,-462.134,43.281]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.183},"t":38,"s":[95.207,-392.324,-54.91],"to":[684.779,209.018,-19.576],"ti":[-315.276,-115.122,160.297]},{"t":97,"s":[1986.864,298.408,-1016.689]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":26,"ty":4,"nm":"Shape Layer 95","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-14,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.774},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[-110.347,-144.949,-65.532],"ti":[-365.662,-268.839,-214.28]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.147},"t":26,"s":[-799.777,-997.863,-393.192],"to":[365.662,268.839,214.28],"ti":[-476.009,-413.788,-279.812]},{"t":76.796875,"s":[2056.275,1484.866,1285.683]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":27,"ty":4,"nm":"Shape Layer 94","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-9,"s":[0]},{"t":100.2763671875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[-90.481,-247.593,46.629],"ti":[90.481,247.593,-46.629]},{"t":44,"s":[-680.583,-1613.73,279.773]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":28,"ty":4,"nm":"Shape Layer 93","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":91.6259765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[69.003,-110.281,-16.168],"ti":[-69.003,110.281,16.168]},{"t":32,"s":[276.323,-789.855,-97.005]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":29,"ty":4,"nm":"Shape Layer 92","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-10,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[305.417,119.743,41.862],"ti":[-305.417,-119.743,-41.862]},{"t":27,"s":[1694.805,590.289,251.17]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":30,"ty":4,"nm":"Shape Layer 91","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[318.521,75.426,-43.518],"ti":[-318.521,-75.426,43.518]},{"t":24,"s":[1773.433,324.385,-261.107]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":31,"ty":4,"nm":"Shape Layer 90","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-4.765,"s":[0]},{"t":89.154296875,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":89.154296875,"s":[1800]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[77.354,134.863,-215.584],"ti":[-77.354,-134.863,215.584]},{"t":27.3662109375,"s":[326.43,681.005,-1293.506]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":32,"ty":4,"nm":"Shape Layer 89","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-1.057,"s":[0]},{"t":102.748046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10,"x":"var $bm_rt;\n$bm_rt = transform.rotation;"},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[301.698,164.851,-47.459],"ti":[-301.698,-164.851,47.459]},{"t":103.9833984375,"s":[1697.223,866.674,-284.755]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]],"v":[[84.659,-333.362],[4,-279],[45.665,-243.617],[119.735,-297.935]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":33,"ty":4,"nm":"Shape Layer 88","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-17,"s":[0]},{"t":95.3330078125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[159.503,223.195,-0.572],"ti":[-159.503,-223.195,0.572]},{"t":37.251953125,"s":[844.051,1216.741,-3.43]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":34,"ty":4,"nm":"Shape Layer 87","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":101.51171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[33.76,196.346,-7.096],"ti":[-33.76,-196.346,7.096]},{"t":40.958984375,"s":[89.594,1055.648,-42.576]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":35,"ty":4,"nm":"Shape Layer 86","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":85.447265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-16.649,163.663,0],"ti":[16.649,-163.663,0]},{"t":24.89453125,"s":[-212.861,859.547,0]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":36,"ty":4,"nm":"Shape Layer 85","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":101.51171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-71.747,128.05,0],"ti":[71.747,-128.05,0]},{"t":49.609375,"s":[-543.449,645.87,0]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":37,"ty":4,"nm":"Shape Layer 84","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-4.765,"s":[0]},{"t":112.6337890625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.728},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-81.837,27.324,33.544],"ti":[-153.275,-262.436,-33.544]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.131},"t":21,"s":[-603.986,41.512,201.262],"to":[153.275,262.436,33.544],"ti":[-235.112,-235.112,0]},{"t":50.845703125,"s":[806.688,1452.185,201.262]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":38,"ty":4,"nm":"Shape Layer 83","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-6,"s":[0]},{"t":85.447265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.652},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-171.831,20.57,3.486],"ti":[602.026,-193.886,-848.553]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.142},"t":35,"s":[-1143.953,0.988,20.917],"to":[-778.519,946.911,884.934],"ti":[-1189.71,-488.979,-884.978]},{"t":139,"s":[5994.306,2934.863,5330.786]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":39,"ty":4,"nm":"Shape Layer 82","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-23,"s":[0]},{"t":97.8046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,135],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.883},"o":{"x":0.167,"y":0.167},"t":0,"s":[-112.965,-122.431,0],"to":[-120.682,-96.313,17.582],"ti":[57.484,-266.769,15.802]},{"i":{"x":0.833,"y":0.817},"o":{"x":0.167,"y":0.198},"t":15.008,"s":[-837.059,-700.31,105.493],"to":[-25.433,118.027,-6.991],"ti":[-511.749,-461.825,46.841]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.143},"t":54.553,"s":[271.713,386.113,403.89],"to":[308.693,278.579,-28.255],"ti":[-142.63,-100.689,10.833]},{"t":76.796875,"s":[1060.877,1065.009,-67.253]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":40,"ty":4,"nm":"Shape Layer 81","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-12,"s":[0]},{"t":102.748046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[135.751,-389.001,217.62],"ti":[-135.751,389.001,-217.62]},{"t":27,"s":[676.808,-2462.175,1305.721]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[7,-27],[35,7],[-41.906,32.862],[0,0]],"o":[[-7,27],[32,27],[23.167,-18.167],[0,0]],"v":[[72,-317],[4,-279],[91,-263.167],[107.167,-289.667]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":41,"ty":4,"nm":"Shape Layer 80","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-25,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.849},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[-70.649,26.407,0],"ti":[-291.085,-122.356,90.248]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.171},"t":16,"s":[-561.59,30.269,0],"to":[291.085,122.356,-90.248],"ti":[-361.735,-95.95,90.248]},{"t":82.9755859375,"s":[1608.818,605.966,-541.488]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":42,"ty":4,"nm":"Shape Layer 79","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":95.3330078125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.704},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[-49.159,-63.557,4.223],"ti":[-0.005,148.838,-20.562]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.144},"t":24,"s":[-432.65,-509.511,25.34],"to":[0.017,-477.286,65.936],"ti":[-415.767,197.78,-61.713]},{"t":91.6259765625,"s":[2061.954,-1696.192,395.618]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":43,"ty":4,"nm":"Shape Layer 78","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-20,"s":[0]},{"t":87.9189453125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[33.977,-166.361,22.596],"ti":[-33.977,166.361,-22.596]},{"t":30,"s":[66.167,-1126.338,135.575]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":44,"ty":4,"nm":"Shape Layer 77","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-16,"s":[0]},{"t":99.041015625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[345.992,-256.663,189.679],"ti":[-345.992,256.663,-189.679]},{"t":27,"s":[1938.258,-1668.151,1138.074]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":45,"ty":4,"nm":"Shape Layer 76","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":45.90234375,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":105.2197265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.826},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[120.875,-60.552,0],"ti":[-340.061,254.939,0]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.163},"t":27.366,"s":[587.554,-491.48,0],"to":[340.061,-254.939,0],"ti":[-219.187,194.387,0]},{"t":82.9755859375,"s":[1902.674,-1657.802,0]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":46,"ty":4,"nm":"Shape Layer 75","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-18,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.926},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[208.471,-17.277,73.2],"ti":[-257.939,74.184,269.358]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.228},"t":15.897,"s":[1113.129,-231.834,439.203],"to":[257.939,-74.184,-269.358],"ti":[-49.469,56.907,342.559]},{"t":90,"s":[1409.942,-573.277,-1616.149]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":47,"ty":4,"nm":"Shape Layer 74","parent":15,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-24,"s":[0]},{"t":94.09765625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,311],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[-137.695,-128.17,0],"to":[308.185,154.066,17.938],"ti":[-308.185,-154.066,-17.938]},{"t":24.89453125,"s":[1711.415,796.226,107.63]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[51,51,51],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":48,"ty":4,"nm":"Shape Layer 73","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":87.9189453125,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10,"x":"var $bm_rt;\n$bm_rt = transform.rotation;"},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-47.653,624.272,396.962],"ti":[47.653,-624.272,-396.962]},{"t":34.7802734375,"s":[-267.632,3580.053,2381.774]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[7,-27],[35,7],[-41.906,32.862],[0,0]],"o":[[-7,27],[32,27],[23.167,-18.167],[0,0]],"v":[[72,-317],[4,-279],[91,-263.167],[107.167,-289.667]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":49,"ty":4,"nm":"Shape Layer 72","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":80.50390625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-143.363,403.815,153.434],"ti":[143.363,-403.815,-153.434]},{"t":54.552734375,"s":[-841.892,2257.314,920.605]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":50,"ty":4,"nm":"Shape Layer 71","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":91.6259765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-196.222,199.787,-42.398],"ti":[196.222,-199.787,42.398]},{"t":32.30859375,"s":[-1159.042,1033.147,-254.39]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":51,"ty":4,"nm":"Shape Layer 70","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":94.09765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.868},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-181.982,76.554,26.907],"ti":[353.39,-64.568,-26.907]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.207},"t":31.073,"s":[-1073.604,293.746,161.442],"to":[-353.39,64.568,26.907],"ti":[171.408,11.986,0]},{"t":73.08984375,"s":[-2102.054,221.83,161.442]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":52,"ty":4,"nm":"Shape Layer 69","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-1.057,"s":[0]},{"t":84.2109375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-128.187,-8.964,0],"ti":[128.187,8.964,0]},{"t":33,"s":[-750.834,-219.36,0]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":53,"ty":4,"nm":"Shape Layer 68","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":100.2763671875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-120.126,-74.06,0],"ti":[120.126,74.06,0]},{"t":39,"s":[-702.471,-609.938,0]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":54,"ty":4,"nm":"Shape Layer 67","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.723},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-82.592,-80.92,0],"ti":[157.866,-68.24,-53.249]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.139},"t":27,"s":[-477.265,-651.099,0],"to":[-147.887,63.926,49.883],"ti":[-19.633,-190.127,-25.485]},{"t":80.50390625,"s":[-147.831,2175.133,319.492]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":55,"ty":4,"nm":"Shape Layer 66","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":146,"s":[1440]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":146,"s":[1440]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.749},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-50.842,-139.975,0],"ti":[92.088,-447.218,16.673]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.14},"t":36,"s":[-286.764,-1005.43,0],"to":[-92.088,447.218,-16.673],"ti":[41.246,-587.193,16.673]},{"t":100.2763671875,"s":[-534.238,2517.728,-100.037]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":56,"ty":4,"nm":"Shape Layer 65","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":144.7646484375,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":144.7646484375,"s":[59]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.767},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[-6.333,-132.667,0],"ti":[83.687,200.398,-334.198]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.153},"t":26,"s":[-31.605,-984.007,0],"to":[-63.434,-151.901,253.321],"ti":[-17.165,-409.463,-112.981]},{"t":111.3984375,"s":[283.153,3184.715,1150.251]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[0,0],[0,0],[3.444,9.757]],"o":[[0,0],[0,0],[0,0],[-4.941,-14]],"v":[[72,-317],[4,-279],[24.151,-217.936],[88.307,-269.617]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.671,0.345,0.345,0.5,0.655,0.247,0.461,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":57,"ty":4,"nm":"Shape Layer 64","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-9,"s":[0]},{"t":89.154296875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.836},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[64.5,-169.594,-9.152],"ti":[-113.79,-117.061,113.731]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.168},"t":27,"s":[393.395,-1205.571,-54.91],"to":[155.887,160.368,-155.806],"ti":[28,-314.6,160.297]},{"t":79,"s":[225.395,682.029,-1016.689]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":58,"ty":4,"nm":"Shape Layer 63","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-14,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.787},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[37,-178.375,-65.532],"ti":[-37,-452.343,-214.28]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.151},"t":28,"s":[228.395,-1258.256,-393.192],"to":[37,452.343,214.28],"ti":[0,-630.718,-279.812]},{"t":88,"s":[228.395,2526.051,1285.683]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":59,"ty":4,"nm":"Shape Layer 62","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-9,"s":[0]},{"t":100.2763671875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[127.5,-230.723,46.629],"ti":[-127.5,230.723,-46.629]},{"t":38,"s":[771.395,-1572.344,279.773]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":60,"ty":4,"nm":"Shape Layer 61","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":91.6259765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[259.979,-20.273,-16.168],"ti":[-259.979,20.273,16.168]},{"t":19,"s":[1566.267,-309.648,-97.005]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":61,"ty":4,"nm":"Shape Layer 60","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-10,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[110,309.06,41.862],"ti":[-110,-309.06,-41.862]},{"t":31,"s":[666.395,1666.35,251.17]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":62,"ty":4,"nm":"Shape Layer 59","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[111,205.897,-43.518],"ti":[-111,-205.897,43.518]},{"t":33,"s":[672.395,1047.376,-261.107]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":63,"ty":4,"nm":"Shape Layer 58","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-4.765,"s":[0]},{"t":89.154296875,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":89.154296875,"s":[1800]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[14.845,244.879,-215.584],"ti":[-14.845,-244.879,215.584]},{"t":27.3662109375,"s":[95.463,1281.264,-1293.506]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":64,"ty":4,"nm":"Shape Layer 57","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-1.057,"s":[0]},{"t":102.748046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10,"x":"var $bm_rt;\n$bm_rt = transform.rotation;"},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[44.909,224.342,-47.459],"ti":[-44.909,-224.342,47.459]},{"t":58.259765625,"s":[287.74,1180.472,-284.755]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]],"v":[[84.659,-333.362],[4,-279],[45.665,-243.617],[119.735,-297.935]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":65,"ty":4,"nm":"Shape Layer 56","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-17,"s":[0]},{"t":95.3330078125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-63.805,266.808,-0.572],"ti":[63.805,-266.808,0.572]},{"t":51,"s":[-364.539,1435.268,-3.43]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":66,"ty":4,"nm":"Shape Layer 55","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":101.51171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.877},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-126.036,154.294,-7.096],"ti":[142.905,-395.535,7.096]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.203},"t":39,"s":[-737.928,760.184,-42.576],"to":[-142.905,395.535,-7.096],"ti":[16.869,-241.241,0]},{"t":117,"s":[-839.144,2207.633,-42.576]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":67,"ty":4,"nm":"Shape Layer 54","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":85.447265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-233.472,311.697,-17.966],"ti":[233.472,-311.697,17.966]},{"t":24,"s":[-1382.547,1704.606,-107.798]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":68,"ty":4,"nm":"Shape Layer 53","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":101.51171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.762},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-130.292,21.146,0],"ti":[485.802,-51.233,12.363]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.14},"t":22,"s":[-763.467,-38.702,0],"to":[-485.802,51.233,-12.363],"ti":[355.509,-30.087,12.363]},{"t":57,"s":[-2896.522,141.82,-74.176]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":69,"ty":4,"nm":"Shape Layer 52","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-4.765,"s":[0]},{"t":112.6337890625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.78},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-74.311,-43.837,33.544],"ti":[97.505,-287.852,-33.544]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.149},"t":34,"s":[-427.58,-428.6,201.262],"to":[-97.505,287.852,33.544],"ti":[23.194,-331.689,0]},{"t":102,"s":[-566.743,1561.534,201.262]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":70,"ty":4,"nm":"Shape Layer 51","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-6,"s":[0]},{"t":85.447265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-124.762,-149.834,3.486],"ti":[124.762,149.834,-3.486]},{"t":26,"s":[-730.286,-1064.579,20.917]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":71,"ty":4,"nm":"Shape Layer 50","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-23,"s":[0]},{"t":97.8046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,184],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.77},"o":{"x":0.167,"y":0.167},"t":0,"s":[18.288,-165.578,0],"to":[-6.486,-154.267,17.582],"ti":[239.046,-131.632,15.802]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.142},"t":30,"s":[-20.629,-1091.18,105.493],"to":[-105.762,58.238,-6.991],"ti":[-17.583,-173.702,10.833]},{"t":76.796875,"s":[-107.774,1499.363,-67.253]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":72,"ty":4,"nm":"Shape Layer 49","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-12,"s":[0]},{"t":102.748046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[382.643,-152.755,217.62],"ti":[-382.643,152.755,-217.62]},{"t":33,"s":[2302.253,-1104.538,1305.721]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[7,-27],[35,7],[-41.906,32.862],[0,0]],"o":[[-7,27],[32,27],[23.167,-18.167],[0,0]],"v":[[72,-317],[4,-279],[91,-263.167],[107.167,-289.667]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":73,"ty":4,"nm":"Shape Layer 48","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-25,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.831},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[64.5,-93.5,0],"ti":[-64.5,-194.5,90.248]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.166},"t":25,"s":[393.395,-749.007,0],"to":[64.5,194.5,-90.248],"ti":[0,-288,90.248]},{"t":82.9755859375,"s":[393.395,978.993,-541.488]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":74,"ty":4,"nm":"Shape Layer 47","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":95.3330078125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.885},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[65.626,-110,4.223],"ti":[-147.468,-10.238,-26.905]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.19},"t":20,"s":[400.149,-848.007,25.34],"to":[361.407,25.09,65.936],"ti":[-121.292,-241.908,-61.713]},{"t":91.6259765625,"s":[1127.898,603.439,395.618]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":75,"ty":4,"nm":"Shape Layer 46","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-20,"s":[0]},{"t":87.9189453125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.816},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[147.845,-83.5,22.596],"ti":[-263.557,-41.975,24.609]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.159},"t":24,"s":[893.467,-689.007,135.575],"to":[408.719,65.094,-38.164],"ti":[88.693,-68.281,27.128]},{"t":74,"s":[-447.485,1243.193,-405.659]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":76,"ty":4,"nm":"Shape Layer 45","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-16,"s":[0]},{"t":99.041015625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.875},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[217.737,24,189.679],"ti":[-563.841,-88.203,-189.679]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.201},"t":26,"s":[1312.816,-44.007,1138.074],"to":[563.841,88.203,189.679],"ti":[-346.104,-64.203,0]},{"t":76.796875,"s":[3389.44,341.208,1138.074]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":77,"ty":4,"nm":"Shape Layer 44","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":45.90234375,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":105.2197265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.866},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[125,51.5,0],"ti":[-125,-69.882,151.388]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.196},"t":27.366,"s":[756.395,120.993,0],"to":[125,69.882,-151.388],"ti":[0,-18.382,151.388]},{"t":72,"s":[756.395,231.284,-908.328]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":78,"ty":4,"nm":"Shape Layer 43","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-18,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[149.808,146,73.2],"ti":[-149.808,-146,-73.2]},{"t":26,"s":[905.246,687.993,439.203]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":79,"ty":4,"nm":"Shape Layer 42","parent":14,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-24,"s":[0]},{"t":94.09765625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[85.912,333.667,17.938],"ti":[-85.912,-333.667,-17.938]},{"t":24.89453125,"s":[521.87,1813.993,107.63]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[64,64,64],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":80,"ty":4,"nm":"Shape Layer 38","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":94.09765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[176.199,-89.062,26.907],"ti":[-176.199,89.062,-26.907]},{"t":31.0732421875,"s":[1063.587,-722.379,161.442]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":81,"ty":4,"nm":"Shape Layer 37","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-1.057,"s":[0]},{"t":84.2109375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[128.5,0,0],"ti":[-128.5,0,0]},{"t":30,"s":[777.395,-188.007,0]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":82,"ty":4,"nm":"Shape Layer 36","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":100.2763671875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[125,65.5,0],"ti":[-125,-65.5,0]},{"t":22,"s":[756.395,204.993,0]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":83,"ty":4,"nm":"Shape Layer 35","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.782},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[88.036,74.962,0],"ti":[-235.276,83.183,-29.696]},{"i":{"x":0.833,"y":0.693},"o":{"x":0.167,"y":0.155},"t":30,"s":[534.609,261.763,0],"to":[283.126,-100.101,35.736],"ti":[32.848,188.294,-25.485]},{"t":123,"s":[-62.525,-2846.235,319.492]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":84,"ty":4,"nm":"Shape Layer 33","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":454.311,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":144.7646484375,"s":[59]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.737},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[42.167,-122.667,0],"ti":[-345.153,74.903,-198.64]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.151},"t":28,"s":[880.395,419.993,0],"to":[432.939,-93.953,249.162],"ti":[-17.165,-409.463,-112.981]},{"t":127,"s":[1064.167,5582.141,1150.251]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[0,0],[0,0],[3.444,9.757]],"o":[[0,0],[0,0],[0,0],[-4.941,-14]],"v":[[72,-317],[4,-279],[24.151,-217.936],[88.307,-269.617]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":85,"ty":4,"nm":"Shape Layer 32","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-9,"s":[0]},{"t":89.154296875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.841},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[21.5,-126.594,-9.152],"ti":[-109.46,-148.367,74.781]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.169},"t":25,"s":[756.395,396.429,-54.91],"to":[113.887,154.368,-77.806],"ti":[7.464,-520.935,-7.703]},{"t":124,"s":[711.613,3522.04,-8.689]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":86,"ty":4,"nm":"Shape Layer 31","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-14,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.843},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[37,-178.375,-65.532],"ti":[-48.635,-750.211,-214.28]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.169},"t":29,"s":[849.395,85.744,-393.192],"to":[48.635,750.211,214.28],"ti":[-11.635,-928.586,-279.812]},{"t":145,"s":[919.208,5657.262,1285.683]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":87,"ty":4,"nm":"Shape Layer 30","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-9,"s":[0]},{"t":100.2763671875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[127.5,-230.723,46.629],"ti":[-127.5,230.723,-46.629]},{"t":25,"s":[1392.395,-228.344,279.773]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":88,"ty":4,"nm":"Shape Layer 29","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":91.6259765625,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[128.5,-20.273,-16.168],"ti":[-128.5,20.273,16.168]},{"t":44,"s":[1398.395,1034.352,-97.005]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":89,"ty":4,"nm":"Shape Layer 28","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-10,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[110,309.06,41.862],"ti":[-110,-309.06,-41.862]},{"t":41,"s":[1287.395,3010.35,251.17]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":90,"ty":4,"nm":"Shape Layer 27","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":8},"ry":{"a":0,"k":0,"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[111,205.897,-43.518],"ti":[-111,-205.897,43.518]},{"t":31,"s":[1293.395,2391.376,-261.107]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":91,"ty":4,"nm":"Shape Layer 26","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-4.765,"s":[0]},{"t":89.154296875,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":89.154296875,"s":[1800]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[1,335.659,-215.584],"ti":[-1,-335.659,215.584]},{"t":58,"s":[633.395,3169.948,-1293.506]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":92,"ty":4,"nm":"Shape Layer 25","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-1.057,"s":[0]},{"t":102.748046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10,"x":"var $bm_rt;\n$bm_rt = transform.rotation;"},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[-147.439,-471.248,-47.459],"ti":[147.439,471.248,47.459]},{"t":137.349609375,"s":[-878.24,-3015.497,-284.755]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[0,0],[0,0],[0,0]],"o":[[0,0],[0,0],[0,0],[0,0]],"v":[[84.659,-333.362],[4,-279],[45.665,-243.617],[119.735,-297.935]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":93,"ty":4,"nm":"Shape Layer 24","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-17,"s":[0]},{"t":95.3330078125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[53.803,-474.312,-0.572],"ti":[-53.803,474.312,0.572]},{"t":71,"s":[329.216,-3033.882,-3.43]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":0,"ind":94,"ty":4,"nm":"Shape Layer 23","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[436.949,-283.973,0],"ti":[-436.949,283.973,0]},{"t":71,"s":[2628.09,-1891.843,0]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":95,"ty":4,"nm":"Shape Layer 22","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-11,"s":[0]},{"t":85.447265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[127.5,-103.954,0],"ti":[-127.5,103.954,0]},{"t":34,"s":[771.395,-811.733,0]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":96,"ty":4,"nm":"Shape Layer 21","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":101.51171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[128.5,-30.183,0],"ti":[-128.5,30.183,0]},{"t":26,"s":[777.395,-369.106,0]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":97,"ty":4,"nm":"Shape Layer 20","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-4.765,"s":[0]},{"t":112.6337890625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.716},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[77.188,38.547,33.544],"ti":[-71.564,470.424,-33.544]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.136},"t":35,"s":[469.524,43.272,201.262],"to":[71.564,-470.424,33.544],"ti":[5.624,508.971,0]},{"t":100,"s":[435.782,-3010.554,201.262]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":98,"ty":4,"nm":"Shape Layer 19","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-6,"s":[0]},{"t":85.447265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[134.91,140.766,3.486],"ti":[-134.91,-140.766,-3.486]},{"t":21,"s":[815.857,656.587,20.917]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":99,"ty":4,"nm":"Shape Layer 18","parent":10,"sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-23,"s":[0]},{"t":97.8046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.828},"o":{"x":0.167,"y":0.167},"t":0,"s":[6.395,-188.007,0],"to":[17.232,153.439,17.582],"ti":[-229.282,147.986,15.802]},{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.165},"t":21,"s":[109.785,732.626,105.493],"to":[101.442,-65.474,-6.991],"ti":[29.657,172.052,10.833]},{"t":76.796875,"s":[76.884,-1982.01,-729.705]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":100,"ty":4,"nm":"Shape Layer 17","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-12,"s":[0]},{"t":102.748046875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[382.643,-152.755,217.62],"ti":[-382.643,152.755,-217.62]},{"t":25,"s":[2923.253,239.462,1305.721]}],"ix":2},"a":{"a":0,"k":[56.08,-285.558,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[7,-27],[35,7],[-41.906,32.862],[0,0]],"o":[[-7,27],[32,27],[23.167,-18.167],[0,0]],"v":[[72,-317],[4,-279],[91,-263.167],[107.167,-289.667]],"c":true},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.345,0.381,0.671,0.5,0.491,0.265,0.624,1,0.638,0.15,0.577],"ix":9}},"s":{"a":0,"k":[0,0],"ix":5},"e":{"a":0,"k":[100,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":101,"ty":4,"nm":"Shape Layer 16","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-25,"s":[0]},{"t":81.740234375,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.829},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[64.5,-93.5,0],"ti":[-78.679,-339.952,90.248]},{"i":{"x":0.833,"y":0.714},"o":{"x":0.167,"y":0.165},"t":30,"s":[1014.395,594.993,0],"to":[78.679,339.952,-90.248],"ti":[-14.179,-433.452,90.248]},{"t":121,"s":[1099.471,3195.705,-541.488]}],"ix":2},"a":{"a":0,"k":[-262,-435,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[46,54],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"gf","o":{"a":0,"k":100,"ix":10},"r":1,"bm":0,"g":{"p":3,"k":{"a":0,"k":[0,0.969,0.18,0.404,0.5,0.97,0.346,0.318,1,0.972,0.512,0.233],"ix":9}},"s":{"a":0,"k":[-49,0],"ix":5},"e":{"a":0,"k":[68,0],"ix":6},"t":1,"nm":"Gradient Fill 1","mn":"ADBE Vector Graphic - G-Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-262,-435],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":102,"ty":4,"nm":"Shape Layer 15","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.822]},"o":{"x":[0.167],"y":[0.167]},"t":0,"s":[0]},{"t":102,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.828},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[65.626,-110,4.223],"ti":[-147.468,-10.238,-26.905]},{"i":{"x":0.833,"y":0.887},"o":{"x":0.167,"y":0.163},"t":28,"s":[1021.149,495.993,25.34],"to":[361.407,25.09,65.936],"ti":[-126.131,-151.254,-61.713]},{"t":71,"s":[1777.936,1403.514,395.618]}],"ix":2},"a":{"a":0,"k":[14.5,-433,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[27,96],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[1,0.25882352941176473,0.25882352941176473,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":1,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.7411764705882353,0.4588235294117647,0.9333333333333333,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[14.5,-433],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":103,"ty":4,"nm":"Shape Layer 5","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-20,"s":[0]},{"t":87.9189453125,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[147.845,-83.5,22.596],"ti":[-147.845,83.5,-22.596]},{"t":19,"s":[1514.467,654.993,135.575]}],"ix":2},"a":{"a":0,"k":[-180.917,-577,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ind":0,"ty":"sh","ix":1,"ks":{"a":0,"k":{"i":[[0,0],[36,32],[26,-104]],"o":[[0,0],[-36,-32],[-11.157,44.627]],"v":[[-161,-650],[-193,-562],[-165,-504]],"c":false},"ix":2},"nm":"Path 1","mn":"ADBE Vector Shape - Group","hd":false},{"ty":"st","c":{"a":0,"k":[0.996078431372549,0.7686274509803922,0.33725490196078434,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":14,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"tr","p":{"a":0,"k":[0,0],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Shape 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":104,"ty":4,"nm":"Shape Layer 4","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-16,"s":[0]},{"t":99.041015625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[393.671,83.751,189.679],"ti":[-393.671,-83.751,-189.679]},{"t":58,"s":[2989.419,1658.499,1138.074]}],"ix":2},"a":{"a":0,"k":[-45,-602,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[40,40],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[-45,-602],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":105,"ty":4,"nm":"Shape Layer 3","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-2.293,"s":[0]},{"t":45.90234375,"s":[1080]}],"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-3.528,"s":[0]},{"t":105.2197265625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[249,109.5,0],"ti":[-249,-109.5,0]},{"t":68,"s":[2121.395,1812.993,0]}],"ix":2},"a":{"a":0,"k":[232.5,-595.5,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[11,67],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.20392156862745098,0.592156862745098,0.9921568627450981,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[232.5,-595.5],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":106,"ty":4,"nm":"Shape Layer 2","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-18,"s":[0]},{"t":86.6826171875,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[499.008,399.431,73.2],"ti":[-499.008,-399.431,-73.2]},{"t":96,"s":[3621.445,3552.58,439.203]}],"ix":2},"a":{"a":0,"k":[120,-142,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[32,44],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.4980392156862745,0.8156862745098039,0.30980392156862746,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[120,-142],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":1,"ind":107,"ty":4,"nm":"Shape Layer 1","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"rx":{"a":0,"k":0,"ix":8},"ry":{"a":1,"k":[{"i":{"x":[0.833],"y":[0.833]},"o":{"x":[0.167],"y":[0.167]},"t":-24,"s":[0]},{"t":94.09765625,"s":[1080]}],"ix":9},"rz":{"a":0,"k":0,"ix":10},"or":{"a":0,"k":[0,0,0],"ix":7},"p":{"a":1,"k":[{"i":{"x":0.833,"y":0.833},"o":{"x":0.167,"y":0.167},"t":0,"s":[627.395,1155.993,0],"to":[535.305,103.127,17.938],"ti":[-535.305,-103.127,-17.938]},{"t":82,"s":[3839.226,1774.756,107.63]}],"ix":2},"a":{"a":0,"k":[116,-254,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"shapes":[{"ty":"gr","it":[{"ty":"rc","d":1,"s":{"a":0,"k":[26,56],"ix":2},"p":{"a":0,"k":[0,0],"ix":3},"r":{"a":0,"k":0,"ix":4},"nm":"Rectangle Path 1","mn":"ADBE Vector Shape - Rect","hd":false},{"ty":"st","c":{"a":0,"k":[0.687843053481,0.678132001091,0.678132001091,1],"ix":3},"o":{"a":0,"k":100,"ix":4},"w":{"a":0,"k":0,"ix":5},"lc":2,"lj":1,"ml":4,"bm":0,"nm":"Stroke 1","mn":"ADBE Vector Graphic - Stroke","hd":false},{"ty":"fl","c":{"a":0,"k":[0.996078431372549,0.5490196078431373,0.00392156862745098,1],"ix":4},"o":{"a":0,"k":100,"ix":5},"r":1,"bm":0,"nm":"Fill 1","mn":"ADBE Vector Graphic - Fill","hd":false},{"ty":"tr","p":{"a":0,"k":[116,-254],"ix":2},"a":{"a":0,"k":[0,0],"ix":1},"s":{"a":0,"k":[100,100],"ix":3},"r":{"a":0,"k":0,"ix":6},"o":{"a":0,"k":100,"ix":7},"sk":{"a":0,"k":0,"ix":4},"sa":{"a":0,"k":0,"ix":5},"nm":"Transform"}],"nm":"Rectangle 1","np":3,"cix":2,"bm":0,"ix":1,"mn":"ADBE Vector Group","hd":false}],"ip":0,"op":112,"st":-1,"bm":0},{"ddd":0,"ind":108,"ty":1,"nm":"Medium Gray-Blue Solid 1","sr":1,"ks":{"o":{"a":0,"k":100,"ix":11},"r":{"a":0,"k":0,"ix":10},"p":{"a":0,"k":[621,1344,0],"ix":2},"a":{"a":0,"k":[621,1344,0],"ix":1},"s":{"a":0,"k":[100,100,100],"ix":6}},"ao":0,"sw":1242,"sh":2688,"sc":"#272d41","ip":112,"op":112,"st":0,"bm":0,"hidden":0}],"markers":[]}
```

## File: app/src/main/res/values/colors.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```

## File: app/src/main/res/values/ic_launcher_background.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#6366F1</color>
</resources>
```

## File: app/src/main/res/xml/backup_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
    Backup rules for Android < 12 (API < 31).
    Exclude all game data so a fresh install always starts clean.
-->
<full-backup-content>
    <!-- All DataStore preference files (game state, streak, score, theme) -->
    <exclude domain="file" path="datastore/"/>
    <!-- Room statistics database -->
    <exclude domain="database" path="sudoku_database"/>
</full-backup-content>
```

## File: app/src/main/res/xml/data_extraction_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
    Data extraction rules for Android 12+ (API 31+).
    Exclude all game data from both cloud backup and device transfer
    so a fresh install always starts clean.
-->
<data-extraction-rules>
    <cloud-backup>
        <!-- All DataStore preference files (game state, streak, score, theme) -->
        <exclude domain="file" path="datastore/"/>
        <!-- Room statistics database -->
        <exclude domain="database" path="sudoku_database"/>
    </cloud-backup>
    <device-transfer>
        <!-- All DataStore preference files -->
        <exclude domain="file" path="datastore/"/>
        <!-- Room statistics database -->
        <exclude domain="database" path="sudoku_database"/>
    </device-transfer>
</data-extraction-rules>
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/LessonCardColumn.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.domain.model.LessonData

@Composable
fun LessonCardColumn(lesson: LessonData, stepNumber: Int) {

    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left side: Column grid - fixed width
        Box(
            modifier = Modifier.width(140.dp),
            contentAlignment = Alignment.Center
        ) {
            VisualLessonGrid(grid = lesson.visualGrid)
        }

        // Right side: Content - takes remaining space
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Step badge
            Text(
                text = "Lesson $stepNumber",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primary,
                modifier = Modifier
                    .background(
                        color = colors.primary.copy(alpha = 0.1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = lesson.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.onBackground,
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp
            )
            Spacer(Modifier.height(12.dp))

            // Body
            Text(
                text = lesson.body,
                fontSize = 16.sp,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Start,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/LessonCardDefault.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LessonCardDefault(lesson: com.quickmindgames.sudoku.domain.model.LessonData, stepNumber: Int) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Step badge
        Text(
            text = "Lesson $stepNumber",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.primary,
            modifier = Modifier
                .background(
                    color = colors.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = lesson.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.onBackground,
            textAlign = TextAlign.Center,
            letterSpacing = 0.3.sp
        )
        Spacer(Modifier.height(8.dp))

        // Visual grid representation
        VisualLessonGrid(grid = lesson.visualGrid)
        Spacer(Modifier.height(12.dp))

        // Body
        Text(
            text = lesson.body,
            fontSize = 16.sp,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/LessonGridCell.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LessonGridCell(digit: Int, numColumns: Int, numRows: Int = 1) {
    // Calculate cell size based on number of columns and rows
    val cellSize = when {
        numColumns >= 9 -> 36.dp    // 9 cells per row (horizontal)
        numColumns == 1 && numRows >= 9 -> 28.dp  // 9 cells per column (vertical) - smaller
        numColumns >= 3 -> 44.dp    // 3 cells per row
        else -> 52.dp               // 1 cell per row
    }

    val fontSize = when {
        numColumns >= 9 -> 14.sp
        numColumns == 1 && numRows >= 9 -> 12.sp  // Smaller font for vertical
        else -> 18.sp
    }
    val lessonCellShape = RoundedCornerShape(6.dp)
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(cellSize)
            .border(2.dp, colors.primary, lessonCellShape)
            .background(
                if (digit == 0)
                    colors.surfaceVariant
                else
                    colors.surface,
                lessonCellShape
            )
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (digit == 0) "?" else digit.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/NumberPad.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.data.state.CellData

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun NumberPad(
    grid: List<List<CellData>>,
    enabled: Boolean = true,
    hideUsedNumbers: Boolean = false,
    onNumberClick: (Int) -> Unit
) {
    // Count how many times each number (1–9) appears in the grid.
    val numberCounts = IntArray(10)
    for (row in grid) for (cell in row) if (cell.value in 1..9) numberCounts[cell.value]++
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..9).forEach { number ->
            val isComplete = numberCounts[number] >= 9
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                if (!hideUsedNumbers || !isComplete) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (enabled) Modifier.clickable { onNumberClick(number) } else Modifier),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = if (enabled) 6.dp else 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.5f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/VisualLessonGrid.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun VisualLessonGrid(grid: List<List<Int>>) {
    val numColumns = grid.firstOrNull()?.size ?: 0
    val numRows = grid.size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        grid.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { digit ->
                    LessonGridCell(digit = digit, numColumns = numColumns, numRows = numRows)
                }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/database/dao/StreakGameDao.kt
```kotlin
package com.quickmindgames.sudoku.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.quickmindgames.sudoku.data.database.entity.StreakGameEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for StreakGameEntity
 */
@Dao
interface StreakGameDao {

    /**
     * Insert a new streak game record
     */
    @Insert
    suspend fun insertStreakGame(game: StreakGameEntity)

    /**
     * Get all streak games ordered by date (descending)
     */
    @Query("SELECT * FROM streak_games ORDER BY date_completed DESC")
    fun getAllStreakGames(): Flow<List<StreakGameEntity>>

    /**
     * Get streak games for a specific day range
     */
    @Query("SELECT * FROM streak_games WHERE streak_day >= :startDay AND streak_day <= :endDay ORDER BY streak_day ASC")
    fun getStreakGamesByDayRange(startDay: Int, endDay: Int): Flow<List<StreakGameEntity>>

    /**
     * Get total streak games count
     */
    @Query("SELECT COUNT(*) FROM streak_games")
    fun getTotalStreakGamesCount(): Flow<Int>

    /**
     * Get total wins in streak mode
     */
    @Query("SELECT COUNT(*) FROM streak_games WHERE is_won = 1")
    fun getStreakWinsCount(): Flow<Int>

    /**
     * Get total losses in streak mode
     */
    @Query("SELECT COUNT(*) FROM streak_games WHERE is_won = 0")
    fun getStreakLossesCount(): Flow<Int>

    /**
     * Get best time in streak mode (across all days)
     */
    @Query("SELECT MIN(time_seconds) FROM streak_games WHERE is_won = 1")
    fun getBestStreakTime(): Flow<Int?>

    /**
     * Get best score in streak mode (across all days)
     */
    @Query("SELECT MAX(score) FROM streak_games WHERE is_won = 1")
    fun getBestStreakScore(): Flow<Int?>

    /**
     * Get the highest streak day reached by winning a game
     */
    @Query("SELECT MAX(streak_day) FROM streak_games WHERE is_won = 1")
    fun getHighestStreakDay(): Flow<Int?>

    /**
     * Get consecutive wins from most recent games
     * Returns total streak wins (user needs to track sequence in ViewModel)
     */
    @Query("""
        SELECT COUNT(*) FROM streak_games WHERE is_won = 1
    """)
    fun getCurrentConsecutiveWins(): Flow<Int>

    /**
     * Delete all streak games (for testing/reset)
     */
    @Query("DELETE FROM streak_games")
    suspend fun deleteAllStreakGames()
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/state/GameState.kt
```kotlin
// This file has been moved to data/state/GameState.kt

package com.quickmindgames.sudoku.data.state

import com.quickmindgames.sudoku.domain.model.Difficulty

/**
 * Represents the complete state of a Sudoku game
 */
data class GameState(
    val difficulty: Difficulty,
    val originalGrid: List<List<Int>>,
    val solutionGrid: List<List<Int>>,
    val userGrid: List<List<CellData>>,
    val wrongCells: Set<Pair<Int, Int>>,
    val mistakes: Int,
    val score: Int,
    val timeSeconds: Int,
    val selectedCell: Pair<Int, Int>?,
    val isNotesMode: Boolean = false,
    val hintsUsed: Int = 0,
    val hasDoublePointsBooster: Boolean = false
) {
    /**
     * Check if the game has been started (timer has ticked at least once)
     */
    fun hasStarted(): Boolean = timeSeconds > 0

    /**
     * Serialize to string for DataStore
     */
    fun serialize(): String {
        return buildString {
            // Difficulty
            append("DIFFICULTY:${difficulty.name}\n")

            // Original Grid (9 lines)
            append("ORIGINAL:\n")
            originalGrid.forEach { row ->
                append(row.joinToString(","))
                append("\n")
            }

            // Solution Grid (9 lines)
            append("SOLUTION:\n")
            solutionGrid.forEach { row ->
                append(row.joinToString(","))
                append("\n")
            }

            // User Grid (9 lines, each cell as value|note1,note2,note3,note4)
            append("USER:\n")
            userGrid.forEach { row ->
                append(row.joinToString(",") { cell ->
                    val notesStr = if (cell.notes.isNotEmpty()) cell.notes.joinToString("|") else ""
                    "${cell.value}${if (notesStr.isNotEmpty()) "|$notesStr" else ""}"
                })
                append("\n")
            }

            // Wrong Cells
            append("WRONG:${wrongCells.joinToString(";") { "${it.first},${it.second}" }}\n")

            // Mistakes
            append("MISTAKES:$mistakes\n")

            // Score
            append("SCORE:$score\n")

            // Time
            append("TIME:$timeSeconds\n")

            // Selected Cell
            append("SELECTED:${selectedCell?.let { "${it.first},${it.second}" } ?: "null"}\n")
            append("NOTES_MODE:$isNotesMode\n") // NEW: persist notes mode
            append("HINTS_USED:").append(hintsUsed.coerceAtLeast(0)).append('\n')
            append("DOUBLE_POINTS_BOOSTER:$hasDoublePointsBooster\n")
        }
    }

    companion object {
        /**
         * Deserialize from string
         */
        fun deserialize(data: String): GameState? {
            return try {
                val lines = data.split("\n")
                var index = 0

                // Parse difficulty
                val difficultyLine = lines[index++]
                val difficulty = Difficulty.valueOf(difficultyLine.substringAfter("DIFFICULTY:"))

                // Parse original grid
                index++ // Skip "ORIGINAL:" header
                val originalGrid = mutableListOf<List<Int>>()
                repeat(9) {
                    originalGrid.add(lines[index++].split(",").map { it.toInt() })
                }

                // Parse solution grid
                index++ // Skip "SOLUTION:" header
                val solutionGrid = mutableListOf<List<Int>>()
                repeat(9) {
                    solutionGrid.add(lines[index++].split(",").map { it.toInt() })
                }

                // Parse user grid
                index++ // Skip "USER:" header
                val userGrid = mutableListOf<List<CellData>>()
                repeat(9) {
                    userGrid.add(lines[index++].split(",").map { cellStr ->
                        val parts = cellStr.split("|")
                        val value = parts[0].toIntOrNull() ?: 0
                        val notes =
                            if (parts.size > 1) parts.drop(1).mapNotNull { it.toIntOrNull() }
                                .toSet() else emptySet()
                        CellData(value, notes)
                    })
                }

                // Parse wrong cells
                val wrongLine = lines[index++]
                val wrongCells = wrongLine.substringAfter("WRONG:")
                    .split(";")
                    .filter { it.isNotEmpty() }
                    .map {
                        val parts = it.split(",")
                        parts[0].toInt() to parts[1].toInt()
                    }
                    .toSet()

                // Parse mistakes
                val mistakes = lines[index++].substringAfter("MISTAKES:").toInt()

                // Parse score
                val score = lines[index++].substringAfter("SCORE:").toInt()

                // Parse time
                val timeSeconds = lines[index++].substringAfter("TIME:").toInt()

                // Parse selected cell
                val selectedLine = lines[index++].substringAfter("SELECTED:")
                val selectedCell = if (selectedLine == "null") {
                    null
                } else {
                    val parts = selectedLine.split(",")
                    parts[0].toInt() to parts[1].toInt()
                }
                // Parse notes mode
                val notesModeLine = lines[index++].substringAfter("NOTES_MODE:")
                val isNotesMode = notesModeLine.toBooleanStrictOrNull() ?: false

                // HINTS_USED is optional so saves created before wallet hints remain readable.
                // Old HINTS values are intentionally ignored because they represented free hints.
                val hintsUsed = lines.drop(index)
                    .firstOrNull { it.startsWith("HINTS_USED:") }
                    ?.substringAfter("HINTS_USED:")
                    ?.toIntOrNull()
                    ?.coerceAtLeast(0)
                    ?: 0
                val hasDoublePointsBooster = lines.drop(index)
                    .firstOrNull { it.startsWith("DOUBLE_POINTS_BOOSTER:") }
                    ?.substringAfter("DOUBLE_POINTS_BOOSTER:")
                    ?.toBooleanStrictOrNull()
                    ?: false
                GameState(
                    difficulty = difficulty,
                    originalGrid = originalGrid,
                    solutionGrid = solutionGrid,
                    userGrid = userGrid,
                    wrongCells = wrongCells,
                    mistakes = mistakes,
                    score = score,
                    timeSeconds = timeSeconds,
                    selectedCell = selectedCell,
                    isNotesMode = isNotesMode,
                    hintsUsed = hintsUsed,
                    hasDoublePointsBooster = hasDoublePointsBooster
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class CellData(val value: Int, val notes: Set<Int> = emptySet())
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/state/StreakState.kt
```kotlin
package com.quickmindgames.sudoku.data.state

import android.util.Base64
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Represents the state of the Streak (daily) challenge.
 */
data class StreakState(
    val streakCount: Int,
    val lastCompletedDate: String?,
    val activeDate: String?,
    val activeGame: GameState?
) {
    fun protectMissedDay(today: LocalDate): StreakState? {
        val completedDate = lastCompletedDate?.let(LocalDate::parse) ?: return null
        if (streakCount <= 0 || ChronoUnit.DAYS.between(completedDate, today) != 2L) {
            return null
        }
        return copy(lastCompletedDate = completedDate.plusDays(1).toString())
    }

    fun serialize(): String {
        val encodedGame = activeGame
            ?.serialize()
            ?.encodeToByteArray()
            ?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
            ?: "null"

        return buildString {
            append("COUNT:$streakCount\n")
            append("LAST_COMPLETED:${lastCompletedDate ?: "null"}\n")
            append("ACTIVE_DATE:${activeDate ?: "null"}\n")
            append("ACTIVE_GAME:$encodedGame\n")
        }
    }

    companion object {
        fun deserialize(data: String): StreakState? {
            return try {
                val lines = data.split("\n")
                var index = 0

                val count = lines[index++].substringAfter("COUNT:").toInt()

                val lastCompleted = lines[index++]
                    .substringAfter("LAST_COMPLETED:")
                    .takeIf { it != "null" }

                val activeDate = lines[index++]
                    .substringAfter("ACTIVE_DATE:")
                    .takeIf { it != "null" }

                val encodedGame = lines[index++].substringAfter("ACTIVE_GAME:")
                val activeGame = if (encodedGame == "null") {
                    null
                } else {
                    val decoded = Base64.decode(encodedGame, Base64.NO_WRAP)
                    GameState.deserialize(decoded.decodeToString())
                }

                StreakState(
                    streakCount = count,
                    lastCompletedDate = lastCompleted,
                    activeDate = activeDate,
                    activeGame = activeGame
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/state/StreakStateManager.kt
```kotlin
package com.quickmindgames.sudoku.data.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.streakDataStore: DataStore<Preferences> by preferencesDataStore(name = "streak_state")

class StreakStateManager(private val context: Context) {

    private val STREAK_STATE_KEY = stringPreferencesKey("streak_state")
    private val REMINDER_SHOWN_DATE_KEY = stringPreferencesKey("reminder_shown_date")
    private val REMINDER_SHOWN_KEY = booleanPreferencesKey("reminder_shown")

    suspend fun saveStreakState(streakState: StreakState) {
        context.streakDataStore.edit { preferences ->
            preferences[STREAK_STATE_KEY] = streakState.serialize()
        }
        AppPreferences.getInstance(context).processStreakMilestones(streakState.streakCount)
    }

    fun getStreakState(): Flow<StreakState?> {
        return context.streakDataStore.data.map { preferences ->
            preferences[STREAK_STATE_KEY]?.let { serialized ->
                StreakState.deserialize(serialized)
            }
        }
    }

    suspend fun setReminderShownDate(date: String) {
        context.streakDataStore.edit { preferences ->
            preferences[REMINDER_SHOWN_DATE_KEY] = date
        }
    }

    suspend fun setReminderShown(shown: Boolean) {
        context.streakDataStore.edit { preferences ->
            preferences[REMINDER_SHOWN_KEY] = shown
        }
    }

    fun getReminderShown(): Flow<Boolean> {
        return context.streakDataStore.data.map { preferences ->
            preferences[REMINDER_SHOWN_KEY] ?: false
        }
    }

    fun getReminderShownDate(): Flow<String?> {
        return context.streakDataStore.data.map { preferences ->
            preferences[REMINDER_SHOWN_DATE_KEY]
        }
    }

    suspend fun clearStreakState() {
        context.streakDataStore.edit { preferences ->
            preferences.remove(STREAK_STATE_KEY)
        }
    }

    companion object {
        @Volatile
        private var instance: StreakStateManager? = null

        fun getInstance(context: Context): StreakStateManager {
            return instance ?: synchronized(this) {
                instance ?: StreakStateManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/util/GameFeedback.kt
```kotlin
package com.quickmindgames.sudoku.data.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import com.quickmindgames.sudoku.R

class GameFeedback(context: Context) {

    private val lock = Any()
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private val vibrator = context.applicationContext.getSystemService(Vibrator::class.java)
    private val loadedSounds = mutableSetOf<Int>()
    private val pendingSounds = mutableListOf<Int>()
    private var released = false

    private val correctSound: Int
    private val wrongSound: Int
    private val completedSound: Int
    private val gameOverSound: Int
    private val spinWheelSound: Int

    init {
        soundPool.setOnLoadCompleteListener { pool, sampleId, status ->
            synchronized(lock) {
                if (status == 0 && !released) {
                    loadedSounds.add(sampleId)
                    val pendingCount = pendingSounds.count { it == sampleId }
                    repeat(pendingCount) {
                        pool.play(sampleId, 1f, 1f, 1, 0, 1f)
                    }
                }
                pendingSounds.removeAll { it == sampleId }
            }
        }
        correctSound = soundPool.load(context.applicationContext, R.raw.number_correct, 1)
        wrongSound = soundPool.load(context.applicationContext, R.raw.number_wrong, 1)
        completedSound = soundPool.load(context.applicationContext, R.raw.puzzle_complete, 1)
        gameOverSound = soundPool.load(context.applicationContext, R.raw.game_over, 1)
        spinWheelSound = soundPool.load(context.applicationContext, R.raw.spin_wheel, 1)
    }

    fun playCorrectNumber(enabled: Boolean) {
        if (enabled) playSound(correctSound)
    }

    fun playWrongNumber(enabled: Boolean) {
        if (enabled) {
            playSound(wrongSound)
            vibrate(durationMillis = 100, amplitude = 156)
        }
    }

    fun playPuzzleCompleted(enabled: Boolean) {
        if (enabled) {
            playSound(completedSound)
            vibrate(durationMillis = 35, amplitude = 64)
        }
    }

    fun playGameOver(enabled: Boolean){
        if (enabled) {
            playSound(gameOverSound)
            vibrate(durationMillis = 35, amplitude = 64)
        }
    }

    fun playSpinWheel(enabled: Boolean) {
        if (enabled) playSound(spinWheelSound)
    }

    fun playPrizeCredited(enabled: Boolean) {
        if (enabled) playSound(completedSound)
    }

    fun release() {
        synchronized(lock) {
            if (released) return
            released = true
            pendingSounds.clear()
            soundPool.release()
        }
    }

    private fun playSound(soundId: Int) {
        synchronized(lock) {
            if (released) return
            if (soundId in loadedSounds) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            } else {
                pendingSounds.add(soundId)
            }
        }
    }

    private fun vibrate(durationMillis: Long, amplitude: Int) {
        vibrator
            ?.takeIf { it.hasVibrator() }
            ?.vibrate(VibrationEffect.createOneShot(durationMillis, amplitude))
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/domain/model/LessonData.kt
```kotlin
package com.quickmindgames.sudoku.domain.model

/**
 * Represents a single lesson in Learning Mode.
 */
data class LessonData(
    val id: Int = 0, // Optional ID for future use
    val title: String,
    val body: String,
    val illustrationEmoji: String,
    val visualGrid: List<List<Int>> // Grid of digits (0 = empty for now)
)

/**
 * All 5 lessons — always shown in order, starting from the first.
 * No progress tracking by design.
 */
val LESSONS: List<LessonData> = listOf(
    LessonData(
        id = 1,
        title = "Row Rule",
        body = "Each row must contain the numbers 1–9 exactly once. " +
                "Look across any row — if a number is already there, it can't appear again.",
        illustrationEmoji = "➡️",
        // Single row with all 9 digits
        visualGrid = listOf(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        )
    ),
    LessonData(
        id = 2,
        title = "Column Rule",
        body = "Each column must contain the numbers 1–9 exactly once. " +
                "Scan top to bottom — no repeats allowed in a column.",
        illustrationEmoji = "⬇️",
        // Single column with all 9 digits (represented as 9 rows, 1 column)
        visualGrid = listOf(
            listOf(1),
            listOf(2),
            listOf(3),
            listOf(4),
            listOf(5),
            listOf(6),
            listOf(7),
            listOf(8),
            listOf(9)
        )
    ),
    LessonData(
        id = 3,
        title = "3×3 Box Rule",
        body = "Each of the nine 3×3 boxes must contain the numbers 1–9 exactly once. " +
                "Check the thick-bordered box around a cell before placing a number.",
        illustrationEmoji = "🔲",
        // 3x3 box with all 9 digits
        visualGrid = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )
    ),
    LessonData(
        id = 4,
        title = "Missing Number",
        body = "When a row, column, or box has 8 of 9 numbers filled, " +
                "the missing number is the only one that can go in the empty cell. Easy win!",
        illustrationEmoji = "🔍",
        // A row with 8 numbers and one empty (0)
        visualGrid = listOf(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
        )
    ),
    LessonData(
        id = 5,
        title = "Cross Checking",
        body = "Combine the row, column, and box rules together. " +
                "Eliminate numbers that already appear in the same row, column, or box — " +
                "the last remaining option is your answer.",
        illustrationEmoji = "✨",
        // A 3x3 grid showing all numbers 1-9
        visualGrid = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )
    )
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/navigation/BottomScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom Navigation Screen Definitions
 * Defines the main bottom navigation tabs
 */
sealed class BottomScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomScreen("home", "Home", Icons.Default.Home)
    object Streak : BottomScreen("streak", "Streak", Icons.Default.Timeline)
    object Rewards : BottomScreen("rewards", "Rewards", Icons.Default.CardGiftcard)
    object Settings : BottomScreen("settings", "Settings", Icons.Default.Settings)
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/viewmodel/StatisticsViewModel.kt
```kotlin
package com.quickmindgames.sudoku.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickmindgames.sudoku.data.repository.StatisticsRepository
import com.quickmindgames.sudoku.domain.model.AllStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for managing statistics UI state
 * Uses lazy repository initialization
 */
class StatisticsViewModel(context: Context) : ViewModel() {

    private val repository by lazy {
        StatisticsRepository(context)
    }

    private val _allStatistics = MutableStateFlow(AllStatistics())
    val allStatistics: StateFlow<AllStatistics> = _allStatistics.asStateFlow()

    private val _hasLoadedStatistics = MutableStateFlow(false)
    val hasLoadedStatistics: StateFlow<Boolean> = _hasLoadedStatistics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshStatistics()
    }

    /**
     * Refresh statistics from database
     * Called on screen focus or manually
     */
    fun refreshStatistics() {
        _hasLoadedStatistics.value = false
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Use .first() to get one value and complete
                val stats = repository.getAllStatistics().first()
                _allStatistics.value = stats
                _hasLoadedStatistics.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/ShareUtils.kt
```kotlin
package com.quickmindgames.sudoku.utils

import android.content.Context
import android.content.Intent
import com.quickmindgames.sudoku.R

fun Context.shareStreak(streakCount: Int) {
    AnalyticsUtils.logStreakShare(this, streakCount)
    val shareText = buildString {
        append("\uD83D\uDD25 I'm on a $streakCount day streak in ${getString(R.string.app_name)}!\n")
        append("Can you beat my streak? Play now:\n")
        append("https://play.google.com/store/apps/details?id=$packageName")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    startActivity(Intent.createChooser(intent, "Share your streak"))
}
```

## File: app/src/main/res/values/themes.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="Theme.Sudoku" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

## File: settings.gradle.kts
```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Sudoku"
include(":app")
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/DifficultyCard.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.domain.model.Difficulty

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    onClick: () -> Unit,
    isLocked: Boolean = false,
    onLockedClick: () -> Unit = {}
) {
    val (emoji, description) = when (difficulty) {
        Difficulty.Breeze -> Pair("🌤️", "Perfect for beginners")
        Difficulty.Pulse -> Pair("⚡", "Moderate challenge")
        Difficulty.Rage -> Pair("🔥", "Test your skills")
        Difficulty.Elite -> Pair("💎", "For masters only")
    }

    val cardColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFFDDEAFE) // Light Blue
        Difficulty.Pulse -> Color(0xFFFEF3C7) // Light Yellow
        Difficulty.Rage -> Color(0xFFFED7D7) // Light Red/Pink
        Difficulty.Elite -> Color(0xFFE9D5FF) // Light Purple
    }

    val accentColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFF3B82F6)
        Difficulty.Pulse -> Color(0xFFF59E0B)
        Difficulty.Rage -> Color(0xFFEF4444)
        Difficulty.Elite -> Color(0xFF8B5CF6)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.5f else 1f)
            .clickable(
                enabled = true,
                onClick = {
                    if (isLocked) {
                        onLockedClick()
                    } else {
                        onClick()
                    }
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = if (isLocked) 0.dp else 8.dp
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji icon with background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 32.sp
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = difficulty.label,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Arrow indicator or lock icon
                if (isLocked) {
                    Text(text = "🔒", fontSize = 24.sp)
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Select ${difficulty.label}",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Lock overlay for locked state
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/LearningCompleteDialog.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Congratulation dialog shown when the user completes a Learning Mode puzzle.
 * Encourages the user to try normal game modes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningCompleteDialog(
    onTryNormalMode: () -> Unit,
    onBackToHome: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ── Gradient header ──────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00897B), Color(0xFF43A047))
                            ),
                            RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                        )
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉", fontSize = 52.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Excellent Work!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "You completed your learning puzzle!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // ── Encouragement text ─────────────────────────
                Text(
                    text = "You've got the skills.\nLet's jump into a real game! \uD83D\uDE80",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                )

                // ── Buttons ────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onTryNormalMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start Game", fontWeight = FontWeight.SemiBold)
                    }

                    FilledTonalButton(
                        onClick = onBackToHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Back to Home", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/PrizeWheel.kt
```kotlin
package com.quickmindgames.sudoku.component

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quickmindgames.sudoku.R

enum class PrizeType {
    POINTS,
    HINTS,
    BOOSTER,
    STREAK_SHIELD
}

// Add custom prizes with their own wheel text, type, and award amount.
data class PrizeWheelPrize(
    val label: String,
    val type: PrizeType,
    val amount: Int,
    val weight: Int = 1
) {
    init {
        require(weight > 0) { "Prize weight must be greater than zero." }
    }
}

val PrizeWheelPrizes = listOf(
    PrizeWheelPrize(label = "+10", type = PrizeType.POINTS, amount = 10, weight = 4),
    PrizeWheelPrize(label = "+20", type = PrizeType.POINTS, amount = 20, 3),
    PrizeWheelPrize(label = "+50", type = PrizeType.POINTS, amount = 50, weight = 2),
    PrizeWheelPrize(label = "+100", type = PrizeType.POINTS, amount = 100),
    PrizeWheelPrize(label = "💡", type = PrizeType.HINTS, amount = 1),
    PrizeWheelPrize(label = "2X", type = PrizeType.BOOSTER, amount = 1),
    PrizeWheelPrize(label = "🔥", type = PrizeType.STREAK_SHIELD, amount = 1)
)

fun weightedPrizeIndex(
    prizes: List<PrizeWheelPrize>,
    random: kotlin.random.Random = kotlin.random.Random.Default
): Int {
    require(prizes.isNotEmpty()) { "At least one prize is required." }
    val totalWeight = prizes.sumOf { it.weight }
    var ticket = random.nextInt(totalWeight)
    return prizes.indexOfFirst { prize ->
        ticket -= prize.weight
        ticket < 0
    }
}

@Preview
@Composable
fun PrizeWheel(
    rotation: Float = 0f,
    isSpinning: Boolean = false,
    canSpin: Boolean = true,
    cooldownLabel: String? = null,
    onSpin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val prizes = PrizeWheelPrizes
    val context = LocalContext.current
    val boldTypeface = remember(context) { context.resources.getFont(R.font.bold) }

    /*val segmentColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.error
    )*/
    val segmentColors = listOf(
        Color(0xFF263238), // blue grey
        Color(0xFF4A148C), // deep purple
        Color(0xFF006064), // dark teal
        Color(0xFF1B5E20), // dark green
        Color(0xFF880E4F)  // deep magenta
    )
    val pointerColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)

    BoxWithConstraints(
        modifier = modifier
            // These layers form the raised gold rim around the colored wheel.
            .shadow(16.dp, CircleShape)
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFFE082), Color(0xFFFFA000), Color(0xFFFFF3C4))
                ),
                shape = CircleShape
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            val radius = size.minDimension / 2f
            // Equal slices are centered from the top, then advance clockwise.
            val segmentAngle = 360f / prizes.size
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                // Adjust these relative sizes to scale the labels with the wheel.
                textSize = radius * 0.17f
                typeface = boldTypeface
                setShadowLayer(radius * 0.025f, 0f, radius * 0.018f, 0x99000000.toInt())
            }
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = radius * 0.07f
                typeface = boldTypeface
            }

            prizes.forEachIndexed { index, prize ->
                val startAngle = -90f + index * segmentAngle
                val baseColor = segmentColors[index % segmentColors.size]
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            tintToward(baseColor, Color.White, 0.32f),
                            baseColor,
                            tintToward(baseColor, Color.Black, 0.18f)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    startAngle = startAngle,
                    sweepAngle = segmentAngle,
                    useCenter = true
                )

                val labelAngle = Math.toRadians((startAngle + segmentAngle / 2f).toDouble())
                // Move labels toward the rim by increasing this ratio; decrease it to pull them inward.
                val labelRadius = radius * 0.65f
                val labelX = center.x + labelRadius * kotlin.math.cos(labelAngle).toFloat()
                val labelY = center.y + labelRadius * kotlin.math.sin(labelAngle).toFloat()
                val prizeCaption = when (prize.type) {
                    PrizeType.POINTS, PrizeType.BOOSTER -> "POINTS"
                    PrizeType.HINTS -> "HINT"
                    PrizeType.STREAK_SHIELD -> "SHIELD"
                }
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        prize.label,
                        labelX,
                        labelY - (labelPaint.ascent() + labelPaint.descent()) / 2f,
                        labelPaint
                    )
                    canvas.nativeCanvas.drawText(
                        prizeCaption,
                        labelX,
                        // This vertical offset controls the gap beneath the prize amount.
                        labelY + radius * 0.18f,
                        subtitlePaint
                    )
                }
            }

            // Slice dividers and concentric strokes define the wheel face and inner rim.
            for (index in prizes.indices) {
                val angle = Math.toRadians((-90f + index * segmentAngle).toDouble())
                val end = Offset(
                    center.x + radius * kotlin.math.cos(angle).toFloat(),
                    center.y + radius * kotlin.math.sin(angle).toFloat()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.82f),
                    start = center,
                    end = end,
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Soft radial overlay gives the flat segments a glossy highlight.
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = radius * 0.94f,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = outlineColor,
                radius = radius,
                style = Stroke(width = 1.5.dp.toPx())
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.23f),
                        Color.White.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(radius * 0.38f, radius * 0.28f),
                    radius = radius * 1.15f
                ),
                radius = radius
            )

            // Layered circles and centered text make the raised center hub.
            val hubRadius = radius * 0.19f
            drawCircle(
                color = Color.Black.copy(alpha = 0.22f),
                radius = hubRadius * 1.12f,
                center = center.copy(y = center.y + 2.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFFD54F), Color(0xFFEF6C00)),
                    radius = hubRadius * 1.8f
                ),
                radius = hubRadius
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = hubRadius * 0.82f,
                style = Stroke(width = 1.5.dp.toPx())
            )
            labelPaint.clearShadowLayer()
        }

        // The center hub is the spin action; it remains stationary as the wheel face rotates.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(if (cooldownLabel == null) maxWidth * 0.38f else maxWidth)
                .clip(CircleShape)
                .then(
                    if (cooldownLabel != null) {
                        Modifier.background(
                            MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.90f),
                            CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable(
                    enabled = canSpin && !isSpinning,
                    role = Role.Button,
                    onClick = onSpin
                )
                .semantics {
                    contentDescription = when {
                        isSpinning -> "Wheel is spinning"
                        cooldownLabel != null -> "Next spin in $cooldownLabel"
                        !canSpin -> "Wheel spin is on cooldown"
                        else -> "Spin the wheel"
                    }
                    role = Role.Button
                },
            contentAlignment = Alignment.Center
        ) {
            if (cooldownLabel != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Next Spin in",
                        color = Color(0xFF5D3100),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = cooldownLabel,
                        color = Color(0xFF5D3100),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            } else {
                Text(
                    text = "SPIN",
                    color = Color(0xFF5D3100),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily(Font(R.font.bold))
                    ),
                )
            }
        }

        // The pointer sits outside the rotating Canvas so it stays fixed while the wheel spins.
        Canvas(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = 42.dp, height = 38.dp)
                .semantics { contentDescription = "Wheel pointer" }
        ) {
            val pointer = Path().apply {
                moveTo(size.width * 0.12f, 0f)
                lineTo(size.width * 0.88f, 0f)
                lineTo(size.width * 0.78f, size.height * 0.58f)
                lineTo(size.width * 0.5f, size.height)
                lineTo(size.width * 0.22f, size.height * 0.58f)
                close()
            }
            val pointerShadow = Path().apply {
                moveTo(size.width * 0.12f, 2.dp.toPx())
                lineTo(size.width * 0.88f, 2.dp.toPx())
                lineTo(size.width * 0.78f, size.height * 0.58f + 2.dp.toPx())
                lineTo(size.width * 0.5f, size.height + 2.dp.toPx())
                lineTo(size.width * 0.22f, size.height * 0.58f + 2.dp.toPx())
                close()
            }
            drawPath(pointerShadow, color = Color.Black.copy(alpha = 0.3f))
            drawPath(
                pointer,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFFF3B0), Color(0xFFFFB300), Color(0xFFFF6F00))
                )
            )
            drawPath(
                pointer,
                color = pointerColor.copy(alpha = 0.7f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

// Lighten or darken a segment color to create its gradient without hard-coded palette variants.
private fun tintToward(color: Color, target: Color, amount: Float): Color =
    Color(
        red = color.red + (target.red - color.red) * amount,
        green = color.green + (target.green - color.green) * amount,
        blue = color.blue + (target.blue - color.blue) * amount,
        alpha = color.alpha + (target.alpha - color.alpha) * amount
    )
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/SudokuGridCanvas.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SudokuGridCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cellSize = size.width / 9f
        // Draw minor grid lines
        for (i in 1 until 9) {
            val stroke = if (i % 3 == 0) 3f else 1f
            // Vertical
            drawLine(
                color = Color.Gray,
                start = Offset(x = i * cellSize, y = 0f),
                end = Offset(x = i * cellSize, y = size.height),
                strokeWidth = stroke
            )
            // Horizontal
            drawLine(
                color = Color.Gray,
                start = Offset(x = 0f, y = i * cellSize),
                end = Offset(x = size.width, y = i * cellSize),
                strokeWidth = stroke
            )
        }
        // Draw outer border
        drawRect(
            color = Color.Black,
            topLeft = Offset.Zero,
            size = size,
            style = Stroke(width = 3f)
        )
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/repository/StatisticsRepository.kt
```kotlin
package com.quickmindgames.sudoku.data.repository

import android.content.Context
import com.quickmindgames.sudoku.data.database.SudokuDatabase
import com.quickmindgames.sudoku.data.database.entity.GameStatisticEntity
import com.quickmindgames.sudoku.data.database.entity.StreakGameEntity
import com.quickmindgames.sudoku.domain.model.AllStatistics
import com.quickmindgames.sudoku.domain.model.DifficultyStatistics
import com.quickmindgames.sudoku.domain.model.GameStatistics
import com.quickmindgames.sudoku.domain.model.StreakStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Repository for managing game statistics
 * Uses lazy singleton pattern for database initialization
 */
class StatisticsRepository(context: Context) {

    private val db: SudokuDatabase by lazy {
        SudokuDatabase.getInstance(context)
    }

    private val gameStatisticDao by lazy { db.gameStatisticDao() }
    private val streakGameDao by lazy { db.streakGameDao() }

    /**
     * Record a completed regular game
     */
    suspend fun recordGameCompletion(
        difficulty: String,
        isWon: Boolean,
        mistakes: Int,
        score: Int,
        timeSeconds: Int
    ) {
        val statistic = GameStatisticEntity(
            difficulty = difficulty,
            isWon = isWon,
            mistakes = mistakes,
            score = score,
            timeSeconds = timeSeconds,
            isFlawFree = mistakes == 0,
            dateCompleted = System.currentTimeMillis()
        )
        gameStatisticDao.insertGameStatistic(statistic)
    }

    /**
     * Record a completed streak game
     */
    suspend fun recordStreakGameCompletion(
        streakDay: Int,
        isWon: Boolean,
        score: Int,
        timeSeconds: Int
    ) {
        val streakGame = StreakGameEntity(
            streakDay = streakDay,
            isWon = isWon,
            score = score,
            timeSeconds = timeSeconds,
            dateCompleted = System.currentTimeMillis()
        )
        streakGameDao.insertStreakGame(streakGame)
    }

    /**
     * Get overall statistics across all games
     */
    fun getOverallStatistics(): Flow<GameStatistics> {
        return combine(
            gameStatisticDao.getTotalGamesCount(),
            gameStatisticDao.getTotalWinsCount(),
            gameStatisticDao.getTotalLossesCount(),
            gameStatisticDao.getBestTimeOverall(),
            gameStatisticDao.getBestScoreOverall(),
            gameStatisticDao.getFlawFreeWinsCountOverall()
        ) { values ->
            val totalGames = values[0] as Int
            val wins = values[1] as Int
            val losses = values[2] as Int
            val bestTime = values[3]
            val bestScore = values[4]
            val flawFree = values[5] as Int

            val winRatio = if (totalGames > 0) (wins.toDouble() / totalGames) * 100 else 0.0
            GameStatistics(
                totalGames = totalGames,
                totalWins = wins,
                totalLosses = losses,
                winRatioPercentage = winRatio,
                bestTime = bestTime,
                bestScore = bestScore,
                flawFreeWins = flawFree
            )
        }
    }

    /**
     * Get statistics for a specific difficulty
     */
    fun getDifficultyStatistics(difficulty: String): Flow<DifficultyStatistics> {
        return combine(
            gameStatisticDao.getTotalGamesByDifficulty(difficulty),
            gameStatisticDao.getWinsCountByDifficulty(difficulty),
            gameStatisticDao.getLossesCountByDifficulty(difficulty),
            gameStatisticDao.getBestTimeByDifficulty(difficulty),
            gameStatisticDao.getBestScoreByDifficulty(difficulty),
            gameStatisticDao.getFlawFreeWinsCountByDifficulty(difficulty)
        ) { values ->
            val totalGames = values[0] as Int
            val wins = values[1] as Int
            val losses = values[2] as Int
            val bestTime = values[3]
            val bestScore = values[4]
            val flawFree = values[5] as Int

            val winRatio = if (totalGames > 0) (wins.toDouble() / totalGames) * 100 else 0.0
            DifficultyStatistics(
                difficulty = difficulty,
                totalGames = totalGames,
                wins = wins,
                losses = losses,
                winRatioPercentage = winRatio,
                bestTime = bestTime,
                bestScore = bestScore,
                flawFreeWins = flawFree
            )
        }
    }

    /**
     * Get statistics for all difficulties
     */
    fun getAllDifficultiesStatistics(): Flow<Quadruple<DifficultyStatistics, DifficultyStatistics, DifficultyStatistics, DifficultyStatistics>> {
        return combine(
            getDifficultyStatistics("Breeze"),
            getDifficultyStatistics("Pulse"),
            getDifficultyStatistics("Rage"),
            getDifficultyStatistics("Elite")
        ) { breeze, pulse, rage, elite ->
            Quadruple(breeze, pulse, rage, elite)
        }
    }

    /**
     * Get streak statistics
     */
    fun getStreakStatistics(): Flow<StreakStatistics> {
        return combine(
            streakGameDao.getTotalStreakGamesCount(),
            streakGameDao.getStreakWinsCount(),
            streakGameDao.getStreakLossesCount(),
            streakGameDao.getBestStreakTime(),
            streakGameDao.getBestStreakScore(),
            streakGameDao.getHighestStreakDay(),
            streakGameDao.getCurrentConsecutiveWins()
        ) { values ->
            val totalGames = values[0] as Int
            val wins = values[1] as Int
            val losses = values[2] as Int
            val bestTime = values[3]
            val bestScore = values[4]
            val highestDay = values[5]
            val consecutiveWins = values[6] as Int

            val winRatio = if (totalGames > 0) (wins.toDouble() / totalGames) * 100 else 0.0
            StreakStatistics(
                totalGames = totalGames,
                totalWins = wins,
                totalLosses = losses,
                winRatioPercentage = winRatio,
                bestTime = bestTime,
                bestScore = bestScore,
                highestStreakDay = highestDay ?: 0,
                currentConsecutiveWins = consecutiveWins
            )
        }
    }

    /**
     * Get all statistics combined
     */
    fun getAllStatistics(): Flow<AllStatistics> {
        return combine(
            getOverallStatistics(),
            getDifficultyStatistics("Breeze"),
            getDifficultyStatistics("Pulse"),
            getDifficultyStatistics("Rage"),
            getDifficultyStatistics("Elite"),
            getStreakStatistics()
        ) { values ->
            val overall = values[0] as GameStatistics
            val breeze = values[1] as DifficultyStatistics
            val pulse = values[2] as DifficultyStatistics
            val rage = values[3] as DifficultyStatistics
            val elite = values[4] as DifficultyStatistics
            val streak = values[5] as StreakStatistics

            AllStatistics(
                overallStats = overall,
                breeze = breeze,
                pulse = pulse,
                rage = rage,
                elite = elite,
                streak = streak
            )
        }
    }

    /**
     * Clear all statistics (for development/testing)
     */
    suspend fun clearAllStatistics() {
        gameStatisticDao.deleteAllGameStatistics()
        streakGameDao.deleteAllStreakGames()
    }
}

/**
 * Helper data class for combining 4 flows
 */
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/score/TotalScoreManager.kt
```kotlin
// This file has been moved to data/score/TotalScoreManager.kt

package com.quickmindgames.sudoku.data.score

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.scoreDataStore: DataStore<Preferences> by preferencesDataStore(name = "total_score")

class TotalScoreManager(private val context: Context) {

    private val TOTAL_SCORE_KEY = intPreferencesKey("total_score")

    val totalScore: Flow<Int> = context.scoreDataStore.data.map { preferences ->
        preferences[TOTAL_SCORE_KEY] ?: 0
    }

    suspend fun addScore(points: Int) {
        require(points >= 0) { "Lifetime points cannot be deducted." }
        if (points == 0) return
        var updatedTotal = 0
        context.scoreDataStore.edit { preferences ->
            val current = preferences[TOTAL_SCORE_KEY] ?: 0
            updatedTotal = (current.coerceAtLeast(0).toLong() + points)
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()
            preferences[TOTAL_SCORE_KEY] = updatedTotal
        }
        AppPreferences.getInstance(context).processPointMilestones(updatedTotal)
    }

    companion object {
        @Volatile
        private var instance: TotalScoreManager? = null

        fun getInstance(context: Context): TotalScoreManager {
            return instance ?: synchronized(this) {
                instance ?: TotalScoreManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/theme/Type.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.R

val customFontFamily = FontFamily(
    Font(R.font.light, FontWeight.Light),           // 300
    Font(R.font.regular, FontWeight.Normal),        // 400
    Font(R.font.medium, FontWeight.Medium),         // 500
    Font(R.font.semi_bold, FontWeight.SemiBold),              // 600
    Font(R.font.bold, FontWeight.Bold)              // 600
)

// Material3 Typography using Poppins Font
val Typography = Typography(
    // Display Styles - Large, prominent text
    displayLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 46.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline Styles
    headlineLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title Styles
    titleLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body Styles - Main text
    bodyLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp
    ),

    // Label Styles - Buttons, labels
    labelLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    )
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/SudokuApp.kt
```kotlin
package com.quickmindgames.sudoku

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.quickmindgames.sudoku.utils.RemoteConfigManager

/**
 *
 * Created by sagar.tahelyani on 07/03/26
 *
 */
class SudokuApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (BuildConfig.DEBUG) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = false
        }
        RemoteConfigManager.init()
    }
}
```

## File: build.gradle.kts
```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.googleservices) apply false
}
```

## File: gradle.properties
```
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. For more details, visit
# https://developer.android.com/r/tools/gradle-multi-project-decoupled-projects
# org.gradle.parallel=true
# AndroidX package structure to make it clearer which packages are bundled with the
# Android operating system, and which are packaged with your app's APK
# https://developer.android.com/topic/libraries/support-library/androidx-rn
android.useAndroidX=true
# Kotlin code style for this project: "official" or "obsolete":
kotlin.code.style=official
# Enables namespacing of each library's R class so that its R class includes only the
# resources declared in the library itself and none from the library's dependencies,
# thereby reducing the size of the R class for that library
android.nonTransitiveRClass=true
android.uniquePackageNames=false
android.dependency.useConstraints=true
android.r8.strictFullModeForKeepRules=false
android.disallowKotlinSourceSets=false
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/StreakNode.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun StreakNode(
    dayNumber: Int,
    isCompleted: Boolean,
    isActive: Boolean,
    isLocked: Boolean,
    isPlayable: Boolean,
    onClick: () -> Unit,
    onLockedClick: () -> Unit = {}
) {
    // 3D Gradient colors for different states
    val nodeData = when {
        isCompleted -> {
            // Completed: Blue gradient
            val primary = MaterialTheme.colorScheme.primary
            NodeColor3D(
                baseColor = primary,
                gradientStart = primary.copy(alpha = 0.9f),
                gradientEnd = primary.copy(alpha = 0.6f),
                shadowElevation = 12.dp
            )
        }

        isActive -> {
            // Active: Amber/Orange gradient with glow
            NodeColor3D(
                baseColor = Color(0xFFFF6F00),
                gradientStart = Color(0xFFFFAA00),
                gradientEnd = Color(0xFFFF6F00),
                shadowElevation = 14.dp
            )
        }

        isPlayable -> {
            // Playable: Secondary gradient
            val secondary = MaterialTheme.colorScheme.secondary
            NodeColor3D(
                baseColor = secondary,
                gradientStart = secondary.copy(alpha = 0.8f),
                gradientEnd = secondary.copy(alpha = 0.5f),
                shadowElevation = 8.dp
            )
        }

        else -> {
            // Locked: Gray gradient
            val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
            NodeColor3D(
                baseColor = surfaceVariant,
                gradientStart = surfaceVariant.copy(alpha = 0.6f),
                gradientEnd = surfaceVariant.copy(alpha = 0.3f),
                shadowElevation = 4.dp
            )
        }
    }

    val textColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        isActive -> Color.White
        isPlayable -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    }

    Column(
        modifier = Modifier
            .width(85.dp)
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D effect: Outer shadow layer
        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
                .shadow(
                    elevation = nodeData.shadowElevation,
                    shape = CircleShape,
                    clip = false
                )
        ) {
            // Gradient background for 3D depth
            ElevatedCard(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = nodeData.baseColor
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isCompleted || isActive) 8.dp
                    else if (isPlayable) 5.dp else 2.dp
                ),
                onClick = if (isPlayable || isActive) {
                    { onClick() }
                } else if (isLocked) {
                    { }
                } else {
                    { onLockedClick() }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // 3D Gradient overlay for depth
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        nodeData.gradientStart,
                                        nodeData.gradientEnd
                                    ),
                                    startY = 0f,
                                    endY = 48f
                                ),
                                shape = CircleShape
                            )
                    )

                    // Active state glow ring
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.85f)
                                .background(
                                    Color(0xFFFFCC02).copy(alpha = 0.2f),
                                    CircleShape
                                )
                        )
                    } else if (isPlayable) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.9f)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                    CircleShape
                                )
                        )
                    }

                    // Content: Icon or number
                    if (isCompleted) {
                        Text(
                            text = "✓",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    } else if (isLocked) {
                        Text(
                            text = "🔒",
                            fontSize = 24.sp
                        )
                    } else {
                        val digits = dayNumber.toString().length
                        val nodeFontSize = when {
                            digits <= 2 -> 22.sp
                            digits == 3 -> 16.sp
                            else -> 12.sp
                        }
                        Text(
                            text = dayNumber.toString(),
                            fontSize = nodeFontSize,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

// Data class for 3D node colors
data class NodeColor3D(
    val baseColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val shadowElevation: androidx.compose.ui.unit.Dp
)
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/SudokuGridLevel.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.data.state.CellData
import com.quickmindgames.sudoku.presentation.util.shakeEffect

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SudokuGridLevel(
    originalGrid: List<List<Int>>,
    grid: List<List<CellData>>,
    wrongCells: Set<Pair<Int, Int>>,
    shakeCells: Set<Pair<Int, Int>>,
    selectedCell: Pair<Int, Int>?,
    highlightRegion: Boolean,
    highlightSameNumbers: Boolean,
    onCellClick: (Int, Int) -> Unit
) {

    val colors = MaterialTheme.colorScheme

    SudokuBoardContainer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) { cellSizeDp, boardSizeDp ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier.requiredSize(boardSizeDp),
            userScrollEnabled = false
        ) {
            items(81) { index ->
                val row = index / 9
                val col = index % 9
                val cell = grid[row][col]
                val value = cell.value
                val original = originalGrid[row][col]
                val isSelected = selectedCell == row to col
                val selectedValue = selectedCell?.let { grid[it.first][it.second].value }
                val isSameNumber = highlightSameNumbers &&
                    selectedValue != null && selectedValue != 0 && value == selectedValue
                val isInSelectedRegion = highlightRegion &&
                    selectedCell?.let { (selectedRow, selectedCol) ->
                        row == selectedRow ||
                            col == selectedCol ||
                            (row / 3 == selectedRow / 3 && col / 3 == selectedCol / 3)
                    } == true
                val isWrong = wrongCells.contains(row to col)
                val shouldShake = shakeCells.contains(row to col)
                val animatedBgColor = when {
                    isWrong -> colors.error.copy(alpha = 0.10f)
                    isSelected -> colors.surfaceVariant
                    isInSelectedRegion -> colors.secondary.copy(alpha = 0.15f)
                    isSameNumber -> colors.secondary.copy(alpha = 0.30f)
                    original != 0 -> colors.surfaceVariant
                    else -> colors.surface
                }
                Box(
                    modifier = Modifier
                        .requiredSize(cellSizeDp)
                        .background(animatedBgColor)
                        .clickable { onCellClick(row, col) },
                    contentAlignment = Alignment.Center
                ) {
                    val showNotes = (cell.notes.isNotEmpty() && (cell.value == 0 || isWrong))
                    if (showNotes) {
                        val notesList = cell.notes.toList().take(4)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    notesList.getOrNull(0)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    notesList.getOrNull(1)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    notesList.getOrNull(2)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    notesList.getOrNull(3)?.let { n ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = n.toString(),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.sp,
                                                lineHeight = 12.sp,
                                                color = colors.onSurface,
                                                modifier = Modifier.padding(0.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = if (value == 0) "" else value.toString(),
                            fontSize = 26.sp,
                            fontWeight = when {
                                isWrong -> FontWeight.Bold
                                else -> FontWeight.Medium
                            },
                            color = when {
                                original != 0 -> colors.onSurface
                                isWrong -> colors.error
                                else -> colors.primary
                            },
                            modifier = Modifier.shakeEffect(shouldShake)
                        )
                    }
                }
            }
        }
        SudokuGridCanvas(modifier = Modifier.requiredSize(boardSizeDp))
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/data/preferences/AppPreferences.kt
```kotlin
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
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/navigation/AppNav.kt
```kotlin
package com.quickmindgames.sudoku.presentation.navigation

import androidx.compose.runtime.Composable
import com.quickmindgames.sudoku.presentation.ui.MainSudokuApp

/**
 * App Navigation Setup
 * Manages navigation between Main screen and Play screen
 */
@Composable
fun AppNav() {
    MainSudokuApp()
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/LessonScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.LessonCardColumn
import com.quickmindgames.sudoku.component.LessonCardDefault
import com.quickmindgames.sudoku.domain.model.LESSONS
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils

@Composable
fun LessonScreen(
    onLessonsComplete: () -> Unit,
    onExit: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.LESSON,
            AnalyticsConstants.LESSON_SCREEN
        )
    }

    Column(
        modifier = Modifier
            .background(colors.background)
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding(),
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
            )
    ) {
        // ── Top bar ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Exit",
                    tint = colors.primary
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.lbl_tutorial),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
        }

        // ── Scrollable lessons ──────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            items(items = LESSONS, key = { lesson -> lesson.id }) { lesson ->
                val index = LESSONS.indexOf(lesson)

                // Special layout for Column Rule (index 1)
                if (index == 1) {
                    LessonCardColumn(lesson = lesson, stepNumber = index + 1)
                } else {
                    LessonCardDefault(lesson = lesson, stepNumber = index + 1)
                }

                // Add divider between lessons (except after last)
                if (index < LESSONS.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        thickness = 4.dp,
                        color = colors.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                }
            }
            item {
                Spacer(Modifier.height(24.dp))
            }
        }

        // ── Start Puzzle button ─────────────────────────────────
        Button(
            onClick = onLessonsComplete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.tertiary
            )
        ) {
            Text(
                text = stringResource(R.string.lbl_lets_practice),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
```

## File: app/src/main/AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="com.google.android.gms.permission.AD_ID" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:name=".SudokuApp"
        android:allowBackup="true"
        android:appCategory="game"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Sudoku">
        <activity
            android:name=".presentation.ui.MainActivity"
            android:exported="true"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.Sudoku">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/StatisticsComponents.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.data.state.StreakState
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.domain.model.AllStatistics
import com.quickmindgames.sudoku.domain.model.DifficultyStatistics
import com.quickmindgames.sudoku.domain.model.GameStatistics
import com.quickmindgames.sudoku.domain.model.StreakStatistics
import java.util.Locale

/**
 * Data class for win ratio-based feedback with title, caption, and emoji
 */
data class WinRatioFeedback(
    val title: String,
    val caption: String,
    val emoji: String
)

/**
 * Get feedback based on win ratio percentage
 * Includes dynamic random captions for variety
 * Now with 10% interval tiers for better progression
 */
fun getWinRatioFeedback(winRatio: String): WinRatioFeedback {
    val ratio = winRatio.removeSuffix("%").toDoubleOrNull() ?: 0.0

    return when {
        ratio == 100.0 -> WinRatioFeedback(
            title = "Sudoku Legend 👑",
            caption = listOf(
                "Numbers fear you.",
                "Sudoku completed flawlessly.",
                "Perfect Mind Detected 🧠✨",
                "Puzzle Destroyer Mode: ON",
                "Are you secretly the developer?"
            ).random(),
            emoji = "🏆"
        )

        ratio >= 96 -> WinRatioFeedback(
            title = "Master",
            caption = listOf(
                "Almost illegal accuracy 😳",
                "Human or hidden AI?",
                "Numbers obey your command.",
                "Perfection is calling…",
                "One step away from legend."
            ).random(),
            emoji = "👑"
        )

        ratio >= 91 -> WinRatioFeedback(
            title = "Grandmaster",
            caption = listOf(
                "Flawless execution incoming.",
                "Your accuracy is legendary.",
                "Mistakes? Never heard of them.",
                "The puzzle gods bow to you.",
                "One mistake away from perfection."
            ).random(),
            emoji = "🎖️"
        )

        ratio >= 86 -> WinRatioFeedback(
            title = "Elite Pro",
            caption = listOf(
                "Your skills are undeniable.",
                "The puzzle solver evolution.",
                "Elite tier unlocked forever.",
                "Zero hesitation, pure skill.",
                "You are the standard now."
            ).random(),
            emoji = "⚡"
        )

        ratio >= 81 -> WinRatioFeedback(
            title = "Expert",
            caption = listOf(
                "Okay genius, we see you.",
                "Mistakes are getting rare.",
                "Sudoku sweating right now.",
                "Elite solver unlocked.",
                "You don't guess. You know."
            ).random(),
            emoji = "🚀"
        )

        ratio >= 76 -> WinRatioFeedback(
            title = "Advanced Player",
            caption = listOf(
                "Speed and accuracy combined.",
                "Patterns speak to you.",
                "You're in your element.",
                "Professional level approaching.",
                "Mastery in progress…"
            ).random(),
            emoji = "📈"
        )

        ratio >= 71 -> WinRatioFeedback(
            title = "Strong Solver",
            caption = listOf(
                "You're unstoppable now.",
                "Confidence at an all-time high.",
                "Puzzle fighter unleashed.",
                "The force is strong with you.",
                "Consistency is your superpower."
            ).random(),
            emoji = "💪"
        )

        ratio >= 66 -> WinRatioFeedback(
            title = "Solid Player",
            caption = listOf(
                "You own the puzzles now.",
                "Strategy meets execution.",
                "Winning becomes your norm.",
                "Two-thirds mastery achieved.",
                "The puzzle fears you slightly."
            ).random(),
            emoji = "🔥"
        )

        ratio >= 61 -> WinRatioFeedback(
            title = "Solver",
            caption = listOf(
                "Sudoku respects you now.",
                "Sharp mind alert!",
                "You solve more than you struggle.",
                "Brain doing gym regularly 💪",
                "Strategic thinker detected."
            ).random(),
            emoji = "🔥"
        )

        ratio >= 56 -> WinRatioFeedback(
            title = "Confident Player",
            caption = listOf(
                "Halfway to mastery.",
                "Your potential is showing.",
                "Puzzles bow to your strategy.",
                "Experience turning into skill.",
                "The momentum is real."
            ).random(),
            emoji = "✨"
        )

        ratio >= 51 -> WinRatioFeedback(
            title = "Improving Solver",
            caption = listOf(
                "Over halfway there!",
                "Growth trajectory: ↗️ Excellent",
                "You're becoming formidable.",
                "Skills upgrading rapidly.",
                "Future expert in the making."
            ).random(),
            emoji = "📊"
        )

        ratio >= 46 -> WinRatioFeedback(
            title = "Above Average",
            caption = listOf(
                "You're officially above the rest.",
                "Consistency showing up.",
                "Puzzle solver in development.",
                "Your wins outnumber losses now.",
                "The curve bends upward."
            ).random(),
            emoji = "😌"
        )

        ratio >= 41 -> WinRatioFeedback(
            title = "Thinker",
            caption = listOf(
                "Now we're talking!",
                "Puzzle fighter in training.",
                "You're officially dangerous now.",
                "Half human, half calculator.",
                "Confidence level increasing 📈"
            ).random(),
            emoji = "😎"
        )

        ratio >= 36 -> WinRatioFeedback(
            title = "Developing Player",
            caption = listOf(
                "You're getting the hang of it.",
                "Patterns starting to reveal themselves.",
                "Improvement noticed!",
                "Keep this momentum going.",
                "The puzzles don't stand a chance."
            ).random(),
            emoji = "🌱"
        )

        ratio >= 31 -> WinRatioFeedback(
            title = "Practicing Hard",
            caption = listOf(
                "Progress is beautiful.",
                "You're closer than yesterday.",
                "Muscle memory building.",
                "Consistency blooming.",
                "One foot in the victory zone."
            ).random(),
            emoji = "💭"
        )

        ratio >= 26 -> WinRatioFeedback(
            title = "Persistent Learner",
            caption = listOf(
                "The dedication is showing.",
                "Quarter victory rate achieved.",
                "Forward is all that matters.",
                "Your brain is adapting.",
                "Small wins lead to big wins."
            ).random(),
            emoji = "🎯"
        )

        ratio >= 21 -> WinRatioFeedback(
            title = "Learner",
            caption = listOf(
                "Okay okay… progress detected!",
                "You're starting to scare easy puzzles.",
                "Mistakes today, mastery tomorrow.",
                "Not bad… not bad at all.",
                "The brain cells have awakened."
            ).random(),
            emoji = "🙂"
        )

        ratio >= 16 -> WinRatioFeedback(
            title = "Beginner+",
            caption = listOf(
                "You're finding your footing.",
                "Every puzzle is a lesson.",
                "Confidence building slowly.",
                "The basics are clicking.",
                "Keep exploring, keep learning."
            ).random(),
            emoji = "🌍"
        )

        ratio >= 11 -> WinRatioFeedback(
            title = "Novice",
            caption = listOf(
                "Welcome to the journey!",
                "Numbers are becoming friends.",
                "You're on the right path.",
                "Every game makes you smarter.",
                "Patience is your strength."
            ).random(),
            emoji = "👋"
        )

        ratio >= 6 -> WinRatioFeedback(
            title = "Getting Started",
            caption = listOf(
                "Your adventure has begun.",
                "Sudoku is saying hello.",
                "First steps are the hardest.",
                "Tiny wins count too.",
                "You're braver than you think."
            ).random(),
            emoji = "🌟"
        )

        ratio > 0.0 -> WinRatioFeedback(
            title = "Rookie",
            caption = listOf(
                "Every master was once confused too 😄",
                "Numbers are warming up to you.",
                "Practice mode activated.",
                "Brain loading… please wait.",
                "Sudoku and you are getting introduced."
            ).random(),
            emoji = "😵"
        )
        // Special case: 0% win ratio with losses
        else -> WinRatioFeedback(
            title = "Keep Trying",
            caption = listOf(
                "Don't give up! Every loss teaches you something.",
                "The best time to try again is now.",
                "Failures are just setups for comebacks.",
                "You've got this! One game at a time.",
                "Persistence beats perfection every time.",
                "Champions aren't built in one day.",
                "Your breakthrough is just one puzzle away."
            ).random(),
            emoji = "💪"
        )
    }
}

@Composable
fun OverallStatsTab(allStatistics: AllStatistics) {
    Column {

        //region Get feedback based on win ratio for streak stats and use it to create a header with title, caption, and emoji
        /*val feedback = getWinRatioFeedback(allStatistics.overallStats.winRatio)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = feedback.emoji,
                fontSize = 32.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feedback.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feedback.caption,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }*/
        //endregion

        OverallStatsCard(allStatistics.overallStats)
    }
}

@Composable
fun OverallStatsCard(stats: GameStatistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatisticRow("Total Games", stats.totalGames.toString())
            StatisticRow("Wins", stats.totalWins.toString(), MaterialTheme.colorScheme.primary)
            StatisticRow("Losses", stats.totalLosses.toString(), MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            StatisticRow("Win Ratio", stats.winRatio, MaterialTheme.colorScheme.secondary)
            StatisticRow("Best Time", stats.bestTime?.let { formatTime(it) } ?: "—")
            StatisticRow("Best Points", stats.bestScore?.toString() ?: "—")
            StatisticRow(
                "Flaw-Free Wins",
                stats.flawFreeWins.toString(),
                MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun DifficultyStatsTab(difficulty: DifficultyStatistics) {
    Column {

        //region Get feedback based on win ratio for streak stats and use it to create a header with title, caption, and emoji
        /*val feedback = getWinRatioFeedback(difficulty.winRatio)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = feedback.emoji,
                fontSize = 32.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feedback.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feedback.caption,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }*/
        //endregion

        DifficultyStatsCard(difficulty)
    }
}

@Composable
fun DifficultyStatsCard(stats: DifficultyStatistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatisticRow("Total Games", stats.totalGames.toString())
            StatisticRow("Wins", stats.wins.toString(), MaterialTheme.colorScheme.primary)
            StatisticRow("Losses", stats.losses.toString(), MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            StatisticRow("Win Ratio", stats.winRatio, MaterialTheme.colorScheme.secondary)
            StatisticRow("Best Time", stats.bestTime?.let { formatTime(it) } ?: "—")
            StatisticRow("Best Points", stats.bestScore?.toString() ?: "—")
            StatisticRow(
                "Flaw-Free Wins",
                stats.flawFreeWins.toString(),
                MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun StreakStatsTab(stats: StreakStatistics) {
    Column {

        //region Get feedback based on win ratio for streak stats and use it to create a header with title, caption, and emoji
        /*val feedback = getWinRatioFeedback(stats.winRatio)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = feedback.emoji,
                fontSize = 32.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feedback.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feedback.caption,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }*/
        //endregion

        StreakStatsCard(stats)
    }
}

@Composable
fun StreakStatsCard(stats: StreakStatistics) {
    val context = LocalContext.current
    val streakStateManager = remember { StreakStateManager.getInstance(context) }
    var streakCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        streakStateManager.getStreakState().collect { saved ->
            val s = saved ?: StreakState(0, null, null, null)
            streakCount = s.streakCount
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatisticRow("Total Streak Games", stats.totalGames.toString())
            StatisticRow("Wins", stats.totalWins.toString(), MaterialTheme.colorScheme.primary)
            StatisticRow("Losses", stats.totalLosses.toString(), MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            StatisticRow("Win Ratio", stats.winRatio, MaterialTheme.colorScheme.secondary)
            StatisticRow("Best Time", stats.bestTime?.let { formatTime(it) } ?: "—")
            StatisticRow("Best Points", stats.bestScore?.toString() ?: "—")
            StatisticRow(
                "Highest Streak Wins",
                stats.highestStreakDay.toString(),
                MaterialTheme.colorScheme.tertiary
            )
            StatisticRow(
                "Current Consecutive Wins",
                streakCount.toString(),
                MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StatisticRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Format time in seconds to readable format (MM:SS)
 */
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/StreakSummaryCard.kt
```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.utils.MultipleEventsCutter
import com.quickmindgames.sudoku.utils.shareStreak

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun StreakSummaryCard(
    streakCount: Int,
    completedToday: Boolean,
    hasActiveToday: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val cutter = MultipleEventsCutter.rememberMultipleEventsCutter()
    val statusText = when {
        hasActiveToday -> "Streak in progress..."
        completedToday -> "Completed today ✓"
        streakCount > 0 -> "Play today to keep it alive!"
        else -> "Start your first streak!"
    }

    val statusColor = when {
        hasActiveToday -> Color(0xFFFF6F00)
        completedToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flame + streak count badge
            Text(text = "🔥", fontSize = 32.sp)

            Spacer(Modifier.width(10.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (streakCount) {
                        0 -> "No Streak"
                        1 -> "1 Day Streak"
                        else -> "$streakCount Days Streak"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Share icon button — only shown when there is an active streak
            if (streakCount > 0) {
                IconButton(
                    onClick = {
                        cutter.processEvent { context.shareStreak(streakCount) }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share streak",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/AnalyticsConstants.kt
```kotlin
package com.quickmindgames.sudoku.utils

object AnalyticsConstants {

    //region screen_view events
    const val HOME = "Home"
    const val HOME_SCREEN = "HomeScreen"
    const val STATISTICS = "Statistics"
    const val STATISTICS_SCREEN = "StatisticsScreen"
    const val SETTINGS = "Settings"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val REWARDS = "Rewards"
    const val REWARDS_SCREEN = "RewardsScreen"
    const val STREAK = "Streak"
    const val STREAK_SCREEN = "StreakScreen"
    const val SUDOKU_GAME = "Sudoku Game"
    const val SUDOKU_SCREEN = "SudokuScreen"
    const val LESSON = "Lesson"
    const val LESSON_SCREEN = "LessonScreen"
    //endregion

    //region Feature events
    const val NEW_GAME = "new_game"
    const val STREAK_GAME = "streak_game"
    const val DIFFICULTY = "difficulty"
    const val DIFFICULTY_BY_HINTS = "difficulty_by_hints"
    const val SAVED_GAME = "saved_game"
    const val RESUME_GAME = "resume_game"
    const val STREAK_COUNT = "streak_count"
    const val STREAK_COMPLETED = "streak_completed"
    const val STREAK_STARTED = "streak_started"
    const val STREAK_COUNT_SHARE = "streak_count_share"
    const val GAME_RESULT = "game_result"
    const val RESULT = "result"
    const val WIN = "win"
    const val LOSS = "loss"
    const val TIME = "time"
    const val MISTAKES = "mistakes"
    const val SCORE = "score"
    const val GAME_ACTION = "game_action"
    const val ACTION = "action"
    const val HINT = "hint"
    const val NOTES = "notes"
    const val NOTES_ON = "notes_on"
    const val NOTES_OFF = "notes_off"
    const val STATE = "state"
    const val ON = "on"
    const val OFF = "off"
    const val UNDO = "undo"
    const val ERASE = "erase"
    const val THEME = "theme"
    const val THEME_TOGGLED = "theme_toggled"
    const val SETTINGS_OPTION_CHANGED = "settings_option_changed"
    const val SETTING = "setting"
    const val NOTIFICATION_RECEIVED = "notification_received"
    const val NOTIFICATION_CLICKED = "notification_clicked"
    const val STREAK_SHARE = "streak_share"
    const val STREAK_TAB = "streak_tab"
    const val IS_STREAK = "is_streak"
    const val HINT_COUNT = "hints_used"
    const val STATISTICS_EVENT = "statistics"
    const val LESSON_STARTED = "lesson_started"
    const val LEARNING_MODE = "learning_mode"
    const val LESSON_COMPLETED = "lesson_completed"
    const val SETTINGS_STATE = "settings_state"
    const val HIDE_USED_NUMBERS = "hide_used_numbers"
    const val HIGHLIGHT_SAME_NUMBERS = "highlight_same_numbers"
    const val HIGHLIGHT_REGION = "highlight_region"
    const val FREE_PLAY = "free_play"
    const val SOUND_VIBRATIONS = "sound_vibration"
    const val YES = "Yes"
    const val NO = "No"
    const val OVERALL_STATS = "overall_stats"
    const val BREEZE_STATS = "breeze_stats"
    const val PULSE_STATS = "pulse_stats"
    const val RAGE_STATS = "rage_stats"
    const val ELITE_STATS = "elite_stats"
    const val STREAK_STATS = "streak_stats"
    //endregion

}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/AnalyticsUtils.kt
```kotlin
package com.quickmindgames.sudoku.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickmindgames.sudoku.domain.model.AllStatistics

object AnalyticsUtils {
    /**
     * Logs a screen view event to Firebase Analytics.
     *
     * @param context The context from which the event is logged.
     * @param screenName The name of the screen being viewed (e.g., "Home", "Statistics").
     * @param screenClass The class name of the screen being viewed.
     */
    fun logScreenView(
        context: Context,
        screenName: String,
        screenClass: String
    ) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /**
     * Logs a "new_gamed" event to Firebase Analytics with the specified difficulty level and resume state.
     *
     * @param context The context from which the event is logged.
     * @param difficulty The difficulty level of the new game (e.g., "Breeze", "Pulse", "Rage", "Elite").
     * @param hasSavedGame Whether a resume game existed before starting a new game.
     */
    fun logNewGameStarted(context: Context, difficulty: String, hasSavedGame: String?) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.DIFFICULTY, difficulty)
            putString(AnalyticsConstants.SAVED_GAME, hasSavedGame)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.NEW_GAME, bundle)
    }

    /**
     * Logs a "resume_game" event to Firebase Analytics with the specified difficulty level.
     *
     * @param context The context from which the event is logged.
     * @param difficulty The difficulty level of the resumed game (e.g., "Breeze", "Pulse", "Rage", "Elite").
     */
    fun logResumeGame(context: Context, difficulty: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.DIFFICULTY, difficulty)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.RESUME_GAME, bundle)
    }

    /**
     * Logs a "streak_game" event to Firebase Analytics.
     *
     * @param context The context from which the event is logged.
     */
    fun logStreakGameStarted(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.STREAK_STARTED, "true")
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.STREAK_GAME, bundle)
    }

    /**
     * Logs a "streak_game" event to Firebase Analytics with the specified streak count.
     *
     * @param context The context from which the event is logged.
     * @param streakCount The current streak count when the game was completed.
     */
    fun logStreakGameCompleted(context: Context, streakCount: Int) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putLong(AnalyticsConstants.STREAK_COUNT, streakCount.toLong())
            putString(AnalyticsConstants.STREAK_COMPLETED, "true")
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.STREAK_GAME, bundle)
    }

    /**
     * Logs a "learning_mode" event to Firebase Analytics when a lesson is started.
     *
     * @param context The context from which the event is logged.
     */
    fun logLearningStarted(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.LESSON_STARTED, "true")
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.LEARNING_MODE, bundle)
    }

    /**
     * Logs a "learning_mode" event to Firebase Analytics when a lesson is completed.
     *
     * @param context The context from which the event is logged.
     */
    fun logLearningCompleted(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(AnalyticsConstants.LESSON_COMPLETED, "true")
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.LEARNING_MODE, bundle)
    }

    /**
     * Logs a "game_result" event to Firebase Analytics with game outcome and stats.
     *
     * @param context The context from which the event is logged.
     * @param win Whether the game was won (true) or lost (false).
     * @param time Time taken to complete the game.
     * @param mistakes Number of mistakes made in the game.
     * @param score Score achieved in the game.
     * @param difficulty Difficulty level of the game.
     * @param isStreak Whether the game was a streak game.
     * @param hintCount Number of hints used in the game.
     */
    fun logGameResult(
        context: Context,
        win: Boolean,
        time: String,
        mistakes: Int,
        score: Int,
        difficulty: String,
        isStreak: Boolean = false,
        hintCount: Int = 0
    ) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(
                AnalyticsConstants.RESULT,
                if (win) AnalyticsConstants.WIN else AnalyticsConstants.LOSS
            )
            putString(AnalyticsConstants.TIME, time)
            putLong(AnalyticsConstants.MISTAKES, mistakes.toLong())
            putLong(AnalyticsConstants.SCORE, score.toLong())
            putString(AnalyticsConstants.DIFFICULTY, difficulty)
            putString(
                AnalyticsConstants.IS_STREAK,
                if (isStreak) AnalyticsConstants.YES else AnalyticsConstants.NO
            )
            putLong(AnalyticsConstants.HINT_COUNT, hintCount.toLong())
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_RESULT, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when a hint is used.
     *
     * @param context The context from which the event is logged.
     * @param difficulty The difficulty level of the game.
     */
    fun logHintUsed(context: Context, difficulty: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.ACTION, AnalyticsConstants.HINT)
            putString(AnalyticsConstants.DIFFICULTY_BY_HINTS, difficulty)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when notes mode is toggled.
     *
     * @param context The context from which the event is logged.
     * @param enabled Whether notes mode was turned on (true) or off (false).
     */
    fun logNotesToggled(context: Context, enabled: Boolean) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(
                AnalyticsConstants.ACTION,
                if (enabled) AnalyticsConstants.NOTES_ON else AnalyticsConstants.NOTES_OFF
            )
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when undo is used.
     *
     * @param context The context from which the event is logged.
     */
    fun logUndoUsed(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.ACTION, AnalyticsConstants.UNDO)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "game_action" event to Firebase Analytics when erase is used.
     *
     * @param context The context from which the event is logged.
     */
    fun logEraseUsed(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.ACTION, AnalyticsConstants.ERASE)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.GAME_ACTION, params)
    }

    /**
     * Logs a "theme_toggled" event to Firebase Analytics when the theme is changed.
     *
     * @param context The context from which the event is logged.
     * @param theme The theme selected by the user (e.g., "light_theme", "dark_theme").
     */
    fun logThemeToggled(context: Context, theme: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(AnalyticsConstants.THEME, theme)
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.THEME_TOGGLED, params)
    }

    /**
     * Logs a "settings_option_changed" event to Firebase Analytics when a setting option is changed.
     *
     * @param context The context from which the event is logged.
     * @param setting The setting that was changed.
     * @param enabled The new state of the setting.
     */
    fun logSettingsOptionChanged(context: Context, setting: String, enabled: Boolean) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val params = Bundle().apply {
            putString(
                AnalyticsConstants.SETTINGS_STATE,
                setting + " - " + if (enabled) AnalyticsConstants.ON else AnalyticsConstants.OFF
            )
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.SETTINGS_OPTION_CHANGED, params)
    }

    /**
     * Logs a "notification_received" event to Firebase Analytics when a notification is received.
     *
     * @param context The context from which the event is logged.
     */
    fun logNotificationReceived(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.logEvent(AnalyticsConstants.NOTIFICATION_RECEIVED, null)
    }

    /**
     * Logs a "notification_clicked" event to Firebase Analytics when a notification is clicked.
     *
     * @param context The context from which the event is logged.
     */
    fun logNotificationClicked(context: Context) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.logEvent(AnalyticsConstants.NOTIFICATION_CLICKED, null)
    }

    /**
     * Logs a "streak_share" event to Firebase Analytics when the user shares their streak.
     *
     * @param context The context from which the event is logged.
     * @param streakCount The current streak count when the share action was triggered.
     */
    fun logStreakShare(context: Context, streakCount: Int) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putLong(AnalyticsConstants.STREAK_COUNT_SHARE, streakCount.toLong())
        }
        firebaseAnalytics.logEvent(AnalyticsConstants.STREAK_SHARE, bundle)
    }

    /**
     * Logs a "statistics" event to Firebase Analytics with statistics data.
     *
     * @param context The context from which the event is logged.
     * @param statistics The statistics data to be included in the event.
     */
    fun logStatisticsViewed(context: Context, statistics: AllStatistics) {
        val params = Bundle().apply {
            putString(
                AnalyticsConstants.OVERALL_STATS,
                encodeStatistics(
                    statistics.overallStats.totalGames,
                    statistics.overallStats.totalWins,
                    statistics.overallStats.totalLosses,
                    statistics.overallStats.winRatioPercentage,
                    statistics.overallStats.bestTime,
                    statistics.overallStats.bestScore,
                    statistics.overallStats.flawFreeWins
                )
            )
            putString(
                AnalyticsConstants.BREEZE_STATS,
                encodeStatistics(
                    statistics.breeze.totalGames,
                    statistics.breeze.wins,
                    statistics.breeze.losses,
                    statistics.breeze.winRatioPercentage,
                    statistics.breeze.bestTime,
                    statistics.breeze.bestScore,
                    statistics.breeze.flawFreeWins
                )
            )
            putString(
                AnalyticsConstants.PULSE_STATS,
                encodeStatistics(
                    statistics.pulse.totalGames,
                    statistics.pulse.wins,
                    statistics.pulse.losses,
                    statistics.pulse.winRatioPercentage,
                    statistics.pulse.bestTime,
                    statistics.pulse.bestScore,
                    statistics.pulse.flawFreeWins
                )
            )
            putString(
                AnalyticsConstants.RAGE_STATS,
                encodeStatistics(
                    statistics.rage.totalGames,
                    statistics.rage.wins,
                    statistics.rage.losses,
                    statistics.rage.winRatioPercentage,
                    statistics.rage.bestTime,
                    statistics.rage.bestScore,
                    statistics.rage.flawFreeWins
                )
            )
            putString(
                AnalyticsConstants.ELITE_STATS,
                encodeStatistics(
                    statistics.elite.totalGames,
                    statistics.elite.wins,
                    statistics.elite.losses,
                    statistics.elite.winRatioPercentage,
                    statistics.elite.bestTime,
                    statistics.elite.bestScore,
                    statistics.elite.flawFreeWins
                )
            )
            putString(
                AnalyticsConstants.STREAK_STATS,
                encodeStatistics(
                    statistics.streak.totalGames,
                    statistics.streak.totalWins,
                    statistics.streak.totalLosses,
                    statistics.streak.winRatioPercentage,
                    statistics.streak.bestTime,
                    statistics.streak.bestScore,
                    0, // Streak statistics don't track flaw-free wins.
                    statistics.streak.highestStreakDay,
                    statistics.streak.currentConsecutiveWins
                )
            )
        }

        FirebaseAnalytics.getInstance(context)
            .logEvent(AnalyticsConstants.STATISTICS_EVENT, params)
    }

    private fun encodeStatistics(
        totalGames: Int,
        wins: Int,
        losses: Int,
        winRatioPercentage: Double,
        bestTime: Int?,
        bestScore: Int?,
        flawFreeWins: Int,
        vararg extraFields: Int
    ): String = (
            listOf(
                totalGames.toString(),
                wins.toString(),
                losses.toString(),
                winRatioPercentage.toString(),
                bestTime?.toString() ?: "-1",
                bestScore?.toString() ?: "-1",
                flawFreeWins.toString()
            ) + extraFields.map(Int::toString)
            ).joinToString(",")

}
```

## File: app/src/main/res/values/strings.xml
```xml
<resources>
    <string name="app_name">Sudoku</string>
    <string name="app_name_full">Sudoku – Classic Puzzle Game</string>
    <string name="lbl_puzzle">Classic Puzzle Game</string>
    <string name="lbl_lets_practice">Let\'s Practice</string>
    <string name="streak_shield_wallet">Shield Wallet</string>
    <string name="streak_shield_prompt_title">Save Your Streak?</string>
    <string name="streak_shield_prompt_description">You missed playing a streak yesterday! Your %1$d-day streak is at risk.\n\n%2$s\n\nUse 1 shield to preserve your streak.</string>
    <plurals name="streak_shields_available">
        <item quantity="one">1 Streak Shield available.</item>
        <item quantity="other">%1$d Streak Shields available.</item>
    </plurals>
    <string name="streak_shield_use_one">Use 1 Shield</string>
    <string name="streak_shield_restart">Restart the streak</string>
    <string name="streak_shield_unavailable">No Streak Shields are available.</string>
    <string name="streak_shield_won">You won +%1$d streak shield!</string>
    <string name="points_wallet_info_title">Total Points</string>
    <string name="wallet_info_earn_label">Earn</string>
    <string name="wallet_info_use_label">Use</string>
    <string name="points_wallet_earn">Earn points by completing games and winning points from the Prize Wheel.</string>
    <string name="hint_wallet_info_title">Hint Wallet</string>
    <string name="hint_wallet_earn_points">Earn 1 hint for every 1,000 points.</string>
    <string name="hint_wallet_earn_streak">Earn 1 hint for every 5 consecutive streak days.</string>
    <string name="hint_wallet_earn_wheel">Spin the Prize Wheel for a chance to win +1 hint.</string>
    <string name="hint_wallet_use">Use up to 3 hints in each game.</string>
    <string name="booster_wallet_info_title">2X Points Wallet</string>
    <string name="booster_wallet_earn">Win 2X Points Boosters from the Prize Wheel.</string>
    <string name="booster_wallet_info">Use a 2X Points Booster on a new game to double the points awarded when you complete it. One booster is consumed per completed boosted game.</string>
    <string name="shield_wallet_info_title">Shield Wallet</string>
    <string name="shield_wallet_earn">Earn Streak Shields from the Prize Wheel.</string>
    <string name="shield_wallet_info">Use one Streak Shield to protect one missed day and preserve your streak count.</string>
    <string name="rewards_wallet_summary_title">Welcome to Rewards!</string>
    <string name="rewards_welcome_subtitle">Your rewards are ready to use</string>
    <string name="rewards_welcome_button">Let\'s Play</string>
    <string name="rewards_welcome_points_hints">%1$d from points</string>
    <string name="rewards_welcome_streak_hints">%1$d from streak</string>
    <string name="rewards_welcome_bonus_hints">%1$d welcome bonus</string>
    <string name="rewards_welcome_shields_detail">Protect a missed streak day. Includes +%1$d welcome shield; earn more from the Prize Wheel.</string>
    <string name="lbl_how_to_play">How to play?</string>
    <string name="lbl_tutorial">Tutorial</string>
    <string name="lbl_practice_mode">Practice Mode</string>
</resources>
```

## File: app/src/main/java/com/quickmindgames/sudoku/component/StreakPath.kt
```kotlin
package com.quickmindgames.sudoku.component

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.quickmindgames.sudoku.BuildConfig
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun StreakPath(
    streakCount: Int,
    hasActiveToday: Boolean,
    completedToday: Boolean,
    onStart: (Int) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BoxWithConstraints {
                val nodeSpacing = 85.dp
                val horizontalPadding = 16.dp
                val availableWidth = maxWidth - horizontalPadding

                val nodesPerRowCalculated = maxOf(
                    2,
                    (availableWidth.value / nodeSpacing.value).toInt()
                )

                // Always show at least 3 full rows beyond the last completed node,
                // so the path always looks like it goes on forever.
                val completedRows = if (streakCount == 0) 0
                else (streakCount + nodesPerRowCalculated - 1) / nodesPerRowCalculated
                val minRows = maxOf(10, completedRows + 3)
                // totalNodes is always an exact multiple of nodesPerRow → every row is complete
                val totalNodes = minRows * nodesPerRowCalculated

                // Group nodes by row
                val nodeRows = (0 until totalNodes).groupBy { index ->
                    index / nodesPerRowCalculated
                }

                val primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                val grayColor = Color.Gray.copy(alpha = 0.5f)

                // Auto-scroll: the active/next node is streakCount+1, in row = streakCount/nodesPerRow.
                // Scroll so that one completed row above it is visible (for context).
                val scrollState = rememberScrollState()
                val density = LocalDensity.current
                val activeRowIndex = streakCount / nodesPerRowCalculated

                LaunchedEffect(streakCount, nodesPerRowCalculated) {
                    val rowHeightPx = with(density) { nodeSpacing.toPx() }
                    // One row above the active row for context; never below 0
                    val targetPx = ((activeRowIndex - 1).coerceAtLeast(0) * rowHeightPx).toInt()
                    // Wait for layout to complete before animating
                    delay(150.milliseconds)
                    scrollState.animateScrollTo(targetPx)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp, horizontal = horizontalPadding)
                        .verticalScroll(scrollState)
                ) {
                    nodeRows.forEach { (rowIndex, nodeIndices) ->
                        val isEvenRow = rowIndex % 2 == 0
                        val sortedIndices = if (isEvenRow) nodeIndices else nodeIndices.reversed()

                        // Row height == nodeSpacing so vertical padding == horizontal padding
                        // → both line segments have the same length
                        val rowHeightDp = nodeSpacing  // 85.dp

                        Box(modifier = Modifier.fillMaxWidth()) {

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeightDp)
                            ) {
                                val nodeSpacingPx = 85.dp.toPx()
                                val nodeWidthPx = 48.dp.toPx()
                                val nodePaddingPx = (nodeSpacingPx - nodeWidthPx) / 2f
                                val lineY = size.height / 2f

                                // Calculate center offset: nodes are centered, so calculate padding on left
                                val totalNodesWidth = nodeIndices.size * nodeSpacingPx
                                val centerOffsetX = (size.width - totalNodesWidth) / 2f

                                val leadingX = if (isEvenRow) {
                                    centerOffsetX + nodePaddingPx + nodeWidthPx / 2f
                                } else {
                                    centerOffsetX + ((nodeIndices.size - 1) * nodeSpacingPx) + nodePaddingPx + nodeWidthPx / 2f
                                }
                                val trailingX = if (isEvenRow) {
                                    centerOffsetX + ((nodeIndices.size - 1) * nodeSpacingPx) + nodePaddingPx + nodeWidthPx / 2f
                                } else {
                                    centerOffsetX + nodePaddingPx + nodeWidthPx / 2f
                                }

                                // ── Horizontal lines: per-segment color ──
                                // Segment between sortedIndices[i] (day d1) and sortedIndices[i+1] (day d2)
                                // is colored primary only if min(d1, d2) <= streakCount.
                                if (nodeIndices.size > 1) {
                                    for (i in 0 until nodeIndices.size - 1) {
                                        val d1 = sortedIndices[i] + 1
                                        val d2 = sortedIndices[i + 1] + 1
                                        val segColor = if (minOf(
                                                d1,
                                                d2
                                            ) <= streakCount
                                        ) primaryColor else grayColor
                                        val lineStart =
                                            centerOffsetX + (i * nodeSpacingPx) + nodePaddingPx + nodeWidthPx
                                        val lineEnd =
                                            centerOffsetX + ((i + 1) * nodeSpacingPx) + nodePaddingPx
                                        drawLine(
                                            color = segColor,
                                            start = Offset(lineStart, lineY),
                                            end = Offset(lineEnd, lineY),
                                            strokeWidth = 2.dp.toPx()
                                        )
                                    }
                                }

                                // ── Top-half vertical connector (entering from previous row) ──
                                // This connector joins day (rowIndex * nodesPerRow) to day (rowIndex * nodesPerRow + 1).
                                // Color it primary only if the earlier of those two days is completed.
                                if (rowIndex > 0) {
                                    val connectingDay = rowIndex * nodesPerRowCalculated
                                    val topVertColor =
                                        if (connectingDay <= streakCount) primaryColor else grayColor
                                    drawLine(
                                        color = topVertColor,
                                        start = Offset(leadingX, 0f),
                                        end = Offset(leadingX, lineY - nodeWidthPx / 2f),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }

                                // ── Bottom-half vertical connector (exiting to next row) ──
                                // This connector joins day ((rowIndex+1) * nodesPerRow) to day ((rowIndex+1) * nodesPerRow + 1).
                                // Color it primary only if the trailing day of this row is completed.
                                if (rowIndex < nodeRows.size - 1) {
                                    val trailingDay = (rowIndex + 1) * nodesPerRowCalculated
                                    val botVertColor =
                                        if (trailingDay <= streakCount) primaryColor else grayColor
                                    drawLine(
                                        color = botVertColor,
                                        start = Offset(trailingX, lineY + nodeWidthPx / 2f),
                                        end = Offset(trailingX, size.height),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }

                            // Nodes layer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeightDp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                            ) {
                                sortedIndices.forEach { index ->
                                    val dayNumber = index + 1
                                    val isCompleted = dayNumber <= streakCount
                                    val isNext = dayNumber == streakCount + 1
                                    val isLocked = dayNumber > streakCount + 1 && !BuildConfig.DEBUG
                                    val isPlayable = BuildConfig.DEBUG || isNext && (hasActiveToday || !completedToday)
                                    val isActive = isNext && hasActiveToday
                                    val isNextButCompletedToday =
                                        isNext && completedToday && !hasActiveToday

                                    StreakNode(
                                        dayNumber = dayNumber,
                                        isCompleted = isCompleted,
                                        isActive = isActive,
                                        isLocked = isLocked,
                                        isPlayable = isPlayable,
                                        onClick = { onStart(dayNumber) },
                                        onLockedClick = {
                                            if (isNextButCompletedToday) {
                                                Toast.makeText(
                                                    context,
                                                    "Come back tomorrow to keep your streak alive!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        // No separate connector canvas needed:
                        // adjacent rows share the same vertical gap via their own top/bottom half-lines
                    } // end nodeRows.forEach
                }
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/RewardsScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.PrizeWheel
import com.quickmindgames.sudoku.component.PrizeWheelPrizes
import com.quickmindgames.sudoku.component.WalletInfoButton
import com.quickmindgames.sudoku.component.WalletInfoSection
import com.quickmindgames.sudoku.component.WalletRewardSummaryRow
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.util.GameFeedback
import com.quickmindgames.sudoku.presentation.viewmodel.PrizeCreditResult
import com.quickmindgames.sudoku.presentation.viewmodel.RewardsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class WalletInfo {
    POINTS,
    HINTS,
    BOOSTERS,
    SHIELDS
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RewardsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rewardsViewModel: RewardsViewModel = viewModel(
        factory = remember(context) { RewardsViewModel.factory(context) }
    )
    val rewardsState by rewardsViewModel.uiState.collectAsState()
    val gameFeedback = remember(context) { GameFeedback(context) }
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val pointsCounter = remember { Animatable(0f) }
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasLoadedPoints by remember { mutableStateOf(false) }
    var animateNextPointsUpdate by remember { mutableStateOf(false) }
    var selectedWalletInfo by remember { mutableStateOf<WalletInfo?>(null) }
    val totalPoints = rewardsState.totalPoints
    val hintWallet = rewardsState.hintWallet
    val doublePointsBoosterWallet = rewardsState.doublePointsBoosterWallet
    val streakShieldWallet = rewardsState.streakShieldWallet
    val initialPointHints = rewardsState.initialPointHints
    val initialStreakHints = rewardsState.initialStreakHints
    val soundAndVibrationEnabled = rewardsState.soundAndVibrationEnabled
    val lastSpinMillis = rewardsState.lastSpinMillis
    val isSpinning = rewardsState.isSpinning
    val showMigrationSummary = rewardsState.showMigrationSummary
    val cooldownRemainingMillis = lastSpinMillis?.let {
        (it + AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS - currentTimeMillis).coerceAtLeast(0L)
    }
    val canSpin = lastSpinMillis != Long.MIN_VALUE &&
            (cooldownRemainingMillis == null || cooldownRemainingMillis == 0L)
    val cooldownLabel = cooldownRemainingMillis
        ?.takeIf { !canSpin && !isSpinning }
        ?.let { remainingMillis ->
            val totalSeconds = (remainingMillis + 999L) / 1_000L
            "%02d:%02d:%02d".format(
                totalSeconds / 3_600L,
                totalSeconds % 3_600L / 60L,
                totalSeconds % 60L
            )
        }

    LaunchedEffect(lastSpinMillis) {
        while (lastSpinMillis != Long.MIN_VALUE) {
            currentTimeMillis = System.currentTimeMillis()
            val previousSpin = lastSpinMillis ?: break
            val remainingMillis =
                previousSpin + AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS - currentTimeMillis
            if (remainingMillis <= 0L) break
            delay(remainingMillis.coerceAtMost(1_000L))
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    LaunchedEffect(totalPoints) {
        if (!hasLoadedPoints) {
            pointsCounter.snapTo(totalPoints.toFloat())
            hasLoadedPoints = true
        } else if (animateNextPointsUpdate) {
            animateNextPointsUpdate = false
            pointsCounter.animateTo(
                targetValue = totalPoints.toFloat(),
                animationSpec = tween(durationMillis = 1000)
            )
        } else {
            pointsCounter.snapTo(totalPoints.toFloat())
        }
    }

    DisposableEffect(gameFeedback) {
        onDispose { gameFeedback.release() }
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.REWARDS,
            AnalyticsConstants.REWARDS_SCREEN
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val wheelSize = minOf(maxWidth * 0.9f, maxHeight * 0.46f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    bottom = 12.dp, start = 12.dp, end = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Total Points",
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    modifier = Modifier.height(28.dp)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "⭐", fontSize = 16.sp)
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = "%,d".format(pointsCounter.value.roundToInt()),
                                        fontSize = walletCountFontSize(
                                            pointsCounter.value.roundToInt()
                                        ),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                            WalletInfoButton("About total points") {
                                selectedWalletInfo = WalletInfo.POINTS
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Hint Wallet",
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "💡", fontSize = 16.sp)
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = "%,d".format(hintWallet),
                                        fontSize = walletCountFontSize(hintWallet),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                            WalletInfoButton("How to earn hints") {
                                selectedWalletInfo = WalletInfo.HINTS
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "2X Points Wallet",
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "2X",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = "%,d".format(doublePointsBoosterWallet),
                                        fontSize = walletCountFontSize(doublePointsBoosterWallet),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                            WalletInfoButton("About the 2X Points Wallet") {
                                selectedWalletInfo = WalletInfo.BOOSTERS
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.streak_shield_wallet),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "🔥", fontSize = 16.sp)
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = "%,d".format(streakShieldWallet),
                                        fontSize = walletCountFontSize(streakShieldWallet),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                            WalletInfoButton("About the Shield Wallet") {
                                selectedWalletInfo = WalletInfo.SHIELDS
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                PrizeWheel(
                    rotation = rotation.value,
                    isSpinning = isSpinning,
                    canSpin = canSpin,
                    cooldownLabel = cooldownLabel,
                    onSpin = {
                        if (!canSpin || isSpinning) return@PrizeWheel
                        coroutineScope.launch {
                            val spin = rewardsViewModel.beginSpin() ?: return@launch
                            try {
                                val segmentAngle = 360f / PrizeWheelPrizes.size
                                val targetRotation =
                                    (360f - segmentAngle / 2f -
                                            spin.prizeIndex * segmentAngle) % 360f
                                val currentRotation = ((rotation.value % 360f) + 360f) % 360f
                                val remainingRotation =
                                    (targetRotation - currentRotation + 360f) % 360f
                                gameFeedback.playSpinWheel(soundAndVibrationEnabled)
                                rotation.animateTo(
                                    targetValue = rotation.value + 10 * 360f + remainingRotation,
                                    animationSpec = tween(
                                        durationMillis = 4_000,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                                when (rewardsViewModel.creditPrize(spin)) {
                                    PrizeCreditResult.POINTS -> {
                                        animateNextPointsUpdate = true
                                        Toast.makeText(
                                            context,
                                            "You won +${spin.prize.amount} points!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                    }

                                    PrizeCreditResult.HINTS_CREDITED -> {
                                        Toast.makeText(
                                            context,
                                            "You won +${spin.prize.amount} hint!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                    }

                                    PrizeCreditResult.BOOSTER_CREDITED -> {
                                        Toast.makeText(
                                            context,
                                            "You won +${spin.prize.amount} 2X points booster!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                    }

                                    PrizeCreditResult.STREAK_SHIELD_CREDITED -> {
                                        Toast.makeText(
                                            context,
                                            context.getString(
                                                R.string.streak_shield_won,
                                                spin.prize.amount
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        gameFeedback.playPrizeCredited(soundAndVibrationEnabled)
                                    }

                                    PrizeCreditResult.ALREADY_CREDITED -> Unit
                                }

                            } finally {
                                rewardsViewModel.finishSpin()
                            }
                        }
                    },
                    modifier = Modifier.size(wheelSize)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

        }

        if (showMigrationSummary) {
            BasicAlertDialog(
                onDismissRequest = { rewardsViewModel.dismissMigrationSummary() }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF1565C0), Color(0xFF6A1B9A))
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 28.dp,
                                        topEnd = 28.dp
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎁", fontSize = 44.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.rewards_wallet_summary_title),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stringResource(R.string.rewards_welcome_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val hintGrantDetails = buildList {
                                if (initialPointHints > 0) {
                                    add(
                                        context.getString(
                                            R.string.rewards_welcome_points_hints,
                                            initialPointHints
                                        )
                                    )
                                }
                                if (initialStreakHints > 0) {
                                    add(
                                        context.getString(
                                            R.string.rewards_welcome_streak_hints,
                                            initialStreakHints
                                        )
                                    )
                                }
                                if (AppPreferences.HINT_WALLET_RELEASE_BONUS > 0) {
                                    add(
                                        context.getString(
                                            R.string.rewards_welcome_bonus_hints,
                                            AppPreferences.HINT_WALLET_RELEASE_BONUS
                                        )
                                    )
                                }
                            }.joinToString(" · ")

                            WalletRewardSummaryRow(
                                emoji = "💡",
                                title = stringResource(R.string.hint_wallet_info_title),
                                amount = initialPointHints +
                                        initialStreakHints +
                                        AppPreferences.HINT_WALLET_RELEASE_BONUS,
                                detail = hintGrantDetails
                            )
                            WalletRewardSummaryRow(
                                emoji = "🔥",
                                title = stringResource(R.string.shield_wallet_info_title),
                                amount = streakShieldWallet,
                                detail = stringResource(
                                    R.string.rewards_welcome_shields_detail,
                                    AppPreferences.STREAK_SHIELD_WALLET_RELEASE_BONUS
                                )
                            )
                            Button(
                                onClick = { rewardsViewModel.dismissMigrationSummary() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.rewards_welcome_button),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedWalletInfo != null) {
            val infoTitle = when (selectedWalletInfo) {
                WalletInfo.POINTS -> stringResource(R.string.points_wallet_info_title)
                WalletInfo.HINTS -> stringResource(R.string.hint_wallet_info_title)
                WalletInfo.BOOSTERS -> stringResource(R.string.booster_wallet_info_title)
                WalletInfo.SHIELDS -> stringResource(R.string.shield_wallet_info_title)
                null -> ""
            }
            BasicAlertDialog(
                onDismissRequest = { selectedWalletInfo = null }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF1565C0), Color(0xFF6A1B9A))
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 28.dp,
                                        topEnd = 28.dp
                                    )
                                )
                                .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = infoTitle,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            when (selectedWalletInfo) {
                                WalletInfo.POINTS -> {
                                    WalletInfoSection(
                                        label = stringResource(R.string.wallet_info_earn_label),
                                        details = listOf(
                                            stringResource(R.string.points_wallet_earn)
                                        )
                                    )
                                }

                                WalletInfo.HINTS -> {
                                    WalletInfoSection(
                                        label = stringResource(R.string.wallet_info_earn_label),
                                        details = listOf(
                                            stringResource(R.string.hint_wallet_earn_points),
                                            stringResource(R.string.hint_wallet_earn_streak),
                                            stringResource(R.string.hint_wallet_earn_wheel)
                                        )
                                    )
                                    WalletInfoSection(
                                        label = stringResource(R.string.wallet_info_use_label),
                                        details = listOf(
                                            stringResource(R.string.hint_wallet_use)
                                        )
                                    )
                                }

                                WalletInfo.BOOSTERS -> {
                                    WalletInfoSection(
                                        label = stringResource(R.string.wallet_info_earn_label),
                                        details = listOf(
                                            stringResource(R.string.booster_wallet_earn)
                                        )
                                    )
                                    WalletInfoSection(
                                        label = stringResource(R.string.wallet_info_use_label),
                                        details = listOf(
                                            stringResource(R.string.booster_wallet_info)
                                        )
                                    )
                                }

                                WalletInfo.SHIELDS -> {
                                    WalletInfoSection(
                                        label = stringResource(R.string.wallet_info_earn_label),
                                        details = listOf(
                                            stringResource(R.string.shield_wallet_earn)
                                        )
                                    )
                                    WalletInfoSection(
                                        label = stringResource(R.string.wallet_info_use_label),
                                        details = listOf(
                                            stringResource(R.string.shield_wallet_info)
                                        )
                                    )
                                }

                                null -> Unit
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun walletCountFontSize(value: Int) = when ("%,d".format(value).length) {
    in 0..4 -> 14.sp
    5 -> 13.sp
    6 -> 12.sp
    7 -> 11.sp
    else -> 10.sp
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/utils/RemoteConfigManager.kt
```kotlin
package com.quickmindgames.sudoku.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.quickmindgames.sudoku.BuildConfig
import kotlinx.coroutines.tasks.await

object RemoteConfigManager {

    /** Remote Config keys and defaults for clue counts by difficulty. */

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
    const val DEBUG_VALUE = 72

    /**
     * Initialize Remote Config with default values and settings.
     * Should be called once at app startup (e.g., in Application.onCreate()).
     */
    fun init() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(86400) // Fetch new values at most once per day
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
            KEY_ELITE_MAX_CLUES to DEFAULT_ELITE_MAX_CLUES
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
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_BREEZE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Breeze difficulty.
     */
    fun getBreezeMaxClues(): Int {
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_BREEZE_MAX_CLUES).toInt()
    }

    /**
     * Get the min clues for Pulse difficulty.
     */
    fun getPulseMinClues(): Int {
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_PULSE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Pulse difficulty.
     */
    fun getPulseMaxClues(): Int {
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_PULSE_MAX_CLUES).toInt()
    }

    /**
     * Get the min clues for Rage difficulty.
     */
    fun getRageMinClues(): Int {
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_RAGE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Rage difficulty.
     */
    fun getRageMaxClues(): Int {
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_RAGE_MAX_CLUES).toInt()
    }

    /**
     * Get the min clues for Elite difficulty.
     */
    fun getEliteMinClues(): Int {
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_ELITE_MIN_CLUES).toInt()
    }

    /**
     * Get the max clues for Elite difficulty.
     */
    fun getEliteMaxClues(): Int {
        return if (BuildConfig.DEBUG) DEBUG_VALUE else FirebaseRemoteConfig.getInstance()
            .getLong(KEY_ELITE_MAX_CLUES).toInt()
    }

}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/StreakScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.component.StreakPath
import com.quickmindgames.sudoku.presentation.viewmodel.StreakViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.MultipleEventsCutter
import com.quickmindgames.sudoku.utils.shareStreak
import java.time.LocalDate

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun StreakScreen(
    modifier: Modifier = Modifier,
    onOpenStreak: (Int) -> Unit
) {
    val context = LocalContext.current
    val streakViewModel: StreakViewModel = viewModel(
        factory = remember(context) { StreakViewModel.factory(context) }
    )
    val streakState by streakViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.STREAK,
            AnalyticsConstants.STREAK_SCREEN
        )
    }

    val today = remember { LocalDate.now().toString() }
    val streakCount = streakState.streakCount
    val hasActiveToday = streakState.activeDate == today &&
        streakState.activeGameStarted
    val completedToday = streakState.lastCompletedDate == today
    val cutter = MultipleEventsCutter.rememberMultipleEventsCutter()
    val statusText = when {
        hasActiveToday -> "Streak in progress..."
        completedToday -> "Completed today ✓"
        streakCount > 0 -> "Play today to keep it alive!"
        else -> "Start your first streak!"
    }
    val statusColor = when {
        hasActiveToday -> Color(0xFFFF6F00)
        completedToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact header section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Compact header with inline streak count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥",
                        fontSize = 32.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "My Current Streak",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Streak counter + share icon stacked
                if (streakCount > 0) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        // Compact streak counter
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .then(
                                    Modifier.clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        cutter.processEvent { context.shareStreak(streakCount) }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$streakCount",
                                fontSize = when (streakCount.toString().length) {
                                    1, 2 -> 24.sp
                                    3 -> 17.sp
                                    else -> 13.sp
                                },
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Tiny share badge pinned to top-end of the circle
                        Box(
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(22.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    CircleShape
                                )
                                .clickable {
                                    cutter.processEvent { context.shareStreak(streakCount) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share streak",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Journey path with more space
        StreakPath(
            streakCount = streakCount,
            hasActiveToday = hasActiveToday,
            completedToday = completedToday,
            onStart = { dayNumber -> onOpenStreak(dayNumber) }
        )
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/MainActivity.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quickmindgames.sudoku.data.preferences.ThemePreferences
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.repository.StatisticsRepository
import com.quickmindgames.sudoku.data.score.TotalScoreManager
import com.quickmindgames.sudoku.data.state.StreakStateManager
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.navigation.AppNav
import com.quickmindgames.sudoku.presentation.navigation.BottomScreen
import com.quickmindgames.sudoku.presentation.ui.screen.HomeScreen
import com.quickmindgames.sudoku.presentation.ui.screen.LessonScreen
import com.quickmindgames.sudoku.presentation.ui.screen.RewardsScreen
import com.quickmindgames.sudoku.presentation.ui.screen.SettingsScreen
import com.quickmindgames.sudoku.presentation.ui.screen.StreakScreen
import com.quickmindgames.sudoku.presentation.ui.screen.SudokuScreen
import com.quickmindgames.sudoku.presentation.ui.theme.SudoTheme
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.RemoteConfigManager
import com.quickmindgames.sudoku.utils.StreakReminderScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Schedule streak reminder notification
        StreakReminderScheduler.schedule(this)

        // Request POST_NOTIFICATIONS permission on Android 13+
        val requestNotificationPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                // Optionally handle result
            }
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Log analytics if opened from streak notification (no navigation)
        if (intent?.getBooleanExtra("from_streak_notification", false) == true) {
            AnalyticsUtils.logNotificationClicked(this)
        }

        // Fetch remote config values on app start
        lifecycleScope.launch {
            RemoteConfigManager.fetchAndActivate()
        }

        setContent {
            val themePreferences = remember { ThemePreferences.getInstance(this) }
            var isDarkMode by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                themePreferences.isDarkMode.collect { darkMode ->
                    isDarkMode = darkMode
                    WindowCompat
                        .getInsetsController(window, window.decorView)
                        .isAppearanceLightStatusBars = !darkMode
                }
            }

            SudoTheme(darkTheme = isDarkMode) {
                AppNav()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Log analytics if opened from streak notification (no navigation)
        if (intent.getBooleanExtra("from_streak_notification", false)) {
            AnalyticsUtils.logNotificationClicked(this)
        }
    }
}

@Composable
fun MainSudokuApp() {
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val totalScoreManager = remember { TotalScoreManager.getInstance(context) }
    val streakStateManager = remember { StreakStateManager.getInstance(context) }
    var hintWalletInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val lifetimePoints = totalScoreManager.totalScore.first()
        val savedStreak = streakStateManager.getStreakState().first()
        val currentStreakDays = savedStreak?.let { streak ->
            val lastCompletedDate = streak.lastCompletedDate
                ?.let(LocalDate::parse)
                ?: return@let 0
            val daysSinceCompletion = ChronoUnit.DAYS.between(
                lastCompletedDate,
                LocalDate.now()
            )
            if (daysSinceCompletion in 0..2) streak.streakCount else 0
        } ?: 0
        appPreferences.migrateHintWallet(lifetimePoints, currentStreakDays)
        appPreferences.grantHintWalletReleaseBonusOnce()
        appPreferences.grantStreakShieldReleaseBonusOnce()
        appPreferences.reconcileHintMilestones(lifetimePoints, currentStreakDays)
        hintWalletInitialized = true
    }

    if (!hintWalletInitialized) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    val screens = listOf(
        BottomScreen.Home,
        BottomScreen.Streak,
        BottomScreen.Rewards,
        BottomScreen.Settings
    )
    val bottomBarRoutes = remember(screens) { screens.map { it.route } }
    val currentRoute = currentRoute(navController)

    // Determine if the bottom bar should be shown based on the current route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar, enter = fadeIn(tween(150)), exit = fadeOut(
                    tween(150)
                )
            ) {
                NavigationBar {
                    screens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) { padding ->

        var stableBottom by remember { mutableStateOf(0.dp) }
        val currentBottom = padding.calculateBottomPadding()
        if (currentBottom > stableBottom) stableBottom = currentBottom

        val tabPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = stableBottom)

        NavHost(
            navController = navController,
            startDestination = BottomScreen.Home.route
        ) {
            // ── HOME TAB ────────────────────────────────────────
            composable(BottomScreen.Home.route) {
                HomeScreen(
                    modifier = Modifier.padding(tabPadding),
                    onNewGameClick = { /* optional analytics, etc. */ },
                    onDifficultySelected = { difficulty: Difficulty, useDoublePointsBooster ->
                        navController.navigate(
                            "play/new/${difficulty.name}/0?useDoublePointsBooster=$useDoublePointsBooster"
                        )
                    },
                    onResume = {
                        navController.navigate("play/resume/none/0")
                    },
                    onStreakClick = {
                        navController.navigate(BottomScreen.Streak.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLearningModeClick = {
                        navController.navigate("learn")
                    }
                )
            }

            // ── REWARDS TAB ────────────────────────────────────
            composable(BottomScreen.Rewards.route) {
                RewardsScreen(modifier = Modifier.padding(tabPadding))
            }

            // ── STREAK TAB ──────────────────────────────────────
            composable(BottomScreen.Streak.route) {
                StreakScreen(
                    modifier = Modifier.padding(tabPadding),
                    onOpenStreak = { dayNumber ->
                        navController.navigate(
                            "play/streak/${Difficulty.Breeze.name}/$dayNumber"
                        )
                    }
                )
            }

            // ── SETTINGS TAB ────────────────────────────────────
            composable(BottomScreen.Settings.route) {
                SettingsScreen(
                    modifier = Modifier.padding(
                        tabPadding
                    )
                )
            }

            // ── LESSON SCREEN (Learning Mode) ───────────────────
            composable("learn") {
                LessonScreen(
                    onLessonsComplete = {
                        navController.navigate("play/learn/${Difficulty.Breeze.name}/0") {
                            popUpTo("learn") { inclusive = true }
                        }
                    },
                    onExit = {
                        navController.popBackStack()
                    }
                )
            }

            // ── PLAY SCREEN (Sudoku) ────────────────────────────
            composable(
                route = "play/{mode}/{difficulty}/{streakDay}?useDoublePointsBooster={useDoublePointsBooster}",
                arguments = listOf(
                    navArgument("mode") { defaultValue = "new" },
                    navArgument("difficulty") { defaultValue = "none" },
                    navArgument("streakDay") { defaultValue = "0" },
                    navArgument("useDoublePointsBooster") { defaultValue = "false" }
                )
            ) { entry ->
                val mode = entry.arguments?.getString("mode") ?: "new"
                val diffStr = entry.arguments?.getString("difficulty") ?: "none"
                val streakDay = entry.arguments?.getString("streakDay")?.toIntOrNull() ?: 0
                val useDoublePointsBooster =
                    entry.arguments?.getString("useDoublePointsBooster")?.toBooleanStrictOrNull()
                        ?: false
                val difficulty = diffStr
                    .takeIf { it != "none" }
                    ?.let { s ->
                        Difficulty.entries.firstOrNull {
                            it.name.equals(s, ignoreCase = true)
                        }
                    }

                SudokuScreen(
                    mode = mode,
                    difficulty = difficulty,
                    streakDay = streakDay,
                    useDoublePointsBooster = useDoublePointsBooster,
                    onExit = { navController.popBackStack() },
                    onStartNormalGame = {
                        navController.navigate("play/new/${Difficulty.Breeze.name}/0") {
                            popUpTo(BottomScreen.Home.route)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}

/**
 * Check if a difficulty level is unlocked
 * Breeze: Always unlocked
 * Pulse: Requires 5 Breeze games
 * Rage: Requires 5 Pulse games
 * Elite: Requires 5 Rage games
 */
@Composable
fun isDifficultyUnlocked(
    difficulty: Difficulty,
    context: Context
): Pair<Boolean, String> {
    var breezeWins by remember { mutableIntStateOf(0) }
    var pulseWins by remember { mutableIntStateOf(0) }
    var rageWins by remember { mutableIntStateOf(0) }

    val repository = remember { StatisticsRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val allStats = repository.getAllStatistics().first()
                breezeWins = allStats.breeze.wins
                pulseWins = allStats.pulse.wins
                rageWins = allStats.rage.wins
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return when (difficulty) {
        Difficulty.Breeze -> Pair(true, "")
        Difficulty.Pulse -> {
            if (breezeWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Breeze games to unlock Pulse")
            }
        }

        Difficulty.Rage -> {
            if (pulseWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Pulse games to unlock Rage")
            }
        }

        Difficulty.Elite -> {
            if (rageWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Rage games to unlock Elite")
            }
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/SettingsScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.BuildConfig
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.presentation.viewmodel.SettingsViewModel
import com.quickmindgames.sudoku.presentation.viewmodel.StatisticsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.launch

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    //region State and preference collection
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = remember(context) { SettingsViewModel.factory(context) }
    )
    val settingsState by settingsViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showStatistics by remember { mutableStateOf(false) }
    //endregion

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.SETTINGS,
            AnalyticsConstants.SETTINGS_SCREEN
        )
    }

    if (showStatistics) {
        val statisticsViewModel =
            remember { StatisticsViewModel(context) }
        StatisticsScreen(
            modifier = modifier,
            viewModel = statisticsViewModel,
            onBackClick = { showStatistics = false }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = 12.dp
                    )
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // region Statistics
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStatistics = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📊", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Statistics",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View your game statistics",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                //endregion

                Spacer(Modifier.height(16.dp))

                //region Gameplay settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GAMEPLAY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.width(12.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "123",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                lineHeight = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Hide used numbers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hide a number once all 9 of its cells are filled.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.hideUsedNumbers,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                if (settingsViewModel.setHideUsedNumbers(enabled)) {
                                    Toast.makeText(
                                        context,
                                        "Free Play is now off because Hide used numbers is enabled.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                // These visual options are saved independently and apply to the Sudoku board.
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LooksOne,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Highlight same numbers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "When you select a number, matching numbers are highlighted across the board.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.highlightSameNumbers,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                settingsViewModel.setHighlightSameNumbers(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Highlight the region",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Highlight the selected cell’s row, column, and 3×3 block.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.highlightRegion,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                settingsViewModel.setHighlightRegion(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Free Play",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "No Mistakes. No Points. Not for Streak Games.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.freePlay,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                val result = settingsViewModel.setFreePlay(enabled)
                                if (result.blockedBySavedGame) {
                                    Toast.makeText(
                                        context,
                                        "Finish your current game or start a new one to change gameplay options.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                                if (result.disabledHideUsedNumbers) {
                                    Toast.makeText(
                                        context,
                                        "Hide used numbers is now off because Free Play is enabled.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Sound & Vibration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Sound effects and haptic feedback",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.soundAndVibration,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                settingsViewModel.setSoundAndVibration(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                //endregion

                Spacer(Modifier.height(16.dp))

                //region Appearance settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "APPEARANCE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.width(12.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (settingsState.isDarkMode) "🌙" else "☀️",
                            fontSize = 24.sp
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dark Mode",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (settingsState.isDarkMode) "Enabled" else "Disabled",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.isDarkMode,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch { settingsViewModel.setDarkMode(enabled) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                //endregion

                Spacer(Modifier.height(12.dp))

                //region App information
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    )
                    Text(
                        text = stringResource(R.string.lbl_puzzle),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "Version ${BuildConfig.VERSION_NAME}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
                //endregion
            } // end inner Column

        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/StatisticsScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.component.DifficultyStatsTab
import com.quickmindgames.sudoku.component.OverallStatsTab
import com.quickmindgames.sudoku.component.StreakStatsTab
import com.quickmindgames.sudoku.presentation.viewmodel.StatisticsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val allStatistics by viewModel.allStatistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val tabs = listOf("Overall", "Breeze", "Pulse", "Rage", "Elite", "Streak")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    // Intercept system back button and forward it to the same callback
    BackHandler {
        onBackClick()
    }

    // Refresh statistics when screen is focused
    LaunchedEffect(Unit) {
        viewModel.refreshStatistics()
        viewModel.hasLoadedStatistics.first { it }
        AnalyticsUtils.logStatisticsViewed(
            context = context,
            statistics = viewModel.allStatistics.value
        )
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.STATISTICS,
            AnalyticsConstants.STATISTICS_SCREEN
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Statistics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Tab Row - synchronized with pager
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Content - Swipeable with HorizontalPager
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading statistics...",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (pageIndex) {
                        0 -> OverallStatsTab(allStatistics)
                        1 -> DifficultyStatsTab(allStatistics.breeze)
                        2 -> DifficultyStatsTab(allStatistics.pulse)
                        3 -> DifficultyStatsTab(allStatistics.rage)
                        4 -> DifficultyStatsTab(allStatistics.elite)
                        5 -> StreakStatsTab(allStatistics.streak)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
```

## File: gradle/libs.versions.toml
```toml
[versions]
agp = "9.4.1"
coreKtx = "1.19.1"
datastorePreferences = "1.2.1"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.11.0"
activityCompose = "1.13.0"
kotlin = "2.4.20"
composeBom = "2026.09.00"
navigationCompose = "2.10.2"
room = "2.8.5"
ksp = "2.3.9"
workRuntimeKtx = "2.12.0"
fragment = "1.9.1"
lottie = "6.7.1"
firebaseBom = "34.19.0"
firebase = "3.0.8"
firebaseRemote = "23.1.0"
sonarqube = "7.5.0.8588"
googleservices = "4.5.0"

[libraries]
androidx-compose-material-icons-extended = { module = "androidx.compose.material:material-icons-extended" }
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastorePreferences" }
androidx-navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigationCompose" }
androidx-work-runtime-ktx = { module = "androidx.work:work-runtime-ktx", version.ref = "workRuntimeKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics" }
firebase-crashlytics = { group = "com.google.firebase", name = "firebase-crashlytics" }
firebase-remote-config = { group = "com.google.firebase", name = "firebase-config", version.ref = "firebaseRemote" }
androidx-fragment = { group = "androidx.fragment", name = "fragment", version.ref = "fragment" }
lottie-compose = { group = "com.airbnb.android", name = "lottie-compose", version.ref = "lottie" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
googleservices = { id = "com.google.gms.google-services", version.ref = "googleservices" }
firebase-crashlytics = { id = "com.google.firebase.crashlytics", version.ref = "firebase" }
sonarqube = { id = "org.sonarqube", version.ref = "sonarqube" }
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/HomeScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.DifficultyCard
import com.quickmindgames.sudoku.component.StreakSummaryCard
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.viewmodel.HomeStreakDialog
import com.quickmindgames.sudoku.presentation.viewmodel.HomeViewModel
import com.quickmindgames.sudoku.presentation.viewmodel.ProtectStreakResult
import com.quickmindgames.sudoku.presentation.ui.isDifficultyUnlocked
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.launch

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNewGameClick: () -> Unit,
    onDifficultySelected: (Difficulty, Boolean) -> Unit,
    onResume: () -> Unit,
    onStreakClick: () -> Unit,
    onLearningModeClick: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var showNewGameConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = remember(context) { HomeViewModel.factory(context) }
    )
    val homeState by homeViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var useDoublePointsBooster by remember { mutableStateOf(false) }

    // Show streak reminder dialog if needed
    if (homeState.streakDialog == HomeStreakDialog.REMINDER) {
        LaunchedEffect(homeState.streakDialog) {
            homeViewModel.onStreakReminderDialogShown()
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "Streak At Risk!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                Text(
                    text = "You missed playing yesterday! Play today to keep your streak alive.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(onClick = {
                    homeViewModel.dismissStreakDialog()
                    onStreakClick() // Redirect to Streak Tab
                }) {
                    Text("Go to Streak")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (homeState.streakDialog == HomeStreakDialog.SHIELD) {
        LaunchedEffect(homeState.streakDialog) {
            homeViewModel.onStreakShieldDialogShown()
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = stringResource(R.string.streak_shield_prompt_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                val shieldsAvailable = pluralStringResource(
                    R.plurals.streak_shields_available,
                    homeState.streakShieldWallet,
                    homeState.streakShieldWallet
                )
                Text(
                    text = stringResource(
                        R.string.streak_shield_prompt_description,
                        homeState.streakCount,
                        shieldsAvailable
                    ),
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        when (homeViewModel.protectStreak()) {
                            ProtectStreakResult.SHIELD_UNAVAILABLE -> Toast.makeText(
                                context,
                                context.getString(R.string.streak_shield_unavailable),
                                Toast.LENGTH_LONG
                            ).show()
                            ProtectStreakResult.PROTECTED,
                            ProtectStreakResult.NO_LONGER_ELIGIBLE -> Unit
                        }
                    }
                }) {
                    Text(stringResource(R.string.streak_shield_use_one))
                }
            },
            dismissButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        homeViewModel.restartStreak()
                        onStreakClick()
                    }
                }) {
                    Text(stringResource(R.string.streak_shield_restart))
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Show streak broken dialog if needed
    if (homeState.streakDialog == HomeStreakDialog.BROKEN) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "Streak Broken!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                Text(
                    text = "You missed a day and your streak has been reset. Start a new streak today!",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(onClick = {
                    homeViewModel.dismissStreakDialog()
                    onStreakClick() // Redirect to Streak Tab
                }) {
                    Text("Restart Streak")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.HOME,
            AnalyticsConstants.HOME_SCREEN
        )
    }

    if (showNewGameConfirmation) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNewGameConfirmation = false },
            title = {
                Text(
                    text = "Start a New Game?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Your current game progress will be lost. Are you sure you want to start a new game?")
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewGameConfirmation = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNewGameConfirmation = false
                        onNewGameClick()
                        showSheet = true
                    }
                ) {
                    Text("Start a New Game")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Header
                Text(
                    text = "Choose Difficulty",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Select your challenge level",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(20.dp))

                // Difficulty cards
                Difficulty.entries.forEach { difficulty ->
                    val (isUnlocked, lockedMessage) = isDifficultyUnlocked(difficulty, context)
                    DifficultyCard(
                        difficulty = difficulty,
                        isLocked = !isUnlocked,
                        onClick = {
                            showSheet = false
                            onDifficultySelected(difficulty, useDoublePointsBooster)
                            useDoublePointsBooster = false
                        },
                        onLockedClick = {
                            Toast.makeText(context, lockedMessage, Toast.LENGTH_SHORT).show()
                            /*// Show toast on main thread
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                Toast.makeText(context, lockedMessage, Toast.LENGTH_SHORT).show()
                            }*/
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2X Points Booster",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Available: ${homeState.doublePointsBoosterWallet} Boosters",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Double the points earned from this game.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = useDoublePointsBooster,
                        onCheckedChange = { useDoublePointsBooster = it },
                        enabled = homeState.doublePointsBoosterWallet > 0
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 12.dp
            )
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        StreakSummaryCard(
            streakCount = homeState.streakCount,
            completedToday = homeState.completedToday,
            hasActiveToday = homeState.hasActiveToday,
            onClick = onStreakClick
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = null,
                modifier = Modifier.size(172.dp)
            )

            Text(
                text = stringResource(R.string.lbl_puzzle),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Total lifetime score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⭐",
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (homeState.totalScore == 0) "No points yet — play a game!"
                    else "Total Points: ${"%,d".format(homeState.totalScore)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (homeState.totalScore == 0)
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resume button
            if (homeState.hasSavedGame) {
                ElevatedButton(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Resume Game",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            homeState.savedGameDifficulty?.let { difficulty ->
                                Text(
                                    text = difficulty.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    if (homeState.hasSavedGame) {
                        showNewGameConfirmation = true
                    } else {
                        onNewGameClick()
                        showSheet = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "New Game", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ── Learning Mode button ── NEW ─────────────────────
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLearningModeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                border = BorderStroke(
                    1.5.dp,
                    MaterialTheme.colorScheme.tertiary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.lbl_how_to_play),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            // ── End Learning Mode button ────────────────────────
        }
    }
}
```

## File: app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/SudokuScreen.kt
```kotlin
package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.component.LearningCompleteDialog
import com.quickmindgames.sudoku.component.NumberPad
import com.quickmindgames.sudoku.component.SudokuGridLevel
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.util.GameFeedback
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.viewmodel.HintResult
import com.quickmindgames.sudoku.presentation.viewmodel.SudokuGameViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import com.quickmindgames.sudoku.utils.formatTime
import com.quickmindgames.sudoku.utils.ordinalSuffix
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val MAX_HINTS_PER_GAME = 3

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    mode: String,
    difficulty: Difficulty?,
    streakDay: Int = 0,
    useDoublePointsBooster: Boolean = false,
    onExit: () -> Unit,
    onStartNormalGame: () -> Unit = onExit
) {
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val gameFeedback = remember(context) { GameFeedback(context) }
    val coroutineScope = rememberCoroutineScope()
    val gameViewModel: SudokuGameViewModel = viewModel(
        key = "$mode-${difficulty?.name}-$streakDay-$useDoublePointsBooster",
        factory = remember(appPreferences, mode, difficulty, streakDay, useDoublePointsBooster) {
            SudokuGameViewModel.factory(
                context = context,
                appPreferences = appPreferences,
                mode = mode,
                difficulty = difficulty,
                streakDay = streakDay,
                useDoublePointsBooster = useDoublePointsBooster
            )
        }
    )
    val preferences by gameViewModel.preferences.collectAsState()
    val timerState by gameViewModel.timerState.collectAsState()
    val hideUsedNumbers = preferences.hideUsedNumbers
    val highlightRegion = preferences.highlightRegion
    val highlightSameNumbers = preferences.highlightSameNumbers
    val freePlayEnabled = preferences.freePlay && mode != "streak"
    val soundAndVibrationEnabled = preferences.soundAndVibration

    DisposableEffect(gameFeedback) {
        onDispose { gameFeedback.release() }
    }

    val originalGrid by gameViewModel.originalGrid
    val userGrid = gameViewModel.userGrid
    val gameOver by gameViewModel.gameOver
    val wrongCells by gameViewModel.wrongCells
    val shakeCells by gameViewModel.shakeCells
    val gameWon by gameViewModel.gameWon
    val confettiComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.confetti)
    )
    val confettiProgress by animateLottieCompositionAsState(
        composition = confettiComposition,
        isPlaying = gameWon,
        restartOnPlay = true,
        iterations = 1
    )
    val selectedCell by gameViewModel.selectedCell
    val mistakes by gameViewModel.mistakes
    val timeSeconds = timerState.elapsedSeconds
    val isRunning = timerState.isRunning
    val score by gameViewModel.score
    val hasDoublePointsBooster by gameViewModel.hasDoublePointsBooster
    val currentDifficulty by gameViewModel.currentDifficulty

    val isNotesMode by gameViewModel.isNotesMode
    val hintsUsed by gameViewModel.hintsUsed
    val isHintProcessing by gameViewModel.isHintProcessing
    val hintWallet = preferences.hintWallet

    val isLearningMode = mode == "learn"
    val remainingGameHints = (MAX_HINTS_PER_GAME - hintsUsed).coerceAtLeast(0)
    val availableHints = minOf(hintWallet, remainingGameHints)
    val canUseHint = !isLearningMode && availableHints > 0
    val pointsToAward = gameViewModel.pointsToAward(freePlayEnabled)
    val learningHint by gameViewModel.learningHint
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(shakeCells) {
        delay(300.milliseconds)
        gameViewModel.clearShakeCells()
    }

    LaunchedEffect(preferences.freePlay, mode) {
        gameViewModel.onPreferencesChanged(freePlayEnabled)
    }

    LaunchedEffect(learningHint) {
        if (learningHint != null) {
            delay(2500.milliseconds)
            gameViewModel.clearLearningHint()
        }
    }

    LaunchedEffect(mode, difficulty) {
        if (gameViewModel.initialize()) onExit()
    }

    // Auto-save game state whenever important state changes
    LaunchedEffect(
        userGrid.hashCode(),
        mistakes,
        score,
        timeSeconds,
        selectedCell,
        isNotesMode,
        hintsUsed,
        hasDoublePointsBooster
    ) {
        gameViewModel.saveCurrentGame()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle observer for background/foreground events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    // Pause the timer and auto-save (already handled by LaunchedEffect)
                    gameViewModel.pauseTimer()
                }
                // ON_RESUME/ON_START: do nothing, keep paused until user resumes
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.SUDOKU_GAME,
            AnalyticsConstants.SUDOKU_SCREEN
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
            .padding(
                top = 12.dp
            )
            .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 32.dp), // Minimized margins
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    bottom = 12.dp, start = 12.dp, end = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onExit() },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.primary
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = if (isLearningMode) stringResource(R.string.lbl_practice_mode)
                    else if (mode == "streak" && streakDay > 0) "${ordinalSuffix(streakDay)} Streak"
                    else if (mode == "streak") "Streak"
                    else currentDifficulty.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }

            Spacer(Modifier.width(8.dp))

            // Keep the timer in the scorecard position during free play.
            if (!isLearningMode && freePlayEnabled) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 4.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(timeSeconds),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onPrimaryContainer
                        )
                        IconButton(
                            onClick = { gameViewModel.toggleTimer(!isLearningMode) },
                            enabled = !gameOver && !gameWon,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRunning) "Pause" else "Resume",
                                modifier = Modifier.size(18.dp),
                                tint = colors.onPrimaryContainer
                            )
                        }
                    }
                }
            } else if (!isLearningMode) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 16.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$score",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer
                        )
                    }
                }
            }
        }

        if (!isLearningMode && !freePlayEnabled) {
            // Mistakes (left) + Play/Pause Icon + Timer (right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mistakes: ",
                        fontSize = 14.sp,
                        color = colors.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    repeat(3) { index ->
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = if (index < mistakes)
                                        colors.errorContainer
                                    else
                                        colors.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (index < mistakes)
                                    colors.error
                                else
                                    colors.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Play/Pause icon and Timer anchored to the right
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(timeSeconds),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        onClick = { gameViewModel.toggleTimer(!isLearningMode) },
                        enabled = !gameOver && !gameWon,
                        modifier = Modifier
                            .background(
                                color = colors.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Resume",
                            modifier = Modifier.size(18.dp),
                            tint = colors.onPrimaryContainer
                        )
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            // Sudoku board — always composed but visually hidden when paused
            SudokuGridLevel(
                originalGrid,
                userGrid,
                wrongCells,
                shakeCells,
                selectedCell,
                highlightRegion,
                highlightSameNumbers,
                onCellClick = { r, c ->
                    gameViewModel.selectCell(r, c)
                }
            )

            // Pause overlay
            if (!isRunning && !gameOver && !gameWon) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = colors.surfaceContainerLow.copy(alpha = 0.98f),
                        )
                        .clickable { gameViewModel.resumeTimer(!isLearningMode) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    color = colors.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = colors.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Game Paused",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tap to resume",
                            fontSize = 14.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        NumberPad(
            grid = userGrid,
            enabled = isRunning && !gameOver && !gameWon,
            hideUsedNumbers = hideUsedNumbers
        ) { number ->
            val result = gameViewModel.enterNumber(
                number = number,
                isLearningMode = isLearningMode,
                freePlay = freePlayEnabled
            ) ?: return@NumberPad
            if (result.isCorrect) {
                gameFeedback.playCorrectNumber(soundAndVibrationEnabled)
            } else {
                gameFeedback.playWrongNumber(soundAndVibrationEnabled)
            }
            if (result.gameOver) gameFeedback.playGameOver(soundAndVibrationEnabled)
            if (result.gameWon) gameFeedback.playPuzzleCompleted(soundAndVibrationEnabled)
        }

        if (!isLearningMode) {

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //region Undo Button
                FilledTonalButton(
                    onClick = { gameViewModel.undoMove() },
                    enabled = gameViewModel.canUndo.value && isRunning && !gameOver && !gameWon,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Undo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                //endregion

                //region Erase Button
                FilledTonalButton(
                    onClick = { gameViewModel.eraseSelectedCell() },
                    enabled = isRunning && selectedCell != null && selectedCell?.let { (r, c) ->
                        (wrongCells.contains(r to c) || userGrid[r][c].notes.isNotEmpty())
                    } == true && !gameOver && !gameWon,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Erase",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Erase",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                //endregion

                //region Notes Toggle Button
                FilledTonalButton(
                    onClick = { gameViewModel.toggleNotes() },
                    enabled = isRunning && !gameOver && !gameWon,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isNotesMode) colors.primary else colors.secondaryContainer,
                        contentColor = if (isNotesMode) colors.onPrimary else colors.onSecondaryContainer
                    ),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (isNotesMode) "Notes" else "Notes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                //endregion

                //region Hint Button
                FilledTonalButton(
                    onClick = {
                        if (!isRunning || gameOver || gameWon || !canUseHint || isHintProcessing) {
                            return@FilledTonalButton
                        }
                        coroutineScope.launch {
                            when (gameViewModel.processHint(isLearningMode, freePlayEnabled)) {
                                HintResult.EMPTY_WALLET ->
                                    Toast.makeText(
                                        context,
                                        "Your Hint Wallet is empty.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                HintResult.CELL_NOT_SELECTED ->
                                    Toast.makeText(
                                        context,
                                        "Please select a cell for which you want to use a hint.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                HintResult.CELL_ALREADY_CORRECT ->
                                    Toast.makeText(
                                        context,
                                        "This cell is already correct.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                HintResult.GAME_WON ->
                                    gameFeedback.playPuzzleCompleted(soundAndVibrationEnabled)

                                HintResult.APPLIED -> Unit
                            }
                        }
                    },
                    enabled = isRunning && !gameOver && !gameWon && canUseHint &&
                            !isHintProcessing,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Hint ($availableHints)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = when {
                                hintsUsed >= MAX_HINTS_PER_GAME -> "Limit reached"
                                hintWallet == 0 -> "Wallet empty"
                                else -> "Used $hintsUsed/$MAX_HINTS_PER_GAME"
                            },
                            fontSize = 10.sp
                        )
                    }
                }
                //endregion
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = learningHint != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            learningHint?.let { hint ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .background(
                            color = colors.errorContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hint,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (gameOver && !isLearningMode) {
            LaunchedEffect(Unit) {
                AnalyticsUtils.logGameResult(
                    context,
                    win = false,
                    time = formatTime(timeSeconds),
                    mistakes = mistakes,
                    score = score,
                    difficulty = currentDifficulty.name,
                    isStreak = mode == "streak",
                    hintCount = hintsUsed
                )
            }

            BasicAlertDialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // ── Gradient header ──────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFB71C1C), Color(0xFFE53935))
                                    ),
                                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💀", fontSize = 52.sp)
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "Game Over",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "3 mistakes — better luck next time!",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // ── Buttons ──────────────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Try Again
                            Button(
                                onClick = {
                                    coroutineScope.launch { gameViewModel.retryGame() }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE53935)
                                )
                            ) { Text("Try Again", fontWeight = FontWeight.SemiBold) }

                            // Exit
                            FilledTonalButton(
                                onClick = {
                                    gameViewModel.dismissGameOver()
                                    onExit()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Exit", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }
        }

    } // end Column

    if (gameWon) {
        if (isLearningMode) {
            // ── Learning mode: show LearningCompleteDialog ──
            LearningCompleteDialog(
                onTryNormalMode = {
                    gameViewModel.dismissGameWon()
                    onStartNormalGame() // Pop back to home — user can tap New Game
                },
                onBackToHome = {
                    gameViewModel.dismissGameWon()
                    onExit()
                }
            )
            AnalyticsUtils.logLearningCompleted(context)
        } else {
            // Persist score & streak state
            LaunchedEffect(Unit) {
                gameViewModel.persistWin(pointsToAward, freePlayEnabled)
                AnalyticsUtils.logGameResult(
                    context,
                    win = true,
                    time = formatTime(timeSeconds),
                    mistakes = mistakes,
                    score = pointsToAward,
                    difficulty = currentDifficulty.name,
                    isStreak = mode == "streak",
                    hintCount = hintsUsed
                )
            }

            BasicAlertDialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // ── Gradient header ──────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1565C0), Color(0xFF6A1B9A))
                                    ),
                                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = confettiComposition,
                                progress = { confettiProgress },
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎉", fontSize = 48.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "You Win!",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Puzzle solved!",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                if (hasDoublePointsBooster && !freePlayEnabled) {
                                    Text(
                                        text = "You've earned $score x 2 = $pointsToAward points!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // ── Stats row ────────────────────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Time
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            colors.primaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) { Text("⏱️", fontSize = 22.sp) }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = formatTime(timeSeconds),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "Time",
                                    fontSize = 12.sp,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            if (!freePlayEnabled) {
                                // Score
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(
                                                colors.primaryContainer,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) { Text("⭐", fontSize = 22.sp) }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "$pointsToAward",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        text = "Points",
                                        fontSize = 12.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                                // Mistakes
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(
                                                colors.primaryContainer,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) { Text(if (mistakes == 0) "✅" else "❌", fontSize = 22.sp) }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "$mistakes",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        text = "Mistakes",
                                        fontSize = 12.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // ── Buttons ──────────────────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 12.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Next Game — only for non-streak mode
                            if (mode != "streak") {
                                Button(
                                    onClick = {
                                        gameViewModel.createNextGame()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1565C0)
                                    )
                                ) {
                                    Text("Next Game", fontWeight = FontWeight.SemiBold)
                                }
                            }

                            // Done
                            FilledTonalButton(
                                onClick = {
                                    gameViewModel.dismissGameWon()
                                    onExit()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Done", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
```

## File: app/build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleservices)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.sonarqube)
}

android {
    namespace = "com.quickmindgames.sudoku"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.quickmindgames.sudoku"
        minSdk = 28
        targetSdk = 36
        versionCode = 12
        versionName = "0.1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("quickmind") {
            keyAlias = "quickmindgames"
            keyPassword = "QMG#126gma"
            storeFile = file("../release key/quickmind.jks")
            storePassword = "QMG#126gma"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("quickmind")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    sonarqube {
        properties {
            property("sonar.projectKey", "QMG_Sudoku")
            property("sonar.projectName", "QMG_Sudoku")
            property("sonar.host.url", "http://localhost:9000")
            property("sonar.token", "sqp_ddd475b0a749cedebd2516f23400cc8d3927fcfb")
            property("sonar.language", "kotlin")
            property("sonar.sourceEncoding", "UTF-8")
            property("sonar.android.resourceFiles.skip", "true")
        }
    }
    tasks.matching { it.name == "sonarResolver" }.configureEach {
        dependsOn(tasks.matching { it.name.contains("generate") && it.name.contains("ResValues") })
    }
}

kotlin {
    // Ensures Gradle uses JDK 17 toolchain for Kotlin compilation
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.fragment)
    implementation(libs.lottie.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // For icons
    implementation(libs.androidx.compose.material.icons.extended)

    // For navigation
    implementation(libs.androidx.navigation.compose)

    // DataStore for persisting game state
    implementation(libs.androidx.datastore.preferences)

    // Room database with KSP
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // WorkManager for background scheduling and notifications
    implementation(libs.androidx.work.runtime.ktx)

    // firebase for analytics and crashlytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Remote Config for dynamic feature flags
    implementation(libs.firebase.remote.config)
}
```
