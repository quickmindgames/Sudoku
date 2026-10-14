# Onboarding

## Prerequisites

| Requirement | Evidence |
|---|---|
| JDK | 17, configured for Java and Kotlin compilation |
| Android SDK | Android Gradle configuration compiles against SDK 37; min SDK 28, target SDK 36 |
| Android Studio | Optional for editing/running; exact required version is `Not determinable from provided context.` |
| Emulator/device | Needed to run the Android app or instrumentation tests; exact device profile is `Not determinable from provided context.` |
| Firebase local configuration | Google Services plugin is applied. Local Firebase setup requirements depend on the local build configuration; do not commit Firebase configuration. |
| Accounts | No account is needed for offline gameplay. Accounts needed for release publication are `Not determinable from provided context.` |

## Build and test

Run from the repository root on Windows:

| Task | Command |
|---|---|
| Assemble debug APK | `.\gradlew.bat assembleDebug` |
| Run unit tests | `.\gradlew.bat test` |
| Build release bundle (also used by active CI) | `.\gradlew.bat bundleRelease` |
| Lint, format, database migration | `Not determinable from provided context.` |

The repository also provides `gradlew` for non-Windows environments. Open the
project in Android Studio or install to a device using the IDE; a specific
install/run command is `Not determinable from provided context.`

## Project tour

- [Architecture](architecture.md) describes components and boundaries.
- [Module map](module-map.md) maps packages and common change paths.
- [Data flow](data-flow.md) covers workflows and storage.
- [Product guide](SUDOKU_KNOWLEDGE.md) summarizes game rules and fragile areas.
- Start at `app/src/main/java/com/quickmindgames/sudoku/presentation/ui/MainActivity.kt`.
- App/module configuration is in `settings.gradle.kts` and `app/build.gradle.kts`;
  versions are in `gradle/libs.versions.toml`.

## Common changes

| Change | Start by inspecting |
|---|---|
| Screen or navigation | `MainActivity.kt`, `presentation/ui/screen/`, matching ViewModel |
| Gameplay rules | `domain/generator/`, `domain/util/`, `utils/SudokuRules.kt` |
| Saved game or streak | `data/state/`, state managers, matching ViewModel |
| Settings or wallet | `data/preferences/AppPreferences.kt`, related ViewModel/screen |
| Statistics | Room entity/DAO, `StatisticsRepository`, statistics UI/tests |
| Android integration | `AndroidManifest.xml`, `utils/`, resources and app Gradle config |

## Troubleshooting

- For release build or publication changes, inspect the active workflow at
  `.github/workflows/android-build.yml`; files outside `.github/workflows/` are
  not automatically active Actions workflows.
- Release minification and resource shrinking are enabled. Validate release
  behavior when changing code affected by R8 or resource shrinking.
- Firebase configuration failures depend on local configuration and are
  `Not determinable from provided context.`
