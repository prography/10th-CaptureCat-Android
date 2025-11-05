package com.prography.domain.usecase.user

import com.prography.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetDeleteChoiceSettingUseCase @Inject constructor(
    private val repo: UserPreferenceRepository
) {
    suspend operator fun invoke(): Result<Boolean> =
        runCatching { repo.isShownDeleteChoiceBottomSheet.first() }
}