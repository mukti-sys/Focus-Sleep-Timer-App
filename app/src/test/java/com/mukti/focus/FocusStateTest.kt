package com.mukti.focus

import com.mukti.focus.data.model.FocusState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusStateTest {

    @Test
    fun testBreakActiveCalculation() {
        val now = System.currentTimeMillis()

        // Future expiration -> active
        val activeState = FocusState(
            isFocusEnabled = true,
            isOnBreak = true,
            breakEndTimestampMs = now + 60_000L
        )
        assertTrue(activeState.isBreakActive)
        assertTrue(activeState.remainingBreakSeconds > 0)

        // Past expiration -> inactive
        val expiredState = FocusState(
            isFocusEnabled = true,
            isOnBreak = true,
            breakEndTimestampMs = now - 1000L
        )
        assertFalse(expiredState.isBreakActive)
        assertEquals(0L, expiredState.remainingBreakSeconds)
    }

    @Test
    fun testDistractingAppSelection() {
        val state = FocusState(
            distractingPackages = setOf("com.google.android.youtube", "com.mxtech.videoplayer.ad")
        )
        assertTrue(state.distractingPackages.contains("com.google.android.youtube"))
        assertTrue(state.distractingPackages.contains("com.mxtech.videoplayer.ad"))
        assertFalse(state.distractingPackages.contains("com.whatsapp"))
    }
}
