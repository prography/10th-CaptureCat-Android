package com.prography.domain.usecase.tag

import com.prography.domain.repository.TagRepository
import javax.inject.Inject

class DeleteUserTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tagId: Long): Result<Unit> {
        return tagRepository.deleteUserTag(tagId)
    }

    suspend fun deleteMultipleTags(tagIds: List<Long>): List<Result<Unit>> {
        return tagIds.map { tagId ->
            tagRepository.deleteUserTag(tagId)
        }
    }
}