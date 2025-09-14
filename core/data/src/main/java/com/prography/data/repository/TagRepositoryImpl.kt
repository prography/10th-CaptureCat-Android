package com.prography.data.repository

import com.prography.data.datasource.local.TagLocalDataSource
import com.prography.data.datasource.remote.TagRemoteDataSource
import com.prography.domain.model.TagModel
import com.prography.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val localDataSource: TagLocalDataSource,
    private val remoteDataSource: TagRemoteDataSource
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

    override suspend fun getUserTags(): List<TagModel> {
        return remoteDataSource.getUserTags().getOrElse { emptyList() }
    }

    override suspend fun getUserTagsFromServer(): Result<List<TagModel>> {
        return remoteDataSource.getUserTags()
    }

    override suspend fun updateUserTag(tagId: Long, newTagName: String) {
        // 향후 로컬 캐시가 도입되면 동기화 로직 추가 예정
        remoteDataSource.updateUserTag(tagId, newTagName)
    }

    override suspend fun updateUserTagOnServer(tagId: Long, newTagName: String): Result<TagModel> {
        return remoteDataSource.updateUserTag(tagId, newTagName)
    }

    override suspend fun deleteUserTags(tagIds: List<Long>) {
        // 일괄 삭제 API가 없으므로 개별 호출
        tagIds.forEach { id ->
            remoteDataSource.deleteUserTag(id)
        }
    }

    override suspend fun deleteUserTag(tagId: Long): Result<Unit> {
        return remoteDataSource.deleteUserTag(tagId)
    }
}