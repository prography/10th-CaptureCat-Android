package com.prography.network.api

import com.prography.network.entity.ApiResponse
import com.prography.network.entity.AuthResponse
import com.prography.network.entity.SocialLoginRequest
import com.prography.network.entity.SocialLoginResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("/v1/auth/{provider}/login")
    suspend fun socialLogin(
        @Path("provider") provider: String,
        @Body request: SocialLoginRequest
    ): Response<AuthResponse>

    @POST("/token/reissue")
    suspend fun refreshToken(
        @Header("Refresh-Token") refreshToken: String
    ): Response<AuthResponse>

    @POST("/logout")
    suspend fun logout(
        @Header("Refresh-Token") refreshToken: String
    ): Response<AuthResponse>
}