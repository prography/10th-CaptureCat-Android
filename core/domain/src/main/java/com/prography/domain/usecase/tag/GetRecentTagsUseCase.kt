package com.prography.domain.usecase.tag

import com.prography.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentTagsUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(): Flow<List<String>> {
        return repository.getRecentTags()
    }
}