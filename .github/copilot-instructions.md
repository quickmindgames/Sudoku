# Copilot Instructions: Sudoku Android App

## Project summary

Offline-first Sudoku game (`com.quickmindgames.sudoku`) with four difficulty levels (Breeze, Pulse, Rage, Elite), guided lessons, daily streak challenges, a persistent points and hint wallet, a prize wheel, settings, and local statistics.

There are no accounts, no cloud saves, and no backend API. Firebase is used only for Analytics, Crashlytics, and Remote Config.

For detailed onboarding and a file map, see `docs/SUDOKU_KNOWLEDGE.md`.

## Tech stack

- Kotlin, JVM 17
- Jetpack Compose with Material 3
- Single module `:app` (`minSdk 28`, `compileSdk 37`, `targetSdk 36`)
- Preferences DataStore for active game, streak, and settings
- Room for completed-game statistics only
- WorkManager for streak reminders
- Dependencies via the version catalog in `gradle/libs.versions.toml`
- No Hilt and no Retrofit

## Build and test

```shell
./gradlew assembleDebug   # Windows: .\gradlew.bat assembleDebug
./gradlew test
```

## Code conventions

- Follow the existing pattern of the feature you are touching: singleton accessors, repositories, and Compose `remember`. Do not introduce a DI framework or new architecture unless explicitly asked.
- `HomeViewModel` owns Home's saved-game, lifetime-points, wallet, and streak state, including streak recovery/reset actions. Keep sheet/dialog presentation, booster selection, and navigation in `HomeScreen`.
- `SettingsViewModel` owns persisted settings state, gameplay-option normalization, preference updates, and the saved-game guard for Free Play. Keep settings row presentation, user feedback, and statistics navigation in `SettingsScreen`.
- `RewardsViewModel` owns reward balances, cooldown eligibility, migration-summary claiming, and prize-spin/payout coordination. Keep wheel/dialog animations and feedback in `RewardsScreen`.
- `StreakViewModel` observes persisted streak progress and exposes the state used by `StreakScreen`; keep streak display, sharing, and navigation in Compose.
- `SudokuGameViewModel` owns Sudoku session state, initialization/resume, gameplay actions, timer behavior, and game persistence. Keep rendering, dialogs, UI feedback, and navigation callbacks in `SudokuScreen`; extend this screen-scoped ViewModel for gameplay behavior rather than adding another game ViewModel.
- Package layout: `presentation/` (screens, navigation, theme), `component/` (reusable Compose UI), `domain/` (generator, validator, models, repository contracts), `data/` (state, preferences, repositories, Room), `utils/`.
- Keep each screen's entry-point composable in its `presentation/ui/screen` file. Put every supporting UI `@Composable` in its own Kotlin file under `component/`; do not define helper or child composables in a screen file. Keep presentation-only state and screen orchestration in the screen, put persistent/business state in its ViewModel when needed, and pass values and callbacks into components.
- Add new dependencies as versions in `gradle/libs.versions.toml` and aliases in the Gradle scripts.
- Put user-facing text in `app/src/main/res/values/strings.xml`.
- Keep changes minimal. Trace the full path (screen, state/repository, persistence) and preserve existing behavior outside the requested change.

## Domain rules and terminology

- **Points vs. score:** the UI says "Points", but code, persistence, and analytics identifiers still use "score". Do not rename them or treat this as a data migration.
- **Clue counts** come from `RemoteConfigManager`. Code defaults: Breeze 46-51, Pulse 32-37, Rage 26-31, Elite 22-26. Debug builds use a fixed clue constant (70). Hint availability comes only from the persistent Hint Wallet, not Remote Config.
- **Hint Wallet** data is in `AppPreferences` (Preferences DataStore). The one-time historical migration grants one hint per 1,000 lifetime points and one per five current streak days; a separate one-time release bonus grants 10 hints. Future point and streak milestones are persisted and must not be credited twice. Each game, including streak games, can use at most three wallet hints; `GameState.hintsUsed` persists usage for unfinished games.
- **Rewards** shows lifetime points, the Hint Wallet, the 2X Points Wallet, and the weighted Prize Wheel. Point prizes go through `TotalScoreManager`; the `+1 Hint` and `2X Booster` prizes are idempotently credited to their respective wallets. Preserve the cooldown configured in `AppPreferences`. The Home difficulty sheet can attach one available booster to a new game; `GameState` persists this selection for resume. A completed boosted game consumes one booster and doubles credited points.
- **Free Play** removes mistakes and points in regular games. It is ignored in streak mode, is mutually exclusive with Hide Used Numbers, and cannot be changed while a saved game exists.
- **Hide Used Numbers** hides a digit once all nine instances are placed.
- **Streaks** use a separate DataStore (`StreakStateManager`) and a separate Room entity (`StreakGameEntity`). Never mix streak results with regular game statistics.
- **Streak reminders** fire daily at 10 p.m. local time, only if today's streak is not completed. Android 13+ requires the notification permission.

## Fragile areas (read before changing)

- **`GameState` serialization** is a hand-built line format persisted across launches. Preserve compatibility with existing saves or explicitly handle old payloads. `deserialize` returns `null` for malformed data.
- **Hint rewards** span separate point, streak, game, and preferences stores. Keep migration and milestone grants atomic within `AppPreferences`, and preserve idempotency during startup reconciliation, wheel payouts, and hint consumption.
- **`SudokuGenerator`** removes cells from a valid solution but does not verify the puzzle has a unique solution. Account for this when changing generation or difficulty tuning.
- **`getCurrentConsecutiveWins()`** counts all streak wins, not consecutive ones, despite its name. Fix the query and define the intended behavior before relying on it.
- **Streak state** (count, last completed date, active date, reminder markers) must stay consistent.
- **Room schema changes** need a migration. Statistics UI depends on DAO query semantics.
- **`MainActivity.kt`** hosts routes, permissions, theme, and startup wiring. Keep route arguments and navigation callbacks in sync with their screens.

## Firebase and analytics

- Use `AnalyticsUtils` and `AnalyticsConstants` for events. Do not call Firebase Analytics directly from screens.
- Settings changes log `settings_option_changed` with a `settings_state` string (setting name plus `on`/`off`). When one preference disables its mutually exclusive partner, log that change too.
- Theme changes use the separate `theme_toggled` event.
- Analytics and Crashlytics are disabled in debug builds.
- Do not add cloud sync, accounts, or network APIs unless explicitly requested.

## Security and repository hygiene

- Never commit `google-services.json`, signing keys, credentials, or `local.properties`.
- Do not commit generated output under `app/build/`.
- The active CI workflow is `.github/workflows/android-build.yml` (triggered by pushes to `Release/release_*`; builds a signed AAB, creates a GitHub Release, publishes to Play alpha, and needs the `PLAY_STORE_JSON_KEY` secret). Read it before changing release behavior.
- `app/release/android-build-copy.yml` is not an active workflow.

## Testing

- Use JUnit and AndroidX test libraries in the matching `app/src/test` or `app/src/androidTest` source sets.
- Prefer focused tests for game rules (`SudokuValidator`, `SudokuRules`), state serialization, and repository aggregation.
