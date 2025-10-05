package com.prography.domain.usecase.tag

import com.prography.domain.model.TagModel
import com.prography.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

class AddRecentTagUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tag: String) : Flow<TagModel> {
        return repository.addUserTag(tag)
    }
    suspend operator fun invoke(tags: List<String>): Flow<TagModel> =
        tags.asFlow().flatMapConcat { repository.addUserTag(it) }
}