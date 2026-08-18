package com.mukti.focus.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.mukti.focus.MainActivity
import com.mukti.focus.data.preferences.FocusPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Accessibility service that intercepts attempts to open paused distracting apps
 * and provides system-level actions like auto screen-lock and navigating to home.
 */
class FocusAccessibilityService : AccessibilityService() {

    companion object {
        var instance: FocusAccessibilityService? = null
            private set
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var preferences: FocusPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        preferences = FocusPreferences(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return

        serviceScope.launch {
            val state = preferences.focusStateFlow.first()

            // If focus is enabled, distracting app opened, and NOT currently on break -> block & exit
            if (state.isFocusEnabled && !state.isBreakActive && state.distractingPackages.contains(packageName)) {
                // Navigate to Home screen
                performGlobalAction(GLOBAL_ACTION_HOME)

                // Launch main focus screen with break option
                val promptIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    action = FocusNotificationManager.ACTION_TAKE_A_BREAK
                }
                startActivity(promptIntent)
            }
        }
    }

    override fun onInterrupt() {}

    /**
     * Closes the active app and locks the screen / turns off the display.
     */
    fun lockScreenAndExit() {
        // 1. Send app to home
        performGlobalAction(GLOBAL_ACTION_HOME)

        // 2. Turn off display & lock device (Available on Android 9 / API 28+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }
}
