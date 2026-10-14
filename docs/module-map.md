# Module map

The Gradle project currently includes only `:app`. The table below maps its
top-level Kotlin source packages; it is not a claim that each package is an
independent Gradle module.

| Module | Responsibility | Key files/classes | Depends on | Used by | Change impact |
|---|---|---|---|---|---|
| `:app` | Android application, resources, build variants | `app/build.gradle.kts`, `AndroidManifest.xml`, `SudokuApp.kt` | Android Gradle Plugin, Kotlin, version catalog | Installed application and CI | Build config changes affect all app code and release builds |
| `presentation/ui` | Activity, Compose screens, theme | `MainActivity.kt`, `ui/screen/*`, `ui/theme/*` | Compose, navigation, ViewModels, domain models | Android entry and navigation | Screen/navigation changes affect user journeys and route arguments |
| `presentation/navigation` | Entry wrapper and bottom-tab definitions | `AppNav.kt`, `BottomScreen.kt` | Compose screens | `MainActivity.kt` | Keep route names and callbacks in sync |
| `presentation/viewmodel` | Persistent/business UI state and actions | `HomeViewModel`, `SudokuGameViewModel`, `SettingsViewModel`, `RewardsViewModel`, `StreakViewModel`, `StatisticsViewModel` | Data managers/repositories, coroutines/Flow | Corresponding screens | Changes may affect lifecycle, persistence, reward or gameplay behavior |
| `component` | Reusable Compose UI | `SudokuGridCanvas.kt`, `NumberPad.kt`, `PrizeWheel.kt` and other components | Compose and presentation models | Screen composables | Reusable visuals can affect multiple screens |
| `domain` | Game rules, models, generators, contracts | `SudokuGenerator.kt`, `SudokuValidator.kt`, `Difficulty.kt`, repository interfaces | Kotlin; limited Compose snapshot-state use in `UndoManager` | ViewModels and data implementations | Rule/model changes can affect gameplay and stored state compatibility |
| `data` | Persistence adapters, repositories, score and completion | `GameStateManager.kt`, `AppPreferences.kt`, `SudokuDatabase.kt`, repositories | DataStore, Room, domain contracts | ViewModels and UI entry wiring | Storage changes affect resume, rewards, and statistics |
| `utils` | Platform integrations and shared game helpers | `AnalyticsUtils.kt`, `RemoteConfigManager.kt`, reminder classes, `SudokuRules.kt` | Firebase, WorkManager, Android APIs | Activity, screens, ViewModels, worker | Cross-cutting changes affect analytics/configuration/platform behavior |
| `app/src/main/res` | Strings, themes, fonts, media, icons, backup rules | `values/strings.xml`, `xml/`, `raw/` | Android resource system | Application UI/platform configuration | Resource-name or permission-linked changes affect presentation/platform behavior |
| `app/src/test`, `app/src/androidTest` | Local and device tests | `HintWalletTest`, `StreakShieldTest`, example tests | JUnit, AndroidX test, Compose UI test libraries | Gradle test tasks | Tests should follow current source-set and package patterns |

## Boundaries and dependency direction

- Compose screen files contain their entry-point screen composables. Supporting
  reusable composables belong in separate Kotlin files under `component/`.
- Screens own presentation and navigation; ViewModels own screen-scoped
  persistent/business state. Pass values and callbacks to components.
- Domain rules/models/contracts do not depend on app screens. Data
  implementations connect persistence to repository contracts where defined.
- Do not introduce a new module, DI framework, or repository layer without a
  project requirement.

## Shared locations

| Concern | Location |
|---|---|
| Models and repository contracts | `app/src/main/java/com/quickmindgames/sudoku/domain/` |
| Room entities and DAOs | `data/database/entity/`, `data/database/dao/` |
| Local preferences and state | `data/preferences/`, `data/state/`, `data/score/` |
| UI strings and resources | `app/src/main/res/` |
| Dependency/plugin versions | `gradle/libs.versions.toml` |
| App/module/build configuration | Root and `app` Gradle Kotlin DSL files |

## Common change scenarios

| Task | Modules likely affected | Files or patterns to inspect |
|---|---|---|
| Add or change a screen/route | `presentation/ui`, `presentation/navigation`, possibly `presentation/viewmodel` | `MainActivity.kt`, screen entry composable, matching ViewModel and callbacks |
| Change game rules or difficulty | `domain`, `utils`, `presentation/viewmodel` | Generator, validator, rules, debug/Remote Config clue settings |
| Change saved game or streak state | `data/state`, `data/preferences`, `presentation/viewmodel` | State serialization, manager, resume/completion paths |
| Change statistics | `data/database`, `data/repository`, statistics presentation | Entity, DAO query semantics, repository mapping, tests |
| Change reward/settings behavior | `data/preferences`, matching ViewModel and screen | Wallet persistence, normalization, user feedback and analytics |
| Add Android integration/dependency | `app`, `utils`, manifest/resources as applicable | Version catalog, app Gradle file, manifest and build variants |
