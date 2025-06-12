package com.prography.navigation

import android.net.Uri

object OrganizeDataCache {

    data class ScreenshotData(
        val id: String,
        val uri: Uri,
        val fileName: String?
    )

    private var _screenshots: List<ScreenshotData> = emptyList()

    fun setScreenshots(screenshots: List<ScreenshotData>) {
        _screenshots = screenshots
    }

    fun getScreenshots(): List<ScreenshotData> {
        return _screenshots
    }

    fun clear() {
        _screenshots = emptyList()
    }
}