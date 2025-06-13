package com.prography.navigation

import android.net.Uri

object OrganizeDataCache {

    data class ScreenshotData(
        val id: String,
        val uri: Uri,
        val fileName: String?
    )

    private var _screenshots: List<ScreenshotData> = emptyList()
    private var _isCompleted: Boolean = false

    fun setScreenshots(screenshots: List<ScreenshotData>) {
        _screenshots = screenshots
        _isCompleted = false // 새 데이터 설정 시 완료 상태 초기화
    }

    fun getScreenshots(): List<ScreenshotData> {
        return _screenshots
    }

    fun setCompleted() {
        _isCompleted = true
    }

    fun isCompleted(): Boolean {
        return _isCompleted
    }

    fun clear() {
        _screenshots = emptyList()
        _isCompleted = false
    }
}
