package com.quickmindgames.sudoku.presentation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LooksOne
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quickmindgames.sudoku.BuildConfig
import com.quickmindgames.sudoku.R
import com.quickmindgames.sudoku.presentation.viewmodel.SettingsViewModel
import com.quickmindgames.sudoku.utils.AnalyticsConstants
import com.quickmindgames.sudoku.utils.AnalyticsUtils
import kotlinx.coroutines.launch

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onStatisticsClick: () -> Unit) {
    //region State and preference collection
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = remember(context) { SettingsViewModel.factory(context) }
    )
    val settingsState by settingsViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    //endregion

    LaunchedEffect(Unit) {
        AnalyticsUtils.logScreenView(
            context,
            AnalyticsConstants.SETTINGS,
            AnalyticsConstants.SETTINGS_SCREEN
        )
    }

    //region Main Design
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp
            )
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Customise your Sudoku experience",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Column(
            modifier = Modifier.padding(top = 8.dp)
        ) {

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                Spacer(Modifier.height(8.dp))

                // region Statistics
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onStatisticsClick,
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
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
                                .size(56.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Statistics",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View your win rates, streaks & best times",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    lineHeight = 20.sp,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                //endregion

                Spacer(Modifier.height(16.dp))

                //region GamePlay Assistance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GAMEPLAY ASSISTANCE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.width(12.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                //region Hide Used Numbers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
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
                                imageVector = Icons.Rounded.VisibilityOff,
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
                            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.hideUsedNumbers,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                if (settingsViewModel.setHideUsedNumbers(enabled)) {
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
                //endregion

                // These visual options are saved independently and apply to the Sudoku board.
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                //region Highlight same numbers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
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
                            imageVector = Icons.Rounded.LooksOne,
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
                            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.highlightSameNumbers,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                settingsViewModel.setHighlightSameNumbers(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                //endregion

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                //region Highlight the region
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
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
                            imageVector = Icons.Rounded.GridView,
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
                            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.highlightRegion,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                settingsViewModel.setHighlightRegion(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                //endregion

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                //region Free Play
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
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
                            imageVector = Icons.Rounded.SportsEsports,
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
                            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.freePlay,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                val result = settingsViewModel.setFreePlay(enabled)
                                if (result.blockedBySavedGame) {
                                    Toast.makeText(
                                        context,
                                        "Finish your current game or start a new one to change gameplay options.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                                if (result.disabledHideUsedNumbers) {
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
                //endregion

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                //region Sound & Vibration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
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
                            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.soundAndVibration,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                settingsViewModel.setSoundAndVibration(enabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                //endregion

                //endregion

                Spacer(Modifier.height(16.dp))

                //region Appearance settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "APPEARANCE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
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
                        .padding(vertical = 14.dp),
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
                        Text(
                            text = if (settingsState.isDarkMode) "🌙" else "☀️",
                            fontSize = 24.sp
                        )
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
                            text = if (settingsState.isDarkMode) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = settingsState.isDarkMode,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch { settingsViewModel.setDarkMode(enabled) }
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
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Normal,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Text(
                        text = stringResource(R.string.lbl_puzzle),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "Version ${BuildConfig.VERSION_NAME}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                }
                //endregion
            }
        } // end inner Column

    }
    //endregion
}