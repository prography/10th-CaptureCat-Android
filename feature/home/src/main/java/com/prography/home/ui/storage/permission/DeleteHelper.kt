package com.prography.home.ui.storage.permission

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.prography.home.ui.storage.contract.ScreenshotItem
import timber.log.Timber

object DeleteHelper {
    fun deleteScreenshots(
        context: Context,
        screenshots: List<ScreenshotItem>,
        deleteLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        onDeleteCompleted: (List<String>) -> Unit
    ) {
        val uris = screenshots.map { it.uri }
        val deletedIds = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val pendingIntent = MediaStore.createDeleteRequest(context.contentResolver, uris)
                val request = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                deleteLauncher.launch(request)
            } catch (e: Exception) {
                Timber.e("Delete request failed", e)
            }
        } else {
            screenshots.forEach {
                try {
                    context.contentResolver.delete(it.uri, null, null)
                    deletedIds.add(it.id)
                } catch (e: Exception) {
                    Timber.e("Failed to delete ${it.uri}", e)
                }
            }
            onDeleteCompleted(deletedIds)
        }
    }
}