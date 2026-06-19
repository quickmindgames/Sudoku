# Plan: Learning Mode Feature

A self-contained learning flow that guides new users through 5 bite-sized lessons then drops them into a forgiving Breeze puzzle — no stats recorded, no mistakes counted, wrong-number entries replaced with an inline explanation banner.

---

## Requirements

- Learning Mode is a separate mode, not a difficulty level.
- Add a "Learning Mode" button on the Home screen below the "New Game" button.
- Every time Learning Mode starts, show 5 very short lessons (always from Lesson 1, no progress tracking):
  1. Row Rule
     2. Column Rule
  3. 3×3 Box Rule
  4. Missing Number
  5. Cross Checking (row + column + box)
- After lessons, launch a Breeze-based Sudoku puzzle.
- Learning Puzzle:
  - **Enabled**: Notes, Undo, Erase
  - **Disabled**: Timer, Score/Coins, Mistakes, Hints
- When user enters a wrong number:
  - Do NOT count a mistake
  - Do NOT end the game
  - Show a short explanation:
    - "Number already exists in this row"
    - "Number already exists in this column"
    - "Number already exists in this 3×3 box"
- On puzzle completion, show a congratulation screen and encourage the user to try normal game modes.

---

## Implementation Steps

---

### Step 1 — Add `LessonData` model

**File**: `app/src/main/java/com/quickmindgames/sudoku/domain/model/LessonData.kt` *(new file)*

```kotlin
package com.quickmindgames.sudoku.domain.model

/**
 * Represents a single lesson in Learning Mode.
 */
data class LessonData(
    val title: String,
    val body: String,
    val illustrationEmoji: String
)

/**
 * All 5 lessons — always shown in order, starting from the first.
 * No progress tracking by design.

  # Learning Mode — Updated Plan

  This file documents the current, implemented Learning Mode flow and the exact behavior and files that were changed during implementation. It replaces the original pager-based plan with the single-scroll lessons UI and reflects the practice-mode behavior implemented in `SudokuScreen` (mode = "learn").

  Summary
  - Lessons are presented as a single scrollable list of card-like items with mini-grids and dividers.
  - The Home CTA is now "How to play?" and opens the Lessons screen (titled "Tutorial").
  - The lesson CTA launches a practice puzzle (mode="learn") that enables controls but disables timer, hints, mistakes and scoring; wrong-number inputs show inline learning hints.

  Quick checklist (implemented)
  - Lesson list: single LazyColumn with card-style lesson items and dividers.
  - Visual grids: `VisualLessonGrid` + `LessonGridCell` render row/column/3×3 examples and adapt sizes so 9 cells fit.
  - Column lesson: `LessonCardColumn` (left grid, right content). Other lessons use centered `LessonCardDefault`.
  - Navigation: `learn` route added; Lesson screen navigates to `SudokuScreen(mode="learn", difficulty=Breeze)`.
  - `SudokuScreen` behavior for practice mode: controls enabled, timer suppressed, hints disabled, mistakes not counted, learning hint banner shown, LearningComplete dialog on solve.

  Files added / changed (current)
  - component/VisualLessonGrid.kt — small responsive mini-grid renderer
  - component/LessonGridCell.kt — adaptive cell UI
  - component/LessonCardColumn.kt — column-lesson layout
  - component/LessonCardDefault.kt — centered lesson layout (polish)
  - component/LearningCompleteDialog.kt — dialog shown after finishing practice puzzle
  - presentation/ui/screen/LessonScreen.kt — single-scroll lessons UI (card wrappers, CTA)
  - presentation/ui/screen/HomeScreen.kt — Home CTA changed to "How to play?" and routed to lessons
  - presentation/navigation/AppNav.kt — `learn` route added
  - presentation/ui/MainActivity.kt / MainSudokuApp — `onOpenLearn` wiring
  - presentation/ui/screen/SudokuScreen.kt — learning-mode logic implemented and guarded

  Behavior details (what the code now does)
  - Lessons UI
    - Lessons are visible together; each lesson shows a mini-grid, title, step indicator and body.
    - Dividers separate lessons; each lesson is wrapped in a card-like box for better visual hierarchy.
  - Practice mode (mode == "learn")
    - Controls: NumberPad, Undo, Erase, Notes are enabled so users can interact and practice.
    - Timer: suppressed — the timer loop only increments when not in learning mode.
    - Hints: disabled (availableHints = 0); Hint button hidden for learning mode.
    - Mistakes: wrong-number entries do not increment mistakes and do not end the game.
    - Wrong-number handling: on incorrect input in learning mode the app computes whether the conflict is a row/column/box conflict and shows an inline `learningHint` banner (auto-dismiss ~2.5s) explaining why the number is wrong.
    - Scoring: scoring logic is skipped in learning mode.
    - Completion: on solving the grid the LearningCompleteDialog is shown with CTAs to try normal mode or go back home.

  Implementation notes & rationale
  - `VisualLessonGrid` avoids full-width rows so non-full-width grids (like 3×3) center naturally.
  - `LessonGridCell` adapts size and font by checking `numColumns`/`numRows` so 9-in-row fits on smaller screens.
  - `SudokuScreen` uses `isLearningMode = mode == "learn"` and wraps/guards UI pieces (score card, mistakes row, hint button) with `if (!isLearningMode)` where appropriate.
  - Timer LaunchedEffect was adjusted to increment only when `!isLearningMode` to keep the timer at 0 during practice while still allowing controls to be active.
  - The learning hint banner uses `AnimatedVisibility` and a `LaunchedEffect` for auto-dismiss to ensure the hint is visible without blocking input.

  String resources (recommended)
  Replace hard-coded labels with resources for localization. Minimum suggested keys:
  - `lbl_how_to_play` = "How to play?"
  - `lbl_tutorial` = "Tutorial"
  - `lbl_lets_practice` = "Let's Practice"
  - `lbl_practice_mode` = "Practice Mode"
  - `lbl_learning_mode` = "Learning Mode"

  Analytics recommendations
  - Add optional analytics events (use stable event keys):
    - `logLearningModeStarted`
    - `logLearningLessonViewed` (pass lesson index)
    - `logLearningGameCompleted`

  Refactor suggestions (small, optional)
  - Split responsibilities: introduce `controlsEnabled` and `timerRunning` flags instead of overloading `isRunning` for both purposes. This clarifies intent and reduces chance of gating bugs.
  - Extract conflict-detection (row/col/box) into a small helper function for testability and clarity.

  Testing checklist (verify these after changes)
  - Lesson screen
    - All 5 lessons appear, dividers present, 3×3 grid is centered, column-lesson layout displays left grid + right text correctly.
  - Practice puzzle
    - No pause overlay blocking the board on entry.
    - NumberPad, Undo, Erase, Notes work and are enabled.
    - Timer remains 0 while in practice.
    - Hint button hidden / disabled.
    - Wrong-number input shows appropriate hint (row/column/box) and mistakes not incremented.
    - LearningCompleteDialog appears on solve and CTAs behave.

  Next small tasks you can pick from
  1) Replace hard-coded strings with `stringResource` and add entries to `res/values/strings.xml` (I can generate the patch).
  2) Implement the `controlsEnabled` / `timerRunning` split for clarity (I can prepare a small refactor patch).
  3) Add analytics calls for learning-mode events.
  4) Add an automated UI test for the learning flow (enter wrong number, assert banner, assert mistakes == 0).

  If you want, I can apply the string-resource patch now (add `strings.xml` entries and replace the in-code strings) — tell me if you'd like me to proceed with that or with the `controlsEnabled` refactor.

                Text(
                    text = l.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))

                // Body
                Text(
                    text = l.body,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        // ── Bottom button ───────────────────────────────────────
        Button(
            onClick = {
                if (isLastStep) onLessonsComplete() else currentStep++
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLastStep)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isLastStep) "Start Puzzle" else "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (!isLastStep) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
```

---

### Step 3 — Create `LearningCompleteDialog`

**File**: `app/src/main/java/com/quickmindgames/sudoku/component/LearningCompleteDialog.kt` *(new file)*

```kotlin
package com.quickmindgames.sudoku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Congratulation dialog shown when the user completes a Learning Mode puzzle.
 * Encourages the user to try normal game modes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningCompleteDialog(
    onTryNormalMode: () -> Unit,
    onBackToHome: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ── Gradient header ──────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00897B), Color(0xFF43A047))
                            ),
                            RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                        )
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎓", fontSize = 52.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Excellent Work!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "You completed your learning puzzle!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // ── Encouragement text ─────────────────────────
                Text(
                    text = "You're ready for a real challenge.\nTry a normal game mode!",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                )

                // ── Buttons ────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onTryNormalMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00897B)
                        )
                    ) {
                        Text("Try Normal Mode", fontWeight = FontWeight.SemiBold)
                    }

                    FilledTonalButton(
                        onClick = onBackToHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Back to Home", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
```

---

### Step 4 — Extend `SudokuScreen` for `mode = "learn"`

**File**: `app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/SudokuScreen.kt` *(modify)*

#### 4a — Add imports at top of file

```kotlin
// Add alongside existing imports
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.text.style.TextAlign
import com.quickmindgames.sudoku.component.LearningCompleteDialog
```

#### 4b — Add `isLearningMode` flag and `learningHint` state

After the existing state declarations (after `var isNotesMode` and `var availableHints`):

```kotlin
    var isNotesMode by remember { mutableStateOf(false) }
    var availableHints by remember(mode, difficulty) { mutableIntStateOf(1) }

    // ── Learning Mode state ────────────────────────────────────
    val isLearningMode = mode == "learn"
    var learningHint by remember { mutableStateOf<String?>(null) }
```

#### 4c — Auto-dismiss learning hint after 2.5 seconds

Add alongside the existing `LaunchedEffect(shakeCells)` block:

```kotlin
    LaunchedEffect(shakeCells) {
        delay(300)
        shakeCells = emptySet()
    }

    // Auto-dismiss learning hint
    LaunchedEffect(learningHint) {
        if (learningHint != null) {
            delay(2500)
            learningHint = null
        }
    }
```

#### 4d — New `"learn"` branch in `LaunchedEffect(mode, difficulty)`

Add `if (mode == "learn")` **before** the existing `if (mode == "new")`:

```kotlin
    LaunchedEffect(mode, difficulty) {
        // ── Learning Mode ──────────────────────────────────
        if (mode == "learn") {
            val diff = Difficulty.Breeze
            currentDifficulty = diff

            userGrid.clear()
            originalGrid.forEach { row ->
                userGrid.add(row.map { CellData(it) }.toMutableStateList())
            }

            clearUndoRedoHistory()
            wrongCells = emptySet()
            mistakes = 0
            score = 0
            timeSeconds = 0
            gameOver = false
            gameWon = false
            selectedCell = null
            shakeCells = emptySet()
            isRunning = false // timer not used in learning mode
            timerKey++
            correctStreak = 0
            lastCorrectTime = 0
            for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false
            availableHints = 0 // hints disabled
        }

        if (mode == "new") {
            // ...existing "new" code unchanged...
        }

        // ...existing "resume" and "streak" code unchanged...
    }
```

#### 4e — Guard auto-save `LaunchedEffect` to skip learning mode

```kotlin
    LaunchedEffect(
        userGrid.hashCode(),
        mistakes,
        score,
        timeSeconds,
        selectedCell,
        isNotesMode,
        availableHints
    ) {
        if (!gameOver && !gameWon && !isLearningMode) { // ← add !isLearningMode
            // ...existing auto-save code unchanged...
        }
    }
```

#### 4f — Top bar: show "Learning Mode" title + hide Score Card

Replace the title `Text` composable in the top bar `Row`:

```kotlin
                Text(
                    text = if (isLearningMode) "Learning Mode"
                           else if (mode == "streak" && streakDay > 0) "${ordinalSuffix(streakDay)} Streak"
                           else if (mode == "streak") "Streak"
                           else currentDifficulty.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
```

Wrap the Score Card with a learning guard:

```kotlin
            Spacer(Modifier.width(8.dp))

            // Score Card — HIDE in learning mode
            if (!isLearningMode) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 16.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$score",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
```

#### 4g — Hide Mistakes + Timer row for learning mode

Wrap the entire `Box` (Mistakes + Timer) with:

```kotlin
        // Mistakes + Timer — HIDE in learning mode
        if (!isLearningMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
            ) {
                // ...existing Mistakes + Timer code unchanged...
            }
        }
```

#### 4h — Add learning hint banner below the board

After the `Box` containing `SudokuGridLevel` + pause overlay (after `Spacer(modifier = Modifier.height(8.dp))`), insert:

```kotlin
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            SudokuGridLevel(/* ...existing params... */)

            // ...existing pause overlay unchanged...
        }

        // ── Learning hint banner ────────────────────────────
        AnimatedVisibility(
            visible = learningHint != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            learningHint?.let { hint ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
```

#### 4i — Modify wrong-number handling in NumberPad callback

Replace the block starting at `if (number != solutionGrid[r][c])` inside the `else` (non-notes) branch:

```kotlin
                    if (number != solutionGrid[r][c]) {
                        if (isLearningMode) {
                            // ── Learning mode: show explanation, don't count mistake ──
                            wrongCells = wrongCells + (r to c)
                            shakeCells = shakeCells + (r to c)

                            // Determine WHY the number is wrong
                            val rowConflict = (0 until 9).any { col ->
                                col != c && userGrid[r][col].value == number
                            }
                            val colConflict = (0 until 9).any { row ->
                                row != r && userGrid[row][c].value == number
                            }
                            val boxRowStart = (r / 3) * 3
                            val boxColStart = (c / 3) * 3
                            val boxConflict = (boxRowStart until boxRowStart + 3).any { br ->
                                (boxColStart until boxColStart + 3).any { bc ->
                                    (br != r || bc != c) && userGrid[br][bc].value == number
                                }
                            }

                            learningHint = when {
                                rowConflict -> "Number already exists in this row"
                                colConflict -> "Number already exists in this column"
                                boxConflict -> "Number already exists in this 3×3 box"
                                else -> "That's not the correct number for this cell"
                            }
                            // Do NOT increment mistakes
                            // Do NOT trigger gameOver
                        } else {
                            // ── Normal mode: existing mistake logic unchanged ──
                            mistakes++
                            wrongCells = wrongCells + (r to c)
                            shakeCells = shakeCells + (r to c)
                            correctStreak = 0

                            if (mistakes >= 3) {
                                gameOver = true
                                isRunning = false
                                coroutineScope.launch {
                                    gameStateManager.clearGameState()
                                    // ...existing game-over recording code unchanged...
                                }
                            }
                        }
                    } else {
                        wrongCells = wrongCells - (r to c)

                        if (!isLearningMode) {
                            // ── Normal mode: existing scoring logic unchanged ──
                            if (!scoredCells[r][c]) {
                                // ...existing scoring code unchanged...
                            }
                        }
                        // Learning mode: no score awarded — just clear wrong state
                    }

                    // Win check — works for both modes
                    if (isWin(userGrid, solutionGrid)) {
                        gameWon = true
                        isRunning = false

                        if (!isLearningMode) {
                            // ...existing GameCompletionRecorder / analytics code unchanged...
                        }
                    }
```

#### 4j — Hide Hint button for learning mode

Wrap the Hint `FilledTonalButton` in the action buttons `Row`:

```kotlin
            // Hint Button — HIDE in learning mode
            if (!isLearningMode) {
                FilledTonalButton(
                    onClick = {
                        // ...existing hint logic unchanged...
                    },
                    enabled = isRunning && !gameOver && !gameWon && availableHints > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    // ...existing hint button content unchanged...
                }
            }
```

#### 4k — Override win dialog for learning mode

Replace the existing `if (gameWon)` block:

```kotlin
    if (gameWon) {
        if (isLearningMode) {
            // ── Learning mode: show LearningCompleteDialog ──
            LearningCompleteDialog(
                onTryNormalMode = {
                    gameWon = false
                    onExit() // Pop back to home — user can tap New Game
                },
                onBackToHome = {
                    gameWon = false
                    onExit()
                }
            )
        } else {
            // ── Normal mode: existing win dialog unchanged ──
            LaunchedEffect(Unit) {
                totalScoreManager.addScore(score)
                // ...existing streak / analytics code unchanged...
            }

            BasicAlertDialog(onDismissRequest = {}) {
                // ...existing win dialog Card unchanged...
            }
        }
    }
```

#### 4l — Guard game-over dialog (safety net)

```kotlin
    if (gameOver && !isLearningMode) {
        // ...existing game over dialog unchanged...
    }
```

---

### Step 5 — Wire navigation

#### 5a — `AppNav.kt` *(modify)*

**File**: `app/src/main/java/com/quickmindgames/sudoku/presentation/navigation/AppNav.kt`

Add the import and a new `"learn"` composable route:

```kotlin
package com.quickmindgames.sudoku.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.presentation.ui.MainSudokuApp
import com.quickmindgames.sudoku.presentation.ui.screen.LessonScreen  // ← NEW
import com.quickmindgames.sudoku.presentation.ui.screen.SudokuScreen

@Composable
fun AppNav() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = "main"
    ) {

        // MAIN SCREEN (contains bottom tabs)
        composable("main") {
            MainSudokuApp(
                onOpenPlay = { mode, difficulty, streakDay ->
                    rootNavController.navigate(
                        "play/$mode/${difficulty?.name ?: "none"}/${streakDay ?: 0}"
                    )
                },
                onOpenLearn = {                                         // ← NEW
                    rootNavController.navigate("learn")
                }
            )
        }

        // LESSON SCREEN (Learning Mode lessons)                       // ← NEW
        composable("learn") {
            LessonScreen(
                onLessonsComplete = {
                    rootNavController.navigate("play/learn/Breeze/0") {
                        popUpTo("learn") { inclusive = true }
                    }
                },
                onExit = {
                    rootNavController.popBackStack()
                }
            )
        }

        // PLAY SCREEN (outside bottom tabs) — unchanged
        composable(
            route = "play/{mode}/{difficulty}/{streakDay}",
            arguments = listOf(
                navArgument("mode") { defaultValue = "new" },
                navArgument("difficulty") { defaultValue = "none" },
                navArgument("streakDay") { defaultValue = "0" }
            )
        ) { entry ->
            val mode = entry.arguments?.getString("mode") ?: "new"
            val diffStr = entry.arguments?.getString("difficulty") ?: "none"
            val streakDay = entry.arguments?.getString("streakDay")?.toIntOrNull() ?: 0
            val difficulty = diffStr
                .takeIf { it != "none" }
                ?.let { s ->
                    Difficulty.entries.firstOrNull {
                        it.name.equals(s, ignoreCase = true)
                    }
                }

            SudokuScreen(
                mode = mode,
                difficulty = difficulty,
                streakDay = streakDay,
                onExit = { rootNavController.popBackStack() }
            )
        }
    }
}
```

#### 5b — `MainSudokuApp` in `MainActivity.kt` *(modify)*

**File**: `app/src/main/java/com/quickmindgames/sudoku/presentation/ui/MainActivity.kt`

Add `onOpenLearn` parameter to `MainSudokuApp` and pass it to `HomeScreen`:

```kotlin
@Composable
fun MainSudokuApp(
    onOpenPlay: (String, Difficulty?, Int?) -> Unit,
    onOpenLearn: () -> Unit                                             // ← NEW
) {
    val navController = rememberNavController()
    val screens = listOf(BottomScreen.Home, BottomScreen.Streak, BottomScreen.Settings)

    Scaffold(
        bottomBar = {
            // ...existing NavigationBar unchanged...
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomScreen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomScreen.Home.route) {
                HomeScreen(
                    onNewGameClick = { /* optional analytics, etc. */ },
                    onDifficultySelected = { difficulty: Difficulty ->
                        onOpenPlay("new", difficulty, null)
                    },
                    onResume = {
                        onOpenPlay("resume", null, null)
                    },
                    onStreakClick = {
                        navController.navigate(BottomScreen.Streak.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLearningModeClick = onOpenLearn                   // ← NEW
                )
            }
            // ...existing Streak and Settings composables unchanged...
        }
    }
}
```

#### 5c — `HomeScreen.kt` *(modify)*

**File**: `app/src/main/java/com/quickmindgames/sudoku/presentation/ui/screen/HomeScreen.kt`

Add imports at the top:
```kotlin
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke
```

Update the composable signature to include `onLearningModeClick`:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewGameClick: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onResume: () -> Unit,
    onStreakClick: () -> Unit,
    onLearningModeClick: () -> Unit                                     // ← NEW
) {
    // ...existing code unchanged...
```

Insert the Learning Mode button immediately after the `New Game` `Button` (after its closing `}`):

```kotlin
            // ...existing New Game Button...
            Button(
                onClick = {
                    onNewGameClick()
                    showSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "New Game", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ── Learning Mode button ── NEW ─────────────────────
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLearningModeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                border = BorderStroke(
                    1.5.dp,
                    MaterialTheme.colorScheme.tertiary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Learning Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            // ── End Learning Mode button ────────────────────────
```

---

## Reusable Existing Components

| Component | Reused In |
|---|---|
| `SudokuGridLevel` | Learning puzzle board (unchanged) |
| `NumberPad` | Learning puzzle number input (unchanged) |
| `SudokuGridCanvas` | Drawn inside `SudokuGridLevel` (unchanged) |
| `BasicAlertDialog` pattern | `LearningCompleteDialog` follows the same Card/gradient pattern |

---

## Data Models Needed

| Model | File | Purpose |
|---|---|---|
| `LessonData` | `domain/model/LessonData.kt` *(new)* | Holds lesson title, body, icon |
| `LESSONS` constant | same file | List of all 5 lessons |
| `learningHint: String?` | local state in `SudokuScreen` | Inline wrong-number explanation |

No new persistent data models are required. Learning mode deliberately does not save or load any state.

---

## New Files Summary

| # | File | Type |
|---|---|---|
| 1 | `domain/model/LessonData.kt` | Data model |
| 2 | `presentation/ui/screen/LessonScreen.kt` | New screen |
| 3 | `component/LearningCompleteDialog.kt` | New component |

## Modified Files Summary

| # | File | Changes |
|---|---|---|
| 1 | `presentation/ui/screen/SudokuScreen.kt` | Add `isLearningMode` flag, `learningHint` state, `"learn"` branch in init, UI guards for timer/score/mistakes/hints, wrong-number explanation banner, learning win dialog override, skip auto-save |
| 2 | `presentation/navigation/AppNav.kt` | Add `"learn"` route, pass `onOpenLearn` to `MainSudokuApp` |
| 3 | `presentation/ui/MainActivity.kt` | Add `onOpenLearn` param to `MainSudokuApp`, pass to `HomeScreen` |
| 4 | `presentation/ui/screen/HomeScreen.kt` | Add `onLearningModeClick` param, add 🎓 Learning Mode `OutlinedButton` below New Game |

---

## Implementation Phases

### Phase 1 — Data & Lesson Content
- Create `LessonData.kt` with all 5 lessons.

### Phase 2 — Lesson Screen
- Build `LessonScreen.kt` with step pager, progress dots, Next/Start Puzzle buttons.

### Phase 3 — Learning Puzzle Mode
- Extend `SudokuScreen` with `mode = "learn"` branch, UI guards, wrong-number banner, and `LearningCompleteDialog`.

### Phase 4 — Navigation & Home Screen
- Add `"learn"` route to `AppNav.kt`.
- Add `onOpenLearn` to `MainSudokuApp`.
- Add **Learning Mode** button to `HomeScreen.kt`.

### Phase 5 — Polish
- Style lesson cards consistently with the app theme.
- Add lesson illustrations (static mini-grid via `SudokuGridCanvas`, or emoji placeholders).
- Optionally add a subtle animation (slide/fade) between lesson steps.

---

## Further Considerations

1. **Lesson illustrations** — each lesson card can highlight a static pre-filled mini-grid using the existing `SudokuGridCanvas` component, or use simple emoji/drawable placeholders to keep scope small in Phase 2, deferring rich illustrations to Phase 5.
2. **Wrong-number UX** — an `AnimatedVisibility` banner inside the board `Box` is more prominent than a `Snackbar`. Recommend the banner approach so it's visible without being a blocking dialog. Auto-dismiss after 2.5 seconds via `LaunchedEffect`.
3. **`LearningCompleteDialog` CTAs** — the **Try Normal Mode** button pops back to Home (letting the user tap New Game themselves). This avoids threading extra callbacks.
4. **Analytics** — add optional `logLearningModeStarted()` and `logLearningGameCompleted()` events using the existing `AnalyticsUtils` pattern without blocking the feature.
5. **Back navigation** — pressing system Back from `LessonScreen` goes to previous lesson or exits to Home. Pressing Back from the learning puzzle returns to Home (since `LessonScreen` was popped from the back stack when navigating to the puzzle).
6. **Timer in learning mode** — `isRunning` is set to `false` in the `"learn"` init branch. The timer `LaunchedEffect` still runs but never increments since `isRunning` is false. The timer/pause UI is hidden entirely via the `if (!isLearningMode)` guard.

