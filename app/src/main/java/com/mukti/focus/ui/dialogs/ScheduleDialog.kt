package com.mukti.focus.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mukti.focus.data.model.FocusSchedule
import com.mukti.focus.ui.theme.SoftPeachSecondaryContainerLight
import com.mukti.focus.ui.theme.TerracottaPrimaryLight

@Composable
fun ScheduleDialog(
    initialSchedule: FocusSchedule,
    onDismiss: () -> Unit,
    onSaveSchedule: (FocusSchedule) -> Unit
) {
    var isEnabled by remember { mutableStateOf(initialSchedule.isEnabled) }
    var selectedDays by remember { mutableStateOf(initialSchedule.activeDays) }
    var startHour by remember { mutableStateOf(initialSchedule.startHour) }
    var startMinute by remember { mutableStateOf(initialSchedule.startMinute) }
    var endHour by remember { mutableStateOf(initialSchedule.endHour) }
    var endMinute by remember { mutableStateOf(initialSchedule.endMinute) }

    val daysLabels = listOf(
        1 to "M", 2 to "T", 3 to "W", 4 to "T", 5 to "F", 6 to "S", 7 to "S"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule Focus",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF202124),
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TerracottaPrimaryLight
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Repeat on",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF5F6368)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Days of week row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysLabels.forEach { (dayInt, label) ->
                        val isSelected = selectedDays.contains(dayInt)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) TerracottaPrimaryLight else Color(0xFFF1F3F4)
                                )
                                .clickable {
                                    selectedDays = if (isSelected) {
                                        selectedDays - dayInt
                                    } else {
                                        selectedDays + dayInt
                                    }
                                }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFF3C4043),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Start and End time summary
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start time", style = MaterialTheme.typography.labelMedium, color = Color(0xFF5F6368))
                        Text(
                            formatTime(startHour, startMinute),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End time", style = MaterialTheme.typography.labelMedium, color = Color(0xFF5F6368))
                        Text(
                            formatTime(endHour, endMinute),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF5F6368))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onSaveSchedule(
                                FocusSchedule(
                                    isEnabled = isEnabled,
                                    startHour = startHour,
                                    startMinute = startMinute,
                                    endHour = endHour,
                                    endMinute = endMinute,
                                    activeDays = selectedDays
                                )
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Save", color = TerracottaPrimaryLight, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val minStr = if (minute < 10) "0$minute" else "$minute"
    val amPm = if (isPm) "PM" else "AM"
    return "$displayHour:$minStr $amPm"
}
