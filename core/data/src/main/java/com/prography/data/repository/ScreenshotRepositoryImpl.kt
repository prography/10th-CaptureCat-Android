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
        localDataSource.deleteById(screenshotId)
    }
}
