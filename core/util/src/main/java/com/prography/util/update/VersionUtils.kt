// VersionUtils.kt
package com.prography.util.update

import android.content.Context
import android.os.Build
import android.content.pm.PackageInfo

object VersionUtils {

    fun getInstalledVersionName(context: Context): String {
        return try {
            val pi = context.packageManager.getPackageInfo(context.packageName, 0)
            pi.versionName ?: "0.0.0"
        } catch (_: Exception) { "0.0.0" }
    }

    fun getInstalledVersionCode(context: Context): Long {
        return try {
            val pi: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pi.longVersionCode
            else @Suppress("DEPRECATION") pi.versionCode.toLong()
        } catch (_: Exception) { 0L }
    }
}
