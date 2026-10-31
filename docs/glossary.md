# Glossary

## Business and domain terms

| Term | Meaning in this project | Related code/module | Notes |
|---|---|---|---|
| Breeze / Pulse / Rage / Elite | The four Sudoku difficulty tiers | `domain/model/Difficulty.kt` | Clue ranges are provided by `RemoteConfigManager` outside debug |
| Points | User-facing name for game score | UI, `TotalScoreManager`, Room score fields | Code/persistence retain “score” naming |
| Hint Wallet | Persistent balance of hints usable in games | `AppPreferences`, `SudokuGameViewModel` | Maximum three wallet hints per game |
| 2X Points Booster | Reward that doubles points for a completed selected game | `AppPreferences`, `GameState` | Selection persists while an unfinished game is saved |
| Streak Shield | Reward used to protect a missed streak day | `AppPreferences`, `StreakState` | Protection does not increase the streak count |
| Streak game | Daily challenge tracked separately from regular play | `StreakState`, `StreakGameEntity` | Has its own DataStore state and Room statistics |
| Free Play | Regular-game setting that removes mistakes and points | `AppPreferences`, settings/game flows | Ignored in streak mode; cannot change while a saved game exists |
| Hide Used Numbers | Setting that hides a digit after all nine copies are placed | `AppPreferences`, game UI | Mutually exclusive with Free Play |
| Flaw-free win | Regular-game win with no mistakes | `GameStatisticEntity.isFlawFree` | Derived when result is recorded |

## Technical terms

| Term | Meaning in this project | Where used | Notes |
|---|---|---|---|
| Active game | Unfinished puzzle state that can be resumed | `GameState`, `GameStateManager` | Serialized in Preferences DataStore |
| `GameData` | Domain-layer alias for `GameState` | `domain/model/GameData.kt` | Not a distinct data structure |
| `MainSudokuApp` | Compose app shell containing navigation and startup state initialization | `presentation/ui/MainActivity.kt` | Called through `AppNav` |
| `game_statistics` | Room table for completed regular-game outcomes | `GameStatisticEntity`, `GameStatisticDao` | Separate from streak results |
| `streak_games` | Room table for completed streak outcomes | `StreakGameEntity`, `StreakGameDao` | Separate from regular results |
| Remote Config | Firebase configuration service used for clue ranges | `RemoteConfigManager` | Debug builds return fixed 72 clue bounds |
| `getCurrentConsecutiveWins()` | DAO method name for a query that counts all streak wins | `StreakGameDao` | It does not calculate consecutive wins |
