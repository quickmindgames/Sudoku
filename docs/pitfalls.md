# Pitfalls and change risks

## Critical pitfalls

| Pitfall | Where it applies | Why it matters | How to avoid it |
|---|---|---|---|
| Saved game format is hand-built | `GameState`, `GameStateManager` | Existing saves survive launches and may predate optional fields | Preserve compatibility; test old and malformed payload behavior |
| Streak and regular persistence are separate | `StreakStateManager`, game manager, Room entities | Mixing results corrupts separate progress/statistics | Trace regular and streak paths independently |
| Wallet rewards span multiple stores and events | `AppPreferences`, score/streak managers, reward/game flows | Startup migration, milestone and wheel grants must not double-credit | Preserve idempotency and test repeated grants/consumption |
| Streak date transitions are sensitive | `StreakState`, `StreakStateManager`, Home and reminder code | Recovery, shields, resets, and active days affect streak continuity | Verify date boundaries and shield consumption paths |
| Navigation is centralized in the activity | `MainActivity.kt`, screen callbacks | Route arguments can diverge from screen setup | Update route declaration, navigation call sites, and callbacks together |
| Activity recreation/startup effects | `MainActivity.kt` | Permission requests, startup fetches, and content setup run from `onCreate` | Check behavior on recreation; keep durable work uniquely scheduled as the reminder already is |
| Streak reminders need app opt-in and Android notification access | Manifest, `AppPreferences`, Settings/post-win prompt, scheduler and worker | Android 13+ permission or system notification settings can prevent delivery even when reminders are opted in; repeated denial can stop Android showing its permission dialog | Request permission only from a user action; explain rationale when appropriate and direct users to app notification settings when Android will no longer show the dialog. Have the worker check opt-in and platform access, and cancel unique work when the app toggle is disabled |
| Context lifetime in state holders | ViewModels constructed with `Context` | Retaining an Activity context beyond its lifecycle can leak it | Prefer application context for long-lived managers; `SudokuGameViewModel` already does so |
| Room query names may misstate semantics | `StreakGameDao.getCurrentConsecutiveWins()` | Current SQL counts all wins, not an ordered consecutive sequence | Read SQL before relying on method names |
| Puzzles are not checked for unique solutions | `SudokuGenerator` | Difficulty/generation changes can yield multiple valid completions | Do not assume unique puzzle solutions without adding/checking that behavior |
| Remote Config refresh failures are hidden | `RemoteConfigManager.fetchAndActivate()` | The fetch exception is caught without exposing refresh failure to the UI | Preserve configured defaults and consider observable failure only if behavior is intentionally changed |
| No app API error contract exists | Firebase configuration and local repositories | Do not apply assumed HTTP auth/retry/error conventions to local code | Follow each current SDK/repository contract; no backend request/response convention is defined |
| Release uses shrinking/minification | `app/build.gradle.kts`, ProGuard rules | Release-only failures may not occur in debug | Validate release bundle after dependency/reflection/resource changes |
| Room schema changes | `data/database/`, `app/schemas/` | Changing an entity without bumping `SudokuDatabase.version` breaks every existing install; with `version = 1`, Room also silently rewrites `1.json` on build | Bump the version, add an AutoMigration/Migration, commit the new `<version>.json`, extend `SudokuDatabaseMigrationTest`. A diff in an existing `<n>.json` means a schema change without a bump. Never use `fallbackToDestructiveMigration()` (wipes stats). `room-testing`'s `MigrationTestHelper` currently can't be used: it needs kotlinx-serialization ≥ 1.8.1, the app ships 1.7.3, so the test builds old versions from the schema JSON itself |
| Blanket R8 keep rules | `app/proguard-rules.pro` | `-keep class androidx.compose.** { *; }` (or Room/WorkManager equivalents) keeps every unused class and icon; it tripled the download size (9.4 MB vs 3.4 MB) | Rely on library consumer rules; add only the specific lines from `build/outputs/mapping/release/missing_rules.txt` |
| Debug clue behavior differs | `RemoteConfigManager` | Debug uses fixed 72 clues rather than normal ranges | Keep debug and non-debug behavior distinct in changes/tests |
| Async state crosses lifecycle-aware layers | Compose screens and ViewModels | Incorrect collection/scope use can stale state or outlive UI | Follow existing lifecycle/ViewModel Flow patterns |
| Security configuration is platform/build-specific | Manifest, Firebase integration, release build config | Permission/exported-component and client-side configuration changes can expose unintended access or configuration | Review manifest and release behavior; never place secrets in client code or docs |

## Change-risk areas

| Area | Risk | Files/modules to inspect | Recommended checks |
|---|---|---|---|
| Serialization | Breaking saved games | `GameState`, `StreakState`, managers | Round-trip, legacy, malformed-save tests |
| Wallet/rewards | Duplicate or lost credits | `AppPreferences`, rewards/game ViewModels | Idempotency, migration and underflow tests |
| Room/statistics | Changed reporting semantics or schema | Entities, DAOs, repository | Verify queries, add migration if schema changes, test aggregation |
| Routes/screens | Invalid route arguments or navigation | `MainActivity`, screens, navigation definitions | Build and exercise affected route |
| Background notifications | Missing/duplicate work or permission denial | Scheduler, worker, manifest | Check unique scheduling, date check and permission behavior |
| Release configuration | Debug/release mismatch | App Gradle config, ProGuard, CI workflow | Build relevant release variant |
| Settings | Conflicting persisted options | `AppPreferences`, Settings ViewModel/screen | Test normalization and saved-game guard |

## Review checklist

- [ ] Preserve existing serialized game and streak saves.
- [ ] Keep streak progress/results separate from regular game data/statistics.
- [ ] Check idempotency for wallet grants and consumption.
- [ ] Keep screen callbacks and navigation arguments in sync.
- [ ] Respect ViewModel and Compose lifecycle when collecting state.
- [ ] Check Activity recreation, context lifetime, and any listener/subscription cleanup.
- [ ] Preserve notification permission checks and completion-date behavior.
- [ ] Read Room query semantics and add migrations for schema changes.
- [ ] Verify debug and release behavior when configuration/build code changes.
- [ ] Add focused tests for changed rules, persistence, and repository behavior.
- [ ] Note that current automated coverage is focused on wallets/shields plus template/example tests; add tests for uncovered changed behavior.
- [ ] Keep user-facing copy in resources and analytics through project helpers.
