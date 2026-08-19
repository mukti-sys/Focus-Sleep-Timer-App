package com.mukti.focus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukti.focus.R
import com.mukti.focus.data.model.AppInfo
import com.mukti.focus.data.model.BreakDuration
import com.mukti.focus.data.model.FocusSchedule
import com.mukti.focus.data.model.FocusState
import com.mukti.focus.ui.components.AppItemRow
import com.mukti.focus.ui.components.PrimaryActionButton
import com.mukti.focus.ui.components.SecondaryActionButton
import com.mukti.focus.ui.dialogs.ScheduleDialog
import com.mukti.focus.ui.dialogs.TakeABreakDialog
import com.mukti.focus.ui.theme.TerracottaPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusMainScreen(
    focusState: FocusState,
    schedule: FocusSchedule,
    installedApps: List<AppInfo>,
    onToggleFocus: (Boolean) -> Unit,
    onStartBreak: (BreakDuration) -> Unit,
    onToggleApp: (String) -> Unit,
    onSaveSchedule: (FocusSchedule) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var showBreakDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }

    val distractingApps = installedApps.filter { it.isDistracting }
    val otherApps = installedApps.filter { !it.isDistracting }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    // Title "Focus" (Matching Screenshot 3)
                    Text(
                        text = stringResource(R.string.focus_title),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtitle (Matching Screenshot 3)
                    Text(
                        text = stringResource(R.string.focus_subtitle),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // "+ Set a schedule" Row (Matching Screenshot 3)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showScheduleDialog = true }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = "Set a schedule",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons (Matching Screenshot 3)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PrimaryActionButton(
                            text = if (focusState.isFocusEnabled) {
                                stringResource(R.string.turn_off_now)
                            } else {
                                stringResource(R.string.turn_on_now)
                            },
                            onClick = { onToggleFocus(!focusState.isFocusEnabled) }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        SecondaryActionButton(
                            text = stringResource(R.string.take_a_break),
                            onClick = { showBreakDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Section: Your distracting apps (Matching Screenshot 3)
            if (distractingApps.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.your_distracting_apps),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TerracottaPrimaryLight,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                items(distractingApps, key = { it.packageName }) { app ->
                    AppItemRow(
                        app = app,
                        onToggle = onToggleApp
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Section: Select more apps (Matching Screenshot 3)
            item {
                Text(
                    text = stringResource(R.string.select_more_apps),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = TerracottaPrimaryLight,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            items(otherApps, key = { it.packageName }) { app ->
                AppItemRow(
                    app = app,
                    onToggle = onToggleApp
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showBreakDialog) {
        TakeABreakDialog(
            onDismiss = { showBreakDialog = false },
            onSelectDuration = { duration ->
                onStartBreak(duration)
            }
        )
    }

    if (showScheduleDialog) {
        ScheduleDialog(
            initialSchedule = schedule,
            onDismiss = { showScheduleDialog = false },
            onSaveSchedule = onSaveSchedule
        )
    }
}
