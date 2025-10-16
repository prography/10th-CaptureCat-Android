package com.prography.data.datasource.local

import com.prography.domain.model.TagModel
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel
import kotlinx.coroutines.flow.Flow

interface ScreenshotLocalDataSource {
    suspend fun getScreenshots(hasTags: Boolean? = null): Flow<List<UiScreenshotModel>>
    suspend fun getById(screenshotId: String): UiScreenshotModel?
    suspend fun insert(screenshot: UiScreenshotModel)
    suspend fun update(screenshot: UiScreenshotModel)
    suspend fun delete(screenshot: UiScreenshotModel)
    suspend fun deleteAll()
    suspend fun deleteById(screenshotId: String)
    suspend fun deleteTag(imageId: String, tagName: String)
    suspend fun addTagsToScreenshot(screenshotId: String, tagNames: List<String>)
    suspend fun getMostUsedTags(size: Int): List<TagWithCount>
    suspend fun getFavoriteTags(size: Int): List<TagWithCount>
    suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int = 0,
        size: Int = 20
    ): List<UiScreenshotModel>

    suspend fun getRelatedTags(tagNames: List<String>, page: Int = 0, size: Int = 10): List<String>
    suspend fun getSearchAutoComplete(keyword: String, size: Int = 10): List<TagModel>

    suspend fun getBookmarkedPagedFiltered(
        tagId: Int,
        page: Int,
        size: Int
    ): List<UiScreenshotModel>

    // 태그 삭제 관련 메소드들
    suspend fun deleteTag(tagId: Int): Result<Unit>
    suspend fun deleteTags(tagIds: List<Int>): Result<Unit>
    suspend fun deleteAllTags(): Result<Unit>
}
