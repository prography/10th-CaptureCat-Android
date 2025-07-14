package com.prography.data.repository

import com.prography.domain.repository.AuthRepository
import com.prography.network.api.AuthService
import com.prography.network.entity.SocialLoginRequest
import com.prography.network.interceptor.TokenManager
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun socialLogin(provider: String, idToken: String): Result<Unit> {
        return try {
            val request = SocialLoginRequest(idToken)
            val response = authService.socialLogin(provider, request)

            if (response.isSuccessful) {
                val authHeader = response.headers()["authorization"]
                val refreshHeader = response.headers()["refresh-token"]

                Timber.d("DEBUG: Auth header: $authHeader")
                Timber.d("DEBUG: Refresh header: $refreshHeader")

                if (!authHeader.isNullOrBlank() && !refreshHeader.isNullOrBlank()) {
                    val accessToken = authHeader.removePrefix("Bearer ")
                    val refreshToken = refreshHeader.removePrefix("Bearer ")

                    tokenManager.saveTokens(accessToken, refreshToken)
                    Result.success(Unit)
                } else {
                    Timber.d("DEBUG: 토큰 헤더가 비어있음 - auth: $authHeader, refresh: $refreshHeader")
                    Result.failure(Exception("토큰을 받지 못했습니다"))
                }
            } else {
                Timber.d("DEBUG: 응답 실패 - code: ${response.code()}, message: ${response.message()}")
                Result.failure(Exception("로그인에 실패했습니다"))
            }
        } catch (e: Exception) {
            Timber.d("DEBUG: 예외 발생 - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                tokenManager.clearTokens()
                return Result.success(Unit)
            }

            val response = authService.logout("Bearer $refreshToken")
            tokenManager.clearTokens()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("로그아웃에 실패했습니다"))
            }
        } catch (e: Exception) {
            tokenManager.clearTokens() // 에러가 발생해도 로컬 토큰은 삭제
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return !tokenManager.getAccessToken().isNullOrBlank()
    }

    suspend fun refreshTokens(): Result<Unit> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                return Result.failure(Exception("리프레시 토큰이 없습니다"))
            }

            val response = authService.refreshToken("Bearer $refreshToken")

            if (response.isSuccessful) {
                val authHeader = response.headers()["authorization"]
                val refreshHeader = response.headers()["refresh-token"]

                if (!authHeader.isNullOrBlank() && !refreshHeader.isNullOrBlank()) {
                    val accessToken = authHeader.removePrefix("Bearer ")
                    val newRefreshToken = refreshHeader.removePrefix("Bearer ")

                    tokenManager.saveTokens(accessToken, newRefreshToken)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("토큰을 받지 못했습니다"))
                }
            } else {
                Result.failure(Exception("토큰 재발급에 실패했습니다"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}