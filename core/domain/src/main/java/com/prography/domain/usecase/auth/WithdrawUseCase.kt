package com.prography.domain.usecase.auth

import com.prography.domain.repository.AuthRepository
import javax.inject.Inject

class WithdrawUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.withdraw()
    }
}