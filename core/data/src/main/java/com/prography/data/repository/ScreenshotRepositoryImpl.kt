package com.prography.data.repository

import com.prography.data.datasource.local.ScreenshotLocalDataSource
import com.prography.data.datasource.remote.PhotoRemoteDataSource
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

    // 토큰 유무에 따른 모드 체크를 위한 헬퍼 함수들
    private suspend fun isLocalMode(): Boolean = userPrefs.accessToken.first().isNullOrBlank()

    private suspend fun <T> executeWithMode(
        localAction: suspend () -> T,
        remoteAction: suspend () -> T
    ): T = if (isLocalMode()) localAction() else remoteAction()

    private suspend fun <T> executeWithModeAndFallback(
        localAction: suspend () -> T,
        remoteAction: suspend () -> Result<T>
    ): T = executeWithMode(
        localAction = localAction,
        remoteAction = {
            remoteAction().fold(
                onSuccess = { it },
                onFailure = { exception ->
                    Timber.e(exception, "Remote operation failed")
                    throw exception
                }
            )
        }
    )

    override suspend fun getLocalScreenshots(): Flow<List<UiScreenshotModel>> {
        return localDataSource.getScreenshots()
    }

    override suspend fun getScreenshots(hasTags: Boolean?): Flow<List<UiScreenshotModel>> {
        return executeWithMode(
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
        return executeWithMode(
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
        Timber.d("BulkInsert - Mode: ${if (isLocalMode()) "Local" else "Remote"}")

        executeWithMode(
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
    }

    override suspend fun deleteScreenshot(screenshotId: String) {
        executeWithMode(
            localAction = {
                // 로컬 모드: 로컬에서만 스크린샷 삭제
                Timber.d("DeleteScreenshot - Local mode: deleting screenshot $screenshotId")
                localDataSource.deleteById(screenshotId)
            },
            remoteAction = {
                // 서버 모드: 서버에서 스크린샷 삭제 시도
                Timber.d("DeleteScreenshot - Remote mode: deleting screenshot $screenshotId")
                remoteDataSource.deleteScreenshot(screenshotId).fold(
                    onSuccess = {
                        Timber.d("DeleteScreenshot - Remote deletion success")
                        // 서버 삭제 성공 시 로컬에서도 삭제 (캐시 정리)
                        try {
                            localDataSource.deleteById(screenshotId)
                        } catch (e: Exception) {
                            Timber.w(e, "Failed to delete local cache after remote deletion")
                        }
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "DeleteScreenshot - Remote deletion failure")
                        throw exception
                    }
                )
            }
        )
    }

    override suspend fun getScreenshotById(screenshotId: String): UiScreenshotModel? {
        return executeWithModeAndFallback(
            localAction = { localDataSource.getById(screenshotId) },
            remoteAction = { remoteDataSource.getScreenshotById(screenshotId) }
        )
    }

    override suspend fun deleteTag(imageId: String, tagName: String) {
        Timber.d("DeleteTag - Mode: ${if (isLocalMode()) "Local" else "Remote"}")

        executeWithMode(
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
        return executeWithMode(
            localAction = {
                // 로컬 모드: UUID 사용
                Result.success(tagNames.map {
                    TagModel(
                        java.util.UUID.randomUUID().toString(),
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
        executeWithMode(
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

                val remoteResult = if (isBookmarked) {
                    remoteDataSource.addBookmark(screenshotId)
                } else {
                    remoteDataSource.removeBookmark(screenshotId)
                }

                remoteResult.fold(
                    onSuccess = {
                        Timber.d("ToggleBookmark - Remote bookmark operation success")
                        // 서버 성공 시 로컬 캐시도 업데이트
                        try {
                            val screenshot = localDataSource.getById(screenshotId)
                            if (screenshot != null) {
                                val updatedScreenshot = screenshot.copy(isBookmarked = isBookmarked)
                                localDataSource.update(updatedScreenshot)
                            }
                        } catch (e: Exception) {
                            Timber.w(
                                e,
                                "Failed to update local cache after remote bookmark operation"
                            )
                        }
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "ToggleBookmark - Remote bookmark operation failure")
                        throw exception
                    }
                )
            }
        )
    }

    override suspend fun getFavoriteImages(page: Int, size: Int): Result<List<UiScreenshotModel>> {
        return executeWithMode(
            localAction = {
                // 로컬 모드: 로컬 DB에서 즐겨찾기 이미지를 페이징하여 가져오기
                Timber.d("GetFavoriteImages - Local mode: page=$page, size=$size")
                try {
                    val allScreenshots = localDataSource.getScreenshots().first()
                    val favoriteScreenshots = allScreenshots.filter { it.isBookmarked }

                    // 페이징 처리
                    val offset = page * size
                    val pagedFavorites = favoriteScreenshots
                        .drop(offset)
                        .take(size)

                    Timber.d("GetFavoriteImages - Local result: ${pagedFavorites.size} items (total: ${favoriteScreenshots.size})")
                    Result.success(pagedFavorites)
                } catch (e: Exception) {
                    Timber.e(e, "GetFavoriteImages - Local mode failure")
                    Result.failure(e)
                }
            },
            remoteAction = {
                // 서버 모드: 서버에서 즐겨찾기 이미지 가져오기
                Timber.d("GetFavoriteImages - Remote mode: page=$page, size=$size")
                remoteDataSource.getFavoriteImages(page, size)
            }
        )
    }

    override suspend fun getMostUsedTags(size: Int): List<TagWithCount> {
        return executeWithModeAndFallback(
            localAction = { localDataSource.getMostUsedTags(size) },
            remoteAction = { remoteDataSource.getMostUsedTags(size) }
        )
    }

    override suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<UiScreenshotModel> {
        return executeWithModeAndFallback(
            localAction = { localDataSource.searchImagesByTags(tagNames, page, size) },
            remoteAction = { remoteDataSource.searchImagesByTags(tagNames, page, size) }
        )
    }

    override suspend fun getRelatedTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<String> {
        return executeWithModeAndFallback(
            localAction = { localDataSource.getRelatedTags(tagNames, page, size) },
            remoteAction = { remoteDataSource.getRelatedTags(tagNames, page, size) }
        )
    }
}
