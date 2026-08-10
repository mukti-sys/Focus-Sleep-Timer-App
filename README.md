# 🌙 Terra Focus - Focus & Sleep Timer for Android

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.0-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> A modern, distraction-free **Focus Mode & Sleep Timer** for Android. Designed to match the native Google Digital Wellbeing experience while adding **extended break timers (1h, 1:30h, 2h)**, **silent background operation**, and **automatic screen-off & lock** for bedtime media listening.

---

## 🎯 The Problem & Purpose

Standard Android Focus Mode limits break intervals to only **5, 15, or 30 minutes** and does not automatically turn the display off or pause background playback when you fall asleep listening to audiobooks, podcasts, YouTube, or music.

**Terra Focus** solves this by providing:
1. **Extended Break Intervals**: Presets for `5 min`, `15 min`, `30 min`, `1 hour`, `1 hour 30 min`, `2 hours`, and custom timers.
2. **Bedtime Auto-Lock & Screen-Off**: When the timer finishes, it cleanly:
   - Pauses media playback (`KEYCODE_MEDIA_PAUSE`).
   - Navigates out of the app to the Home screen (`GLOBAL_ACTION_HOME`).
   - Turns off the screen and locks the device automatically (`GLOBAL_ACTION_LOCK_SCREEN`).
3. **Silent Background Engine**: No loud countdown clocks or distracting screens—runs silently in the background while you relax and sleep.
4. **Exact Google System Notification**:
   - **On Break**: *"You're on a break | Focus will resume at [time]"* + `[Resume now]` button.
   - **Focus Active**: *"Focus is on | Distracting apps are paused"* + `[Take a break]` `[Turn off now]` buttons.

---

## 🎨 Terra Focus Design System

Designed using the warm **Terra Focus** palette inspired by Material You:
- **Primary**: `#E27D60` (Warm Terracotta)
- **Secondary / Container**: `#F4D3C4` (Soft Peach)
- **Surface**: `#FAF9F6` (Off-white / Warm Gray)
- **Text & Accents**: `#2D2D2D` (Soft Charcoal)
- **Dark Mode**: `#1A1C1E` / `#2D2D2D`
- *Strictly zero purple or artificial neon tones.*

---

## 🏗️ Architecture & Modules

```
app/src/main/java/com/mukti/focus/
├── data/
│   ├── model/           # AppInfo, FocusState, BreakDuration, Schedule
│   ├── repository/      # AppInfoRepository (package manager scanner)
│   └── preferences/     # FocusPreferences (DataStore persistence)
├── service/
│   ├── FocusAccessibilityService.kt   # Window state observer & auto screen locker
│   ├── FocusTimerService.kt           # Silent background countdown service
│   └── FocusNotificationManager.kt    # System notification state machine
├── media/
│   └── MediaControllerHelper.kt       # Audio focus & media pause dispatcher
├── receiver/
│   ├── BootReceiver.kt                # Boot completion reschedule handler
│   └── AlarmReceiver.kt               # Bedtime schedule alarm triggers
├── ui/
│   ├── components/      # Action pills, App item rows, Search bars
│   ├── dialogs/         # TakeABreakDialog, ScheduleDialog
│   ├── screens/         # FocusMainScreen, PermissionSetupScreen
│   └── theme/           # Color, Theme, Type (Terra Focus M3)
└── MainActivity.kt      # Application root & permission controller
```

---

## 📱 Permissions Required

- **Accessibility Service (`BIND_ACCESSIBILITY_SERVICE`)**: For real-time window tracking, exiting paused apps, and locking screen upon timer expiration.
- **Post Notifications (`POST_NOTIFICATIONS`)**: For displaying the live resume timestamp notification.
- **Query All Packages (`QUERY_ALL_PACKAGES`)**: For selecting apps to restrict.
- **Schedule Exact Alarm (`SCHEDULE_EXACT_ALARM`)**: For exact bedtime schedules.

---

## 🧪 Testing

Comprehensive unit test suite covering:
- AM/PM resume time formatting and midnight rollover.
- Schedule validation and active day intervals.
- Focus session state transitions and package filtering.

Run tests via:
```bash
./gradlew test
```

---

## 📄 License
MIT License. Created by [mukti-sys](https://github.com/mukti-sys).
