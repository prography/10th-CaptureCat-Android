package com.prography.data.repository

import com.prography.data.datasource.local.TagLocalDataSource
import com.prography.data.datasource.remote.TagRemoteDataSource
import com.prography.data.util.RepositoryModeExecutor
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.model.TagModel
import com.prography.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val localDataSource: TagLocalDataSource,
    private val remoteDataSource: TagRemoteDataSource,
    private val userPrefs: UserPreferenceDataStore
) : TagRepository {

    private val modeExecutor = RepositoryModeExecutor(userPrefs)

    override suspend fun getRecentTags(): Flow<List<TagModel>> {
        return modeExecutor.executeWithMode(
            localAction = { localDataSource.getRecentTags() },
            remoteAction = {
                remoteDataSource.getUserTags().fold(
                    onSuccess = { tags ->
                        Timber.d("Remote success: ${tags.size} screenshots with hasTags=$tags")
                        flowOf(tags)
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "Remote failure")
                        throw exception
                    }
                )
            }
        )
    }

    override suspend fun addUserTag(tag: String): Flow<TagModel> {
        return modeExecutor.executeWithMode(
            localAction = {
                localDataSource.addUserTag(tag)
            },
            remoteAction = {
                // 서버 업로드
                Timber.d("addUserTag - Remote mode: uploading $tag")
                remoteDataSource.addUserTag(tag).fold(
                    onSuccess = {
                        Timber.d("addUserTag - Remote upload success ${it}")
                        flowOf(it)
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "addUserTag - Remote upload failure")
                        throw exception
                    }
                )
            }
        )
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