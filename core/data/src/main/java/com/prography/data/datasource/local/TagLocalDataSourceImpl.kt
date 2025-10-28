package com.prography.data.datasource.local

import com.prography.datastore.tag.TagDataStore
import com.prography.domain.model.TagModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class TagLocalDataSourceImpl @Inject constructor(
    private val tagDataStore: TagDataStore
) : TagLocalDataSource {

    override suspend fun getRecentTags(): Flow<List<TagModel>> {
        return tagDataStore.recentTags.map { tags ->
            tags.map { TagModel(id = null, name = it) } // id는 로컬이므로 null
        }
    }

    override suspend fun addUserTag(tag: String): Flow<TagModel> = kotlinx.coroutines.flow.flow {
        Timber.d("TocalDataSourceImpl addUserTag: $tag")
        // 실제 저장
        tagDataStore.addRecentTag(tag)
        // 저장 성공 가정하고 바로 방출(로컬 id 없음)
        emit(TagModel(id = null, name = tag))
    }

    override suspend fun deleteUserTag(tagId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserTag(tagId: Long, newTagName: String): Result<TagModel> {
        TODO("Not yet implemented")
    }

    override suspend fun clearRecentTags() {
        tagDataStore.clearRecentTags()
    }
}
