package com.prography.domain.usecase.auth

import com.prography.domain.repository.AuthRepository
import javax.inject.Inject

class SocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(provider: String, idToken: String): Result<Unit> {
        return authRepository.socialLogin(provider, idToken)
    }
}