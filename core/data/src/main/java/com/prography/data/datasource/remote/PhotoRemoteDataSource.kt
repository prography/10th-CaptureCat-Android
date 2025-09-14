package com.prography.data.datasource.remote

import com.prography.domain.model.AutocompleteTagModel
import com.prography.domain.model.TagModel
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel

interface PhotoRemoteDataSource {
    suspend fun getScreenshots(
        hasTags: Boolean? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<List<UiScreenshotModel>>
    suspend fun uploadScreenshots(screenshots: List<UiScreenshotModel>): Result<Unit>
    suspend fun getScreenshotById(screenshotId: String): Result<UiScreenshotModel>
    suspend fun deleteTag(imageId: String, tagName: String): Result<Unit>
    suspend fun deleteScreenshot(screenshotId: String): Result<Unit>
    suspend fun addBookmark(imageId: String): Result<Unit>
    suspend fun removeBookmark(imageId: String): Result<Unit>
    suspend fun getFavoriteImages(page: Int = 0, size: Int = 10): Result<List<UiScreenshotModel>>
    suspend fun addTagsToScreenshot(
        screenshotId: String,
        tagNames: List<String>
    ): Result<List<TagModel>>
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

    suspend fun getSearchAutoComplete(
        keyword: String,
        size: Int = 10
    ): Result<List<TagModel>>

    // 태그 삭제 관련 메소드들
    suspend fun deleteTag(tagId: Int): Result<Unit>
    suspend fun deleteTags(tagIds: List<Int>): Result<Unit>
    suspend fun deleteAllTags(): Result<Unit>
}