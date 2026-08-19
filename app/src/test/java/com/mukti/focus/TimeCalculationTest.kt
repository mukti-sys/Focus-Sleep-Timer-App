package com.mukti.focus

import com.mukti.focus.data.model.BreakDuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class TimeCalculationTest {

    @Test
    fun testPresetDurationsMatchMinutes() {
        assertEquals(5, BreakDuration.FiveMinutes.minutes)
        assertEquals(15, BreakDuration.FifteenMinutes.minutes)
        assertEquals(30, BreakDuration.ThirtyMinutes.minutes)
        assertEquals(60, BreakDuration.OneHour.minutes)
        assertEquals(90, BreakDuration.OneHourThirtyMinutes.minutes)
        assertEquals(120, BreakDuration.TwoHours.minutes)
    }

    @Test
    fun testCustomDurationLabels() {
        val custom45 = BreakDuration.Custom(45)
        assertEquals("45 minutes", custom45.label)

        val custom60 = BreakDuration.Custom(60)
        assertEquals("1 hour", custom60.label)

        val custom90 = BreakDuration.Custom(90)
        assertEquals("1h 30m", custom90.label)

        val custom120 = BreakDuration.Custom(120)
        assertEquals("2 hours", custom120.label)
    }

    @Test
    fun testResumeTimeCalculationAccuracy() {
        // Set fixed timestamp: 12:03 AM
        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, 0) // 12 AM
            set(Calendar.MINUTE, 3)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startMs = calendar.timeInMillis

        // 6 minute break -> 12:09 am
        val resumeString = BreakDuration.formatResumeTime(startMs, 6)
        assertTrue(resumeString.contains("12:09") && resumeString.contains("am"))

        // 60 minute break -> 1:03 am
        val resumeString1h = BreakDuration.formatResumeTime(startMs, 60)
        assertTrue(resumeString1h.contains("1:03") && resumeString1h.contains("am"))

        // 90 minute break -> 1:33 am
        val resumeString90m = BreakDuration.formatResumeTime(startMs, 90)
        assertTrue(resumeString90m.contains("1:33") && resumeString90m.contains("am"))
    }
}
