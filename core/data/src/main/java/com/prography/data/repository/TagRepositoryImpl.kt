package com.prography.data.repository

import com.prography.data.datasource.local.TagLocalDataSource
import com.prography.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val localDataSource: TagLocalDataSource
) : TagRepository {

    override suspend fun getRecentTags(): Flow<List<String>> {
        return localDataSource.getRecentTags()
    }

    override suspend fun addRecentTags(tags: List<String>) {
        localDataSource.addRecentTags(tags)
    }

    override suspend fun addRecentTag(tag: String) {
        localDataSource.addRecentTag(tag)
    }

    override suspend fun clearRecentTags() {
        localDataSource.clearRecentTags()
    }
}