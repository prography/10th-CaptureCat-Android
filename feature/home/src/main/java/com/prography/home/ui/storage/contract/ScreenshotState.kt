package com.prography.home.ui.storage.contract

import android.net.Uri

data class ScreenshotState(
    val groupedScreenshots: Map<String, List<ScreenshotItem>> = emptyMap(),
    val isSelectionMode: Boolean = false,
    val selectedCount: Int = 0,
    val totalCount: Int = 0,
    val isAllSelected: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val hasMoreData: Boolean = true,
    val currentPage: Int = 0,
    val pageSize: Int = 20,
    val selectedItems: Set<String> = emptySet(), // Paging3에서 선택된 아이템 ID 추적
    val organizedScreenshotIds: Set<String> = emptySet(), // 정리 완료된 스크린샷 ID 추적
    val refreshVersion: Long = 0L // UI 강제 새로고침 트리거
)

data class ScreenshotItem(
    val id: String,
    val uri: Uri,
    val dateGroup: String,
    val isSelected: Boolean,
    val fileName: String? = null,
    val isOrganized: Boolean = false // 정리하기 완료 여부
)
