package com.prography.domain.usecase.auth

import com.prography.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}