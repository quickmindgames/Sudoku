package com.gamestudio.sudo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gamestudio.sudo.component.Difficulty
import com.gamestudio.sudo.data.util.GameCompletionRecorder
import com.gamestudio.sudo.domain.util.clearUndoRedoHistory
import com.gamestudio.sudo.domain.util.saveUndoState
import com.gamestudio.sudo.domain.util.undo
import com.gamestudio.sudo.domain.util.undoStack
import com.gamestudio.sudo.presentation.navigation.AppNav
import com.gamestudio.sudo.presentation.navigation.BottomScreen
import com.gamestudio.sudo.presentation.util.shakeEffect
import com.gamestudio.sudo.ui.theme.SudoTheme
import com.quickmindgames.sudoku.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.floor
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themePreferences = remember { ThemePreferences.getInstance(this) }
            var isDarkMode by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                themePreferences.isDarkMode.collect { darkMode ->
                    isDarkMode = darkMode
                    // isAppearanceLightStatusBars = true → dark icons (visible on light bg)
                    // isAppearanceLightStatusBars = false → light icons (visible on dark bg)
                    androidx.core.view.WindowCompat
                        .getInsetsController(window, window.decorView)
                        .isAppearanceLightStatusBars = !darkMode
                }
            }

            SudoTheme(darkTheme = isDarkMode) {
                AppNav()
            }
        }
    }
}

@Composable
fun MainSudokuApp(
    onOpenPlay: (String, Difficulty?, Int?) -> Unit
) {
    val navController = rememberNavController()
    val screens = listOf(BottomScreen.Home, BottomScreen.Streak, BottomScreen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = currentRoute(navController)

                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomScreen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            //composable(BottomScreen.Sudoku.route) { SudokuScreen(modifier = Modifier.fillMaxSize()) }
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
                    }
                )
            }
            composable(BottomScreen.Streak.route) {
                StreakScreen(
                    onOpenStreak = { dayNumber ->
                        onOpenPlay(
                            "streak",
                            Difficulty.Breeze,
                            dayNumber
                        )
                    }
                )
            }
            composable(BottomScreen.Settings.route) { SettingsScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewGameClick: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onResume: () -> Unit,
    onStreakClick: () -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val gameStateManager = remember { GameStateManager.getInstance(context) }
    val streakStateManager = remember { StreakStateManager.getInstance(context) }
    val totalScoreManager = remember { TotalScoreManager.getInstance(context) }

    // Saved game state
    var hasSavedGame by remember { mutableStateOf(false) }
    var savedGameDifficulty by remember { mutableStateOf<Difficulty?>(null) }

    // Streak state
    var streakCount by remember { mutableStateOf(0) }
    var completedToday by remember { mutableStateOf(false) }
    var hasActiveToday by remember { mutableStateOf(false) }

    // Total lifetime score
    var totalScore by remember { mutableIntStateOf(0) }

    val today = remember { LocalDate.now().toString() }

    LaunchedEffect(Unit) {
        gameStateManager.getSavedGameState().collect { savedState ->
            hasSavedGame = savedState != null
            savedGameDifficulty = savedState?.difficulty
        }
    }

    LaunchedEffect(Unit) {
        streakStateManager.getStreakState().collect { saved ->
            val s = saved ?: StreakState(0, null, null, null)
            streakCount = s.streakCount
            completedToday = s.lastCompletedDate == today
            hasActiveToday = s.activeDate == today && s.activeGame?.hasStarted() == true
        }
    }

    LaunchedEffect(Unit) {
        totalScoreManager.totalScore.collect { saved ->
            totalScore = saved
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Header
                Text(
                    text = "Choose Difficulty",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Select your challenge level",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(20.dp))

                // Difficulty cards
                Difficulty.entries.forEach { difficulty ->
                    val (isUnlocked, lockedMessage) = isDifficultyUnlocked(difficulty, context)
                    DifficultyCard(
                        difficulty = difficulty,
                        isLocked = !isUnlocked,
                        onClick = {
                            showSheet = false
                            onDifficultySelected(difficulty)
                        },
                        onLockedClick = {
                            Toast.makeText(context, lockedMessage, Toast.LENGTH_SHORT).show()
                            /*// Show toast on main thread
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                Toast.makeText(context, lockedMessage, Toast.LENGTH_SHORT).show()
                            }*/
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            )
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Streak Card ──────────────────────────────────────────────────
        StreakSummaryCard(
            streakCount = streakCount,
            completedToday = completedToday,
            hasActiveToday = hasActiveToday,
            onClick = onStreakClick
        )

        // ── Logo + Buttons centred in remaining space ─────────────────────
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧩", fontSize = 64.sp)
            }

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Master the puzzle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Total lifetime score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⭐",
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (totalScore == 0) "No score yet — play a game!"
                    else "Total Score: ${"%,d".format(totalScore)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (totalScore == 0)
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resume button
            if (hasSavedGame) {
                ElevatedButton(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Resume Game",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            savedGameDifficulty?.let { difficulty ->
                                Text(
                                    text = difficulty.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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
        }
    }
}

@Composable
fun StreakSummaryCard(
    streakCount: Int,
    completedToday: Boolean,
    hasActiveToday: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val appName = stringResource(R.string.app_name)

    val statusText = when {
        completedToday -> "Completed today ✓"
        hasActiveToday && streakCount > 0 -> "Keep going!"
        streakCount > 0 -> "Play today to keep it alive!"
        else -> "Start your first streak!"
    }

    val statusColor = when {
        completedToday -> MaterialTheme.colorScheme.primary
        hasActiveToday -> Color(0xFFFF6F00)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flame + streak count badge
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔥", fontSize = 22.sp)
            }

            Spacer(Modifier.width(14.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (streakCount) {
                        0 -> "No Streak"
                        1 -> "1 Day Streak"
                        else -> "$streakCount Days Streak"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    color = statusColor,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Share icon button — only shown when there is an active streak
            if (streakCount > 0) {
                IconButton(
                    onClick = {
                        val shareText = buildString {
                            append("🔥 I'm on a $streakCount day Sudoku streak in $appName!\n")
                            append("Can you beat my streak? Play now:\n")
                            append("https://play.google.com/store/apps/details?id=${context.packageName}")
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share your streak"))
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share streak",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StreakScreen(
    onOpenStreak: (Int) -> Unit
) {
    val context = LocalContext.current
    val appName = stringResource(R.string.app_name)
    val streakStateManager = remember { StreakStateManager.getInstance(context) }

    var streakState by remember {
        mutableStateOf(
            StreakState(
                streakCount = 0,
                lastCompletedDate = null,
                activeDate = null,
                activeGame = null
            )
        )
    }

    LaunchedEffect(Unit) {
        streakStateManager.getStreakState().collect { saved ->
            streakState = saved ?: StreakState(0, null, null, null)
        }
    }

    val today = remember { LocalDate.now().toString() }
    val streakCount = streakState.streakCount
    val hasActiveToday = streakState.activeDate == today
            && streakState.activeGame?.hasStarted() == true
    val completedToday = streakState.lastCompletedDate == today
    val canPlayToday = !completedToday

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Compact header section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding()
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Compact header with inline streak count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥",
                        fontSize = 32.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "My Current Streak",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Keep it alive!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                // Streak counter + share icon stacked
                Box(contentAlignment = Alignment.TopEnd) {
                    // Compact streak counter
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            .then(
                                if (streakCount > 0) Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    val shareText = buildString {
                                        append("🔥 I'm on a $streakCount day Sudoku streak in $appName!\n")
                                        append("Can you beat my streak? Play now:\n")
                                        append("https://play.google.com/store/apps/details?id=${context.packageName}")
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Share your streak"
                                        )
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$streakCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Tiny share badge pinned to top-end of the circle
                    if (streakCount > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(22.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    CircleShape
                                )
                                .clickable {
                                    val shareText = buildString {
                                        append("🔥 I'm on a $streakCount day Sudoku streak in $appName!\n")
                                        append("Can you beat my streak? Play now:\n")
                                        append("https://play.google.com/store/apps/details?id=${context.packageName}")
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Share your streak"
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share streak",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Journey path with more space
        StreakPath(
            streakCount = streakCount,
            hasActiveToday = hasActiveToday,
            completedToday = completedToday,
            onStart = { dayNumber -> onOpenStreak(dayNumber) }
        )
    }
}

@Composable
fun StreakPath(
    streakCount: Int,
    hasActiveToday: Boolean,
    completedToday: Boolean,
    onStart: (Int) -> Unit
) {
    val totalNodes = 14
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Journey path with gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                userScrollEnabled = true
            ) {
                items(totalNodes) { index ->
                    val dayNumber = index + 1
                    val isCompleted = dayNumber <= streakCount
                    val isNext = dayNumber == streakCount + 1
                    val isLocked = dayNumber > streakCount + 1
                    val isPlayable = isNext && (hasActiveToday || !completedToday)
                    val isActive = isNext && hasActiveToday
                    val isNextButCompletedToday = isNext && completedToday && !hasActiveToday

                    StreakNode(
                        dayNumber = dayNumber,
                        isCompleted = isCompleted,
                        isActive = isActive,
                        isLocked = isLocked,
                        isPlayable = isPlayable,
                        onClick = { onStart(dayNumber) },
                        onLockedClick = {
                            val message = if (isNextButCompletedToday) {
                                "Come back tomorrow to continue your streak!"
                            } else {
                                "Finish the above streaks to unlock this!"
                            }
                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StreakNode(
    dayNumber: Int,
    isCompleted: Boolean,
    isActive: Boolean,
    isLocked: Boolean,
    isPlayable: Boolean,
    onClick: () -> Unit,
    onLockedClick: () -> Unit = {}
) {
    // Active = orange/amber to stand out as "in progress"
    val nodeColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary
        isActive -> Color(0xFFFF6F00)   // deep amber — clearly "in progress"
        isPlayable -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        isActive -> Color.White
        isPlayable -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    }

    val isMilestone = dayNumber % 5 == 0

    val pathColor = if (isCompleted || isActive)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    else
        Color.Gray.copy(alpha = 0.2f)

    val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

    val nodeSize = if (isMilestone && !isLocked) 68.dp else 60.dp
    val boxHeight = if (isMilestone && !isLocked) 110.dp else 95.dp
    val nodeCenterFraction = 0.62f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight)
    ) {
        // Curved path
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = size.width / 2f
            val nodeOffsetX = if (dayNumber % 2 == 0) 110.dp.toPx() else -110.dp.toPx()
            val currentNodeX = centerX + nodeOffsetX
            val prevNodeX = centerX - nodeOffsetX
            val nextNodeX = centerX + nodeOffsetX
            val nodeY = size.height * nodeCenterFraction

            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(prevNodeX, 0f)
                cubicTo(
                    x1 = prevNodeX, y1 = nodeY * 0.5f,
                    x2 = currentNodeX, y2 = nodeY * 0.7f,
                    x3 = currentNodeX, y3 = nodeY
                )
                cubicTo(
                    x1 = currentNodeX, y1 = nodeY + (size.height - nodeY) * 0.35f,
                    x2 = nextNodeX, y2 = nodeY + (size.height - nodeY) * 0.65f,
                    x3 = nextNodeX, y3 = size.height
                )
            }

            if (isActive || isPlayable) {
                drawPath(
                    path = path,
                    color = glowColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = if (isActive) 16f else 12f,
                        cap = StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }

            drawPath(
                path = path,
                color = pathColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = if (isCompleted || isActive) 10f else 5f,
                    cap = StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            val nodeOffsetX = if (dayNumber % 2 == 0) 110.dp else (-110).dp

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset {
                        IntOffset(
                            x = nodeOffsetX.roundToPx(),
                            y = (boxHeight * nodeCenterFraction - nodeSize / 2).roundToPx()
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isMilestone && !isLocked) {
                    Text(
                        text = "👑",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                ElevatedCard(
                    modifier = Modifier.size(nodeSize),
                    shape = CircleShape,
                    colors = CardDefaults.elevatedCardColors(containerColor = nodeColor),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = if (isCompleted || isActive) 8.dp
                        else if (isPlayable) 5.dp else 2.dp
                    ),
                    onClick = if (isPlayable || isActive) {
                        { onClick() }
                    } else if (isLocked || (!isPlayable && !isCompleted && !isActive)) {
                        { onLockedClick() }
                    } else {
                        {}
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Amber ring for active state
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.88f)
                                    .background(
                                        Color(0xFFFFCC02).copy(alpha = 0.25f),
                                        CircleShape
                                    )
                            )
                        } else if (isPlayable) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.95f)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                            )
                        }

                        if (isCompleted) {
                            Text(
                                text = "✓",
                                fontSize = if (isMilestone) 32.sp else 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        } else {
                            Text(
                                text = dayNumber.toString(),
                                fontSize = if (isMilestone && !isLocked) 22.sp else 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }

                // Milestone badge
                if (isMilestone && (isCompleted || isPlayable || isActive)) {
                    Spacer(Modifier.height(6.dp))
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🏆", fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Milestone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    var showStatistics by remember { mutableStateOf(false) }

    var isDarkMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        themePreferences.isDarkMode.collect { darkMode ->
            isDarkMode = darkMode
        }
    }

    if (showStatistics) {
        val statisticsViewModel =
            remember { com.gamestudio.sudo.presentation.viewmodel.StatisticsViewModel(context) }
        com.gamestudio.sudo.presentation.ui.screen.StatisticsScreen(
            viewModel = statisticsViewModel,
            onBackClick = { showStatistics = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Settings header + cards ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .padding(
                        top = WindowInsets.statusBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = "Game",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStatistics = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📊", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Statistics",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View your game statistics",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Appearance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isDarkMode) "🌙" else "☀️", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dark Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkMode) "Enabled" else "Disabled",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { enabled ->
                                coroutineScope.launch { themePreferences.setDarkMode(enabled) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "About",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🧩", fontSize = 24.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Version 1.0.0",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            } // end inner Column

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    mode: String,
    difficulty: Difficulty?,
    streakDay: Int = 0,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gameStateManager = remember { GameStateManager.getInstance(context) }
    val streakStateManager = remember { StreakStateManager.getInstance(context) }
    val totalScoreManager = remember { TotalScoreManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    // Generate puzzle and solution
    var puzzleData by remember(mode, difficulty) {
        val diff = difficulty ?: Difficulty.Breeze
        val (puzzle, solution) = SudokuGenerator.instance.generate(diff)
        mutableStateOf(Pair(puzzle, solution))
    }

    var originalGrid by remember(mode, difficulty) {
        mutableStateOf(puzzleData.first.map { it.toList() })
    }

    var solutionGrid by remember(mode, difficulty) {
        mutableStateOf(puzzleData.second)
    }

    // Don't key userGrid by originalGrid to avoid recreation during resume
    val userGrid = remember {
        mutableStateListOf(*originalGrid.map { it.toMutableStateList() }.toTypedArray())
    }
    var gameOver by remember { mutableStateOf(false) }
    var wrongCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var shakeCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var gameWon by remember { mutableStateOf(false) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var mistakes by remember { mutableIntStateOf(0) }
    var timeSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }
    var timerKey by remember { mutableIntStateOf(0) }
    var currentDifficulty by remember { mutableStateOf(difficulty ?: Difficulty.Breeze) }
    var correctStreak by remember { mutableIntStateOf(0) }   // consecutive correct cells
    var lastCorrectTime by remember { mutableIntStateOf(0) } // timeSeconds when last correct cell placed
    val scoredCells = remember {
        Array(9) { mutableStateListOf(*BooleanArray(9) { false }.toTypedArray()) }
    }

    LaunchedEffect(shakeCells) {
        delay(300)
        shakeCells = emptySet()
    }

    LaunchedEffect(timerKey) {
        while (true) {
            if (isRunning) {
                delay(1000)
                if (isRunning) timeSeconds++
            } else {
                delay(100) // poll until resumed
            }
        }
    }

    LaunchedEffect(mode, difficulty) {

        if (mode == "new") {
            // Generate new puzzle
            val diff = difficulty ?: Difficulty.Breeze
            val (puzzle, solution) = SudokuGenerator.instance.generate(diff)
            puzzleData = Pair(puzzle, solution)
            originalGrid = puzzle.map { it.toList() }
            solutionGrid = solution
            currentDifficulty = diff

            // Properly reset user grid
            userGrid.clear()
            originalGrid.forEach { row ->
                userGrid.add(row.toMutableStateList())
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
            isRunning = true
            timerKey++
            correctStreak = 0
            lastCorrectTime = 0
            for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false
        }

        if (mode == "resume") {
            // Restore from DataStore
            gameStateManager.getSavedGameState().collect { savedState ->
                savedState?.let { state ->
                    currentDifficulty = state.difficulty
                    originalGrid = state.originalGrid
                    solutionGrid = state.solutionGrid.map { it.toIntArray() }.toTypedArray()

                    // Properly restore user grid by clearing and repopulating
                    userGrid.clear()
                    state.userGrid.forEach { row ->
                        userGrid.add(row.toMutableStateList())
                    }

                    wrongCells = state.wrongCells
                    mistakes = state.mistakes
                    score = state.score
                    timeSeconds = state.timeSeconds
                    selectedCell = state.selectedCell
                    gameOver = false
                    gameWon = false
                    isRunning = true
                    timerKey++ // Restart timer from saved time
                }
            }
        }

        if (mode == "streak") {
            val today = LocalDate.now().toString()
            val savedStreak = streakStateManager.getStreakState().first()
                ?: StreakState(0, null, null, null)

            val activeGame = savedStreak.activeGame
            if (activeGame != null && savedStreak.activeDate == today) {
                // Resume active streak game
                currentDifficulty = activeGame.difficulty
                originalGrid = activeGame.originalGrid
                solutionGrid = activeGame.solutionGrid.map { it.toIntArray() }.toTypedArray()

                userGrid.clear()
                activeGame.userGrid.forEach { row ->
                    userGrid.add(row.toMutableStateList())
                }

                wrongCells = activeGame.wrongCells
                mistakes = activeGame.mistakes
                score = activeGame.score
                timeSeconds = activeGame.timeSeconds
                selectedCell = activeGame.selectedCell
                gameOver = false
                gameWon = false
                isRunning = true
                timerKey++
            } else if (savedStreak.lastCompletedDate == today) {
                // Already completed today, go back
                onExit()
            } else {
                // Start new streak game
                val (puzzle, solution) = SudokuGenerator.instance.generate(Difficulty.Breeze)
                puzzleData = Pair(puzzle, solution)
                originalGrid = puzzle.map { it.toList() }
                solutionGrid = solution
                currentDifficulty = Difficulty.Breeze

                userGrid.clear()
                originalGrid.forEach { row ->
                    userGrid.add(row.toMutableStateList())
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
                isRunning = true
                timerKey++
                correctStreak = 0
                lastCorrectTime = 0
                for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false

                // Save initial streak state
                val newGame = GameState(
                    difficulty = currentDifficulty,
                    originalGrid = originalGrid,
                    solutionGrid = solutionGrid.map { it.toList() },
                    userGrid = userGrid.map { it.toList() },
                    wrongCells = wrongCells,
                    mistakes = mistakes,
                    score = score,
                    timeSeconds = timeSeconds,
                    selectedCell = selectedCell
                )

                streakStateManager.saveStreakState(
                    savedStreak.copy(
                        activeDate = today,
                        activeGame = newGame
                    )
                )
            }
        }
    }

    // Clear saved game when starting a new game
    LaunchedEffect(mode) {
        if (mode == "new") {
            gameStateManager.clearGameState()
        }
    }

    // Auto-save game state whenever important state changes
    LaunchedEffect(userGrid.hashCode(), mistakes, score, timeSeconds, selectedCell) {
        // Only save if game has started (timer ticked at least once) and is not over/won
        if (!gameOver && !gameWon) {
            val currentState = GameState(
                difficulty = currentDifficulty,
                originalGrid = originalGrid,
                solutionGrid = solutionGrid.map { it.toList() },
                userGrid = userGrid.map { it.toList() },
                wrongCells = wrongCells,
                mistakes = mistakes,
                score = score,
                timeSeconds = timeSeconds,
                selectedCell = selectedCell
            )

            // Save as soon as timer has started (timeSeconds > 0)
            if (currentState.hasStarted()) {
                if (mode == "streak") {
                    val today = LocalDate.now().toString()
                    val savedStreak = streakStateManager.getStreakState().first()
                        ?: StreakState(0, null, null, null)

                    streakStateManager.saveStreakState(
                        savedStreak.copy(
                            activeDate = today,
                            activeGame = currentState
                        )
                    )
                } else {
                    gameStateManager.saveGameState(currentState)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onExit() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = if (mode == "streak" && streakDay > 0) "${ordinalSuffix(streakDay)} Streak"
                    else if (mode == "streak") "Streak"
                    else currentDifficulty.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(8.dp))

            // Score Card
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

        // Mistakes (left) + Timer + Pause (right)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            // Mistakes anchored to the left
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mistakes: ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                repeat(3) { index ->
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (index < mistakes)
                                    MaterialTheme.colorScheme.errorContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (index < mistakes)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Timer anchored to the right
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = formatTime(timeSeconds),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            // Sudoku board — always composed but visually hidden when paused
            SudokuGridLevel2(originalGrid, userGrid, wrongCells, shakeCells, selectedCell) { r, c ->
                if (isRunning) selectedCell = r to c
            }

            // Pause overlay
            if (!isRunning && !gameOver && !gameWon) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.93f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { isRunning = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Game Paused",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tap to resume",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        NumberPad(grid = userGrid) { number ->
            selectedCell?.let { (r, c) ->
                if (originalGrid[r][c] != 0 || gameOver || gameWon) return@NumberPad

                saveUndoState(userGrid)
                userGrid[r][c] = number

                if (number != solutionGrid[r][c]) {
                    mistakes++
                    wrongCells = wrongCells + (r to c)
                    shakeCells = shakeCells + (r to c)
                    // Break correct streak on mistake
                    correctStreak = 0

                    if (mistakes >= 3) {
                        gameOver = true
                        isRunning = false
                        coroutineScope.launch { gameStateManager.clearGameState() }

                        // Record the game loss
                        GameCompletionRecorder.recordGameCompletion(
                            context = context,
                            scope = coroutineScope,
                            difficulty = currentDifficulty.name,
                            isWon = false,
                            mistakes = mistakes,
                            score = score,
                            timeSeconds = timeSeconds
                        )
                    }
                } else {
                    wrongCells = wrongCells - (r to c)

                    if (!scoredCells[r][c]) {
                        // 1. Base points by difficulty
                        val basePoints = when (currentDifficulty) {
                            Difficulty.Breeze -> 1
                            Difficulty.Pulse -> 2
                            Difficulty.Rage -> 3
                            Difficulty.Elite -> 4
                        }

                        // 2. Speed bonus — seconds since last correct cell
                        val secondsSinceLast = timeSeconds - lastCorrectTime
                        val speedMultiplier = when {
                            secondsSinceLast <= 5 -> 2.0f   // Lightning fast  → ×2
                            secondsSinceLast <= 10 -> 1.5f   // Fast            → ×1.5
                            secondsSinceLast <= 20 -> 1.25f  // Good pace       → ×1.25
                            else -> 1.0f   // No bonus
                        }

                        // 3. Streak multiplier — consecutive correct cells
                        correctStreak++
                        val streakMultiplier = when {
                            correctStreak >= 5 -> 2.0f   // On fire!  → ×2
                            correctStreak >= 3 -> 1.5f   // Streak    → ×1.5
                            else -> 1.0f
                        }

                        // 4. Mistake penalty — each mistake reduces points by 10%
                        val mistakePenalty = 1f - (mistakes * 0.10f)

                        val earned =
                            (basePoints * speedMultiplier * streakMultiplier * mistakePenalty)
                                .toInt()
                                .coerceAtLeast(1)

                        score += earned
                        scoredCells[r][c] = true
                        lastCorrectTime = timeSeconds
                    }
                }

                if (isWin(userGrid, solutionGrid)) {
                    gameWon = true
                    isRunning = false

                    // Record the game completion
                    GameCompletionRecorder.recordGameCompletion(
                        context = context,
                        scope = coroutineScope,
                        difficulty = currentDifficulty.name,
                        isWon = true,
                        mistakes = mistakes,
                        score = score,
                        timeSeconds = timeSeconds
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Undo Button
            FilledTonalButton(
                onClick = {
                    undo(userGrid)
                    wrongCells = recomputeWrongCells(userGrid, solutionGrid)
                },
                enabled = undoStack.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Undo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Erase Button
            FilledTonalButton(
                onClick = {
                    selectedCell?.let { (r, c) ->
                        if (originalGrid[r][c] == 0) {
                            saveUndoState(userGrid)
                            userGrid[r][c] = 0
                            wrongCells = wrongCells - (r to c)
                        }
                    }
                },
                enabled = selectedCell != null && selectedCell?.let { (r, c) ->
                    wrongCells.contains(r to c)
                } == true,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Erase",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Erase",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Pause / Resume Button
            FilledTonalButton(
                onClick = { isRunning = !isRunning },
                enabled = !gameOver && !gameWon,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Resume",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "Pause" else "Resume",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (gameOver) {
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
                                        listOf(Color(0xFFB71C1C), Color(0xFFE53935))
                                    ),
                                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💀", fontSize = 52.sp)
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "Game Over",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "3 mistakes — better luck next time!",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // ── Buttons ──────────────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Try Again
                            Button(
                                onClick = {
                                    userGrid.clear()
                                    originalGrid.forEach { row -> userGrid.add(row.toMutableStateList()) }
                                    clearUndoRedoHistory()
                                    gameOver = false
                                    wrongCells = emptySet()
                                    mistakes = 0
                                    score = 0
                                    timeSeconds = 0
                                    gameWon = false
                                    selectedCell = null
                                    shakeCells = emptySet()
                                    isRunning = true
                                    timerKey++
                                    correctStreak = 0
                                    lastCorrectTime = 0
                                    for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false
                                    coroutineScope.launch {
                                        if (mode != "streak") gameStateManager.clearGameState()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE53935)
                                )
                            ) { Text("Try Again", fontWeight = FontWeight.SemiBold) }

                            // Exit
                            FilledTonalButton(
                                onClick = { onExit() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Exit", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }
        }

    } // end Column

    if (gameWon) {
        // Persist score & streak state
        LaunchedEffect(Unit) {
            totalScoreManager.addScore(score)
            if (mode == "streak") {
                val today = LocalDate.now().toString()
                val savedStreak = streakStateManager.getStreakState().first()
                    ?: StreakState(0, null, null, null)
                if (savedStreak.lastCompletedDate != today) {
                    streakStateManager.saveStreakState(
                        savedStreak.copy(
                            streakCount = savedStreak.streakCount + 1,
                            lastCompletedDate = today,
                            activeDate = null,
                            activeGame = null
                        )
                    )
                } else {
                    streakStateManager.saveStreakState(
                        savedStreak.copy(activeDate = null, activeGame = null)
                    )
                }
            } else {
                gameStateManager.clearGameState()
            }
        }

        BasicAlertDialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // ── Gradient header ──────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1565C0), Color(0xFF6A1B9A))
                                ),
                                RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                            )
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎉", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "You Win!",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Puzzle solved!",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // ── Stats row ────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Time
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) { Text("⏱️", fontSize = 22.sp) }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = formatTime(timeSeconds),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Time",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Score
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) { Text("⭐", fontSize = 22.sp) }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "$score",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Score",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Mistakes
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) { Text(if (mistakes == 0) "✅" else "❌", fontSize = 22.sp) }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "$mistakes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Mistakes",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // ── Buttons ──────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Next Game — only for non-streak mode
                        if (mode != "streak") {
                            Button(
                                onClick = {
                                    val (newPuzzle, newSolution) = SudokuGenerator.instance.generate(
                                        currentDifficulty
                                    )
                                    originalGrid = newPuzzle.map { it.toList() }
                                    solutionGrid = newSolution
                                    userGrid.clear()
                                    originalGrid.forEach { row -> userGrid.add(row.toMutableStateList()) }
                                    clearUndoRedoHistory()
                                    gameOver = false
                                    wrongCells = emptySet()
                                    mistakes = 0
                                    score = 0
                                    timeSeconds = 0
                                    gameWon = false
                                    selectedCell = null
                                    shakeCells = emptySet()
                                    isRunning = true
                                    timerKey++
                                    correctStreak = 0
                                    lastCorrectTime = 0
                                    for (r in 0..8) for (c in 0..8) scoredCells[r][c] = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1565C0)
                                )
                            ) {
                                Text("Next Game", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Done
                        FilledTonalButton(
                            onClick = { onExit() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Done", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}


fun formatTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
}

fun ordinalSuffix(n: Int): String {
    val suffix = when {
        n % 100 in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
}

fun resetGrid(grid: List<MutableList<Int>>, original: List<List<Int>>) {
    for (r in 0..8) {
        for (c in 0..8) {
            grid[r][c] = original[r][c]
        }
    }
}

@Composable
fun NumberPad(
    grid: List<List<Int>>,
    onNumberClick: (Int) -> Unit
) {
    // Count how many times each number (1–9) appears in the grid.
    // No remember() — must recompute on every recomposition so undo/redo
    // changes are reflected immediately (SnapshotStateList mutations trigger recompose).
    val numberCounts = IntArray(10)
    for (row in grid) for (cell in row) if (cell in 1..9) numberCounts[cell]++

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..9).forEach { number ->
            val isComplete = numberCounts[number] >= 9

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                if (!isComplete) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onNumberClick(number) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuGridLevel2(
    originalGrid: List<List<Int>>,
    grid: List<List<Int>>,
    wrongCells: Set<Pair<Int, Int>>,
    shakeCells: Set<Pair<Int, Int>>,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit
) {
    SudokuBoardContainer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // responsive
    ) { cellSizeDp, boardSizeDp ->

        // ✅ Your LazyVerticalGrid uses EXACT cell size now
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier.requiredSize(boardSizeDp),
            userScrollEnabled = false
        ) {
            items(81) { index ->
                val row = index / 9
                val col = index % 9
                val value = grid[row][col]
                val original = originalGrid[row][col]

                val isSelected = selectedCell == row to col
                val selectedValue = selectedCell?.let { grid[it.first][it.second] }
                val isSameNumber =
                    selectedValue != null && selectedValue != 0 && value == selectedValue
                val isWrong = wrongCells.contains(row to col)
                val shouldShake = shakeCells.contains(row to col)

                Box(
                    modifier = Modifier
                        .requiredSize(cellSizeDp) // ✅ exact dp snapped from px
                        .background(
                            when {
                                isSelected -> Color.LightGray
                                isSameNumber -> Color.Cyan.copy(alpha = 0.3f)
                                original != 0 -> Color(0xFFEFEFEF)
                                else -> Color.White
                            }
                        )
                        .clickable { onCellClick(row, col) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (value == 0) "" else value.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            original != 0 -> Color.Black
                            isWrong -> Color.Red
                            else -> Color.Blue
                        },
                        modifier = Modifier.shakeEffect(shouldShake)
                    )
                }
            }
        }

        // If you draw Canvas lines, keep it same snapped size:
        SudokuGridCanvas(modifier = Modifier.requiredSize(boardSizeDp))
    }
}

@Composable
fun SudokuGridCanvas(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    Canvas(modifier = modifier.clipToBounds()) {
        val thin = with(density) { 1.dp.toPx() }
        val thick = with(density) { 2.dp.toPx() }

        val outerColor = Color.Black.copy(alpha = 0.9f)
        val innerColor = Color.Black.copy(alpha = 0.6f)

        // ✅ Force perfect square drawing area
        val boardSize = min(size.width, size.height)
        val cellSize = boardSize / 9f

        // Optional: center the board if canvas is larger
        val offsetX = (size.width - boardSize) / 2f
        val offsetY = (size.height - boardSize) / 2f

        fun lineStroke(i: Int): Float =
            when {
                i == 0 || i == 9 -> thick
                i % 3 == 0 -> thick
                else -> thin
            }

        for (i in 0..9) {
            val stroke = lineStroke(i)
            val half = stroke / 2f
            val isOuter = (i == 0 || i == 9)
            val color = if (isOuter) outerColor else innerColor

            val x = offsetX + i * cellSize
            val y = offsetY + i * cellSize

            // ✅ Vertical lines end at boardSize, not canvas height
            drawLine(
                color = color,
                start = Offset(x, offsetY + half),
                end = Offset(x, offsetY + boardSize - half),
                strokeWidth = stroke,
                cap = StrokeCap.Butt
            )

            // ✅ Horizontal lines end at boardSize, not canvas width
            drawLine(
                color = color,
                start = Offset(offsetX + half, y),
                end = Offset(offsetX + boardSize - half, y),
                strokeWidth = stroke,
                cap = StrokeCap.Butt
            )
        }
    }
}

@Composable
fun rememberSudokuGrid(initialGrid: List<List<Int>>): SnapshotStateList<SnapshotStateList<Int>> {
    return remember(initialGrid) {
        mutableStateListOf(*initialGrid.map { it.toMutableStateList() }.toTypedArray())
    }
}

fun isWin(userGrid: List<MutableList<Int>>, solution: Array<IntArray>): Boolean {
    for (r in 0..8) {
        for (c in 0..8) {
            if (userGrid[r][c] != solution[r][c]) {
                return false
            }
        }
    }
    return true
}

fun recomputeWrongCells(
    grid: List<MutableList<Int>>,
    solution: Array<IntArray>
): Set<Pair<Int, Int>> {

    val wrong = mutableSetOf<Pair<Int, Int>>()

    for (r in 0..8) {
        for (c in 0..8) {
            val v = grid[r][c]
            if (v != 0 && v != solution[r][c]) {
                wrong.add(r to c)
            }
        }
    }
    return wrong
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}

@Composable
fun SudokuBoardContainer(
    modifier: Modifier = Modifier,
    content: @Composable (cellSizeDp: androidx.compose.ui.unit.Dp, boardSizeDp: androidx.compose.ui.unit.Dp) -> Unit
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val boardDpRaw = min(maxWidth, maxHeight)

        // Convert to px, then snap to multiples of 9 px
        val boardPxRaw = with(density) { boardDpRaw.toPx() }
        val cellPx = floor(boardPxRaw / 9f) // integer px per cell
        val boardPx = cellPx * 9f           // snapped board size px

        val cellDp = with(density) { cellPx.toDp() }
        val boardDp = with(density) { boardPx.toDp() }

        Box(
            modifier = Modifier
                .requiredSize(boardDp)   // ✅ board is exact size
                .clipToBounds()
        ) {
            content(cellDp, boardDp)
        }
    }
}

@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    onClick: () -> Unit,
    isLocked: Boolean = false,
    onLockedClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val (emoji, description) = when (difficulty) {
        Difficulty.Breeze -> Pair("🌤️", "Perfect for beginners")
        Difficulty.Pulse -> Pair("⚡", "Moderate challenge")
        Difficulty.Rage -> Pair("🔥", "Test your skills")
        Difficulty.Elite -> Pair("💎", "For masters only")
    }

    val cardColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFFDDEAFE) // Light Blue
        Difficulty.Pulse -> Color(0xFFFEF3C7) // Light Yellow
        Difficulty.Rage -> Color(0xFFFED7D7) // Light Red/Pink
        Difficulty.Elite -> Color(0xFFE9D5FF) // Light Purple
    }

    val accentColor = when (difficulty) {
        Difficulty.Breeze -> Color(0xFF3B82F6)
        Difficulty.Pulse -> Color(0xFFF59E0B)
        Difficulty.Rage -> Color(0xFFEF4444)
        Difficulty.Elite -> Color(0xFF8B5CF6)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.5f else 1f)
            .clickable(
                enabled = true,
                onClick = {
                    if (isLocked) {
                        onLockedClick()
                    } else {
                        onClick()
                    }
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = if (isLocked) 0.dp else 8.dp
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji icon with background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 32.sp
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = difficulty.label,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Arrow indicator or lock icon
                if (isLocked) {
                    Text(text = "🔒", fontSize = 24.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Select ${difficulty.label}",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Lock overlay for locked state
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                }
            }
        }
    }
}

/**
 * Check if a difficulty level is unlocked
 * Breeze: Always unlocked
 * Pulse: Requires 5 Breeze games
 * Rage: Requires 5 Pulse games
 * Elite: Requires 5 Rage games
 */
@Composable
fun isDifficultyUnlocked(
    difficulty: Difficulty,
    context: android.content.Context
): Pair<Boolean, String> {
    var breezeWins by remember { mutableIntStateOf(0) }
    var pulseWins by remember { mutableIntStateOf(0) }
    var rageWins by remember { mutableIntStateOf(0) }

    val repository = remember { com.gamestudio.sudo.data.repository.StatisticsRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val allStats = repository.getAllStatistics().first()
                breezeWins = allStats.breeze.wins
                pulseWins = allStats.pulse.wins
                rageWins = allStats.rage.wins
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return when (difficulty) {
        Difficulty.Breeze -> Pair(true, "")
        Difficulty.Pulse -> {
            if (breezeWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Breeze games to unlock Pulse")
            }
        }

        Difficulty.Rage -> {
            if (pulseWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Pulse games to unlock Rage")
            }
        }

        Difficulty.Elite -> {
            if (rageWins >= 5) {
                Pair(true, "")
            } else {
                Pair(false, "Win 5 Rage games to unlock Elite")
            }
        }
    }
}

