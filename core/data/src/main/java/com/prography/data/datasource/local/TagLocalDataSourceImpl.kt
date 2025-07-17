package com.prography.data.datasource.local

import com.prography.datastore.tag.TagDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagLocalDataSourceImpl @Inject constructor(
    private val tagDataStore: TagDataStore
) : TagLocalDataSource {

    override suspend fun getRecentTags(): Flow<List<String>> {
        return tagDataStore.recentTags
    }

    override suspend fun addRecentTags(tags: List<String>) {
        tagDataStore.addRecentTags(tags)
    }

    override suspend fun addRecentTag(tag: String) {
        tagDataStore.addRecentTag(tag)
    }

    override suspend fun clearRecentTags() {
        tagDataStore.clearRecentTags()
    }
}