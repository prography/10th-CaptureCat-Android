package com.prography.domain.usecase.user

import com.prography.domain.repository.UserPreferenceRepository
import javax.inject.Inject

class SetStartTagScreenShownUseCase @Inject constructor(
    private val repo: UserPreferenceRepository
) {
    suspend operator fun invoke(shown: Boolean) {
        repo.setStartTagScreenShown(shown)
    }
}