package com.mukti.focus.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mukti.focus.data.model.FocusSchedule
import com.mukti.focus.data.model.FocusState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focus_settings")

/**
 * DataStore preference manager for persisting focus state, schedules, and selected distracting apps.
 */
class FocusPreferences(private val context: Context) {

    companion object {
        val KEY_FOCUS_ENABLED = booleanPreferencesKey("is_focus_enabled")
        val KEY_ON_BREAK = booleanPreferencesKey("is_on_break")
        val KEY_BREAK_END_TIMESTAMP = longPreferencesKey("break_end_timestamp")
        val KEY_BREAK_DURATION_MINUTES = intPreferencesKey("break_duration_minutes")
        val KEY_DISTRACTING_PACKAGES = stringSetPreferencesKey("distracting_packages")
        val KEY_AUTO_LOCK_SCREEN = booleanPreferencesKey("auto_lock_screen")
        val KEY_PAUSE_MEDIA = booleanPreferencesKey("pause_media")

        // Schedule preferences
        val KEY_SCHEDULE_ENABLED = booleanPreferencesKey("schedule_enabled")
        val KEY_SCHEDULE_START_HOUR = intPreferencesKey("schedule_start_hour")
        val KEY_SCHEDULE_START_MINUTE = intPreferencesKey("schedule_start_minute")
        val KEY_SCHEDULE_END_HOUR = intPreferencesKey("schedule_end_hour")
        val KEY_SCHEDULE_END_MINUTE = intPreferencesKey("schedule_end_minute")
        val KEY_SCHEDULE_DAYS = stringSetPreferencesKey("schedule_days")
    }

    val focusStateFlow: Flow<FocusState> = context.dataStore.data.map { prefs ->
        FocusState(
            isFocusEnabled = prefs[KEY_FOCUS_ENABLED] ?: false,
            isOnBreak = prefs[KEY_ON_BREAK] ?: false,
            breakEndTimestampMs = prefs[KEY_BREAK_END_TIMESTAMP] ?: 0L,
            breakDurationMinutes = prefs[KEY_BREAK_DURATION_MINUTES] ?: 0,
            distractingPackages = prefs[KEY_DISTRACTING_PACKAGES] ?: emptySet(),
            turnOffScreenOnExpiry = prefs[KEY_AUTO_LOCK_SCREEN] ?: true,
            pauseMediaOnExpiry = prefs[KEY_PAUSE_MEDIA] ?: true
        )
    }

    val scheduleFlow: Flow<FocusSchedule> = context.dataStore.data.map { prefs ->
        val daysStrings = prefs[KEY_SCHEDULE_DAYS] ?: setOf("1", "2", "3", "4", "5", "6", "7")
        val activeDays = daysStrings.mapNotNull { it.toIntOrNull() }.toSet()
        FocusSchedule(
            isEnabled = prefs[KEY_SCHEDULE_ENABLED] ?: false,
            startHour = prefs[KEY_SCHEDULE_START_HOUR] ?: 23,
            startMinute = prefs[KEY_SCHEDULE_START_MINUTE] ?: 0,
            endHour = prefs[KEY_SCHEDULE_END_HOUR] ?: 7,
            endMinute = prefs[KEY_SCHEDULE_END_MINUTE] ?: 0,
            activeDays = activeDays
        )
    }

    suspend fun setFocusEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FOCUS_ENABLED] = enabled
            if (!enabled) {
                prefs[KEY_ON_BREAK] = false
                prefs[KEY_BREAK_END_TIMESTAMP] = 0L
            }
        }
    }

    suspend fun startBreak(durationMinutes: Int) {
        val endTimestamp = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        context.dataStore.edit { prefs ->
            prefs[KEY_ON_BREAK] = true
            prefs[KEY_BREAK_END_TIMESTAMP] = endTimestamp
            prefs[KEY_BREAK_DURATION_MINUTES] = durationMinutes
        }
    }

    suspend fun endBreak() {
        context.dataStore.edit { prefs ->
            prefs[KEY_ON_BREAK] = false
            prefs[KEY_BREAK_END_TIMESTAMP] = 0L
            prefs[KEY_BREAK_DURATION_MINUTES] = 0
        }
    }

    suspend fun toggleDistractingApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_DISTRACTING_PACKAGES]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[KEY_DISTRACTING_PACKAGES] = current
        }
    }

    suspend fun updateSchedule(schedule: FocusSchedule) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SCHEDULE_ENABLED] = schedule.isEnabled
            prefs[KEY_SCHEDULE_START_HOUR] = schedule.startHour
            prefs[KEY_SCHEDULE_START_MINUTE] = schedule.startMinute
            prefs[KEY_SCHEDULE_END_HOUR] = schedule.endHour
            prefs[KEY_SCHEDULE_END_MINUTE] = schedule.endMinute
            prefs[KEY_SCHEDULE_DAYS] = schedule.activeDays.map { it.toString() }.toSet()
        }
    }
}
