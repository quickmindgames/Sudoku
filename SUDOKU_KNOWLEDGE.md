# Sudoku Android Application - Developer Onboarding

This guide is for developers who need to build, navigate, and change the Sudoku Android app. It describes the current repository structure and implementation, rather than a target architecture.

## Project Overview

| | |
|---|---|
| **Project** | Sudoku (`com.quickmindgames.sudoku`) |
| **Purpose** | An offline-first Sudoku game with difficulty-based puzzles, guided lessons, daily streak challenges, settings, and local statistics. |
| **Primary language** | Kotlin |
| **UI** | Android with Jetpack Compose and Material 3 |
| **Local persistence** | Preferences DataStore for active game, streak, and settings; Room for completed-game statistics |
| **Platform integrations** | Firebase Analytics, Crashlytics, Remote Config; WorkManager for streak reminders |
| **Android SDK** | `minSdk 28`, `compileSdk 37`, `targetSdk 36` |
| **JVM** | Java/Kotlin 17 |

The project is a single Android app module (`:app`). The normal game loop is local: choose a difficulty, generate and play a puzzle, save the active board, then record the result. Firebase is used for analytics, crash reporting, and remote tuning—not for accounts, game saves, or cloud synchronization. There is no Retrofit API layer.

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

The app is organized by responsibility rather than by a strict dependency-injection or use-case framework. Compose screens own much of the gameplay interaction and state coordination; the statistics screen has the clearest ViewModel/repository separation.

### UI/UX Features

**Purpose:** Compose screens, reusable widgets, and visual styling.

**Key files and folders:**

- `app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/` — Home, Sudoku play, lessons, streak, settings, and statistics screens.
- `app/src/main/java/com/quickmindgames/sudoku/component/` — Shared board, number pad, difficulty, lesson, streak, and statistics components.
- `app/src/main/java/com/quickmindgames/sudoku/presentation/ui/theme/` — Color, typography, and Material theme.
- `app/src/main/res/` — Strings, themes, fonts, launcher assets, sounds, animations, and backup rules.

### Presentation & Navigation

**Purpose:** Android entry point, Compose app shell, navigation routes, and the statistics state holder.

**Key files:**

- `presentation/ui/MainActivity.kt` — Activity lifecycle, notification permission, theme collection, startup Remote Config fetch, and `MainSudokuApp`.
- `presentation/navigation/AppNav.kt` — Thin entry composable delegating to `MainSudokuApp`.
- `presentation/navigation/BottomScreen.kt` — Home, Streak, and Settings bottom-tab definitions.
- `presentation/viewmodel/StatisticsViewModel.kt` — Loads statistics from the repository and exposes `StateFlow` to the UI.

Navigation is hosted by a `NavHost` in `MainActivity.kt`; routes cover the three primary tabs, learning, and play modes (`new`, `resume`, `streak`, and `learn`). Keep route arguments and navigation callbacks coordinated with the corresponding screens.

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
- `data/preferences/AppPreferences.kt` and `ThemePreferences.kt` — Gameplay and theme preferences.
- `data/repository/` — Game and theme adapters plus statistics aggregation.
- `data/util/GameCompletionRecorder.kt` — Records completed-game outcomes through the statistics repository.
- `data/score/TotalScoreManager.kt` and `data/util/GameFeedback.kt` — Score and gameplay feedback support.

### Persistence & Integrations

**Purpose:** Local database, key-value persistence, Firebase services, Android background work, and platform configuration.

**Key files:**

- `data/database/SudokuDatabase.kt` — Singleton Room database.
- `data/database/entity/` and `data/database/dao/` — Regular-game and streak-game records and queries.
- `utils/RemoteConfigManager.kt` — Remote clue-count and hint settings.
- `utils/AnalyticsUtils.kt` and `AnalyticsConstants.kt` — Firebase Analytics event helpers and names.
- `utils/StreakReminderScheduler.kt` and `StreakReminderWorker.kt` — Reminder scheduling and execution.
- `SudokuApp.kt` — Firebase initialization and startup Remote Config defaults.
- `app/src/main/AndroidManifest.xml` — Application/activity declaration and notification, advertising-ID, and vibration permissions.

### Build, Release & Documentation

**Purpose:** Gradle configuration, CI publication, and developer/product documentation.

**Key files:**

- `settings.gradle.kts` — Repository configuration and `:app` inclusion.
- `build.gradle.kts` and `app/build.gradle.kts` — Plugins, Android build settings, dependencies, and release build type.
- `gradle/libs.versions.toml` — Dependency and plugin version catalog.
- `.github/workflows/android-build.yml` — Active GitHub Actions workflow under GitHub's workflow directory.
- `app/release/android-build-copy.yml` — A checked-in workflow copy; because it is not under `.github/workflows/`, GitHub Actions does not discover it as a workflow at that path.
- `PRODUCT_KNOWLEDGE.md` — Product and architecture summary.

The release workflow is configured for pushes to `Release/release_*` branches. It builds a signed AAB, creates a GitHub Release, and publishes to the Google Play alpha track. The Play upload expects the `PLAY_STORE_JSON_KEY` repository secret. Read the workflow before changing release behavior; the copy under `app/release/` is not the active workflow location.

## Key Concepts & Design Decisions

### Puzzle generation and difficulty

`Difficulty` defines `Breeze`, `Pulse`, `Rage`, and `Elite`. `SudokuGenerator` fills a 9-by-9 grid using randomized backtracking, then removes cells to reach a randomly selected clue count. `RemoteConfigManager` provides the clue ranges and hint counts; its code defaults are Breeze 46-51, Pulse 32-37, Rage 26-31, Elite 22-26, and one hint per level. In debug builds, the generator's clue getters use the debug constant (70) rather than the normal configured ranges.

The generator removes cells from a valid completed solution, but does not test that the resulting puzzle has exactly one solution. Consider that behavior when changing puzzle generation or difficulty tuning.

### Active game and resume behavior

`GameState` captures the difficulty, original and solution grids, user values and notes, wrong-cell coordinates, mistakes, score, elapsed time, selected cell, notes mode, and remaining hints. It serializes this state to a string in Preferences DataStore through `GameStateManager`; `GameRepositoryImpl` adapts the manager to the `GameRepository` contract. This is a local save, not a cloud save.

The current serialized representation is a hand-built line format. If fields or delimiters change, preserve compatibility with existing saved state or explicitly handle old payloads. `GameState.deserialize` currently returns `null` for malformed payloads.

### Streak state and reminders

`StreakState` stores the streak count, last completed date, active date, and optional active game. It is stored separately from ordinary game state using a dedicated DataStore in `StreakStateManager`. `StreakReminderScheduler` schedules reminder work and `StreakReminderWorker` performs it. Streak game results are stored separately from ordinary game statistics.

`StreakReminderScheduler` targets 10 p.m. local time and repeats daily. The worker compares the saved `lastCompletedDate` with today's local date and posts a reminder only if the player has not completed today's streak. Android 13 and later require the notification permission before the worker can post.

### Statistics and Room

Room stores completed results, not the live board. `GameStatisticEntity` represents regular game outcomes; `StreakGameEntity` represents streak outcomes. DAOs provide reactive `Flow` queries, and `StatisticsRepository` combines those flows into domain statistics models. `StatisticsViewModel` exposes the combined result to the statistics screen.

Check DAO semantics when changing statistics: the current `getCurrentConsecutiveWins()` query counts all streak wins, despite its name. Do not treat it as a true consecutive-win calculation without correcting the query and defining the intended sequence behavior.

### UI state and dependency wiring

The UI is Compose-first, and `SudokuScreen` coordinates most gameplay behavior directly rather than delegating it to a dedicated game ViewModel. `StatisticsViewModel` is the main ViewModel in the project. Dependencies are constructed through singleton accessors, repositories, and Compose `remember`; there is no Hilt graph. Follow the established pattern in the touched feature unless the change explicitly includes an architecture refactor.

### Preferences and interaction details

`AppPreferences` persists options such as hiding used numbers, free play, highlighting, and sound/vibration. Free play and hide-used-numbers are mutually exclusive; enabling either disables the other. `SettingsScreen` prevents changing the **Free Play** option while a saved game exists, because that setting affects gameplay rules; it does not apply the same saved-game gate to the visual or sound toggles. Theme preferences are handled separately. Before changing settings behavior, review both the settings UI and its persistence implementation.

### Firebase behavior

`SudokuApp` initializes Firebase and Remote Config defaults. Analytics and Crashlytics collection are disabled in debug builds. `MainActivity` fetches and activates Remote Config at startup. The Firebase SDKs are operational integrations; puzzle state and statistics remain local.

## Guided Tour

1. **Application entry and navigation** — Start with `presentation/ui/MainActivity.kt`. Follow `AppNav()` into `MainSudokuApp()` and inspect its routes and callbacks.
2. **Puzzle generation and rules** — Read `domain/generator/SudokuGenerator.kt`, then `utils/RemoteConfigManager.kt` and `domain/util/SudokuValidator.kt`. This shows where clue counts originate and how move legality is checked.
3. **Gameplay and resume state** — Follow `presentation/ui/screen/SudokuScreen.kt` to `data/state/GameState.kt`, `GameStateManager.kt`, and `GameRepositoryImpl.kt`.
4. **Statistics and local database** — Trace `GameCompletionRecorder.kt` to `StatisticsRepository.kt`, `SudokuDatabase.kt`, the DAOs, and `StatisticsViewModel.kt`.
5. **Streak retention flow** — Start with `StreakScreen.kt`; then inspect `StreakState.kt`, `StreakStateManager.kt`, `StreakReminderScheduler.kt`, and `StreakReminderWorker.kt`.
6. **Build and release** — Review `app/build.gradle.kts` and `.github/workflows/android-build.yml`. Confirm required SDK/JDK settings and CI secrets before changing the release pipeline.

## File Map

Paths below are relative to the repository root.
In tables, `.../` abbreviates `app/src/main/java/com/quickmindgames/sudoku/`.

### App entry and navigation

| File | Responsibility |
|---|---|
| `app/src/main/java/com/quickmindgames/sudoku/SudokuApp.kt` | Application-level Firebase and Remote Config initialization. |
| `.../presentation/ui/MainActivity.kt` | Activity setup, theme, permission handling, route host, and app shell. |
| `.../presentation/navigation/AppNav.kt` | Navigation entry point used by the activity. |
| `.../presentation/navigation/BottomScreen.kt` | Bottom-tab route, label, and icon definitions. |

### Screens, components, and theme

| File(s) | Responsibility |
|---|---|
| `.../presentation/ui/screen/HomeScreen.kt` | New game, resume, difficulty, streak, and learning entry points. |
| `.../presentation/ui/screen/SudokuScreen.kt` | Active puzzle interface and gameplay interaction. |
| `.../presentation/ui/screen/LessonScreen.kt` | Guided lesson experience and completion callback. |
| `.../presentation/ui/screen/StreakScreen.kt` | Streak challenge and progress interface. |
| `.../presentation/ui/screen/SettingsScreen.kt` | User preferences interface. |
| `.../presentation/ui/screen/StatisticsScreen.kt` | Overall and category statistics presentation. |
| `.../presentation/viewmodel/StatisticsViewModel.kt` | Statistics loading and UI state. |
| `.../component/` | Reusable Compose UI: difficulty cards, lesson cards/grid/dialog, number pad, Sudoku board/grid, statistics, and streak path/node/summary. |
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
| `.../data/state/GameState.kt` | Active game and cell/note data; serialization format. |
| `.../data/state/GameStateManager.kt` | DataStore read/write/clear operations for resumable games. |
| `.../data/state/StreakState.kt` | Streak progress and optional active game model. |
| `.../data/state/StreakStateManager.kt` | DataStore operations for streak progress and reminder markers. |
| `.../data/preferences/AppPreferences.kt` | Gameplay preference flows and setters. |
| `.../data/preferences/ThemePreferences.kt` | Theme preference persistence. |
| `.../data/source/ThemePreferencesDataSource.kt` | Theme preference source abstraction. |
| `.../data/repository/GameRepositoryImpl.kt` | Game repository adapter backed by `GameStateManager`. |
| `.../data/repository/ThemeRepositoryImpl.kt` | Theme repository adapter. |
| `.../data/repository/StatisticsRepository.kt` | Writes game results and maps Room flows into statistics models. |
| `.../data/database/SudokuDatabase.kt` | Room database singleton and DAO accessors. |
| `.../data/database/entity/GameStatisticEntity.kt`, `StreakGameEntity.kt` | Persisted result records. |
| `.../data/database/dao/GameStatisticDao.kt`, `StreakGameDao.kt` | Queries and inserts for completed games. |
| `.../data/util/GameCompletionRecorder.kt` | Completion recording helper. |
| `.../data/score/TotalScoreManager.kt` | Score management support. |
| `.../data/util/GameFeedback.kt` | Sound and vibration feedback support. |

### Shared utilities and integrations

| File(s) | Responsibility |
|---|---|
| `.../utils/RemoteConfigManager.kt` | Firebase Remote Config defaults, fetch, clue and hint accessors. |
| `.../utils/AnalyticsUtils.kt`, `AnalyticsConstants.kt` | Analytics event wrapper and event names. |
| `.../utils/StreakReminderScheduler.kt`, `StreakReminderWorker.kt` | Streak reminder scheduling and work execution. |
| `.../utils/SudokuRules.kt` | Gameplay win-state and wrong-cell helpers. |
| `.../utils/ShareUtils.kt` | Streak sharing support. |
| `.../utils/TimeFormatters.kt` | Time and ordinal formatting helpers. |
| `.../utils/MultipleEventsCutter.kt` | Compose event-throttling helper. |

### Android resources and build files

| Path | Responsibility |
|---|---|
| `app/src/main/res/values/` | Strings, colors, and theme resources. |
| `app/src/main/res/font/` | Bundled font weights. |
| `app/src/main/res/raw/` | Gameplay audio and confetti animation assets. |
| `app/src/main/res/drawable/`, `mipmap-*` | Launcher and splash assets. |
| `app/src/main/res/xml/` | Backup and data extraction rules. |
| `app/src/main/AndroidManifest.xml` | App declaration, launcher activity, and permissions. |
| `app/build.gradle.kts` | Android SDK levels, app version, plugins, dependencies, and build types. |
| `gradle/libs.versions.toml` | Dependency/plugin version catalog. |
| `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties` | Gradle repositories, module inclusion, root plugins, and build settings. |
| `.github/workflows/android-build.yml` | Active release build and publication pipeline. |

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

- Keep generated outputs under `app/build/` out of source changes.
- `local.properties` is machine-specific and ignored by Git. Avoid putting credentials into tracked Gradle files or documentation.
- Dependencies are declared through the version catalog; add or update versions in `gradle/libs.versions.toml` and aliases in the Gradle scripts as needed.
- The active CI workflow is under `.github/workflows/`. A YAML file elsewhere in the repository is not automatically an Actions workflow.
- The app uses JUnit and AndroidX test dependencies; place automated tests in the corresponding app test source sets and prefer focused coverage for game rules, state serialization, and repository aggregation.
- `PRODUCT_KNOWLEDGE.md` remains the product-level summary; this file is the more detailed developer onboarding and code-navigation guide.
