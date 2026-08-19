package com.mukti.focus.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mukti.focus.data.model.BreakDuration
import com.mukti.focus.ui.theme.TerracottaPrimaryLight

@Composable
fun TakeABreakDialog(
    onDismiss: () -> Unit,
    onSelectDuration: (BreakDuration) -> Unit
) {
    var showCustomSlider by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableFloatStateOf(45f) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                // Header (Matching Screenshot 2)
                Text(
                    text = "Take a break for",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color(0xFF202124),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )

                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                if (!showCustomSlider) {
                    // Standard and Extended Preset Options (5m, 15m, 30m, 1h, 1:30h, 2h)
                    BreakDuration.standardPresets.forEach { preset ->
                        BreakOptionItem(
                            label = preset.label,
                            onClick = {
                                onSelectDuration(preset)
                                onDismiss()
                            }
                        )
                        HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    }

                    // Option for Custom Time
                    BreakOptionItem(
                        label = "Custom time...",
                        onClick = { showCustomSlider = true }
                    )
                } else {
                    // Custom Duration Slider
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                        val minutesInt = customMinutes.toInt()
                        val durationLabel = if (minutesInt < 60) {
                            "$minutesInt minutes"
                        } else {
                            val h = minutesInt / 60
                            val m = minutesInt % 60
                            if (m == 0) "$h hour${if (h > 1) "s" else ""}" else "$h hr $m min"
                        }

                        Text(
                            text = "Duration: $durationLabel",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF202124)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Slider(
                            value = customMinutes,
                            onValueChange = { customMinutes = it },
                            valueRange = 5f..180f,
                            steps = 34, // 5 min increments
                            colors = SliderDefaults.colors(
                                thumbColor = TerracottaPrimaryLight,
                                activeTrackColor = TerracottaPrimaryLight
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { showCustomSlider = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back", color = Color(0xFF5F6368))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    onSelectDuration(BreakDuration.Custom(customMinutes.toInt()))
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start", color = TerracottaPrimaryLight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakOptionItem(
    label: String,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
        color = Color(0xFF202124),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp)
    )
}
