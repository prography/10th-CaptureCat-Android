package com.prography.data.datasource.local

import com.prography.domain.model.TagModel
import kotlinx.coroutines.flow.Flow

interface TagLocalDataSource {
    suspend fun getRecentTags(): Flow<List<TagModel>>
    suspend fun addUserTag(tag: String) : Flow<TagModel>

    suspend fun deleteUserTag(tagId: Long): Result<Unit>
    suspend fun updateUserTag(tagId: Long, newTagName: String): Result<TagModel>
    suspend fun clearRecentTags()
}