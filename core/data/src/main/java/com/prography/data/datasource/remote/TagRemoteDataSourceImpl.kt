package com.prography.data.datasource.remote

import com.prography.domain.model.TagModel
import com.prography.network.api.TagService
import com.prography.network.entity.UpdateTagRequest
import com.prography.network.util.NetworkState
import com.prography.network.util.getDataOrNull
import timber.log.Timber
import javax.inject.Inject

class TagRemoteDataSourceImpl @Inject constructor(
    private val tagService: TagService
) : TagRemoteDataSource {

    override suspend fun getUserTags(): Result<List<TagModel>> {
        return when (val state = tagService.getUserTags()) {
            is NetworkState.Success -> {
                val items = state.body.data?.items?.map { TagModel(id = it.id, name = it.name) }
                    ?: emptyList()
                Result.success(items)
            }

            is NetworkState.Failure -> Result.failure(Exception(state.error ?: "유저 태그 조회 실패"))
            is NetworkState.NetworkError -> Result.failure(state.error)
            is NetworkState.UnknownError -> Result.failure(state.t ?: Exception(state.errorState))
        }
    }

    override suspend fun addUserTag(tagName: String): Result<TagModel> {
        return when (val state = tagService.addUserTag(tagName)) {
            is NetworkState.Success -> {
                val resp = state.body.getDataOrNull()
                if (resp != null) Result.success(TagModel(id = resp.id, name = resp.name))
                else Result.failure(Exception("응답 데이터가 비어있습니다."))
            }

            is NetworkState.Failure -> Result.failure(Exception(state.error ?: "유저 태그 조회 실패"))
            is NetworkState.NetworkError -> Result.failure(state.error)
            is NetworkState.UnknownError -> Result.failure(state.t ?: Exception(state.errorState))
        }
    }
    override suspend fun updateUserTag(tagId: Long, newTagName: String): Result<TagModel> {
        return when (val state = tagService.updateUserTag(
            UpdateTagRequest(
                currentTagId = tagId,
                newTagName = newTagName
            )
        )) {
            is NetworkState.Success -> {
                val resp = state.body.getDataOrNull()
                if (resp != null) Result.success(TagModel(id = resp.id, name = resp.name))
                else Result.failure(Exception("응답 데이터가 비어있습니다."))
            }

            is NetworkState.Failure -> Result.failure(Exception(state.error ?: "유저 태그 수정 실패"))
            is NetworkState.NetworkError -> Result.failure(state.error)
            is NetworkState.UnknownError -> Result.failure(state.t ?: Exception(state.errorState))
        }
    }

    override suspend fun deleteUserTag(tagId: Long): Result<Unit> {
        return when (val state = tagService.deleteUserTag(tagId)) {
            is NetworkState.Success -> Result.success(Unit)
            is NetworkState.Failure -> Result.failure(Exception(state.error ?: "유저 태그 삭제 실패"))
            is NetworkState.NetworkError -> Result.failure(state.error)
            is NetworkState.UnknownError -> Result.failure(state.t ?: Exception(state.errorState))
        }
    }
}