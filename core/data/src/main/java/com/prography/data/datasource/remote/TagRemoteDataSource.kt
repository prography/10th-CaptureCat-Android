package com.prography.data.datasource.remote

import com.prography.domain.model.TagModel

interface TagRemoteDataSource {
    suspend fun getUserTags(): Result<List<TagModel>>
    suspend fun addUserTag(tagName: String): Result<TagModel>
    suspend fun updateUserTag(tagId: Long, newTagName: String): Result<TagModel>
    suspend fun deleteUserTag(tagId: Long): Result<Unit>
}