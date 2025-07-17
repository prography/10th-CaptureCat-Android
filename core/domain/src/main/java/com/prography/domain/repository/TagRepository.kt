package com.prography.domain.repository

import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun getRecentTags(): Flow<List<String>>
    suspend fun addRecentTags(tags: List<String>)
    suspend fun addRecentTag(tag: String)
    suspend fun clearRecentTags()
}