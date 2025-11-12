package com.prography.data.repository

import com.prography.data.datasource.local.ScreenshotLocalDataSource
import com.prography.data.datasource.remote.PhotoRemoteDataSource
import com.prography.data.util.RepositoryModeExecutor
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.TagModel
import com.prography.domain.repository.ScreenshotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

class ScreenshotRepositoryImpl @Inject constructor(
    private val remoteDataSource: PhotoRemoteDataSource,
    private val localDataSource: ScreenshotLocalDataSource,
    private val userPrefs: UserPreferenceDataStore
) : ScreenshotRepository {

    private val modeExecutor = RepositoryModeExecutor(userPrefs)

    override suspend fun getLocalScreenshots(): Flow<List<UiScreenshotModel>> {
        return localDataSource.getScreenshots()
    }

    override suspend fun getScreenshots(hasTags: Boolean?): Flow<List<UiScreenshotModel>> {
        return modeExecutor.executeWithMode(
            localAction = { localDataSource.getScreenshots(hasTags) },
            remoteAction = {
            remoteDataSource.getScreenshots(hasTags).fold(
                    onSuccess = { screenshots ->
                        Timber.d("Remote success: ${screenshots.size} screenshots with hasTags=$hasTags")
                        flowOf(screenshots)
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "Remote failure")
                        throw exception
                    }
                )
            }
        )
    }

    override suspend fun getScreenshots(
        page: Int,
        pageSize: Int,
        hasTags: Boolean?
    ): List<UiScreenshotModel> {
        return modeExecutor.executeWithMode(
            localAction = {
                // 로컬 모드: 전체 데이터를 가져와서 페이징 처리
                val allScreenshots = localDataSource.getScreenshots(hasTags).first()
                val offset = page * pageSize
                val result = allScreenshots.drop(offset).take(pageSize)
                Timber.d("Local paged success: ${result.size} screenshots (page=$page, size=$pageSize, total=${allScreenshots.size})")
                result
            },
            remoteAction = {
                remoteDataSource.getScreenshots(page = page, size = pageSize).fold(
                    onSuccess = { screenshots -> screenshots },
                    onFailure = { exception ->
                        Timber.e(exception, "Remote failure")
                        throw exception
                    }
                )
            }
        )
    }

    override suspend fun insert(screenshot: UiScreenshotModel) {
        localDataSource.insert(screenshot)
    }

    override suspend fun bulkInsert(screenshots: List<UiScreenshotModel>) {
        Timber.d("BulkInsert - Mode: ${modeExecutor.getCurrentModeForLogging()}")

        modeExecutor.executeWithMode(
            localAction = {
                // 로컬 저장
                Timber.d("BulkInsert - Local mode: inserting ${screenshots.size} screenshots")
                screenshots.forEach { screenshot ->
                    localDataSource.insert(screenshot)
                }
            },
            remoteAction = {
                // 서버 업로드
                Timber.d("BulkInsert - Remote mode: uploading ${screenshots.size} screenshots")
                remoteDataSource.uploadScreenshots(screenshots).fold(
                    onSuccess = { Timber.d("BulkInsert - Remote upload success") },
                    onFailure = { exception ->
                        Timber.e(exception, "BulkInsert - Remote upload failure")
                        throw exception
                    }
                )
            }
        )
    }

    override suspend fun update(screenshot: UiScreenshotModel) {
        localDataSource.update(screenshot)
    }

    override suspend fun delete(screenshot: UiScreenshotModel) {
        localDataSource.delete(screenshot)
    }

    override suspend fun deleteAll() {
        localDataSource.deleteAll()
        localDataSource.deleteAllTags()
    }

    override suspend fun deleteScreenshot(screenshotId: String) {
        modeExecutor.executeWithModeAndCacheUpdateUnit(
            localAction = {
                // 로컬 모드: 로컬에서만 스크린샷 삭제
                Timber.d("DeleteScreenshot - Local mode: deleting screenshot $screenshotId")
                localDataSource.deleteById(screenshotId)
            },
            remoteAction = {
                // 서버 모드: 서버에서 스크린샷 삭제 시도
                Timber.d("DeleteScreenshot - Remote mode: deleting screenshot $screenshotId")
                remoteDataSource.deleteScreenshot(screenshotId)
            },
            onRemoteSuccess = {
                // 서버 삭제 성공 시 로컬에서도 삭제 (캐시 정리)
                Timber.d("DeleteScreenshot - Remote deletion success, clearing local cache")
                localDataSource.deleteById(screenshotId)
            }
        )
    }

    override suspend fun getScreenshotById(screenshotId: String): UiScreenshotModel? {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.getById(screenshotId) },
            remoteAction = { remoteDataSource.getScreenshotById(screenshotId) }
        )
    }

    override suspend fun deleteTag(imageId: String, tagName: String) {
        Timber.d("DeleteTag - Mode: ${modeExecutor.getCurrentModeForLogging()}")

        modeExecutor.executeWithMode(
            localAction = {
                // 로컬 모드: 로컬에서만 태그 삭제
                Timber.d("DeleteTag - Local mode: deleting tag '$tagName' from image $imageId")
                // 로컬에서는 UpdateScreenshotUseCase를 사용하는 것이 더 적합
                throw UnsupportedOperationException("Use UpdateScreenshotUseCase for local tag deletion")
            },
            remoteAction = {
                // 서버 모드: 서버에서 태그 삭제
                Timber.d("DeleteTag - Remote mode: deleting tag '$tagName' from image $imageId")
                remoteDataSource.deleteTag(imageId, tagName).fold(
                    onSuccess = { Timber.d("DeleteTag - Remote tag deletion success") },
                    onFailure = { exception ->
                        Timber.e(exception, "DeleteTag - Remote tag deletion failure")
                        throw exception
                    }
                )
            }
        )
    }

    override suspend fun addTagsToScreenshot(
        screenshotId: String,
        tagNames: List<String>
    ): Result<List<TagModel>> {
        return modeExecutor.executeWithMode(
            localAction = {
                Result.success(tagNames.map {
                    TagModel(
                        System.currentTimeMillis(),
                        it
                    )
                })
            },
            remoteAction = {
                // 서버 모드
                remoteDataSource.addTagsToScreenshot(screenshotId, tagNames)
            }
        )
    }

    override suspend fun toggleBookmark(screenshotId: String, isBookmarked: Boolean) {
        modeExecutor.executeWithModeAndCacheUpdateUnit(
            localAction = {
                // 로컬 모드: 로컬에서만 북마크 상태 업데이트
                Timber.d("ToggleBookmark - Local mode: updating bookmark for screenshot $screenshotId to $isBookmarked")
                val screenshot = localDataSource.getById(screenshotId)
                if (screenshot != null) {
                    val updatedScreenshot = screenshot.copy(isBookmarked = isBookmarked)
                    localDataSource.update(updatedScreenshot)
                }
            },
            remoteAction = {
                // 서버 모드: 서버에서 북마크 추가/삭제
                Timber.d("ToggleBookmark - Remote mode: ${if (isBookmarked) "adding" else "removing"} bookmark for screenshot $screenshotId")

                if (isBookmarked) {
                    remoteDataSource.addBookmark(screenshotId)
                } else {
                    remoteDataSource.removeBookmark(screenshotId)
                }
            },
            onRemoteSuccess = {
                // 서버 성공 시 로컬 캐시도 업데이트
                Timber.d("ToggleBookmark - Remote bookmark operation success, updating local cache")
                val screenshot = localDataSource.getById(screenshotId)
                if (screenshot != null) {
                    val updatedScreenshot = screenshot.copy(isBookmarked = isBookmarked)
                    localDataSource.update(updatedScreenshot)
                }
            }
        )
    }

    override suspend fun getFavoriteImages(page: Int, size: Int, tagId: Int): Result<List<UiScreenshotModel>> {
        return modeExecutor.executeWithMode(
            localAction = {
                try {
                    val paged = localDataSource.getBookmarkedPagedFiltered(tagId, page, size)
                    Timber.d("GetFavoriteImages - Local result: ${paged.size} items (page=$page, size=$size, tagId=$tagId)")
                    Result.success(paged)
                } catch (e: Exception) {
                    Timber.e(e, "GetFavoriteImages - Local mode failure")
                    Result.failure(e)
                }
            },
            remoteAction = {
                // 서버 모드는 기존 그대로
                remoteDataSource.getFavoriteImages(page, size, tagId)
            }
        )
    }


    override suspend fun getMostUsedTags(size: Int): List<TagWithCount> {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.getMostUsedTags(size) },
            remoteAction = { remoteDataSource.getMostUsedTags(size) }
        )
    }

    override suspend fun getFavoriteTags(size: Int): List<TagWithCount> {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.getFavoriteTags(size) },
            remoteAction = { remoteDataSource.getFavoriteTags(size) }
        )
    }

    override suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<UiScreenshotModel> {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.searchImagesByTags(tagNames, page, size) },
            remoteAction = { remoteDataSource.searchImagesByTags(tagNames, page, size) }
        )
    }

    override suspend fun getRelatedTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<String> {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.getRelatedTags(tagNames, page, size) },
            remoteAction = { remoteDataSource.getRelatedTags(tagNames, page, size) }
        )
    }

    override suspend fun getSearchAutoComplete(
        keyword: String,
        size: Int
    ): List<TagModel> {
        return modeExecutor.executeWithMode(
            localAction = { localDataSource.getSearchAutoComplete(keyword, size) },
            remoteAction = {
                remoteDataSource.getSearchAutoComplete(keyword, size).getOrElse { emptyList() }
            }
        )
    }

    override suspend fun deleteTag(tagId: Int) {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.deleteTag(tagId) },
            remoteAction = { remoteDataSource.deleteTag(tagId) }
        )
    }

    override suspend fun deleteTags(tagIds: List<Int>) {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.deleteTags(tagIds) },
            remoteAction = { remoteDataSource.deleteTags(tagIds) }
        )
    }

    override suspend fun deleteAllTags() {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.deleteAllTags() },
            remoteAction = { remoteDataSource.deleteAllTags() }
        )
    }
}
