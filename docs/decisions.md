# Architecture and technology decisions

Status describes the evidence in this repository, not a claim that a formal
decision record exists.

| Decision | Context | Rationale | Trade-offs | Evidence in repo |
|---|---|---|---|---|
| Kotlin and JVM 17 — Inferred | Android application language/runtime | Matches current source and configured compiler targets | Kotlin/JVM toolchain and Android plugin versions must remain compatible | Kotlin source; root/app Gradle files |
| Jetpack Compose and Material 3 — Documented | App UI | Compose screens/components and Material theme are used throughout | Compose state/lifecycle concerns apply; no XML screen UI identified | `presentation/ui`, `component`, Compose dependencies |
| Screen ViewModels with domain/data packages — Inferred | Feature state and persistence | ViewModels and repository contracts organize responsibilities without a larger framework | Some construction and platform wiring stays in screens/activity | `presentation/viewmodel`, `domain`, `data` |
| Preferences DataStore plus Room — Documented | Local state vs completed results | DataStore keeps app/game preferences; Room persists statistics | Separate stores require coordinated behavior; Room changes require schema consideration | DataStore managers, `SudokuDatabase`, entities and DAOs |
| No DI framework — Documented | Object construction | Existing code uses singleton accessors, constructors, and Compose `remember` | Manual wiring/context use remain present | No DI plugin/dependency; app code patterns |
| No app REST client or backend — Documented | Game data and remote settings | Game state/statistics are local; Firebase SDKs are used for analytics, crash reporting and Remote Config | No cloud sync or application API contract | Dependencies and source packages; project guide |
| Navigation Compose — Inferred | Screen routing | `MainActivity` creates a Compose `NavHost` and routes | Route strings/arguments and callbacks must stay coordinated | `MainActivity.kt`, Navigation Compose dependency |
| JUnit 4 and AndroidX test libraries — Documented | Automated tests | Unit and instrumentation source sets use standard Android test dependencies | Current targeted feature coverage is limited | `app/src/test`, `app/src/androidTest`, Gradle dependencies |
| Gradle Kotlin DSL and version catalog — Documented | Build/dependencies | Shared plugin/library aliases are configured centrally | Dependency changes must update catalog and module build file | `settings.gradle.kts`, Gradle scripts, `libs.versions.toml` |
| Release minification, resource shrinking, CI AAB publication — Documented | Release packaging | Release build type and active workflow build/distribute an AAB | Release behavior depends on configuration outside local debug flow | `app/build.gradle.kts`, `.github/workflows/android-build.yml` |

Formal rationale beyond current repository evidence is
`Not determinable from provided context.`
