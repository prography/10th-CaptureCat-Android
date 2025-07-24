package com.prography.data.repository

import com.prography.data.datasource.local.ScreenshotLocalDataSource
import com.prography.data.datasource.remote.PhotoRemoteDataSource
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.model.TagWithCount
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

    override suspend fun getLocalScreenshots(): Flow<List<UiScreenshotModel>> {
        return localDataSource.getScreenshots()
    }

    override suspend fun getScreenshots(hasTags: Boolean?): Flow<List<UiScreenshotModel>> {
        val token = userPrefs.accessToken
        return if (token.first().isNullOrBlank()) {
            localDataSource.getScreenshots(hasTags)
        } else {
            remoteDataSource.getScreenshots(hasTags)
                .fold(
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
    }

    override suspend fun getScreenshots(
        page: Int,
        pageSize: Int,
        hasTags: Boolean?
    ): List<UiScreenshotModel> {
        val token = userPrefs.accessToken.first()
        return if (token.isNullOrBlank()) {
            // 로컬 모드: 전체 데이터를 가져와서 페이징 처리
            val allScreenshots = localDataSource.getScreenshots(hasTags).first()
            val offset = page * pageSize
            val result = allScreenshots.drop(offset).take(pageSize)
            Timber.d("Local paged success: ${result.size} screenshots (page=$page, size=$pageSize, total=${allScreenshots.size})")
            result
        } else {
            val allScreenshots = remoteDataSource.getScreenshots(
                page = page,
                size = pageSize)
                .fold(
                    onSuccess = { screenshots -> screenshots },
                    onFailure = { exception ->
                        Timber.e(exception, "Remote failure")
                        throw exception
                    }
                )
            allScreenshots
        }
    }

    override suspend fun insert(screenshot: UiScreenshotModel) {
        localDataSource.insert(screenshot)
    }

    override suspend fun bulkInsert(screenshots: List<UiScreenshotModel>) {
        val token = userPrefs.accessToken
        Timber.d("BulkInsert - userPrefs.accessToken: ${token.first()}")

        if (token.first().isNullOrBlank()) {
            // 로컬 저장
            Timber.d("BulkInsert - Local mode: inserting ${screenshots.size} screenshots")
            screenshots.forEach { screenshot ->
                localDataSource.insert(screenshot)
            }
        } else {
            // 서버 업로드
            Timber.d("BulkInsert - Remote mode: uploading ${screenshots.size} screenshots")
            remoteDataSource.uploadScreenshots(screenshots)
                .fold(
                    onSuccess = {
                        Timber.d("BulkInsert - Remote upload success")
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "BulkInsert - Remote upload failure")
                        throw exception
                    }
                )
        }
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
        val token = userPrefs.accessToken.first()

        if (token.isNullOrBlank()) {
            // 로컬 모드: 로컬에서만 스크린샷 삭제
            Timber.d("DeleteScreenshot - Local mode: deleting screenshot $screenshotId")
            localDataSource.deleteById(screenshotId)
        } else {
            // 서버 모드: 서버에서 스크린샷 삭제 시도
            Timber.d("DeleteScreenshot - Remote mode: deleting screenshot $screenshotId")
            remoteDataSource.deleteScreenshot(screenshotId)
                .fold(
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
    }

    override suspend fun getScreenshotById(screenshotId: String): UiScreenshotModel? {
        val token = userPrefs.accessToken.first()
        return if (token.isNullOrBlank()) {
            // 로컬 모드
            localDataSource.getById(screenshotId)
        } else {
            // 서버 모드
            remoteDataSource.getScreenshotById(screenshotId).getOrNull()
        }
    }

    override suspend fun deleteTag(imageId: String, tagName: String) {
        val token = userPrefs.accessToken
        Timber.d("DeleteTag - userPrefs.accessToken: ${token.first()}")

        if (token.first().isNullOrBlank()) {
            // 로컬 모드: 로컬에서만 태그 삭제
            Timber.d("DeleteTag - Local mode: deleting tag '$tagName' from image $imageId")
            // 로컬에서는 UpdateScreenshotUseCase를 사용하는 것이 더 적합
            // 여기서는 로그만 남기고 실제 구현은 ViewModel에서 처리
            throw UnsupportedOperationException("Use UpdateScreenshotUseCase for local tag deletion")
        } else {
            // 서버 모드: 서버에서 태그 삭제
            Timber.d("DeleteTag - Remote mode: deleting tag '$tagName' from image $imageId")
            remoteDataSource.deleteTag(imageId, tagName)
                .fold(
                    onSuccess = {
                        Timber.d("DeleteTag - Remote tag deletion success")
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "DeleteTag - Remote tag deletion failure")
                        throw exception
                    }
                )
        }
    }

    override suspend fun addTagsToScreenshot(
        screenshotId: String,
        tagNames: List<String>
    ) {
        val token = userPrefs.accessToken.first()
        return if (token.isNullOrBlank()) {
            // 로컬 모드: 로컬 데이터소스를 사용하여 태그 추가
            Timber.d("AddTagsToScreenshot - Local mode: adding tags $tagNames to screenshot $screenshotId")
            localDataSource.addTagsToScreenshot(screenshotId, tagNames)
        } else {
            // 서버 모드: 서버에 태그 추가
            Timber.d("AddTagsToScreenshot - Remote mode: adding tags $tagNames to screenshot $screenshotId")
            remoteDataSource.addTagsToScreenshot(screenshotId, tagNames).fold(
                onSuccess = {
                    Timber.d("AddTag - Remote tag Add success")
                },
                onFailure = { exception ->
                    Timber.e(exception, "AddTag - Remote tag Add failure")
                    throw exception
                }
            )
        }
    }

    override suspend fun toggleBookmark(screenshotId: String, isBookmarked: Boolean) {
        val token = userPrefs.accessToken.first()

        if (token.isNullOrBlank()) {
            // 로컬 모드: 로컬에서만 북마크 상태 업데이트
            Timber.d("ToggleBookmark - Local mode: updating bookmark for screenshot $screenshotId to $isBookmarked")
            val screenshot = localDataSource.getById(screenshotId)
            if (screenshot != null) {
                val updatedScreenshot = screenshot.copy(isBookmarked = isBookmarked)
                localDataSource.update(updatedScreenshot)
            }
        } else {
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
                        Timber.w(e, "Failed to update local cache after remote bookmark operation")
                    }
                },
                onFailure = { exception ->
                    Timber.e(exception, "ToggleBookmark - Remote bookmark operation failure")
                    throw exception
                }
            )
        }
    }

    override suspend fun getFavoriteImages(page: Int, size: Int): Result<List<UiScreenshotModel>> {
        val token = userPrefs.accessToken.first()

        return if (token.isNullOrBlank()) {
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
        } else {
            // 서버 모드: 서버에서 즐겨찾기 이미지 가져오기
            Timber.d("GetFavoriteImages - Remote mode: page=$page, size=$size")
            remoteDataSource.getFavoriteImages(page, size)
        }
    }

    override suspend fun getMostUsedTags(size: Int): List<TagWithCount> {
        val token = userPrefs.accessToken.first()
        return if (token.isNullOrBlank()) {
            // 로컬 모드
            localDataSource.getMostUsedTags(size)
        } else {
            // 서버 모드
            remoteDataSource.getMostUsedTags(size).getOrElse { exception ->
                Timber.w(exception, "Server getMostUsedTags failed")
                throw exception
            }
        }
    }

    override suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<UiScreenshotModel> {
        val token = userPrefs.accessToken.first()
        return if (token.isNullOrBlank()) {
            // 로컬 모드
            localDataSource.searchImagesByTags(tagNames, page, size)
        } else {
            // 서버 모드
            remoteDataSource.searchImagesByTags(tagNames, page, size).getOrElse { exception ->
                Timber.w(exception, "Server searchImagesByTags failed")
                throw exception
            }
        }
    }

    override suspend fun getRelatedTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<String> {
        val token = userPrefs.accessToken.first()
        return if (token.isNullOrBlank()) {
            // 로컬 모드
            localDataSource.getRelatedTags(tagNames, page, size)
        } else {
            // 서버 모드
            remoteDataSource.getRelatedTags(tagNames, page, size).getOrElse { exception ->
                Timber.w(exception, "Server getRelatedTags failed")
                throw exception
            }
        }
    }
}
