package com.prography.domain.repository

import com.prography.domain.model.TagModel
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun getRecentTags(): Flow<List<TagModel>>
    suspend fun addUserTag(tag: String) : Flow<TagModel>
    suspend fun clearRecentTags()

    // 사용자가 등록한 태그 목록 가져오기
    suspend fun getUserTags(): List<TagModel>
    suspend fun getUserTagsFromServer(): Result<List<TagModel>>
    suspend fun updateUserTag(tagId: Long, newTagName: String)
    suspend fun updateUserTagOnServer(tagId: Long, newTagName: String): Result<TagModel>
    suspend fun deleteUserTags(tagIds: List<Long>)
    suspend fun deleteUserTag(tagId: Long): Result<Unit>
}