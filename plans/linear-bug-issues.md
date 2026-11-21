# Linear issues: bugs from the app analysis (2026-11-16)

Each issue below is ready to paste into Linear: the **Title** goes in the title field and everything under **Description** goes in the body. The suggested priority and labels are listed for each one.

> Secret values (keystore passwords, Sonar token) are deliberately **not** written here. Refer to the file and line instead.

> **Status (2026-11-21):** #1–#9 are resolved in the working tree (see the ✅ markers below; #6 still needs a device check and #9 a DebugView check). The other 11 are still open.

---

## 1. Generated puzzles often have multiple solutions; valid moves are marked as mistakes

**Priority:** Urgent · **Labels:** `bug`, `gameplay`, `generator`

**Status:** ✅ Resolved (verified 2026-11-20). `SudokuGenerator.createUniquePuzzle` now removes cells one at a time and keeps a removal only while `countSolutions(puzzle, limit = 2) == 1`, so every generated puzzle has exactly one solution (`MIN_CLUES = 17`; up to `MAX_ATTEMPTS` grids tried for the closest clue count). Covered by `SudokuGeneratorTest` — uniqueness per difficulty (50 runs each) plus a <300 ms Elite speed test. Caveat: generation still runs in the ViewModel constructor, so the "main thread never blocked" criterion is tracked by #16. Files: `SudokuGenerator.kt`, `SudokuGeneratorTest.kt`.

### Description

**Summary**
`SudokuGenerator` removes cells at random until it reaches the target clue count and never checks that the puzzle still has exactly one solution. Moves are checked against the single stored solution, so a number that obeys every Sudoku rule can still be counted as a mistake. Three of those end the game.

**Measured impact** (simulation of the current generator, 60 puzzles per difficulty):

| Difficulty | Clues | Puzzles with more than one solution |
|---|---|---|
| Breeze | 46–51 | 16/60 (27%) |
| Pulse | 32–37 | 54/60 (90%) |
| Rage | 26–31 | 60/60 (100%) |
| Elite | 22–26 | 60/60 (100%) |

**Steps to reproduce**
1. Start a Rage or Elite game.
2. Use logic to find a cell with two valid candidates (an "unavoidable rectangle").
3. Enter the candidate that differs from the stored solution.

**Expected:** A move that satisfies every row, column and box constraint is never penalised. Each puzzle has exactly one solution.
**Actual:** The move is marked wrong and counts as a mistake.

**Root cause**
`app/src/main/java/com/quickmindgames/sudoku/domain/generator/SudokuGenerator.kt:132-145` shuffles the positions and blanks the first `81 - clues` of them with no uniqueness check.

**Suggested fix**
- Remove cells one at a time in shuffled order. After each removal, run a solution counter that stops at 2. Put the value back if the count is greater than 1.
- Stop at the target clue count, or when no more cells can be removed. Accept the closest result (very low clue counts such as 22 are not always reachable).
- Run generation off the main thread (`Dispatchers.Default`).
- Longer term, rate difficulty by the solving techniques required instead of by clue count alone.

**Acceptance criteria**
- [x] Unit test: generated puzzles per difficulty all have exactly 1 solution (50 runs each in `SudokuGeneratorTest`).
- [x] Each puzzle's clue count falls within the configured range, or the closest reachable value is logged.
- [ ] Generation p95 is under 300 ms on a mid-range device, and the main thread is never blocked. *(Speed test passes; off-main-thread work tracked by #16.)*

---

## 2. Release keystore, signing passwords and Sonar token are committed to the repository

**Priority:** Urgent · **Labels:** `security`, `build`, `ci`

**Status:** ✅ Resolved in-tree (verified 2026-11-20). No keystore/AAB/token is tracked (`git ls-files` clean), and `.gitignore` ignores `*.jks`, `*.keystore`, `/release key/`, `/app/release/`, `*.aab`. `build.gradle.kts` reads signing from `local.properties`/env via a `signingSecret(...)` helper with graceful fallback (debug builds still work), and no Sonar token remains in the build file. `.github/workflows/android-build.yml` decodes the keystore from `secrets.RELEASE_KEYSTORE_BASE64` and reads passwords from `secrets.*`. **Not verifiable from code (ops follow-ups):** revoking the old Sonar token, rotating the Play upload key, and purging the secrets from git history (optional).

### Description

**Summary**
Signing material and credentials are tracked in git:
- `release key/quickmind.jks`, the release keystore (tracked since the first commit).
- The store and key passwords in plain text at `app/build.gradle.kts:24-30`.
- A SonarQube token at `app/build.gradle.kts:57`.
- `app/release/app-release.aab`, a 23 MB build output.

Anyone with read access to the repo, or to any fork or clone of it, can sign APKs/AABs as the app.

**Expected:** No secrets or build outputs in version control. CI gets signing material from encrypted secrets.
**Actual:** Secrets sit in the working tree and in git history.

**Suggested fix**
1. **Rotate:** revoke the Sonar token. If Play App Signing is enabled, request an upload-key reset in the Play Console. If it isn't, assess whether to enrol now.
2. Move signing config to `local.properties` or environment variables (`SIGNING_STORE_PASSWORD`, etc.) and read them in `build.gradle.kts`. Fail gracefully when they are missing so debug builds still work.
3. CI: store the keystore as a base64 secret, decode it in the workflow (`.github/workflows/android-build.yml`) and pass the passwords via `env`.
4. `git rm --cached` the keystore and the AAB. Add `*.jks`, `*.keystore`, `/app/release/` and `*.aab` to `.gitignore`.
5. Optionally purge them from history (`git filter-repo`). This needs a coordinated force-push.

**Acceptance criteria**
- [x] No keystore, password, token or AAB in the tree (`git ls-files` is clean).
- [ ] Old token revoked and upload key rotated. *(External ops step — can't be verified from the repo.)*
- [x] The release workflow builds a signed bundle using only GitHub secrets (`android-build.yml`).

---

## 3. Prize wheel reward lost if the user leaves the Rewards screen mid-spin

**Priority:** High · **Labels:** `bug`, `rewards`

**Status:** ✅ Resolved (2026-11-20). `beginSpin` records the prize as a *pending spin* together with the cooldown in one atomic write; the wheel animation is cosmetic and the prize is credited when it stops (`creditSpin`). If the user leaves mid-spin, the pending prize is recovered and credited on the next Rewards visit (`recoverPendingSpin`). Points prizes are idempotent per `spinId` (`recordWheelPointsSpinOnce`). Files: `AppPreferences.kt`, `RewardsViewModel.kt`, `RewardsScreen.kt`.

### Description

**Summary**
`beginSpin()` saves `lastSpin` (which starts the 4-hour cooldown) before the 4-second animation. The prize is only credited after the animation finishes, inside a `rememberCoroutineScope()` that belongs to the composable. If the user switches tab, presses Home, or the process dies during the spin, the scope is cancelled. `creditPrize()` never runs, but the cooldown has already started.

**Steps to reproduce**
1. Open Rewards and tap Spin.
2. Within 4 seconds, tap another bottom-nav tab.
3. Return to Rewards.

**Expected:** The prize is credited (or the spin is not counted).
**Actual:** No prize is credited and the cooldown timer is running.

**Root cause**
`presentation/ui/screen/RewardsScreen.kt:466-485` runs the animation and then credits the prize in the composition scope. `presentation/viewmodel/RewardsViewModel.kt:113-136` persists `lastSpin` in `beginSpin`.

**Suggested fix**
Credit the prize in `RewardsViewModel` within the same DataStore transaction as `setLastSpin`, keeping it idempotent via `spinId`. Treat the animation as cosmetic only. Alternatively, save a "pending spin" and credit it on the next launch.

**Acceptance criteria**
- [x] Leaving the screen or killing the app mid-spin still credits the prize exactly once.
- [x] Points prizes are also idempotent per `spinId` (they currently have no guard).

---

## 4. Streak reminder worker throws an NPE for users who have never played a streak

**Priority:** High · **Labels:** `bug`, `notifications`, `crash`

**Status:** ✅ Resolved (2026-11-20). `doWork()` treats a null state as streak 0 via a `when` (no more `!!`), and `title`/`message` are parameters of `sendNotification`. Covered by `StreakReminderWorkerTest` (instrumentation, `TestListenableWorkerBuilder`) for null / streak 0 / streak > 0 not played today / played today. Files: `StreakReminderWorker.kt`, `StreakReminderWorkerTest.kt`, `app/build.gradle.kts` + version catalog (`work-testing`).

### Description

**Summary**
In `StreakReminderWorker.doWork()`, when there is no saved streak state (`null`, as on every fresh install), the first `if` is false and the second one evaluates `streakState?.streakCount!!`, which throws `NullPointerException`. The worker fails every day, so new users never receive the "Start your streak!" notification. Only users who have played a streak at least once get reminders.

**Steps to reproduce**
1. Fresh install, with notification permission granted. Don't play a streak.
2. Wait for (or force-run) the `streak_reminder` work.

**Expected:** The "Start your streak!" notification is shown.
**Actual:** The worker fails with an NPE and no notification is shown.

**Root cause**
`app/src/main/java/com/quickmindgames/sudoku/utils/StreakReminderWorker.kt:31-41`. A `null` state isn't treated as streak 0, and the `!!` throws.

**Suggested fix**
```kotlin
val state = streakStateManager.getStreakState().first() ?: StreakState(0, null, null, null)
when {
    state.streakCount == 0 -> notifyStart()
    state.lastCompletedDate != today -> notifyKeepAlive()
}
```
Also move `title`/`message` out of mutable `var` fields and into parameters.

**Acceptance criteria**
- [x] Unit/instrumented test using `TestListenableWorkerBuilder` covers the null state, streak 0, streak > 0 not played today, and played today.

---

## 5. Toggling Free Play resets mistakes, and Free Play wins count toward unlocks and stats

**Priority:** High · **Labels:** `bug`, `gameplay`, `exploit`

**Status:** ✅ Resolved (2026-11-20). Free Play wins are no longer recorded — `recordCompletion` skips `recordGameCompletion` when Free Play is on, so they don't count toward unlocks or win-rate/time stats. The lives-reset path is unreachable: Free Play and Hide Used Numbers can't be toggled while a saved game exists (blocked with the "finish your current game" message) and are now independent of each other (toggling one no longer flips the other). Implemented without an `is_free_play` column/migration — the live preference is reliable because the toggles are locked mid-game. (Free Play also hides hints, so no hint is debited when no points are credited.) Files: `SudokuGameViewModel.kt`, `SettingsViewModel.kt`, `SettingsScreen.kt`, `AppPreferences.kt`.

### Description

**Summary**
`onPreferencesChanged(freePlay = true)` sets `mistakes` and `score` to 0. Turning Free Play on and then off again during a saved game restores all 3 lives. Separately, Free Play games can't be lost but are still recorded as wins in `game_statistics`. Those wins unlock Pulse, Rage and Elite (5 wins each) and inflate the win rate and best time.

**Steps to reproduce (lives reset)**
1. Start a Breeze game and make 2 mistakes. Exit (the game is auto-saved).
2. Settings → enable Free Play → resume → exit.
3. Settings → disable Free Play → resume.

**Expected:** Mistakes stay at 2.
**Actual:** Mistakes are 0.

**Steps to reproduce (unlock farming)**
1. Enable Free Play and win 5 Breeze games.

**Expected:** Pulse stays locked, or Free Play results are excluded from stats.
**Actual:** Pulse unlocks.

**Root cause**
- `presentation/viewmodel/SudokuGameViewModel.kt:322-327`: `onPreferencesChanged` resets the counters.
- `presentation/viewmodel/SudokuGameViewModel.kt:597-646`: `recordCompletion` records stats regardless of Free Play.

**Suggested fix**
- Fix the Free Play setting per game at game start (persist it in `GameState`) instead of reacting to live preference changes. Never touch `mistakes`.
- Skip `recordGameCompletion` for Free Play games, or store a `is_free_play` column and exclude those rows from unlock and stat queries.

**Acceptance criteria**
- [x] Toggling Free Play never changes the mistake count of an in-progress game.
- [x] Free Play wins don't count toward difficulty unlocks or win-rate stats.

---

## 6. "Today" is cached, so streak status is wrong after midnight while the app stays open

**Priority:** High · **Labels:** `bug`, `streak`

**Status:** ✅ Resolved in code (2026-11-20); device check pending. A new `utils/CurrentDateProvider` exposes `today: StateFlow<LocalDate>`. It refreshes on screen resume (`LifecycleResumeEffect` in `HomeScreen`/`StreakScreen` → `refreshDate()`) and at local midnight (`tickAtMidnight()` in `viewModelScope`), and emits only when the date changes. `HomeViewModel` combines it with the streak flows, so `completedToday`, `hasActiveToday` and the reminder/shield/broken dialogs are re-evaluated when the day changes. `StreakViewModel` now computes `hasActiveToday`/`completedToday` itself, and `StreakScreen` no longer caches the date. Covered by `CurrentDateProviderTest` (date rollover, same-day refresh, time until midnight). Files: `CurrentDateProvider.kt`, `HomeViewModel.kt`, `StreakViewModel.kt`, `HomeScreen.kt`, `StreakScreen.kt`, `CurrentDateProviderTest.kt`, `docs/SUDOKU_KNOWLEDGE.md`.

### Description

**Summary**
The current date is captured once and never refreshed:
- `presentation/viewmodel/HomeViewModel.kt:55`: `private val today = LocalDate.now().toString()`
- `presentation/ui/screen/StreakScreen.kt:75`: `remember { LocalDate.now().toString() }`

If the app stays in memory across midnight, `completedToday`, `hasActiveToday` and the status text ("Completed today ✓") all refer to the previous day. The home streak dialogs (reminder, shield, broken) are also only evaluated when a DataStore flow emits, not when the date changes.

**Steps to reproduce**
1. Complete today's streak at 23:58. Leave the app in the background.
2. After 00:00, return to the Streak tab.

**Expected:** "Play today to keep it alive!"
**Actual:** "Completed today ✓"

**Suggested fix**
Expose a `currentDate` flow that re-emits on `ON_RESUME` (and optionally at midnight using `Intent.ACTION_DATE_CHANGED` or a delay to the next midnight). Combine it into the Home and Streak UI state. Read `LocalDate.now()` at the moment of each comparison instead of caching it.

**Acceptance criteria**
- [ ] Changing the device date and resuming the app updates the Home and Streak status correctly. *(Implemented and unit-tested; needs a manual check on a device.)*

---

## 7. A misconfigured Remote Config clue range crashes puzzle generation

**Priority:** High · **Labels:** `bug`, `crash`, `remote-config`

**Status:** ✅ Resolved (2026-11-20). `SudokuGenerator.cluesFor` now picks the clue count through `SudokuGenerator.pickClues(min, max)`, which swaps a reversed range (min > max) and clamps both ends to 17..81, so a bad Remote Config value can no longer throw. Covered by `SudokuGeneratorTest` (reversed range, valid range, equal bounds, negative and >81 values). Not done: Crashlytics non-fatal logging and validation inside `RemoteConfigManager` (the guard lives at the single point of use). Files: `SudokuGenerator.kt`, `SudokuGeneratorTest.kt`.

### Description

**Summary**
The clue counts come straight from Firebase Remote Config, and `(min..max).random()` throws `NoSuchElementException` when `min > max`. Out-of-range values (for example 0 or 90) are not validated either. A single bad console edit would crash every new game for every user.

**Root cause**
`domain/generator/SudokuGenerator.kt:109-130` and `utils/RemoteConfigManager.kt` (no validation).

**Suggested fix**
In `RemoteConfigManager`, clamp each value to a safe range (for example 17..80), swap min and max if they are reversed, and fall back to the defaults when a value is invalid. Log a non-fatal error to Crashlytics when that happens.

**Acceptance criteria**
- [x] Unit test: min > max, negative values and values above 81 all produce a valid range and no exception.

---

## 8. Inconsistent score when a hint completes a puzzle with a 2× booster; debug multiplier mutates live score

**Priority:** Medium · **Labels:** `bug`, `scoring`

**Status:** ✅ Resolved (2026-11-20). Win stats are no longer recorded at the moment of winning; `SudokuGameViewModel.persistWin(freePlay)` is now the single win path. It consumes the booster first, doubles only if one was actually consumed (otherwise it clears the booster flag so the dialog drops "x 2"), computes the award once via `finalScore()`, and uses that same value for the stats row, lifetime points and the return value logged to analytics. It snapshots score, mistakes, time and difficulty before suspending, and runs `NonCancellable` so leaving the screen can't consume a booster without crediting points. The debug multiplier no longer mutates `score`; it applies only to the lifetime-points credit (`creditedPoints()`), so stats and dialog stay real in debug builds. Kept the documented rule that a booster is consumed on a win (not reserved at game start). Covered by `SudokuGameViewModelTest` (doubling, overflow, debug credit). Files: `SudokuGameViewModel.kt`, `SudokuScreen.kt`, `SudokuGameViewModelTest.kt`, `docs/SUDOKU_KNOWLEDGE.md`.

### Description

**Summary**
1. When the final cell is filled by a hint, `recordCompletion(useHintScore = true)` saves the **non-doubled** score to statistics. `persistWin(pointsToAward)` then adds the **doubled** score to lifetime points and the win dialog shows the doubled value. Stats and wallet disagree.
2. `persistWin` calls `consumeDoublePointsBooster()` but ignores its return value, so points are doubled even if the wallet balance is already 0.
3. In debug builds, `recordCompletion` does `score *= 17` in place, so the UI and saved results show the multiplied value. That makes debug testing of scoring misleading.

**Root cause**
`presentation/viewmodel/SudokuGameViewModel.kt:517-521`, `:526-532` and `:598`.

**Suggested fix**
Compute the final award in one place (`finalScore()`) and use it for stats, wallet and UI. Reserve or consume the booster when the game starts and save that in `GameState`. Apply the debug multiplier only to the awarded amount, behind an explicit debug toggle.

**Acceptance criteria**
- [x] Stats row, lifetime points delta and dialog value are equal for every win path (normal, hint-finished, booster, free play). *(In debug builds the lifetime-points credit is multiplied by `DebugOptions.SCORE_MULTIPLIER` on purpose.)*

---

## 9. `logLearningCompleted` analytics event fires on every recomposition

**Priority:** Medium · **Labels:** `bug`, `analytics`

**Status:** ✅ Resolved in code (2026-11-21); DebugView check pending. The call was removed from the composable body and moved to `SudokuGameViewModel.enterNumber`, which logs `learning_completed` once, at the moment the learning puzzle is solved. `enterNumber` returns early once `gameWon` is set, so it can't fire twice. This is stricter than a `LaunchedEffect(Unit)`, which would log again after a configuration change. Files: `SudokuGameViewModel.kt`, `SudokuScreen.kt`.

### Description

**Summary**
`AnalyticsUtils.logLearningCompleted(context)` is called directly in the composable body when `gameWon && isLearningMode`. It runs on every recomposition while the dialog is visible (animations, configuration changes), which inflates the event count.

**Root cause**
`presentation/ui/screen/SudokuScreen.kt:776-777`

**Suggested fix**
Wrap it in `LaunchedEffect(Unit) { ... }`, as the non-learning branch already does, or log it from the ViewModel when the win happens.

**Acceptance criteria**
- [ ] Exactly one `learning_completed` event per completed learning puzzle (checked in DebugView). *(Implemented; needs a DebugView check.)*

---

## 10. Auto-save is driven by the per-second timer; the last move can be lost and disk writes happen every second

**Priority:** Medium · **Labels:** `bug`, `persistence`, `performance`

### Description

**Summary**
The auto-save `LaunchedEffect` is keyed on `userGrid.hashCode()` and other values, including `timeSeconds`. `SnapshotStateList` doesn't hash by content, so in practice the **timer tick** triggers the saves:
- The full game state is serialized and written to DataStore every second. In streak mode each save also reads the streak store, writes it, and runs `processStreakMilestones` (another write). That is 2–3 disk writes per second.
- When the timer is paused (`ON_PAUSE` pauses it), nothing triggers a save. A move made less than 1 second before the app is backgrounded may never be saved.

**Root cause**
`presentation/ui/screen/SudokuScreen.kt:190-221`. Also, `StreakStateManager.saveStreakState` always calls `processStreakMilestones`.

**Suggested fix**
- Save from the ViewModel after each state-changing action (enter, erase, undo, hint, notes toggle), debounced to about 500 ms.
- Save explicitly on `ON_STOP` (in `viewModelScope` or with `NonCancellable`).
- Don't save on timer ticks. Save the elapsed time on pause or stop only.
- Only call `processStreakMilestones` when `streakCount` changes.

**Acceptance criteria**
- [ ] Making a move and immediately swiping the app away, then relaunching, shows the move.
- [ ] No more than one DataStore write per user action while idle in a game.

---

## 11. R8 keep rules prevent shrinking Compose; release AAB is about 23 MB

**Priority:** Medium · **Labels:** `build`, `performance`, `app-size`

### Description

**Summary**
`app/proguard-rules.pro:38` has `-keep class androidx.compose.** { *; }`, and similar blanket keeps exist for `androidx.room.**` and `androidx.work.**`. These stop R8 from removing unused code. Combined with `material-icons-extended`, the release bundle is about 23 MB, which is large for a Sudoku game.

**Suggested fix**
- Remove the blanket `-keep` rules for Compose, Room and WorkManager. These libraries ship their own consumer rules.
- Replace `material-icons-extended` with only the icons used (copied as vector drawables or `ImageVector`s), or rely on R8 once the keep rules are gone.
- Compare the size with `bundletool get-size total` before and after.

**Acceptance criteria**
- [ ] Release download size is reduced significantly (target under 8 MB) with no runtime crashes in a smoke test of every screen.

---

## 12. The 3-hints-per-game limit is enforced only in the UI

**Priority:** Low · **Labels:** `bug`, `hints`

### Description

**Summary**
`MAX_HINTS_PER_GAME = 3` lives in `SudokuScreen.kt:87` and only disables the button. `SudokuGameViewModel.useHint()` doesn't check `hintsUsed`, so any other call path (or a race from fast taps before recomposition) can go over the limit.

**Suggested fix**
Move the constant into the ViewModel or domain layer and return a new `HintResult.LIMIT_REACHED` from `useHint()` before consuming from the wallet.

**Acceptance criteria**
- [ ] Unit test: the 4th `useHint()` call returns `LIMIT_REACHED` and the wallet balance is unchanged.

---

## 13. Resumed games lose `scoredCells` and undo history; streak resume loses notes mode and booster flag

**Priority:** Low · **Labels:** `bug`, `persistence`

### Description

**Summary**
- `scoredCells` and `undoHistory` aren't part of `GameState`. After a resume, erasing and re-entering a correct value can score it again, and Undo is empty.
- `initializeStreakGame()` restores the game without `isNotesMode` and `puzzleData` (unlike `restoreRegularGame()`).

**Root cause**
`data/state/GameState.kt` (missing fields) and `presentation/viewmodel/SudokuGameViewModel.kt:216-233`.

**Suggested fix**
Add `scoredCells` to `GameState` (as an 81-bit string or a list of indexes) and optionally a capped undo history. Share a single `restoreFrom(state)` function between the regular and streak paths.

**Acceptance criteria**
- [ ] Exit and resume keeps the score correct (no double scoring) and keeps notes mode in both regular and streak games.

---

## 14. Statistics are wrong: "consecutive wins", average time and overall totals

**Priority:** Low · **Labels:** `bug`, `statistics`

### Description

**Summary**
1. `StreakGameDao.getCurrentConsecutiveWins()` (`data/database/dao/StreakGameDao.kt:84-88`) returns `COUNT(*) WHERE is_won = 1`, which is total wins, not consecutive wins.
2. Average time queries include lost games, which skews the average down.
3. Streak games are written to both `streak_games` and `game_statistics` (difficulty `"streak"`), so overall totals mix streak and regular games while the per-difficulty totals don't add up to the overall total.

**Suggested fix**
1. Compute consecutive wins from the most recent rows ordered by `date_completed DESC` until the first loss.
2. Add `AND is_won = 1` to the average-time queries, or label the stat "average time (all games)".
3. Decide on one source of truth for streak results and filter it consistently.

**Acceptance criteria**
- [ ] DAO tests with seeded data verify each stat.

---

## 15. Prize wheel cooldown relies on the device wall clock

**Priority:** Low · **Labels:** `bug`, `rewards`, `exploit`

### Description

**Summary**
The cooldown compares `System.currentTimeMillis()` with the stored `lastSpin`. Moving the device clock forward allows unlimited spins. Moving it backward after a spin locks the wheel for a very long time.

**Root cause**
`presentation/viewmodel/RewardsViewModel.kt:113-121` and `presentation/ui/screen/RewardsScreen.kt:92-132`.

**Suggested fix**
Also store `SystemClock.elapsedRealtime()` and the boot count. If `lastSpin > now` (the clock went backwards), reset the cooldown to its maximum remaining value instead of a far-future time. For real anti-cheat, validate spins against a server time source.

**Acceptance criteria**
- [ ] Moving the clock backward after a spin never leaves a cooldown longer than 4 hours.
- [ ] Moving the clock forward within the same boot does not unlock a spin.

---

## 16. Puzzle generated on the main thread in the ViewModel constructor, including when not needed

**Priority:** Low · **Labels:** `performance`, `tech-debt`

### Description

**Summary**
`SudokuGameViewModel` calls `SudokuGenerator.generate()` in a property initializer, so it runs on the main thread during ViewModel creation. It runs even in `resume` mode (where the result is thrown away) and runs twice in `streak` mode. Once uniqueness checking lands (issue 1), this will cause visible jank.

**Root cause**
`presentation/viewmodel/SudokuGameViewModel.kt:82-87`

**Suggested fix**
Generate lazily inside `initialize()` only for the `new`, `learn` and new-streak paths, using `withContext(Dispatchers.Default)`. Show a loading state until the puzzle is ready.

**Acceptance criteria**
- [ ] No generation happens on resume. Generation never runs on the main thread (checked with StrictMode or a trace).

---

## 17. Room database has no migration path (`version = 1`, `exportSchema = false`)

**Priority:** Low · **Labels:** `tech-debt`, `database`

### Description

**Summary**
`data/database/SudokuDatabase.kt` uses `exportSchema = false` and has no migrations. The first schema change (for example adding `is_free_play` from issue 5) will crash existing installs with `IllegalStateException`, unless someone adds `fallbackToDestructiveMigration`, which would wipe users' stats.

**Suggested fix**
Enable `exportSchema = true` with `room { schemaDirectory("$projectDir/schemas") }`, commit the schema JSON, and use `AutoMigration` or explicit migrations with `MigrationTestHelper` tests.

**Acceptance criteria**
- [ ] Schema JSON is committed. Future schema bumps come with a migration test.

---

## 18. Locked difficulties flash as locked before stats load

**Priority:** Low · **Labels:** `bug`, `ui`

### Description

**Summary**
`isDifficultyUnlocked()` (`presentation/ui/MainActivity.kt`, bottom of the file) starts with `breezeWins = pulseWins = rageWins = 0` and loads the real values asynchronously. Users who have unlocked levels briefly see them locked (with the lock message) each time the composable is created. The extra `coroutineScope.launch` inside `LaunchedEffect` is also unnecessary.

**Suggested fix**
Move the unlock state into `HomeViewModel` as a `StateFlow<UnlockState?>` from `getAllDifficultiesStatistics()`, and show a neutral or loading state while it is `null`.

**Acceptance criteria**
- [ ] No locked-to-unlocked flash on the Home or difficulty picker for a user with all levels unlocked.

---

## 19. Notification UX: permission asked on first launch, high-importance channel, non-monochrome icon

**Priority:** Low · **Labels:** `notifications`, `ux`

### Description

**Summary**
- `POST_NOTIFICATIONS` is requested in `MainActivity.onCreate` on every launch until granted (`presentation/ui/MainActivity.kt:96`), with no context for the user. This lowers the grant rate.
- The channel uses `IMPORTANCE_HIGH` (heads-up banner) for a daily reminder.
- `setSmallIcon(R.mipmap.ic_launcher)` renders as a blank or white square on most devices. Small icons must be monochrome.

**Suggested fix**
Ask for the permission after the first completed streak, or from a pre-prompt explaining reminders. Use `IMPORTANCE_DEFAULT`. Add a monochrome `ic_stat_notification` vector.

**Acceptance criteria**
- [ ] No permission prompt on first launch. The notification shows the correct icon on Android 13–16.

---

## 20. Notes are capped at 4 candidates per cell

**Priority:** Low · **Labels:** `gameplay`, `ux`

### Description

**Summary**
In notes mode, `enterNumber` silently ignores a 5th note (`notes.size < 4`). Standard Sudoku notes allow all 9 candidates, so experienced players find this surprising and get no feedback when it happens.

**Root cause**
`presentation/viewmodel/SudokuGameViewModel.kt:408-410`

**Suggested fix**
Allow up to 9 notes. Update the cell's note layout to a 3×3 mini-grid if it currently assumes 4 slots. If the cap is intentional, show feedback when it is reached.

**Acceptance criteria**
- [ ] A cell can hold all 9 candidates and renders them legibly on a small (360 dp) screen.
