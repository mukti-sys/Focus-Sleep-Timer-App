package com.mukti.focus.data.model

/**
 * State representing current Focus and Sleep Timer status.
 */
data class FocusState(
    val isFocusEnabled: Boolean = false,
    val isOnBreak: Boolean = false,
    val breakEndTimestampMs: Long = 0L,
    val breakDurationMinutes: Int = 0,
    val distractingPackages: Set<String> = emptySet(),
    val turnOffScreenOnExpiry: Boolean = true,
    val pauseMediaOnExpiry: Boolean = true
) {
    val isBreakActive: Boolean
        get() = isOnBreak && System.currentTimeMillis() < breakEndTimestampMs

    val remainingBreakSeconds: Long
        get() {
            if (!isBreakActive) return 0L
            val diffMs = breakEndTimestampMs - System.currentTimeMillis()
            return if (diffMs > 0) diffMs / 1000L else 0L
        }

    val formattedResumeTime: String
        get() {
            if (breakEndTimestampMs <= 0L) return ""
            val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
            return formatter.format(java.util.Date(breakEndTimestampMs)).lowercase(java.util.Locale.getDefault())
        }
}
