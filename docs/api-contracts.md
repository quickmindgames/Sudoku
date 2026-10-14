# API and service contracts

No app REST, GraphQL, gRPC, or WebSocket client is present in the configured
dependencies or source package layout. Firebase Remote Config is the configured
remote service. The app also has internal Kotlin repository interfaces and a
WorkManager worker contract; these are the contracts documented below.

| Method/Operation | Path/Topic | Purpose | Auth | Request shape | Response shape | Main errors | Handler/repository/service |
|---|---|---|---|---|---|---|---|
| `saveGame`, `loadGame`, `deleteGame` | Internal `GameRepository` | Save, load, or clear active game | Not applicable | `GameData` for save; no arguments for load/delete | `Unit`; nullable `GameData`; `Unit` | Load can return `null`; serialization rejects malformed state | `GameRepositoryImpl`, `GameStateManager` |
| `isDarkMode`, `setDarkMode` | Internal `ThemeRepository` | Observe/update theme preference | Not applicable | Boolean for setter | `Flow<Boolean>`; setter completes without a value | No shared error contract defined | `ThemeRepositoryImpl`, theme preferences |
| `fetchAndActivate` | Firebase Remote Config SDK | Refresh configured clue-count values | Firebase SDK configuration; user auth not present | SDK operation; detailed wire request not exposed here | SDK task result is awaited; app method returns `Unit` | Exceptions are caught without surfacing a result | `RemoteConfigManager` |
| `get*MinClues`, `get*MaxClues` | Firebase Remote Config values | Read per-difficulty clue bounds | Not applicable | Difficulty-specific accessor | Integer value; debug build returns fixed debug value | Missing/invalid remote values are not separately reported by accessors | `RemoteConfigManager` |
| `doWork` | WorkManager worker | Check streak completion and post reminder | Android notification permission where required | WorkManager parameters; input data not used by the implementation | `Result.success()` after normal execution | Permission absence skips notification; exceptions are not mapped to an app error type | `StreakReminderWorker` |

## Conventions and unavailable contracts

- No endpoint paths, authentication scheme, API error envelope, pagination,
  filtering, or API versioning convention exists for an app backend.
- Shared request validation and API error-response conventions are
  `Not determinable from provided context.`
- Firebase Remote Config SDK wire request/response fields are
  `Not determinable from provided context.`
- Internal repository methods are Kotlin interfaces, not HTTP endpoints. Their
  call sites and local persistence implementations are the source of truth.
- Request/response fields not shown by the internal method signatures are
  `Not determinable from provided context.`
