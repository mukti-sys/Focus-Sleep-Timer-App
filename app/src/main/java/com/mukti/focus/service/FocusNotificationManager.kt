package com.mukti.focus.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mukti.focus.MainActivity
import com.mukti.focus.R
import com.mukti.focus.data.model.FocusState

/**
 * Manages system notifications matching the exact Google Digital Wellbeing / Focus Mode style:
 * State A: "Focus is on" | "Distracting apps are paused" | Actions: [Take a break] [Turn off now]
 * State B: "You're on a break" | "Focus will resume at 12:09 am" | Action: [Resume now]
 */
class FocusNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "focus_service_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_TAKE_A_BREAK = "com.mukti.focus.ACTION_TAKE_A_BREAK"
        const val ACTION_TURN_OFF = "com.mukti.focus.ACTION_TURN_OFF"
        const val ACTION_RESUME_NOW = "com.mukti.focus.ACTION_RESUME_NOW"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Mode Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active Focus Mode and Break status"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(focusState: FocusState): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)

        if (focusState.isBreakActive) {
            // State: On Break (Matching Screenshot 1)
            val resumeTime = focusState.formattedResumeTime
            val subtitle = if (resumeTime.isNotEmpty()) {
                "Focus will resume at $resumeTime"
            } else {
                "Focus will resume shortly"
            }

            val resumeNowIntent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_RESUME_NOW
            }
            val resumePendingIntent = PendingIntent.getService(
                context,
                1,
                resumeNowIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.setContentTitle(context.getString(R.string.youre_on_a_break))
                .setContentText(subtitle)
                .addAction(
                    0,
                    context.getString(R.string.resume_now),
                    resumePendingIntent
                )
        } else {
            // State: Focus is on (Matching Screenshot 4)
            val takeBreakIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_TAKE_A_BREAK
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val takeBreakPendingIntent = PendingIntent.getActivity(
                context,
                2,
                takeBreakIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val turnOffIntent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_TURN_OFF
            }
            val turnOffPendingIntent = PendingIntent.getService(
                context,
                3,
                turnOffIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.setContentTitle(context.getString(R.string.focus_is_on))
                .setContentText(context.getString(R.string.distracting_apps_paused))
                .addAction(
                    0,
                    context.getString(R.string.take_a_break),
                    takeBreakPendingIntent
                )
                .addAction(
                    0,
                    context.getString(R.string.turn_off_now),
                    turnOffPendingIntent
                )
        }

        return builder.build()
    }

    fun updateNotification(focusState: FocusState) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(focusState))
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
