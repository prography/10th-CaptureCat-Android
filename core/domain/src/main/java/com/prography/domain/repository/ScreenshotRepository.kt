package com.prography.domain.repository

import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel
import kotlinx.coroutines.flow.Flow

interface ScreenshotRepository {

    suspend fun getLocalScreenshots(): Flow<List<UiScreenshotModel>>
    suspend fun getScreenshots(hasTags: Boolean? = null): Flow<List<UiScreenshotModel>>
    suspend fun getScreenshots(
        page: Int,
        pageSize: Int,
        hasTags: Boolean? = null
    ): List<UiScreenshotModel>
    suspend fun getScreenshotById(screenshotId: String): UiScreenshotModel?
    suspend fun insert(screenshot: UiScreenshotModel)
    suspend fun bulkInsert(screenshots: List<UiScreenshotModel>)
    suspend fun update(screenshot: UiScreenshotModel)
    suspend fun delete(screenshot: UiScreenshotModel)
    suspend fun deleteAll()
    suspend fun deleteScreenshot(screenshotId: String)
    suspend fun deleteTag(imageId: String, tagName: String)
    suspend fun addTagsToScreenshot(screenshotId: String, tagNames: List<String>)
    suspend fun toggleBookmark(screenshotId: String, isBookmarked: Boolean)
    suspend fun getFavoriteImages(page: Int = 0, size: Int = 10): Result<List<UiScreenshotModel>>
    suspend fun getMostUsedTags(size: Int): List<TagWithCount>
    suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int = 0,
        size: Int = 20
    ): List<UiScreenshotModel>

    suspend fun getRelatedTags(tagNames: List<String>, page: Int = 0, size: Int = 10): List<String>
}
