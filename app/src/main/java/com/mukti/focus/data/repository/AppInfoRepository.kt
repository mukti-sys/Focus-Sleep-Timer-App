package com.mukti.focus.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.mukti.focus.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository to query installed applications and cache their metadata.
 */
class AppInfoRepository(private val context: Context) {

    suspend fun getInstalledApps(distractingPackages: Set<String>): List<AppInfo> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedApps = pm.queryIntentActivities(mainIntent, 0)
            val currentPackageName = context.packageName

            resolvedApps.mapNotNull { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                if (pkgName == currentPackageName) return@mapNotNull null

                val appName = resolveInfo.loadLabel(pm).toString()
                val icon = resolveInfo.loadIcon(pm)
                val isSystem = try {
                    val appInfo = pm.getApplicationInfo(pkgName, 0)
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                } catch (e: Exception) {
                    false
                }

                AppInfo(
                    packageName = pkgName,
                    appName = appName,
                    icon = icon,
                    isDistracting = distractingPackages.contains(pkgName),
                    isSystemApp = isSystem
                )
            }.sortedWith(
                compareByDescending<AppInfo> { it.isDistracting }
                    .thenBy { it.appName.lowercase() }
            )
        }
}
