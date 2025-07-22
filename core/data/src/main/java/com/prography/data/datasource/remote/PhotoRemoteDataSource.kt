package com.prography.data.datasource.remote

import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel

interface PhotoRemoteDataSource {
    suspend fun getScreenshots(page: Int = 0, size: Int = 20): Result<List<UiScreenshotModel>>
    suspend fun uploadScreenshots(screenshots: List<UiScreenshotModel>): Result<Unit>
    suspend fun getScreenshotById(screenshotId: String): Result<UiScreenshotModel>
    suspend fun deleteTag(imageId: String, tagName: String): Result<Unit>
    suspend fun deleteScreenshot(screenshotId: String): Result<Unit>
    suspend fun addBookmark(imageId: String): Result<Unit>
    suspend fun removeBookmark(imageId: String): Result<Unit>
    suspend fun getFavoriteImages(page: Int = 0, size: Int = 10): Result<List<UiScreenshotModel>>
    suspend fun addTagsToScreenshot(screenshotId: String, tagNames: List<String>): Result<Unit>
    suspend fun getMostUsedTags(size: Int): Result<List<TagWithCount>>
    suspend fun completeTutorial(): Result<Unit>
    suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int = 0,
        size: Int = 20
    ): Result<List<UiScreenshotModel>>

    suspend fun getRelatedTags(
        tagNames: List<String>,
        page: Int = 0,
        size: Int = 10
    ): Result<List<String>>
}