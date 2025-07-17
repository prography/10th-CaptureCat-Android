package com.prography.domain.usecase.tag

import com.prography.domain.repository.TagRepository
import javax.inject.Inject

class AddRecentTagUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tag: String) {
        repository.addRecentTag(tag)
    }
}