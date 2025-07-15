package com.prography.network.interceptor

import com.prography.network.api.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    @Named("TokenRefreshAuthService") private val tokenRefreshAuthService: AuthService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 토큰이 필요 없는 API들 (로그인, 토큰 재발급 등)
        val noAuthUrls = listOf(
            "/v1/auth/",
            "/token/reissue"
        )

        // 인증이 필요 없는 URL인지 확인
        val isNoAuthUrl = noAuthUrls.any { originalRequest.url.toString().contains(it) }

        if (isNoAuthUrl) {
            return chain.proceed(originalRequest)
        }

        // 액세스 토큰 추가
        val accessToken = tokenManager.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(authenticatedRequest)

        // 401 에러 시 토큰 재발급 시도
        if (response.code == 401) {
            Timber.d("401 error detected, attempting token refresh")

            val refreshToken = tokenManager.getRefreshToken()
            if (!refreshToken.isNullOrBlank()) {
                try {
                    val refreshResult = runBlocking {
                        tokenRefreshAuthService.refreshToken(refreshToken)
                    }

                    if (refreshResult.isSuccessful) {
                        val authHeader = refreshResult.headers()["authorization"]
                        val newRefreshHeader = refreshResult.headers()["refresh-token"]

                        if (!authHeader.isNullOrBlank() && !newRefreshHeader.isNullOrBlank()) {
                            val newAccessToken = authHeader.removePrefix("Bearer ")
                            val newRefreshToken = newRefreshHeader.removePrefix("Bearer ")

                            tokenManager.saveTokens(newAccessToken, newRefreshToken)
                            Timber.d("Token refresh successful")

                            // 새로운 액세스 토큰으로 원래 요청 재시도
                            val newAuthenticatedRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer $newAccessToken")
                                .build()

                            response.close()
                            return chain.proceed(newAuthenticatedRequest)
                        } else {
                            Timber.e("Token refresh failed: Missing headers")
                            tokenManager.clearTokens()
                        }
                    } else {
                        Timber.e("Token refresh failed: ${refreshResult.code()}")
                        tokenManager.clearTokens()
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Token refresh exception")
                    tokenManager.clearTokens()
                }
            } else {
                Timber.d("No refresh token available")
                tokenManager.clearTokens()
            }
        }

        return response
    }
}