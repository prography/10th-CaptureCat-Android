package com.prography.domain.usecase.tag

import com.prography.domain.repository.TagRepository
import javax.inject.Inject

class AddRecentTagsUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tags: List<String>) {
        repository.addRecentTags(tags)
    }
}