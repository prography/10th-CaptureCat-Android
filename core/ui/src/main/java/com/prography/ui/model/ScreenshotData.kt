package com.prography.ui.model

import android.net.Uri

data class ScreenshotData(
    val id: String,
    val uri: Uri,
    val fileName: String?,
    val dateGroup: String
)