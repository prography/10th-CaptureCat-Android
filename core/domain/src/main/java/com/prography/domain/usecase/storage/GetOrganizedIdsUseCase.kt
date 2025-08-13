package com.prography.domain.usecase.storage

import com.prography.domain.repository.OrganizedRepository
import javax.inject.Inject

class GetOrganizedIdsUseCase @Inject constructor(
    private val organizedRepository: OrganizedRepository
) {
    suspend operator fun invoke(): Set<String> = organizedRepository.getOrganizedIds()
}