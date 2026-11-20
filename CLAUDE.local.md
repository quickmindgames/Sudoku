# Sudoku (Android) — Claude Code project instructions

<!--
Converted from .github/copilot-instructions.md for Claude Code.
Keep this file under ~200 lines: it is loaded into every session.
Block-level HTML comments like this one are stripped before loading and cost no tokens.
Path-specific rules live in .claude/rules/ and load only when matching files are read.
-->

Sudoku is an offline-first Android Sudoku game with guided lessons, daily streak challenges, rewards, settings, and local statistics. Single Gradle module `:app`, Kotlin, Jetpack Compose + Material 3, screen-scoped ViewModels, domain rules/models, and local data implementations. App package: `com.quickmindgames.sudoku`.

## Working efficiently (keep token use low)

- Use the docs index below to find where code lives **before** searching the codebase. Open only the one or two docs the task needs, never the whole `docs/` folder.
- Read only the files the task touches. Search for a specific symbol or file name instead of reading whole directories.
- Don't re-read a file you already read in this session unless it has changed.
- Make targeted edits. Never rewrite a whole file to change a few lines.
- Don't open `knowledge/repo-map.md` during normal work (see "Repo-map maintenance").
- Keep replies short: what changed and which files. Don't echo full files or long code back.
- If the request is ambiguous, ask one short question before exploring.

## Docs index (read only when relevant)

Consult the matching doc before making non-trivial changes:

| Task | Read first |
|---|---|
| Architecture, modules, navigation, dependencies | `docs/architecture.md` |
| Locating code, refactoring, module boundaries | `docs/module-map.md` |
| User journeys, navigation, data flow, integrations | `docs/data-flow.md` |
| APIs, request/response models, auth, errors | `docs/api-contracts.md` |
| Entities, relationships, business rules, state changes | `docs/domain-model.md` |
| Architecture and technology decisions | `docs/decisions.md` |
| Setup, Gradle tasks, run/test commands, common changes | `docs/onboarding.md` |
| Android-specific risks, lifecycle issues, review checklist | `docs/pitfalls.md` |
| Project-specific terminology and naming | `docs/glossary.md` |
| Naming, code organization, testing, workflow rules | `docs/conventions.md` |
| Product rules and fragile game behavior | `docs/SUDOKU_KNOWLEDGE.md` |

## Android architecture

- Compose screens, an Activity-hosted Navigation Compose `NavHost`, screen-scoped ViewModels, repository contracts where present, Preferences DataStore, Room, Firebase integrations, and a WorkManager streak reminder.
- `:app` is the only Gradle module. Packages: presentation, reusable components, domain, data, utilities. Preserve those boundaries and the existing dependency direction.
- Local game/streak/settings/wallet state is distinct from Room-completed statistics. Firebase is used for Analytics, Crashlytics, and Remote Config only. There is no app backend API or cloud-save layer.

## Android coding rules

- Follow the existing Compose UI and screen/ViewModel split. Keep each screen entry point in its screen file and reusable composables in separate files under `component/`.
- Respect Android and ViewModel lifecycles; use the existing coroutine/Flow collection and persistence patterns.
- Follow the established domain/data/repository boundaries and the current singleton/constructor/`remember` wiring. Don't add a DI framework or new architecture without an explicit need.
- Don't add a dependency when an existing library or project helper already covers the requirement.

## Data rules

- Reuse existing domain models, repository interfaces/implementations, DataStore managers, Room DAOs, and Firebase helpers.
- Repository contracts live in `domain/repository`, implementations in `data/repository` (`GameRepository`/`GameRepositoryImpl`, `ThemeRepository`/`ThemeRepositoryImpl`). `StatisticsRepository` is a concrete class in `data/repository` with no domain interface; don't add one unless explicitly requested.
- There is no application REST/GraphQL API. Don't add network APIs, accounts, or cloud sync unless explicitly requested.
- Keep regular-game and streak state/statistics separate. Preserve idempotent wallet credit/consumption behavior.
- `GameState` (`data/state/GameState.kt`) is persisted by `GameStateManager` to Preferences DataStore via `GameState.serialize()`/`GameState.deserialize()`. Keep that format backward compatible so saved games still load; give new fields defaults.
- Route analytics through `AnalyticsUtils` and `AnalyticsConstants`; never log sensitive data.

## Android safety rules

- Current manifest permissions: `POST_NOTIFICATIONS` (streak reminders; runtime permission on Android 13+), `com.google.android.gms.permission.AD_ID`, `VIBRATE`.
- Add manifest permissions only when a feature requires them, and handle runtime permission behavior where Android requires it.
- Never add secrets, credentials, Firebase configuration, signing values, or local environment values to docs or tracked source.
- Consider manifest/exported-component, deep-link, file-access, and release-shrinking behavior when the relevant surface changes; follow the existing project configuration.

## Testing rules

- Add or update focused tests for changed game rules, state serialization, repositories, ViewModels, persistence, and critical UI behavior.
- JVM unit tests: `app/src/test` (JUnit). Instrumentation tests: `app/src/androidTest` (AndroidX Test).
- Put new tests under `com.quickmindgames.sudoku`, mirroring the package of the code under test, named `<ClassUnderTest>Test`. The `ExampleUnitTest`/`ExampleInstrumentedTest` stubs under `com.gamestudio.sudo` are template leftovers; don't follow their package.
- Before proposing completion, run `.\gradlew.bat test` (Windows) or `./gradlew test`. Run `connectedAndroidTest` on a device/emulator when instrumentation tests change. Other commands: `docs/onboarding.md`.

## Documentation maintenance

For every non-trivial change, decide whether docs are affected. Update the relevant doc in the same change when it:
- Adds, removes, moves, or renames a Gradle module
- Adds or changes a screen, navigation route, ViewModel, repository, worker, service, or major component
- Adds or changes an API endpoint, DTO, database entity, migration, or integration
- Changes architecture, major dependencies, build variants, permissions, or security behavior
- Changes build, test, lint, migration, deployment, or developer workflow

Update only the directly relevant doc. Don't rewrite unrelated documentation.

## Repo-map maintenance

`knowledge/repo-map.md` is a generated Repomix snapshot. Never `@import` it or read it during normal tasks. Regenerate it only after a Gradle module is added/removed, a significant package restructure, a major dependency/framework/build-tool upgrade, a large architecture or data-flow refactor, or when curated docs are materially out of sync with the code. After regenerating, use it only to audit and correct `docs/*.md`.

## Safety and accuracy

- Current source code is the source of truth when it conflicts with documentation.
- Don't invent files, APIs, commands, dependencies, permissions, or business rules.
- Ask for clarification when required Android or project context is missing.
- Never expose secrets, credentials, tokens, private keys, signing passwords, or environment values.