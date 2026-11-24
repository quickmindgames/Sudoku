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
  game consumes one booster and doubles points credited. Points are doubled only
  if a booster is actually consumed. `SudokuGameViewModel.persistWin` is the one
  place a win is recorded: it computes the award once (`finalScore`) and uses it
  for stats, lifetime points and the win dialog. The debug score multiplier
  applies only to the lifetime-points credit (`creditedPoints`).
- Free Play removes mistakes and points in regular games and is ignored in streak
  mode. Free Play and Hide Used Numbers are independent settings (toggling one has
  no effect on the other). Neither gameplay option can be changed while a saved
  game exists — the Settings toggle is blocked with a "finish your game" message.
  Free Play games are not recorded in `game_statistics`, so they never count
  toward difficulty unlocks or win-rate, average-time and best-time statistics.
- Hide Used Numbers hides a digit after all nine instances are placed.
- Difficulty unlocks: Breeze is always open; Pulse, Rage and Elite each need 5
  wins at the previous level (`domain/util/DifficultyUnlocks`). `HomeViewModel`
  loads the win counts once (`StatisticsRepository.getDifficultyUnlocks()`, a
  live flow) into `HomeScreenState.difficultyUnlocks`; while it is `null` the
  difficulty cards show a neutral, non-tappable state instead of "locked".
- Puzzles are generated only when needed (new game, practice, new streak game,
  resume with no save, "Next Game"), in `SudokuGameViewModel.loadNewPuzzle()` on
  `Dispatchers.Default`, never in the ViewModel constructor (it runs on the main
  thread). Until a puzzle is ready the board is empty, `isLoading` is true, a
  spinner covers the board and input is ignored.
- The prize-wheel cooldown (4 hours; 24 s in debug) is computed by
  `domain/util/SpinCooldown`, not by wall-clock arithmetic. A spin stores the
  wall time, `SystemClock.elapsedRealtime()` and the boot count (`last_spin`,
  `last_spin_elapsed`, `last_spin_boot_count`). Within the same boot the boot
  clock is used, so changing the device time can't skip or extend the cooldown.
  After a reboot, or for spins saved by older versions, the wall clock is used,
  clamped to 0..cooldown. Known limit: rebooting and then moving the clock
  forward still skips the cooldown; full protection needs a server time source.
- Overall statistics cover every recorded game, regular and streak, by design.
  A finished streak game is written to `game_statistics` once (difficulty
  `"streak"`), which Overall reads, and also to `streak_games`, which feeds only
  the Streak stats. Breeze + Pulse + Rage + Elite + streak = Overall. Do not
  filter `"streak"` out of Overall or remove either write. Free Play and
  practice games are not recorded.
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
  `null`. Add new fields only as optional trailing `KEY:value` lines read by
  prefix with a default (see `HINTS_USED`, `DOUBLE_POINTS_BOOSTER`, `SCORED`);
  older builds ignore unknown lines. `GameStateTest` checks an old-format save.
- A cell earns points at most once per game (`scoredCells`), including hinted
  cells (no points) and cells cleared by Undo or Erase and entered again.
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
