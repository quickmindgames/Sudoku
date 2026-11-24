package com.quickmindgames.sudoku.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Migration safety net for [SudokuDatabase], based on the committed schema files in
 * `app/schemas/` (the Room Gradle plugin also packages them as androidTest assets).
 *
 * Version 1 is the schema every release so far has shipped (verified against 0.1.6).
 * This test builds a version-1 database exactly as `1.json` describes it, with user rows,
 * and opens it with the current Room code. Room checks the schema identity hash on open,
 * so if the entities drift from version 1 without a version bump, this test fails.
 *
 * When the schema changes: bump the version, add a migration, and add a test here that
 * builds the old version, opens it with the new code (running the migration) and checks
 * the rows survived.
 *
 * Note: androidx.room:room-testing's MigrationTestHelper isn't used because it needs a
 * newer kotlinx-serialization than the app currently ships (1.7.3); see docs/pitfalls.md.
 */
@RunWith(AndroidJUnit4::class)
class SudokuDatabaseMigrationTest {

    private val testDbName = "migration-test"
    private val context: Context = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        context.deleteDatabase(testDbName)
    }

    /** Creates [testDbName] from `app/schemas/.../<version>.json`, the way Room would have. */
    private fun createDatabaseFromSchema(version: Int): SQLiteDatabase {
        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val json = assets.open("${SudokuDatabase::class.java.name}/$version.json")
            .bufferedReader().use { it.readText() }
        val database = JSONObject(json).getJSONObject("database")
        context.deleteDatabase(testDbName)
        val db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(testDbName), null)
        val entities = database.getJSONArray("entities")
        for (i in 0 until entities.length()) {
            val entity = entities.getJSONObject(i)
            db.execSQL(
                entity.getString("createSql")
                    .replace("\${TABLE_NAME}", entity.getString("tableName"))
            )
        }
        val setupQueries = database.getJSONArray("setupQueries")
        for (i in 0 until setupQueries.length()) db.execSQL(setupQueries.getString(i))
        db.version = database.getInt("version")
        return db
    }

    /**
     * Room rewrites `1.json` on every build while the version stays 1, so a schema change
     * without a version bump would silently update it. This pins the identity hash that
     * real users' databases already contain (read from a device database created by an
     * older release); it fails if the entities change but the version doesn't.
     */
    @Test
    fun schemaVersion1_matchesWhatShippedToUsers() {
        val db = Room.databaseBuilder(context, SudokuDatabase::class.java, testDbName).build()
        try {
            val cursor = db.openHelper.readableDatabase
                .query("SELECT identity_hash FROM room_master_table WHERE id = 42")
            val hash = cursor.use { it.moveToFirst(); it.getString(0) }
            assertEquals(
                "Schema changed without a version bump: bump SudokuDatabase.version and add a migration",
                SHIPPED_V1_IDENTITY_HASH,
                hash
            )
            assertEquals(1, db.openHelper.readableDatabase.version)
        } finally {
            db.close()
        }
    }

    @Test
    fun version1Database_withUserData_opensWithCurrentRoomCode() = runBlocking {
        createDatabaseFromSchema(1).apply {
            execSQL(
                "INSERT INTO game_statistics " +
                    "(difficulty, is_won, mistakes, score, time_seconds, is_flaw_free, date_completed) " +
                    "VALUES ('Breeze', 1, 0, 30, 68, 1, 1000)"
            )
            execSQL(
                "INSERT INTO streak_games " +
                    "(streak_day, is_won, score, time_seconds, date_completed) " +
                    "VALUES (1, 1, 25, 90, 2000)"
            )
            close()
        }

        // The current app code must open it without a migration and read the data.
        val db = Room.databaseBuilder(context, SudokuDatabase::class.java, testDbName)
            .allowMainThreadQueries()
            .build()
        try {
            assertEquals(1, db.gameStatisticDao().getTotalGamesCount().first())
            assertEquals(30, db.gameStatisticDao().getBestScoreOverall().first())
            assertEquals(1, db.streakGameDao().getTotalStreakGamesCount().first())
            assertEquals(1, db.streakGameDao().getHighestStreakDay().first())
        } finally {
            db.close()
        }
    }

    private companion object {
        /** Version 1 identity hash, as stored on users' devices (0.1.x releases). */
        const val SHIPPED_V1_IDENTITY_HASH = "7a4b5eaf6f3df6929e3e3aebd9c243e2"
    }
}
