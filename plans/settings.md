# Gameplay and Feedback Settings

## User story

As a Sudoku player, I want to customize number-pad behavior, challenge tracking, and gameplay feedback so I can play in the way that suits me.

## Gameplay options

### Hide used numbers

**Type:** Toggle (off by default)

- When enabled, a number is hidden from the number pad after all nine instances have been placed in the grid.
- When disabled, completed numbers remain visible on the number pad.
- The setting is persisted across app launches and applies to the current game and subsequent games.

### Free Play

**Type:** Toggle (off by default)

- When enabled, score and mistake tracking are disabled, and their gameplay and completion-summary views are hidden.
- The timer continues running and is shown in the top bar in place of the score card, alongside the pause/resume control.
- Free Play does not award a score or treat incorrect entries as challenge mistakes.
- When disabled, standard challenge gameplay shows the score, mistakes, and timer.
- The setting is persisted across app launches.

### Mutually exclusive options and active games

- Free Play and Hide used numbers cannot be enabled at the same time.
- Enabling either option automatically disables the other and displays a brief message explaining the change.
- If both are enabled in stored preferences, Free Play is retained and Hide used numbers is disabled.
- While a resumable game is saved, gameplay options cannot be changed. The settings screen explains that the player must finish or start a new game first; attempting to change an option displays a brief message.

## Sound & Vibration

**Type:** Toggle (on by default)

- When enabled, correct entries play a success sound, incorrect entries play an error sound and vibration, and puzzle completion plays a completion sound.
- When disabled, gameplay sounds and vibration are suppressed.
- The setting is persisted across app launches.

## Acceptance criteria

- All three settings are available on the Settings page.
- Setting values persist across app launches and are applied during gameplay.
- Free Play and Hide used numbers are mutually exclusive, with a brief explanation when one disables the other.
- A saved resumable game prevents changes to gameplay options until the game is finished or a new game is started.
- Free Play hides score and mistake tracking while keeping the timer visible and running; standard challenge mode shows all three.
- Hide used numbers hides completed numbers from the number pad, and the number pad reflects the current grid when resuming a game or starting a new one.
- Sound & Vibration honors its toggle for correct entries, incorrect entries, and puzzle completion.
