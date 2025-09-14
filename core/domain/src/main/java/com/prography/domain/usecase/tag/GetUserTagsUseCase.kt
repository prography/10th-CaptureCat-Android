package com.prography.domain.usecase.tag

import com.prography.domain.model.TagModel
import com.prography.domain.repository.TagRepository
import javax.inject.Inject

class GetUserTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(): Result<List<TagModel>> {
        return tagRepository.getUserTagsFromServer()
    }
}