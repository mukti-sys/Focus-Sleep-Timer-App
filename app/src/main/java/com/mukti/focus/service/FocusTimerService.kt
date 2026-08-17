package com.mukti.focus.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mukti.focus.data.model.FocusState
import com.mukti.focus.data.preferences.FocusPreferences
import com.mukti.focus.media.MediaControllerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Silent Foreground Service managing background countdown timers without obtrusive screens.
 */
class FocusTimerService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var preferences: FocusPreferences
    private lateinit var notificationManager: FocusNotificationManager
    private lateinit var mediaController: MediaControllerHelper
    private var countdownJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        preferences = FocusPreferences(applicationContext)
        notificationManager = FocusNotificationManager(applicationContext)
        mediaController = MediaControllerHelper(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        serviceScope.launch {
            val currentState = preferences.focusStateFlow.first()

            when (action) {
                FocusNotificationManager.ACTION_TURN_OFF -> {
                    preferences.setFocusEnabled(false)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    return@launch
                }
                FocusNotificationManager.ACTION_RESUME_NOW -> {
                    preferences.endBreak()
                    onBreakExpired(currentState)
                }
            }

            // Start or update foreground notification
            startForeground(
                FocusNotificationManager.NOTIFICATION_ID,
                notificationManager.buildNotification(currentState)
            )

            // Listen for state updates
            observeFocusState()
        }

        return START_STICKY
    }

    private fun observeFocusState() {
        countdownJob?.cancel()
        countdownJob = serviceScope.launch {
            preferences.focusStateFlow.collect { state ->
                if (!state.isFocusEnabled) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    return@collect
                }

                notificationManager.updateNotification(state)

                if (state.isOnBreak) {
                    val remainingSeconds = state.remainingBreakSeconds
                    if (remainingSeconds <= 0L && state.breakEndTimestampMs > 0L) {
                        preferences.endBreak()
                        onBreakExpired(state)
                    } else {
                        // Silent tick delay
                        delay(1000L)
                        if (isActive && state.remainingBreakSeconds <= 0L) {
                            preferences.endBreak()
                            onBreakExpired(state)
                        }
                    }
                }
            }
        }
    }

    private fun onBreakExpired(state: FocusState) {
        // 1. Pause any active media (YouTube, Spotify, etc.)
        if (state.pauseMediaOnExpiry) {
            mediaController.pauseMediaPlayback()
        }

        // 2. Trigger auto screen-off and exit via Accessibility Service
        if (state.turnOffScreenOnExpiry) {
            FocusAccessibilityService.instance?.lockScreenAndExit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownJob?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
