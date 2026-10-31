# Conventions

## Naming and organization

| Item | Observed convention |
|---|---|
| Packages | Lowercase package hierarchy under `com.quickmindgames.sudoku`; grouped by `presentation`, `component`, `domain`, `data`, and `utils` |
| Classes/types | PascalCase, e.g. `SudokuGameViewModel`, `GameState` |
| Functions/properties | lowerCamelCase, e.g. `saveGame`, `hintWallet` |
| Constants | Upper snake case for compile-time constants, e.g. `WHEEL_SPIN_COOLDOWN_MILLIS` |
| Screens/components | Screen entry composable is in `presentation/ui/screen`; each supporting reusable composable has its own Kotlin file under `component/` |
| Resources | Android resources under `app/src/main/res`; user-facing strings belong in `values/strings.xml` |
| Tests | Matching `app/src/test` and `app/src/androidTest` source sets; feature tests use descriptive `*Test` class names and behavior-oriented test methods |
| Database objects | Room entities specify table names and columns; DAO SQL names the corresponding table/columns |

## Code organization and behavior

- Keep presentation-only state and navigation orchestration in screens; keep
  persistent/business screen state in its ViewModel. Pass data and callbacks to
  components rather than defining helper composables in screen files.
- Follow existing singleton accessors, constructors, repositories, and Compose
  `remember` patterns. There is no DI framework.
- Use `AppPreferences` for gameplay settings and wallet rules; preserve its
  DataStore transaction/idempotency behavior.
- Use repository contracts/adapters where they already exist. Do not invent a
  new repository or use-case layer for a one-off change.
- Use coroutines and Flow in established ViewModel/data patterns. Tie UI
  collection to lifecycle-aware existing patterns.
- Route analytics through `AnalyticsUtils` and event names through
  `AnalyticsConstants`; do not log Firebase Analytics directly from screens.
- Handle game save format changes compatibly. Keep user-facing text in string
  resources and avoid logging sensitive data.
- Add dependency versions in `gradle/libs.versions.toml` and aliases/use in
  Gradle scripts.

## Testing

- Unit tests are in `app/src/test`; instrumentation tests are in
  `app/src/androidTest`.
- JUnit 4 is used for unit tests. AndroidX JUnit, Espresso, and Compose UI test
  dependencies are configured for device/UI tests.
- Prefer focused behavior tests for Sudoku rules, state serialization,
  persistence/reward idempotency, and statistics aggregation.
- Run `.\gradlew.bat test` for unit tests. A required mocking library,
  fixture convention, lint task, or formatter is
  `Not determinable from provided context.`

## Workflow and documentation

- The active release workflow is `.github/workflows/android-build.yml`.
- Do not commit local environment files, Firebase configuration, signing
  credentials, or generated build output.
- Update directly relevant docs when changing architecture, routes, persistence,
  external integrations, build/test workflows, or reusable project conventions.
