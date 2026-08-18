package com.mukti.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mukti.focus.data.preferences.FocusPreferences
import com.mukti.focus.service.FocusTimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Receiver to activate scheduled bedtime / focus intervals automatically.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferences = FocusPreferences(context)
                val schedule = preferences.scheduleFlow.first()

                if (schedule.isEnabled) {
                    val calendar = Calendar.getInstance()
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sun ... 7 = Sat
                    // Map Java Calendar day to 1=Mon ... 7=Sun format
                    val normalizedDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    if (schedule.isScheduleActiveAt(normalizedDay, hour, minute)) {
                        preferences.setFocusEnabled(true)
                        val serviceIntent = Intent(context, FocusTimerService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
