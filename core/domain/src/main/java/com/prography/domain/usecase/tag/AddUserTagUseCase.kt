package com.prography.domain.usecase.tag

import com.prography.domain.model.TagModel
import com.prography.domain.repository.TagRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserTagUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tags: List<String>): Flow<List<TagModel>> = flow {
        val savedTags = mutableListOf<TagModel>()
        tags.forEach { tag ->
            repository.addUserTag(tag).collect { tagModel ->
                savedTags.add(tagModel)
            }
        }
        emit(savedTags) // 모든 태그 저장 후 리스트 방출
    }
}