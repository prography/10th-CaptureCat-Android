package com.prography.data.util

import com.prography.datastore.user.UserPreferenceDataStore
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Repository에서 로컬/서버 모드를 분기하여 실행하기 위한 유틸리티 클래스
 * 토큰 유무에 따라 로컬 모드와 서버 모드를 자동으로 분기 처리
 */
class RepositoryModeExecutor(
    private val userPrefs: UserPreferenceDataStore
) {

    /**
     * 현재 로컬 모드인지 확인
     * @return 토큰이 없으면 true (로컬 모드), 있으면 false (서버 모드)
     */
    suspend fun isLocalMode(): Boolean = userPrefs.accessToken.first().isNullOrBlank()

    /**
     * 모드에 따라 로컬 액션 또는 서버 액션을 실행
     * @param localAction 로컬 모드에서 실행할 액션
     * @param remoteAction 서버 모드에서 실행할 액션
     * @return 실행 결과
     */
    suspend fun <T> executeWithMode(
        localAction: suspend () -> T,
        remoteAction: suspend () -> T
    ): T = if (isLocalMode()) localAction() else remoteAction()

    /**
     * 서버 액션이 Result를 반환하는 경우의 모드별 실행
     * 서버 모드에서 실패 시 자동으로 예외를 던짐
     * @param localAction 로컬 모드에서 실행할 액션
     * @param remoteAction 서버 모드에서 실행할 액션 (Result<T> 반환)
     * @return 실행 결과
     */
    suspend fun <T> executeWithModeAndFallback(
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

    /**
     * 서버 모드에서 성공 시 로컬 캐시 업데이트를 포함한 실행
     * @param localAction 로컬 모드에서 실행할 액션
     * @param remoteAction 서버 모드에서 실행할 액션 (Result<T> 반환)
     * @param onRemoteSuccess 서버 성공 시 로컬 캐시 업데이트 액션
     * @return 실행 결과
     */
    suspend fun <T> executeWithModeAndCacheUpdate(
        localAction: suspend () -> T,
        remoteAction: suspend () -> Result<T>,
        onRemoteSuccess: suspend (T) -> Unit
    ): T = executeWithMode(
        localAction = localAction,
        remoteAction = {
            remoteAction().fold(
                onSuccess = { result ->
                    try {
                        onRemoteSuccess(result)
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to update local cache after remote success")
                    }
                    result
                },
                onFailure = { exception ->
                    Timber.e(exception, "Remote operation failed")
                    throw exception
                }
            )
        }
    )

    /**
     * 서버 모드에서 Unit을 반환하는 액션에 대한 캐시 업데이트 포함 실행
     * @param localAction 로컬 모드에서 실행할 액션
     * @param remoteAction 서버 모드에서 실행할 액션 (Result<Unit> 반환)
     * @param onRemoteSuccess 서버 성공 시 로컬 캐시 업데이트 액션
     */
    suspend fun executeWithModeAndCacheUpdateUnit(
        localAction: suspend () -> Unit,
        remoteAction: suspend () -> Result<Unit>,
        onRemoteSuccess: suspend () -> Unit
    ): Unit = executeWithMode(
        localAction = localAction,
        remoteAction = {
            remoteAction().fold(
                onSuccess = {
                    try {
                        onRemoteSuccess()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to update local cache after remote success")
                    }
                },
                onFailure = { exception ->
                    Timber.e(exception, "Remote operation failed")
                    throw exception
                }
            )
        }
    )

    /**
     * 현재 모드를 로깅용으로 반환
     * @return "Local" 또는 "Remote"
     */
    suspend fun getCurrentModeForLogging(): String = if (isLocalMode()) "Local" else "Remote"
}