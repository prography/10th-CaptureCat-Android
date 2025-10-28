package com.prography.domain.usecase.tag

import com.prography.domain.model.TagModel
import com.prography.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(): Flow<List<TagModel>> {
        return tagRepository.getUserTags()
    }
}