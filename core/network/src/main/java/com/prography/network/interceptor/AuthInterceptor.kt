package com.prography.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
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

        // 401 에러 시 토큰 삭제 (재발급은 Repository에서 처리)
        if (response.code == 401) {
            tokenManager.clearTokens()
        }

        return response
    }
}