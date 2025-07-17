package com.prography.data.datasource.local

import kotlinx.coroutines.flow.Flow

interface TagLocalDataSource {
    suspend fun getRecentTags(): Flow<List<String>>
    suspend fun addRecentTags(tags: List<String>)
    suspend fun addRecentTag(tag: String)
    suspend fun clearRecentTags()
}