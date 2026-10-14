# Android native project context

Sudoku is an offline-first Android Sudoku game with guided lessons, daily streak challenges, rewards, settings, and local statistics. It is a single-module Kotlin application using Jetpack Compose and Material 3, with screen ViewModels, domain rules/models, and local data implementations.

## Android architecture

- The app uses Compose screens, an Activity-hosted Navigation Compose `NavHost`, screen-scoped ViewModels, repository contracts where present, Preferences DataStore, Room, Firebase integrations, and a WorkManager streak reminder.
- `:app` is the only Gradle module. Source packages are organized into presentation, reusable components, domain, data, and utilities; preserve those boundaries and existing dependency direction.
- Local game/streak/settings/wallet state is distinct from Room-completed statistics. Firebase is used for Analytics, Crashlytics, and Remote Config; there is no app backend API or cloud-save layer.

## Essential documentation

Consult the relevant document before making non-trivial changes:

| Task | Read first |
|---|---|
| Architecture, modules, navigation, dependencies | [Architecture](../docs/architecture.md) |
| Locating code, refactoring, module boundaries | [Module map](../docs/module-map.md) |
| User journeys, navigation, data flow, integrations | [Data flow](../docs/data-flow.md) |
| APIs, request/response models, auth, errors | [API contracts](../docs/api-contracts.md) |
| Entities, relationships, business rules, state changes | [Domain model](../docs/domain-model.md) |
| Architecture and technology decisions | [Decisions](../docs/decisions.md) |
| Setup, Gradle tasks, run/test commands, common changes | [Onboarding](../docs/onboarding.md) |
| Android-specific risks, lifecycle issues, review checklist | [Pitfalls](../docs/pitfalls.md) |
| Project-specific terminology and naming | [Glossary](../docs/glossary.md) |
| Naming, code organization, testing, workflow rules | [Conventions](../docs/conventions.md) |
| Product rules and fragile game behavior | [Sudoku product guide](../docs/SUDOKU_KNOWLEDGE.md) |

## Android coding rules

- Follow the existing Compose UI and screen/ViewModel split. Keep each screen entry point in its screen file and reusable composables in separate files under `component/`.
- Respect Android and ViewModel lifecycles; use existing coroutine/Flow collection and persistence patterns.
- Follow established domain/data/repository boundaries and the current singleton/constructor/`remember` wiring; do not add a DI framework or new architecture without an explicit need.
- Do not add a dependency when an existing library or project helper already covers the requirement.

## Data and networking rules

- Reuse existing domain models, repository interfaces/implementations, DataStore managers, Room DAOs, and Firebase helpers.
- There is no application REST/GraphQL API. Do not add network APIs, accounts, or cloud sync unless explicitly requested.
- Keep regular-game and streak state/statistics separate. Preserve serialized `GameState` compatibility and idempotent wallet credit/consumption behavior.
- Route analytics through `AnalyticsUtils` and `AnalyticsConstants`; do not log sensitive data.

## Android safety rules

- Add manifest permissions only when required by the feature and handle runtime permission behavior where Android requires it.
- Never add secrets, credentials, Firebase configuration, signing values, or local environment values to documentation or tracked source.
- Consider manifest/exported-component, deep-link, file-access, and release shrinking behavior when the relevant surface changes; follow the existing project configuration.

## Testing rules

- Add or update focused tests for changed game rules, state serialization, repositories, ViewModels, persistence, and critical UI behavior.
- Follow the existing JUnit and AndroidX test source sets and naming patterns.
- Run the relevant project test task before proposing completion.

## Documentation maintenance

For every non-trivial change, determine whether documentation is affected.

Update the relevant docs in the same pull request when the change:
- Adds, removes, moves, or renames a Gradle module
- Adds or changes a screen, navigation route, ViewModel, repository, worker, service, or major component
- Adds or changes an API endpoint, DTO, database entity, migration, or integration
- Changes app architecture, major dependencies, build variants, permissions, or security behavior
- Changes build, test, lint, migration, deployment, or developer workflow

Update only the directly relevant documentation file. Do not rewrite unrelated documentation.

## Repo-map maintenance

`knowledge/repo-map.md` is a generated Repomix snapshot. Do not regenerate it for every small change.

Regenerate it only after:
- A new Gradle module is added or removed
- The package/module structure changes significantly
- A major dependency, framework, or build-tool upgrade occurs
- A large refactor changes architecture or data flow
- Curated docs become materially inconsistent with the codebase

After regeneration, use it only to audit and correct curated `docs/*.md` files. Do not add `knowledge/repo-map.md` to always-on Copilot context.

## Safety and accuracy

- Treat current source code as the source of truth when it conflicts with documentation.
- Do not invent files, APIs, commands, dependencies, permissions, or business rules.
- Ask for clarification when required Android or project context is missing.
- Never expose secrets, credentials, tokens, private keys, signing passwords, or environment values.
