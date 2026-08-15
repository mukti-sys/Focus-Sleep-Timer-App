package com.mukti.focus.data.model

import android.graphics.drawable.Drawable

/**
 * Domain model representing an installed application.
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isDistracting: Boolean = false,
    val isSystemApp: Boolean = false
)
