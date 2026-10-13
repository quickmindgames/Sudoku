# Sudoku Android Application - Developer Onboarding

This guide is for developers who need to build, navigate, and change the Sudoku Android app. It describes the current repository structure and implementation, rather than a target architecture.

## Project Overview

| | |
|---|---|
| **Project** | Sudoku (`com.quickmindgames.sudoku`) |
| **Purpose** | An offline-first Sudoku game with difficulty-based puzzles, guided lessons, daily streak challenges, points, Hint, 2X Points Booster, and Streak Shield wallets, a prize wheel, settings, and local statistics. |
| **Primary language** | Kotlin |
| **UI** | Android with Jetpack Compose and Material 3 |
| **Local persistence** | Preferences DataStore for active game, streak, and settings; Room for completed-game statistics |
| **Platform integrations** | Firebase Analytics, Crashlytics, Remote Config; WorkManager for streak reminders |
| **Android SDK** | `minSdk 28`, `compileSdk 37`, `targetSdk 36` |
| **JVM** | Java/Kotlin 17 |

The project is a single Android app module (`:app`). Quick Mind Games Sudoku is an offline-first app for standard Sudoku play, guided practice, daily streak challenges, points and wallet rewards, and local statistics. Puzzles have four difficulty levels: Breeze, Pulse, Rage, and Elite. The normal game loop is local: choose a difficulty, generate and play a puzzle, save the active board, then record the result. Firebase is used for analytics, crash reporting, and remote tuning—not for accounts, game saves, or cloud synchronization. There is no Retrofit API layer.

### First build

Open the repository in Android Studio with a compatible JDK 17 and Android SDK, or run the Gradle wrapper from the repository root:

```shell
./gradlew assembleDebug
```

On Windows, use:

```powershell
.\gradlew.bat assembleDebug
```

The application module applies the Google Services Gradle plugin. Local Firebase configuration may therefore be required for a configured build; do not commit Firebase configuration or signing credentials unless repository policy explicitly permits it. Release builds use the `release` build type and produce an Android App Bundle with minification and resource shrinking enabled.

## Architecture Layers

The app is organized by responsibility rather than by a strict dependency-injection or use-case framework. Compose screens render UI and coordinate navigation/UI effects; `SudokuGameViewModel` owns the active Sudoku session, while the statistics screen has a separate ViewModel/repository flow.

### UI/UX Features

**Purpose:** Compose screens, reusable widgets, and visual styling.

**Key files and folders:**

- `../app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen` — Home (new-game selection and booster toggle), Sudoku play, lessons, streak, rewards, settings, and statistics screens.
- `../app/src/main/java/com/quickmindgames/sudoku/component` — Shared board, number pad, difficulty, lesson, streak, statistics, and prize wheel components.
- `../app/src/main/java/com/quickmindgames/sudoku/presentation/ui/theme` — Color, typography, and Material theme.
- `../app/src/main/res` — Strings, themes, fonts, launcher assets, sounds, animations, and backup rules.

Keep each screen's entry-point composable in its screen file. Place each supporting UI `@Composable` in a separate Kotlin file under `component/`; screen files should coordinate state and compose those components rather than define helper/child composables inline.

### Presentation & Navigation

**Purpose:** Android entry point, Compose app shell, navigation routes, and the statistics state holder.

**Key files:**

- `presentation/ui/MainActivity.kt` — Activity lifecycle, notification permission, theme collection, startup Remote Config fetch, and `MainSudokuApp`.
- `presentation/navigation/AppNav.kt` — Thin entry composable delegating to `MainSudokuApp`.
- `presentation/navigation/BottomScreen.kt` — Home, Streak, Rewards, and Settings bottom-tab definitions.
- `presentation/viewmodel/StatisticsViewModel.kt` — Loads statistics from the repository and exposes `StateFlow` to the UI.

Navigation is hosted by a `NavHost` in `MainActivity.kt`; routes cover the four primary tabs (Home, Streak, Rewards, and Settings), learning, and play modes (`new`, `resume`, `streak`, and `learn`). Home passes the selected 2X Booster option into a new-game route; the difficulty sheet shows the available balance and disables the switch when the wallet is empty. The Rewards tab displays lifetime points and the Hint, 2X Points, and Streak Shield wallets alongside a weighted Prize Wheel. Each wallet card has an info button that opens a dismissible rounded-card dialog, styled consistently with the one-time Rewards welcome dialog. Dialog content is divided into Earn and/or Use sections with text-only chips; points has only an Earn section, while the other wallets explain both earning and usage. The current wheel includes point prizes, a `+1 Hint` prize, a `2X` Booster prize, and a `+1 Streak Shield` prize. Spin cooldown timestamps are persisted in `AppPreferences`; while cooling down, the wheel hub shows a live countdown and cannot be tapped. Keep route arguments and navigation callbacks coordinated with the corresponding screens.

### Business Logic

**Purpose:** Sudoku generation, rule checking, domain data types, and repository contracts.

**Key files:**

- `domain/generator/SudokuGenerator.kt` — Backtracking solution generation and clue removal based on difficulty.
- `domain/util/SudokuValidator.kt` and `utils/SudokuRules.kt` — Move and game-rule checks used by gameplay.
- `domain/util/UndoManager.kt` — Undo/redo history for board edits.
- `domain/model/` — Difficulty, puzzle, lesson, game-data, and statistics models.
- `domain/repository/` — Game and theme repository contracts.

### Data Management

**Purpose:** Active game and streak state, user preferences, repositories, scoring, feedback, and game completion.

**Key files:**

- `data/state/GameState.kt` and `GameStateManager.kt` — Active puzzle model and DataStore serialization/persistence.
- `data/state/StreakState.kt` and `StreakStateManager.kt` — Daily streak model and its separate DataStore store.
- `data/preferences/AppPreferences.kt` and `ThemePreferences.kt` — Gameplay and theme preferences, Hint, 2X Points Booster, and Streak Shield wallet balances, hint reward progress, idempotent wheel rewards, last wheel-spin timestamp, and sound/vibration setting.
- `data/rewards/HintRewardRules.kt` — Pure calculations for initial hint grants and point/streak milestone rewards.
- `data/repository/` — Game and theme adapters plus statistics aggregation.
- `data/util/GameCompletionRecorder.kt` — Records completed-game outcomes through the statistics repository.
- `data/score/TotalScoreManager.kt` and `data/util/GameFeedback.kt` — Score and gameplay feedback support.

### Persistence & Integrations

**Purpose:** Local database, key-value persistence, Firebase services, Android background work, and platform configuration.

**Key files:**

- `data/database/SudokuDatabase.kt` — Singleton Room database.
- `data/database/entity/` and `data/database/dao/` — Regular-game and streak-game records and queries.
- `utils/RemoteConfigManager.kt` — Remote clue-count settings.
- `utils/AnalyticsUtils.kt` and `AnalyticsConstants.kt` — Firebase Analytics event helpers and names.
- `utils/StreakReminderScheduler.kt` and `StreakReminderWorker.kt` — Reminder scheduling and execution.
- `SudokuApp.kt` — Firebase initialization and startup Remote Config defaults.
- `../app/src/main/AndroidManifest.xml` — Application/activity declaration and notification, advertising-ID, and vibration permissions.

### Build, Release & Documentation

**Purpose:** Gradle configuration, CI publication, and developer/product documentation.

**Key files:**

- `../settings.gradle.kts` — Repository configuration and `:app` inclusion.
- `../build.gradle.kts` and `../app/build.gradle.kts` — Plugins, Android build settings, dependencies, and release build type.
- `../gradle/libs.versions.toml` — Dependency and plugin version catalog.
- `../.github/workflows/android-build.yml` — Active GitHub Actions workflow under GitHub's workflow directory.
- `app/release/android-build-copy.yml` — A checked-in workflow copy; because it is not under `../.github/workflows`, GitHub Actions does not discover it as a workflow at that path.
- `PRODUCT_KNOWLEDGE.md` — Product and architecture summary.

The release workflow is configured for pushes to `Release/release_*` branches. It builds a signed AAB, creates a GitHub Release, and publishes to the Google Play alpha track. The Play upload expects the `PLAY_STORE_JSON_KEY` repository secret. Read the workflow before changing release behavior; the copy under `../app/release` is not the active workflow location.

## Key Concepts & Design Decisions

### Puzzle generation and difficulty

`Difficulty` defines `Breeze`, `Pulse`, `Rage`, and `Elite`. `SudokuGenerator` fills a 9-by-9 grid using randomized backtracking, then removes cells to reach a randomly selected clue count. `RemoteConfigManager` provides clue ranges; its code defaults are Breeze 46-51, Pulse 32-37, and Rage 26-31, Elite 22-26. In debug builds, the generator's clue getters use the debug constant (70) rather than the normal configured ranges. Hint availability is independent of difficulty and comes from the persistent Hint Wallet.

The generator removes cells from a valid completed solution, but does not test that the resulting puzzle has exactly one solution. Consider that behavior when changing puzzle generation or difficulty tuning.

### Active game and resume behavior

`GameState` captures the difficulty, original and solution grids, user values and notes, wrong-cell coordinates, mistakes, score, elapsed time, selected cell, notes mode, the number of wallet hints used, and whether the game has a 2X Points Booster selected. It serializes this state to a string in Preferences DataStore through `GameStateManager`; `GameRepositoryImpl` adapts the manager to the `GameRepository` contract. Older saves without `HINTS_USED` or `DOUBLE_POINTS_BOOSTER` resume with zero hints used and no booster. This is a local save, not a cloud save.

The current serialized representation is a hand-built line format. If fields or delimiters change, preserve compatibility with existing saved state or explicitly handle old payloads. `GameState.deserialize` currently returns `null` for malformed payloads.

### Streak state and reminders

`StreakState` stores the streak count, last completed date, active date, and optional active game. It is stored separately from ordinary game state using a dedicated DataStore in `StreakStateManager`. `StreakReminderScheduler` schedules reminder work and `StreakReminderWorker` performs it. Streak game results are stored separately from ordinary game statistics.

`StreakReminderScheduler` targets 10 p.m. local time and repeats daily. The worker compares the saved `lastCompletedDate` with today's local date and posts a reminder only if the player has not completed today's streak. Android 13 and later require the notification permission before the worker can post.

### Statistics and Room

Room stores completed results, not the live board. `GameStatisticEntity` represents regular game outcomes; `StreakGameEntity` represents streak outcomes. DAOs provide reactive `Flow` queries, and `StatisticsRepository` combines those flows into domain statistics models. `StatisticsViewModel` exposes the combined result to the statistics screen.

Check DAO semantics when changing statistics: the current `getCurrentConsecutiveWins()` query counts all streak wins, despite its name. Do not treat it as a true consecutive-win calculation without correcting the query and defining the intended sequence behavior.

### UI state and dependency wiring

The UI is Compose-first. `HomeViewModel` owns Home's saved-game, lifetime-points, wallet, and streak state, including streak recovery/reset actions; `HomeScreen` retains sheet/dialog presentation, booster selection, and navigation. `SettingsViewModel` observes persisted settings, normalizes mutually exclusive gameplay options, applies setting changes, enforces the saved-game guard for Free Play, and logs setting changes; `SettingsScreen` retains row presentation, feedback messages, and statistics navigation. `RewardsViewModel` observes reward balances and sound settings, manages cooldown eligibility and one-time migration-summary claiming, and coordinates prize selection/payout; `RewardsScreen` retains wheel/dialog animations, presentation, analytics, sounds, and toasts. `StreakViewModel` observes persisted streak progress and exposes the state used by `StreakScreen`, which retains streak display, sharing, analytics, and navigation. `SudokuGameViewModel` owns mode-specific initialization and resume, board/session state, gameplay actions and scoring, Hint Wallet use, timer state, autosave, and completion persistence. `SudokuScreen` renders that state and retains dialog presentation, navigation, analytics presentation, and UI feedback such as sounds and toasts. `StatisticsViewModel` owns statistics presentation state. Dependencies are constructed through singleton accessors, repositories, and Compose `remember`; there is no Hilt graph.

### Preferences and interaction details

`AppPreferences` persists options such as hiding used numbers, free play, highlighting, and sound/vibration. Free play and hide-used-numbers are mutually exclusive; enabling either disables the other. **Hide used numbers** hides a digit after all nine instances are filled. **Free Play** removes mistakes and points from regular games, but `SudokuScreen` explicitly ignores the preference in streak mode. Its Settings row has an info tooltip explaining that limitation. `SettingsScreen` prevents changing **Free Play** while a saved game exists, because that setting affects gameplay rules; visual and sound toggles are not subject to this saved-game gate. Theme preferences are handled separately. Before changing settings behavior, review both the settings UI and its persistence implementation.

The UI now labels the gameplay score as **Points** (including the home total, game HUD, and statistics summaries); the underlying score model, persistence, and analytics field names remain score-oriented. Do not treat this copy change as a data-schema migration.

### Rewards and wheel cooldown

`RewardsScreen` observes lifetime points from `TotalScoreManager` and wallet balances from `AppPreferences`. Before navigation is enabled, `MainSudokuApp` runs the initial Hint Wallet migration (one hint per 1,000 existing lifetime points plus one per five current streak days), a separate 10-hint release bonus, and a one-time one-shield release bonus. The release bonuses add to rather than overwrite their wallets and each has its own persistent completion flag. The hint migration records its historical grant breakdown and milestone cursors atomically. On the first Rewards visit after migration, a persisted one-time flag triggers a dismissible welcome dialog that summarizes the migrated point/streak hints, release bonus, and shields.

After migration, point milestones award one hint per additional 1,000 lifetime points, including all thresholds crossed by one update. Streak milestones award one hint at each new multiple of five consecutive streak wins. Losing or retrying a streak game does not advance the completed streak count; only a newly completed streak day does. When the streak count resets, milestone progress resets too, allowing the player to earn milestones again in a later streak. `HintRewardRules` contains pure calculations for initial grants and point/streak milestone updates; `AppPreferences` persists progress cursors and wallet changes together to prevent duplicate claims. Wallet info dialogs explain that points come from completed games and wheel prizes; hints come from point milestones, streak milestones, or the Prize Wheel and are usable up to three times per game; Boosters come from the Prize Wheel and double points on one completed boosted game; and Shields come from the Prize Wheel and protect a missed streak day. The sections are labeled by text-only Earn/Use chips. Prize-wheel point rewards update lifetime points through `TotalScoreManager`; the `+1 Hint`, `2X` Booster, and `+1 Streak Shield` rewards are credited once for their spin IDs. The current cooldown is `AppPreferences.WHEEL_SPIN_COOLDOWN_MILLIS`; the wheel hub displays a live `Next Spin in HH:MM:SS` countdown while cooling down. Spin and prize-credit sounds use `GameFeedback` and respect the sound/vibration preference.

`component/PrizeWheel.kt` defines `PrizeWheelPrize` with prize type, amount, relative weight, display label, and caption. The wheel uses a fixed five-color dark palette for contrast with its white prize labels, with a gold rim, pointer, and center hub; the Rewards layout centers the wheel vertically within the space below the wallet cards. Add future wheel entries there with their desired text and weight; add corresponding payout handling in `RewardsScreen` before a new type grants anything. Keep prize selection and slice-angle calculations based on the same prize list so the pointer lands on the selected segment.

### Hint Wallet and game usage

The persistent Hint Wallet balance is stored in Preferences DataStore through `AppPreferences`; it never goes below zero, and hints are not deducted from lifetime points. `SudokuScreen` uses wallet hints exclusively—there are no difficulty-specific free hints—and limits usage to three hints per game in regular and streak modes. A hint is deducted only after checking that the selected cell can be filled. The `hintsUsed` count is saved with an unfinished `GameState`, restored with that game, and reset when a genuinely new game starts. Remote Config still tunes puzzle clue ranges but no longer controls hint availability.

The 2X Points Booster balance is also stored in `AppPreferences`. A player can select one booster in the Home difficulty sheet when the wallet is non-empty. The selected booster is saved in `GameState` and restored on resume; when the game is completed, it doubles the points credited to lifetime points and regular-game statistics, displays the base and doubled totals in the win dialog, and consumes one booster.

The Streak Shield balance is stored in `AppPreferences`, shown in Rewards, and can be earned from the Prize Wheel. A one-time launch bonus credits one shield. When one calendar day has been missed, Home offers to spend one shield to preserve the streak; this advances `lastCompletedDate` by one day without increasing `streakCount`. Each subsequently missed day can consume another shield. Restarting resets the streak immediately, and leaving the streak unprotected until the next day keeps the existing automatic reset and message.

### Firebase behavior

`SudokuApp` initializes Firebase and Remote Config defaults. Analytics and Crashlytics collection are disabled in debug builds. `MainActivity` fetches and activates Remote Config at startup. Screen views are logged for the Rewards screen as well as the other instrumented screens. Settings changes log `settings_option_changed` with a string `settings_state` value containing the setting name and its new `on`/`off` state; when one gameplay preference disables the mutually exclusive option, that change is logged too. Theme changes use the separate `theme_toggled` event. The Firebase SDKs are operational integrations; puzzle state and statistics remain local.

## Guided Tour

1. **Application entry and navigation** — Start with `presentation/ui/MainActivity.kt`. Follow `AppNav()` into `MainSudokuApp()` and inspect its routes and callbacks.
2. **Puzzle generation and rules** — Read `domain/generator/SudokuGenerator.kt`, then `utils/RemoteConfigManager.kt` and `domain/util/SudokuValidator.kt`. This shows where clue counts originate and how move legality is checked.
3. **Gameplay and resume state** — Follow `presentation/ui/screen/SudokuScreen.kt` to `data/state/GameState.kt`, `GameStateManager.kt`, and `GameRepositoryImpl.kt`.
4. **Statistics and local database** — Trace `GameCompletionRecorder.kt` to `StatisticsRepository.kt`, `SudokuDatabase.kt`, the DAOs, and `StatisticsViewModel.kt`.
5. **Streak retention flow** — Start with `StreakScreen.kt`; then inspect `StreakState.kt`, `StreakStateManager.kt`, `StreakReminderScheduler.kt`, and `StreakReminderWorker.kt`.
6. **Rewards and settings** — Inspect `RewardsScreen.kt`, `component/PrizeWheel.kt`, `AppPreferences.kt`, `HintRewardRules.kt`, `TotalScoreManager.kt`, and `GameFeedback.kt` for wallet migration, milestones, wheel payouts, cooldown, countdown, and sound behavior; inspect `SudokuScreen.kt` and `GameState.kt` for wallet hint use and per-game persistence.
7. **Build and release** — Review `../app/build.gradle.kts` and `../.github/workflows/android-build.yml`. Confirm required SDK/JDK settings and CI secrets before changing the release pipeline.

## File Map

Paths below are relative to the repository root.
In tables, `.../` abbreviates `../app/src/main/java/com/quickmindgames/sudoku`.

### App entry and navigation

| File | Responsibility |
|---|---|
| `../app/src/main/java/com/quickmindgames/sudoku/SudokuApp.kt` | Application-level Firebase and Remote Config initialization. |
| `.../presentation/ui/MainActivity.kt` | Activity setup, theme, permission handling, route host, and app shell. |
| `.../presentation/navigation/AppNav.kt` | Navigation entry point used by the activity. |
| `.../presentation/navigation/BottomScreen.kt` | Home, Streak, Rewards, and Settings bottom-tab route, label, and icon definitions. |

### Screens, components, and theme

| File(s) | Responsibility |
|---|---|
| `.../presentation/ui/screen/HomeScreen.kt` | Home UI, dialog/sheet presentation, booster selection, navigation callbacks, and learning entry point. |
| `.../presentation/viewmodel/HomeViewModel.kt` | Saved-game and wallet state, total points, streak status, missed-day checks, shield protection, and streak reset. |
| `.../presentation/ui/screen/SudokuScreen.kt` | Active puzzle interface, dialog presentation, navigation, analytics presentation, and UI feedback. |
| `.../presentation/ui/screen/LessonScreen.kt` | Guided lesson experience and completion callback. |
| `.../presentation/ui/screen/StreakScreen.kt` | Streak progress display, sharing, analytics, and navigation. |
| `.../presentation/ui/screen/RewardsScreen.kt` | Rewards tab layout, wallet info dialogs, prize-wheel animation, welcome summary presentation, and screen-view analytics. |
| `.../presentation/ui/screen/SettingsScreen.kt` | User preferences rows, setting feedback, and statistics navigation. |
| `.../presentation/ui/screen/StatisticsScreen.kt` | Overall and category statistics presentation. |
| `.../presentation/viewmodel/SudokuGameViewModel.kt` | Screen-scoped Sudoku session initialization/resume, board state and actions, scoring, Hint Wallet use, timer, autosave, and completion persistence. |
| `.../presentation/viewmodel/SettingsViewModel.kt` | Settings preference state, gameplay-option normalization and guard, preference writes, and setting-change analytics. |
| `.../presentation/viewmodel/RewardsViewModel.kt` | Reward balance and sound preference state, cooldown and spin eligibility, migration-summary claim, and prize payout coordination. |
| `.../presentation/viewmodel/StreakViewModel.kt` | Observes persisted streak progress and exposes the current count, active-game status, and completion date. |
| `.../presentation/viewmodel/StatisticsViewModel.kt` | Statistics loading and UI state. |
| `.../component/PrizeWheel.kt` | Reusable animated prize wheel, point/hint/booster/shield prize display model, center-hub spin action/cooldown overlay, and pointer drawing. |
| `.../component/WalletInfoButton.kt` | Reusable info icon button on each Rewards wallet card. |
| `.../component/WalletInfoSection.kt` | Text-only section chip and explanatory copy for Earn/Use sections in wallet dialogs. |
| `.../component/WalletRewardSummaryRow.kt` | Wallet amount and grant details in the one-time Rewards welcome dialog. |
| `.../component/` | Other reusable Compose UI: difficulty cards, lesson cards/grid/dialog, number pad, Sudoku board/grid, statistics, and streak path/node/summary. |
| `.../presentation/ui/theme/Color.kt`, `Theme.kt`, `Type.kt` | App colors, Material theme, and typography. |
| `.../presentation/util/ShakeModifier.kt` | Compose shake animation modifier. |

### Domain

| File(s) | Responsibility |
|---|---|
| `.../domain/generator/SudokuGenerator.kt` | Complete-grid generation and difficulty-based puzzle construction. |
| `.../domain/util/SudokuValidator.kt` | Row, column, and box legality checks for a move. |
| `.../domain/util/UndoManager.kt` | Undo/redo history for board state. |
| `.../domain/model/Difficulty.kt` | Difficulty enum and labels. |
| `.../domain/model/GameData.kt`, `SudokuPuzzle.kt` | Active-game alias and puzzle model. |
| `.../domain/model/LessonData.kt` | Lesson content/progression model. |
| `.../domain/model/Statistics.kt` | Aggregated statistics data models. |
| `.../domain/repository/GameRepository.kt`, `ThemeRepository.kt` | Contracts for game persistence and theme settings. |

### Data and local persistence

| File(s) | Responsibility |
|---|---|
| `.../data/state/GameState.kt` | Active game and cell/note data, per-game hint usage and 2X Booster selection, and serialization format. |
| `.../data/state/GameStateManager.kt` | DataStore read/write/clear operations for resumable games. |
| `.../data/state/StreakState.kt` | Streak progress and optional active game model. |
| `.../data/state/StreakStateManager.kt` | DataStore operations for streak progress and reminder markers. |
| `.../data/preferences/AppPreferences.kt` | Gameplay preference flows/setters, persistent Hint, 2X Points Booster, and Streak Shield wallets, hint migration/milestones, per-spin reward idempotency, wallet consumption, and wheel last-spin timestamp/cooldown constant. |
| `.../data/rewards/HintRewardRules.kt` | Initial wallet grant and point/streak milestone calculation rules. |
| `.../data/preferences/ThemePreferences.kt` | Theme preference persistence. |
| `.../data/source/ThemePreferencesDataSource.kt` | Theme preference source abstraction. |
| `.../data/repository/GameRepositoryImpl.kt` | Game repository adapter backed by `GameStateManager`. |
| `.../data/repository/ThemeRepositoryImpl.kt` | Theme repository adapter. |
| `.../data/repository/StatisticsRepository.kt` | Writes game results and maps Room flows into statistics models. |
| `.../data/database/SudokuDatabase.kt` | Room database singleton and DAO accessors. |
| `.../data/database/entity/GameStatisticEntity.kt`, `StreakGameEntity.kt` | Persisted result records. |
| `.../data/database/dao/GameStatisticDao.kt`, `StreakGameDao.kt` | Queries and inserts for completed games. |
| `.../data/util/GameCompletionRecorder.kt` | Completion recording helper. |
| `.../data/points/TotalPointsManager.kt` | Lifetime points persistence (currently retains the `data.score` package and `TotalScoreManager` class name). |
| `.../data/util/GameFeedback.kt` | Sound and vibration feedback, including wheel-spin and prize-credit sounds. |

### Shared utilities and integrations

| File(s) | Responsibility |
|---|---|
| `.../utils/RemoteConfigManager.kt` | Firebase Remote Config defaults, fetch, and clue-count accessors. |
| `.../utils/AnalyticsUtils.kt`, `AnalyticsConstants.kt` | Analytics event wrapper and event names. |
| `.../utils/StreakReminderScheduler.kt`, `StreakReminderWorker.kt` | Streak reminder scheduling and work execution. |
| `.../utils/SudokuRules.kt` | Gameplay win-state and wrong-cell helpers. |
| `.../utils/ShareUtils.kt` | Streak sharing support. |
| `.../utils/TimeFormatters.kt` | Time and ordinal formatting helpers. |
| `.../utils/MultipleEventsCutter.kt` | Compose event-throttling helper. |

### Android resources and build files

| Path | Responsibility |
|---|---|
| `../app/src/main/res/values` | Strings, colors, and theme resources. |
| `../app/src/main/res/font` | Bundled font weights. |
| `../app/src/main/res/raw` | Gameplay audio and confetti animation assets. |
| `../app/src/main/res/drawable`, `mipmap-*` | Launcher and splash assets. |
| `../app/src/main/res/xml` | Backup and data extraction rules. |
| `../app/src/main/AndroidManifest.xml` | App declaration, launcher activity, and permissions. |
| `../app/build.gradle.kts` | Android SDK levels, app version, plugins, dependencies, and build types. |
| `../gradle/libs.versions.toml` | Dependency/plugin version catalog. |
| `../settings.gradle.kts`, `../build.gradle.kts`, `../gradle.properties` | Gradle repositories, module inclusion, root plugins, and build settings. |
| `../.github/workflows/android-build.yml` | Active release build and publication pipeline. |

## Complexity Hotspots

The generated knowledge graph labels the indexed source files as **medium** complexity and has no file-level entries marked high or very high. Treat the following as practical areas that need extra care because they coordinate state or encode cross-cutting behavior:

- **`presentation/ui/screen/SudokuScreen.kt`** — Central gameplay surface; UI interactions, game rules, persistence, and completion can converge here.
- **`domain/generator/SudokuGenerator.kt`** — Recursive backtracking plus difficulty configuration; changes can affect puzzle validity and play balance.
- **`data/state/GameState.kt` and `GameStateManager.kt`** — Manual serialized format is persisted across app launches; format changes can break saved games.
- **`data/state/StreakState.kt` and `StreakStateManager.kt`** — Date progression, active game, and reminder markers must remain consistent.
- **`data/repository/StatisticsRepository.kt` and both DAOs** — Aggregated UI metrics depend on query semantics and consistent outcome recording.
- **`presentation/ui/MainActivity.kt`** — Route host, permissions, theme, and app startup wiring share one large presentation entry point.

When changing these areas, trace the full feature path (screen → state/repository → persistence or platform integration) and preserve existing behavior outside the requested change.

## Development Notes

- Keep generated outputs under `../app/build` out of source changes.
- `../local.properties` is machine-specific and ignored by Git. Avoid putting credentials into tracked Gradle files or documentation.
- Dependencies are declared through the version catalog; add or update versions in `../gradle/libs.versions.toml` and aliases in the Gradle scripts as needed.
- The active CI workflow is under `../.github/workflows`. A YAML file elsewhere in the repository is not automatically an Actions workflow.
- The app uses JUnit and AndroidX test dependencies; place automated tests in the corresponding app test source sets and prefer focused coverage for game rules, state serialization, and repository aggregation.
- `PRODUCT_KNOWLEDGE.md` remains the product-level summary; this file is the more detailed developer onboarding and code-navigation guide.
