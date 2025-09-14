package com.prography.domain.usecase.tag

import com.prography.domain.model.TagModel
import com.prography.domain.repository.TagRepository
import javax.inject.Inject

class UpdateUserTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tagId: Long, newTagName: String): Result<TagModel> {
        return tagRepository.updateUserTagOnServer(tagId, newTagName)
    }
}