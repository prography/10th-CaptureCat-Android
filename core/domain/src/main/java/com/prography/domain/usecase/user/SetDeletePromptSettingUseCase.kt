package com.prography.domain.usecase.user

import com.prography.domain.repository.UserPreferenceRepository
import javax.inject.Inject

class SetDeletePromptSettingUseCase @Inject constructor(
    private val repo: UserPreferenceRepository
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> =
        runCatching { repo.setDeletePromptEnabled(enabled) }
}