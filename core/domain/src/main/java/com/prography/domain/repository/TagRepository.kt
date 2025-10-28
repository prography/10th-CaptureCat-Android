package com.prography.domain.repository

import com.prography.domain.model.TagModel
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun getUserTags(): Flow<List<TagModel>>
    suspend fun addUserTag(tag: String) : Flow<TagModel>
    suspend fun clearRecentTags()

    suspend fun deleteUserTags(tagIds: List<Long>)

    suspend fun deleteUserTag(tagId: Long): Result<Unit>
    suspend fun updateUserTag(tagId: Long, newTagName: String): Result<TagModel>

}