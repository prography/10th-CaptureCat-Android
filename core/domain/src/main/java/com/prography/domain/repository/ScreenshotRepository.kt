package com.prography.domain.repository

import com.prography.domain.model.UiScreenshotModel
import kotlinx.coroutines.flow.Flow

interface ScreenshotRepository {
    suspend fun getScreenshots(): Flow<List<UiScreenshotModel>>
    suspend fun getScreenshotById(screenshotId: String): UiScreenshotModel?
    suspend fun insert(screenshot: UiScreenshotModel)
    suspend fun bulkInsert(screenshots: List<UiScreenshotModel>)
    suspend fun update(screenshot: UiScreenshotModel)
    suspend fun delete(screenshot: UiScreenshotModel)
    suspend fun deleteScreenshot(screenshotId: String)
    suspend fun deleteTag(imageId: String, tagName: String)
}
