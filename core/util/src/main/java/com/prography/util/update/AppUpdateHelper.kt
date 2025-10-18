// AppUpdateHelper.kt
package com.prography.util.update

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

object AppUpdateHelper {
    suspend fun isUpdateAvailable(context: Context): Boolean {
        return try {
            val info = AppUpdateManagerFactory.create(context).appUpdateInfo.await()
            info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) ||
                            info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
        } catch (_: Exception) {
            false
        }
    }
}
