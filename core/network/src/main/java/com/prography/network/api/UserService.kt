package com.prography.network.api

import com.prography.network.entity.ApiResponse
import com.prography.network.entity.UserInfoResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("/v1/user/info")
    suspend fun getUserInfo(): Response<ApiResponse<UserInfoResponse>>
}
