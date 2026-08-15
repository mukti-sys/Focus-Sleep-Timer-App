package com.mukti.focus.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Break duration options including extended presets for sleep sessions.
 */
sealed class BreakDuration(val minutes: Int, val label: String) {
    object FiveMinutes : BreakDuration(5, "5 minutes")
    object FifteenMinutes : BreakDuration(15, "15 minutes")
    object ThirtyMinutes : BreakDuration(30, "30 minutes")
    object OneHour : BreakDuration(60, "1 hour")
    object OneHourThirtyMinutes : BreakDuration(90, "1 hour 30 minutes")
    object TwoHours : BreakDuration(120, "2 hours")
    class Custom(minutes: Int) : BreakDuration(
        minutes,
        if (minutes < 60) "$minutes minutes"
        else if (minutes % 60 == 0) "${minutes / 60} hour${if (minutes / 60 > 1) "s" else ""}"
        else "${minutes / 60}h ${minutes % 60}m"
    )

    companion object {
        val standardPresets: List<BreakDuration> = listOf(
            FiveMinutes,
            FifteenMinutes,
            ThirtyMinutes,
            OneHour,
            OneHourThirtyMinutes,
            TwoHours
        )

        /**
         * Calculates the formatted resume time string (e.g. "12:09 am") given a start timestamp and duration in minutes.
         */
        fun formatResumeTime(startTimestampMs: Long, durationMinutes: Int): String {
            val endTimestampMs = startTimestampMs + (durationMinutes * 60 * 1000L)
            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            return formatter.format(Date(endTimestampMs)).lowercase(Locale.getDefault())
        }
    }
}
