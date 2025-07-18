package com.prography.network.api

import com.prography.network.entity.ApiListResponse
import com.prography.network.entity.ApiResponse
import com.prography.network.entity.PhotoResponse
import com.prography.network.util.NetworkState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotoService {
    @GET("v1/images")
    suspend fun getScreenshots(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): NetworkState<ApiListResponse<PhotoResponse>>

    @GET("v1/images/{imageId}")
    suspend fun getScreenshotById(
        @Path("imageId") imageId: String
    ): NetworkState<ApiResponse<PhotoResponse>>

    @Multipart
    @POST("v1/images/upload")
    suspend fun uploadScreenshots(
        @Part uploadItems: MultipartBody.Part,
        @Part files: List<MultipartBody.Part>
    ): NetworkState<ApiResponse<String>>

    @DELETE("v1/images/{imageId}/tags/{tagId}")
    suspend fun deleteTag(
        @Path("imageId") imageId: String,
        @Path("tagId") tagId: String
    ): NetworkState<ApiResponse<String>>

    @POST("/v1/images/{id}/tags")
    suspend fun addTagsToScreenshot(
        @Path("id") screenshotId: String,
        @Body body: Map<String, List<String>>
    ): NetworkState<ApiResponse<Unit>>

}
