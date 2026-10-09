package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickmindgames.sudoku.BuildConfig
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.data.preferences.AppPreferences
import com.quickmindgames.sudoku.data.preferences.ThemePreferences
import com.quickmindgames.sudoku.data.state.GameStateManager
import com.quickmindgames.sudoku.presentation.viewmodel.StatisticsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    //region State and preference collection
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences.getInstance(context) }
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val gameStateManager = remember { GameStateManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    var showStatistics by remember { mutableStateOf(false) }

    var isDarkMode by remember { mutableStateOf(false) }
    var hideUsedNumbers by remember { mutableStateOf(false) }
    var highlightRegion by remember { mutableStateOf(false) }
    var highlightSameNumbers by remember { mutableStateOf(false) }
    var freePlay by remember { mutableStateOf(false) }
    var hasSavedGame by remember { mutableStateOf(false) }
    var soundAndVibration by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        themePreferences.isDarkMode.collect { darkMode ->
            isDarkMode = darkMode
        }
    }

    LaunchedEffect(Unit) {
        // Hide used numbers and Free Play are mutually exclusive preferences.
        appPreferences.normalizeGameplayOptions()
        combine(appPreferences.hideUsedNumbers, appPreferences.freePlay) { hide, free ->
            hide to free
        }.collect { (hide, free) ->
            hideUsedNumbers = hide
            freePlay = free
        }
    }

    LaunchedEffect(Unit) {
        appPreferences.highlightRegion.collect { enabled ->
            highlightRegion = enabled
        }
    }

    LaunchedEffect(Unit) {
        appPreferences.highlightSameNumbers.collect { enabled ->
            highlightSameNumbers = enabled
        }
    }

    LaunchedEffect(Unit) {
        gameStateManager.getSavedGameState().collect { savedGame ->
            hasSavedGame = savedGame != null
        }
    }

    LaunchedEffect(Unit) {
        appPreferences.soundAndVibration.collect { enabled ->
            soundAndVibration = enabled
        }
    }
    //endregion

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.SETTINGS,
            AnalyticsConstants.SETTINGS_SCREEN
        )
    }

    if (showStatistics) {
        val statisticsViewModel =
            remember { StatisticsViewModel(context) }
        StatisticsScreen(
            modifier = modifier,
            viewModel = statisticsViewModel,
            onBackClick = { showStatistics = false }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = 12.dp
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

                // region Statistics
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
                            .padding(12.dp),
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                //endregion

                Spacer(Modifier.height(16.dp))

                //region Gameplay settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GAMEPLAY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.width(12.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "123",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                lineHeight = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Hide used numbers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hide a number once all 9 of its cells are filled.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = hideUsedNumbers,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                val disabledFreePlay =
                                    appPreferences.setHideUsedNumbers(enabled)
                                AnalyticsUtils.logSettingsOptionChanged(
                                    context,
                                    AnalyticsConstants.HIDE_USED_NUMBERS,
                                    enabled
                                )
                                if (disabledFreePlay) {
                                    AnalyticsUtils.logSettingsOptionChanged(
                                        context,
                                        AnalyticsConstants.FREE_PLAY,
                                        false
                                    )
                                    Toast.makeText(
                                        context,
                                        "Free Play is now off because Hide used numbers is enabled.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                // These visual options are saved independently and apply to the Sudoku board.
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                        Icon(
                            imageVector = Icons.Default.LooksOne,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Highlight same numbers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "When you select a number, matching numbers are highlighted across the board.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = highlightSameNumbers,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                appPreferences.setHighlightSameNumbers(enabled)
                                AnalyticsUtils.logSettingsOptionChanged(
                                    context,
                                    AnalyticsConstants.HIGHLIGHT_SAME_NUMBERS,
                                    enabled
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Highlight the region",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Highlight the selected cell’s row, column, and 3×3 block.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = highlightRegion,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                appPreferences.setHighlightRegion(enabled)
                                AnalyticsUtils.logSettingsOptionChanged(
                                    context,
                                    AnalyticsConstants.HIGHLIGHT_REGION,
                                    enabled
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Free Play",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "No Mistakes. No Points. Not for Streak Games.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = freePlay,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                // Prevent gameplay rules from changing during a saved game.
                                if (gameStateManager.getSavedGameState().first() != null
                                ) {
                                    hasSavedGame = true
                                    Toast.makeText(
                                        context,
                                        "Finish your current game or start a new one to change gameplay options.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                                val disabledHideUsedNumbers =
                                    appPreferences.setFreePlay(enabled)
                                AnalyticsUtils.logSettingsOptionChanged(
                                    context,
                                    AnalyticsConstants.FREE_PLAY,
                                    enabled
                                )
                                if (disabledHideUsedNumbers) {
                                    AnalyticsUtils.logSettingsOptionChanged(
                                        context,
                                        AnalyticsConstants.HIDE_USED_NUMBERS,
                                        false
                                    )
                                    Toast.makeText(
                                        context,
                                        "Hide used numbers is now off because Free Play is enabled.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Sound & Vibration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Sound effects and haptic feedback",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = soundAndVibration,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                appPreferences.setSoundAndVibration(enabled)
                                AnalyticsUtils.logSettingsOptionChanged(
                                    context,
                                    AnalyticsConstants.SOUND_VIBRATIONS,
                                    enabled
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                //endregion

                Spacer(Modifier.height(16.dp))

                //region Appearance settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "APPEARANCE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.width(12.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
                            val themeName = if (enabled) "dark_theme" else "light_theme"
                            AnalyticsUtils.logThemeToggled(context, themeName)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                //endregion

                Spacer(Modifier.height(12.dp))

                //region App information
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    )
                    Text(
                        text = stringResource(R.string.lbl_puzzle),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "Version ${BuildConfig.VERSION_NAME}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
                //endregion
            } // end inner Column

        }
    }
}