package com.prography.data.util

import com.prography.datastore.user.UserPreferenceDataStore
import timber.log.Timber
import javax.inject.Inject

/**
 * RepositoryModeExecutor 사용 예제
 * 다른 Repository에서 이 패턴을 따라 구현할 수 있습니다.
 */
class ExampleRepositoryImpl @Inject constructor(
    private val localDataSource: ExampleLocalDataSource,
    private val remoteDataSource: ExampleRemoteDataSource,
    userPrefs: UserPreferenceDataStore
) {

    private val modeExecutor = RepositoryModeExecutor(userPrefs)

    // 1. 기본적인 모드별 분기 실행
    suspend fun getSampleData(): List<SampleModel> {
        return modeExecutor.executeWithMode(
            localAction = { localDataSource.getAllSamples() },
            remoteAction = {
                remoteDataSource.getSamples().fold(
                    onSuccess = { it },
                    onFailure = { exception ->
                        Timber.e(exception, "Failed to fetch samples from server")
                        throw exception
                    }
                )
            }
        )
    }

    // 2. Result를 반환하는 Remote 액션 처리 (자동 에러 핸들링)
    suspend fun getSampleById(id: String): SampleModel? {
        return modeExecutor.executeWithModeAndFallback(
            localAction = { localDataSource.getSampleById(id) },
            remoteAction = { remoteDataSource.getSampleById(id) }
        )
    }

    // 3. 서버 성공 시 로컬 캐시 업데이트 포함
    suspend fun updateSample(sample: SampleModel) {
        modeExecutor.executeWithModeAndCacheUpdateUnit(
            localAction = {
                Timber.d("Updating sample locally: ${sample.id}")
                localDataSource.updateSample(sample)
            },
            remoteAction = {
                Timber.d("Updating sample on server: ${sample.id}")
                remoteDataSource.updateSample(sample)
            },
            onRemoteSuccess = {
                Timber.d("Server update successful, updating local cache")
                localDataSource.updateSample(sample)
            }
        )
    }

    // 4. 결과값이 있는 캐시 업데이트
    suspend fun createSample(sample: SampleModel): SampleModel {
        return modeExecutor.executeWithModeAndCacheUpdate(
            localAction = {
                // 로컬 모드에서는 UUID로 ID 생성
                val localSample = sample.copy(id = java.util.UUID.randomUUID().toString())
                localDataSource.insertSample(localSample)
                localSample
            },
            remoteAction = { remoteDataSource.createSample(sample) },
            onRemoteSuccess = { createdSample ->
                // 서버에서 생성된 실제 ID로 로컬 캐시 업데이트
                localDataSource.insertSample(createdSample)
            }
        )
    }

    // 5. 현재 모드 확인 (디버깅용)
    suspend fun debugCurrentMode() {
        val mode = modeExecutor.getCurrentModeForLogging()
        Timber.d("Current repository mode: $mode")

        val isLocal = modeExecutor.isLocalMode()
        Timber.d("Is local mode: $isLocal")
    }
}

// 예제용 인터페이스들 (실제로는 구현되어야 함)
interface ExampleLocalDataSource {
    suspend fun getAllSamples(): List<SampleModel>
    suspend fun getSampleById(id: String): SampleModel?
    suspend fun updateSample(sample: SampleModel)
    suspend fun insertSample(sample: SampleModel)
}

interface ExampleRemoteDataSource {
    suspend fun getSamples(): Result<List<SampleModel>>
    suspend fun getSampleById(id: String): Result<SampleModel>
    suspend fun updateSample(sample: SampleModel): Result<Unit>
    suspend fun createSample(sample: SampleModel): Result<SampleModel>
}

// 예제용 모델 (실제로는 domain 모델을 사용)
data class SampleModel(
    val id: String,
    val name: String,
    val description: String
)