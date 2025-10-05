package com.prography.network.api

import com.prography.network.entity.ApiListResponse
import com.prography.network.entity.ApiResponse
import com.prography.network.entity.TagResponse
import com.prography.network.entity.UpdateTagRequest
import com.prography.network.util.NetworkState
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TagService {
    @GET("v1/user-tags")
    suspend fun getUserTags(): NetworkState<ApiListResponse<TagResponse>>

    @POST("v1/user-tags")
    suspend fun addUserTag(
        @Query("tagName") tagName: String
    ): NetworkState<ApiResponse<TagResponse>>

    @PATCH("v1/user-tags")
    suspend fun updateUserTag(
        @Body body: UpdateTagRequest
    ): NetworkState<ApiResponse<TagResponse>>

    @DELETE("v1/user-tags")
    suspend fun deleteUserTag(
        @Query("tagId") tagId: Long
    ): NetworkState<ApiResponse<String>>
}