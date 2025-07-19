package com.prography.data.repository

import com.prography.data.datasource.local.ScreenshotLocalDataSource
import com.prography.data.datasource.remote.PhotoRemoteDataSource
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.model.UiScreenshotModel
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

    override suspend fun getScreenshots(): Flow<List<UiScreenshotModel>> {
        val token = userPrefs.accessToken
        Timber.d("userPrefs.accessToken  ${token.first()}")

        return if (token.first().isNullOrBlank()) {
            localDataSource.getScreenshots()
        } else {
            remoteDataSource.getScreenshots()
                .fold(
                    onSuccess = { screenshots ->
                        Timber.d("Remote success: ${screenshots.size} screenshots")
                        flowOf(screenshots)
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "Remote failure, fallback to local")
                        localDataSource.getScreenshots()
                    }
                )
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
                        // 서버 업로드 성공 시 로컬에도 저장 (캐시 목적)
/*                        screenshots.forEach { screenshot ->
                            localDataSource.insert(screenshot)
                        }*/
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "BulkInsert - Remote upload failure, fallback to local")
                        // 서버 업로드 실패 시 로컬에 저장
                        screenshots.forEach { screenshot ->
                            localDataSource.insert(screenshot)
                        }
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
                        Timber.e(
                            exception,
                            "DeleteScreenshot - Remote deletion failure, fallback to local"
                        )
                        // 서버 삭제 실패 시 로컬에서만 삭제
                        localDataSource.deleteById(screenshotId)
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
}
