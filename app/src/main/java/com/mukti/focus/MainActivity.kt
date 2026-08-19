package com.mukti.focus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.mukti.focus.data.model.AppInfo
import com.mukti.focus.data.model.BreakDuration
import com.mukti.focus.data.model.FocusSchedule
import com.mukti.focus.data.model.FocusState
import com.mukti.focus.data.preferences.FocusPreferences
import com.mukti.focus.data.repository.AppInfoRepository
import com.mukti.focus.service.FocusTimerService
import com.mukti.focus.ui.screens.FocusMainScreen
import com.mukti.focus.ui.theme.TerraFocusTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferences: FocusPreferences
    private lateinit var appRepository: AppInfoRepository

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val installedApps = _installedApps.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = FocusPreferences(applicationContext)
        appRepository = AppInfoRepository(applicationContext)

        lifecycleScope.launch {
            preferences.focusStateFlow.collect { state ->
                val apps = appRepository.getInstalledApps(state.distractingPackages)
                _installedApps.value = apps
            }
        }

        setContent {
            TerraFocusTheme {
                val focusState by preferences.focusStateFlow.collectAsState(initial = FocusState())
                val schedule by preferences.scheduleFlow.collectAsState(initial = FocusSchedule())
                val apps by installedApps.collectAsState()

                FocusMainScreen(
                    focusState = focusState,
                    schedule = schedule,
                    installedApps = apps,
                    onToggleFocus = { enabled ->
                        lifecycleScope.launch {
                            preferences.setFocusEnabled(enabled)
                            if (enabled) {
                                startTimerService()
                            }
                        }
                    },
                    onStartBreak = { duration ->
                        lifecycleScope.launch {
                            preferences.startBreak(duration.minutes)
                            startTimerService()
                        }
                    },
                    onToggleApp = { pkg ->
                        lifecycleScope.launch {
                            preferences.toggleDistractingApp(pkg)
                        }
                    },
                    onSaveSchedule = { newSchedule ->
                        lifecycleScope.launch {
                            preferences.updateSchedule(newSchedule)
                        }
                    },
                    onBackClick = { finish() }
                )
            }
        }
    }

    private fun startTimerService() {
        val intent = Intent(this, FocusTimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
