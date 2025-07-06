package com.prography.data.datasource.local


import com.prography.domain.model.UiScreenshotModel
import kotlinx.coroutines.flow.Flow

interface ScreenshotLocalDataSource {
    suspend fun getScreenshots(): Flow<List<UiScreenshotModel>>
    suspend fun insert(screenshot: UiScreenshotModel)
    suspend fun update(screenshot: UiScreenshotModel)
    suspend fun delete(screenshot: UiScreenshotModel)
    suspend fun deleteById(screenshotId: String)
}
