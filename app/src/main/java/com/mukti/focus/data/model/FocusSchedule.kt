package com.mukti.focus.data.model

import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Focus schedule configuration for automatic bedtime routines.
 */
data class FocusSchedule(
    val isEnabled: Boolean = false,
    val startHour: Int = 23,
    val startMinute: Int = 0,
    val endHour: Int = 7,
    val endMinute: Int = 0,
    val activeDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7) // 1 = Monday ... 7 = Sunday
) {
    /**
     * Checks if a given time and day of week falls within the active schedule.
     */
    fun isScheduleActiveAt(dayOfWeek: Int, hour: Int, minute: Int): Boolean {
        if (!isEnabled) return false
        if (!activeDays.contains(dayOfWeek)) return false

        val currentMinutes = hour * 60 + minute
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        return if (startMinutes <= endMinutes) {
            currentMinutes in startMinutes..endMinutes
        } else {
            // Crosses midnight (e.g. 23:00 to 07:00)
            currentMinutes >= startMinutes || currentMinutes <= endMinutes
        }
    }
}
