package com.prography.network.api

import com.prography.network.entity.ApiResponse
import com.prography.network.entity.AuthResponse
import com.prography.network.entity.SocialLoginRequest
import com.prography.network.entity.SocialLoginResponse
import com.prography.network.entity.WithdrawRequest
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("/v1/auth/{provider}/login")
    suspend fun socialLogin(
        @Path("provider") provider: String,
        @Body request: SocialLoginRequest
    ): Response<ApiResponse<SocialLoginResponse>>

    @POST("/token/reissue")
    suspend fun refreshToken(
        @Header("Refresh-Token") refreshTokenWithBearer: String // Should include "Bearer " prefix
    ): Response<AuthResponse>

    @POST("/logout")
    suspend fun logout(
        @Header("Refresh-Token") refreshTokenWithBearer: String // Should include "Bearer " prefix
    ): Response<AuthResponse>

    @HTTP(method = "DELETE", path = "/v1/user/withdraw", hasBody = true)
    suspend fun withdraw(
        @Header("Refresh-Token") refreshTokenWithBearer: String,
        @Body request: WithdrawRequest
    ): Response<ApiResponse<String>>
}