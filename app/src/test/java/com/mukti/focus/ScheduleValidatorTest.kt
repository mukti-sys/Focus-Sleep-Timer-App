package com.mukti.focus

import com.mukti.focus.data.model.FocusSchedule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleValidatorTest {

    @Test
    fun testSameDayScheduleActive() {
        val schedule = FocusSchedule(
            isEnabled = true,
            startHour = 9,
            startMinute = 0,
            endHour = 17,
            endMinute = 0,
            activeDays = setOf(1, 2, 3, 4, 5) // Mon to Fri
        )

        // Monday 12:30 -> Active
        assertTrue(schedule.isScheduleActiveAt(dayOfWeek = 1, hour = 12, minute = 30))

        // Monday 8:59 -> Inactive
        assertFalse(schedule.isScheduleActiveAt(dayOfWeek = 1, hour = 8, minute = 59))

        // Saturday 12:30 -> Inactive (not in activeDays)
        assertFalse(schedule.isScheduleActiveAt(dayOfWeek = 6, hour = 12, minute = 30))
    }

    @Test
    fun testOvernightBedtimeScheduleCrossesMidnight() {
        // Bedtime schedule: 11:00 PM (23:00) to 7:00 AM (07:00)
        val bedtimeSchedule = FocusSchedule(
            isEnabled = true,
            startHour = 23,
            startMinute = 0,
            endHour = 7,
            endMinute = 0,
            activeDays = setOf(1, 2, 3, 4, 5, 6, 7)
        )

        // 23:30 (Night) -> Active
        assertTrue(bedtimeSchedule.isScheduleActiveAt(dayOfWeek = 1, hour = 23, minute = 30))

        // 02:15 (Midnight / Early morning) -> Active
        assertTrue(bedtimeSchedule.isScheduleActiveAt(dayOfWeek = 2, hour = 2, minute = 15))

        // 06:59 -> Active
        assertTrue(bedtimeSchedule.isScheduleActiveAt(dayOfWeek = 2, hour = 6, minute = 59))

        // 07:01 -> Inactive
        assertFalse(bedtimeSchedule.isScheduleActiveAt(dayOfWeek = 2, hour = 7, minute = 1))

        // 14:00 (Afternoon) -> Inactive
        assertFalse(bedtimeSchedule.isScheduleActiveAt(dayOfWeek = 3, hour = 14, minute = 0))
    }

    @Test
    fun testDisabledScheduleIsNeverActive() {
        val disabledSchedule = FocusSchedule(
            isEnabled = false,
            startHour = 23,
            startMinute = 0,
            endHour = 7,
            endMinute = 0,
            activeDays = setOf(1, 2, 3, 4, 5, 6, 7)
        )
        assertFalse(disabledSchedule.isScheduleActiveAt(dayOfWeek = 1, hour = 23, minute = 30))
    }
}
