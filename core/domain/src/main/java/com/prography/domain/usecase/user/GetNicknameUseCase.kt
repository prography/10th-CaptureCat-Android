package com.prography.domain.usecase.user

import com.prography.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNicknameUseCase @Inject constructor(
    private val repo: UserPreferenceRepository
) {
    operator fun invoke(): Flow<String?> {
        return repo.nickname
    }
}