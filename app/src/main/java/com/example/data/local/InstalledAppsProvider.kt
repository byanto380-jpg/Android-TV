package com.example.data.local

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.model.InstalledAppInfo

class InstalledAppsProvider(private val context: Context) {

    fun loadInstalledApps(): List<InstalledAppInfo> {
        val pm = context.packageManager
        val selfPkg = context.packageName
        val appMap = linkedMapOf<String, InstalledAppInfo>()

        val leanbackIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val standardIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        queryIntent(pm, leanbackIntent).forEach { resolveInfo ->
            val pkg = resolveInfo.activityInfo?.packageName ?: return@forEach
            if (pkg != selfPkg) {
                appMap[pkg] = InstalledAppInfo(
                    packageName = pkg,
                    label = resolveInfo.loadLabel(pm).toString(),
                    isTvLeanback = true,
                    icon = runCatching { resolveInfo.loadIcon(pm) }.getOrNull()
                )
            }
        }

        queryIntent(pm, standardIntent).forEach { resolveInfo ->
            val pkg = resolveInfo.activityInfo?.packageName ?: return@forEach
            if (pkg != selfPkg && !appMap.containsKey(pkg)) {
                appMap[pkg] = InstalledAppInfo(
                    packageName = pkg,
                    label = resolveInfo.loadLabel(pm).toString(),
                    isTvLeanback = false,
                    icon = runCatching { resolveInfo.loadIcon(pm) }.getOrNull()
                )
            }
        }

        return appMap.values.sortedWith(
            compareByDescending<InstalledAppInfo> { it.isTvLeanback }
                .thenBy { it.label.lowercase() }
        )
    }

    fun getLaunchIntent(packageName: String): Intent? {
        val pm = context.packageManager
        return pm.getLeanbackLaunchIntentForPackage(packageName)
            ?: pm.getLaunchIntentForPackage(packageName)
    }

    @Suppress("DEPRECATION")
    private fun queryIntent(pm: PackageManager, intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            pm.queryIntentActivities(intent, 0)
        }
}
