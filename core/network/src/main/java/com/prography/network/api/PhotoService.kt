package com.prography.network.api

import com.prography.network.entity.ApiListResponse
import com.prography.network.entity.PhotoResponse
import com.prography.network.util.NetworkState
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoService {
    @GET("v1/images")
    suspend fun getScreenshots(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiListResponse<PhotoResponse>
}
