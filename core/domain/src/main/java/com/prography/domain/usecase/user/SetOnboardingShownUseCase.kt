package com.prography.domain.usecase.user

import com.prography.domain.repository.UserPreferenceRepository
import javax.inject.Inject

class SetOnboardingShownUseCase @Inject constructor(
    private val repo: UserPreferenceRepository
) {
    suspend operator fun invoke(shown: Boolean) {
        repo.setOnboardingShown(shown)
    }
}