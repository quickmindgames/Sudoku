# Sudoku product guide

This file preserves product-specific rules and navigation notes that complement the
curated engineering references in this directory. See [Onboarding](onboarding.md)
for setup and [Domain model](domain-model.md) for state and entity details.

## Product

- Offline-first Sudoku app with Breeze, Pulse, Rage, and Elite difficulties.
- Features include guided lessons, daily streak games, statistics, settings, a
  prize wheel, and persistent points, Hint, 2X Points Booster, and Streak Shield
  wallets.
- The app has no accounts, cloud saves, or backend API. Firebase is used for
  Analytics, Crashlytics, and Remote Config.

## Gameplay and rewards

- Regular and streak game state are saved locally. A game can use at most three
  wallet hints; `GameState.hintsUsed` is persisted for unfinished games.
- Debug builds use 72 clues; non-debug clue ranges come from Remote Config, with
  defaults Breeze 46–51, Pulse 32–37, Rage 26–31, Elite 22–26.
- The Hint Wallet is independent of difficulty and Remote Config. Wallet
  migration and milestone crediting are persisted so rewards are not granted
  twice.
- A selected 2X Points Booster is part of the saved game; a completed boosted
  game consumes one booster and doubles points credited.
- Free Play removes mistakes and points in regular games and is ignored in streak
  mode. Free Play and Hide Used Numbers are independent settings (toggling one has
  no effect on the other). Neither gameplay option can be changed while a saved
  game exists — the Settings toggle is blocked with a "finish your game" message.
  Free Play games are not recorded in `game_statistics`, so they never count
  toward difficulty unlocks or win-rate, average-time and best-time statistics.
- Hide Used Numbers hides a digit after all nine instances are placed.
- Streak progress and streak game results are stored separately from regular
  game state and statistics. A Streak Shield can protect a missed day without
  increasing the streak count.
- Home and Streak status compare against `CurrentDateProvider.today`, which
  refreshes on screen resume and at local midnight. Never cache
  `LocalDate.now()` in a ViewModel or `remember`.
- The reminder is scheduled for 10 p.m. local time and skips a day already
  completed. Android 13+ requires notification permission to display it.
- UI copy uses “Points”; score-oriented code, persistence, and analytics names
  are retained and are not a data migration.

## Change cautions

- `GameState` uses a hand-built line format for saved games. Preserve old-save
  compatibility or explicitly handle prior payloads; malformed data returns
  `null`.
- `SudokuGenerator` removes cells from a valid completed grid but does not
  verify puzzle uniqueness.
- Room holds separate regular and streak result tables. Check DAO query
  semantics before changing statistics. `getCurrentConsecutiveWins()` counts
  all streak wins, despite its name.
- `MainActivity.kt` coordinates the Compose route host, permission request,
  theme observation, and startup work; keep route arguments and screen callbacks
  aligned.

## Engineering references

- [Architecture](architecture.md) · [Module map](module-map.md) ·
  [Data flow](data-flow.md) · [API contracts](api-contracts.md)
- [Domain model](domain-model.md) · [Decisions](decisions.md) ·
  [Onboarding](onboarding.md) · [Pitfalls](pitfalls.md)
- [Glossary](glossary.md) · [Conventions](conventions.md)
