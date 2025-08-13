package com.prography.domain.usecase.storage

import com.prography.domain.repository.OrganizedRepository
import javax.inject.Inject

class SaveOrganizedIdsUseCase @Inject constructor(
    private val organizedRepository: OrganizedRepository
) {
    suspend operator fun invoke(ids: List<String>) {
        organizedRepository.addOrganizedIds(ids)
    }
}