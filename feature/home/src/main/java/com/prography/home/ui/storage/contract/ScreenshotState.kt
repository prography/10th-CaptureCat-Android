package com.prography.home.ui.storage.contract

import android.net.Uri

data class ScreenshotState(
    val groupedScreenshots: Map<String, List<ScreenshotItem>> = emptyMap(),
    val isSelectionMode: Boolean = false,
    val selectedCount: Int = 0,
    val totalCount: Int = 0,
    val isAllSelected: Boolean = false,
    val showDeleteDialog: Boolean = false
)

data class ScreenshotItem(
    val id: String,
    val uri: Uri,
    val dateGroup: String,
    val isSelected: Boolean,
    val fileName: String? = null
)
