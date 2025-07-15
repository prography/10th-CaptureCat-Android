package com.prography.network.api

import com.prography.network.entity.ApiListResponse
import com.prography.network.entity.ApiResponse
import com.prography.network.entity.PhotoResponse
import com.prography.network.util.NetworkState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PhotoService {
    @GET("v1/images")
    suspend fun getScreenshots(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiListResponse<PhotoResponse>

    @Multipart
    @POST("v1/images/upload")
    suspend fun uploadScreenshots(
        @Part("uploadItems") uploadItems: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): ApiResponse<String>
}
