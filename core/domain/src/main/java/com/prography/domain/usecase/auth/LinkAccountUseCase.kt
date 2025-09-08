package com.prography.domain.usecase.auth

import com.prography.domain.model.LoginResult
import com.prography.domain.repository.AuthRepository
import javax.inject.Inject

class LinkAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        provider: String,
        idToken: String,
        linkToken: String,
        accessToken: String? = null
    ): Result<LoginResult> {
        return authRepository.linkAccount(
            provider = provider,
            idToken = idToken,
            linkToken = linkToken,
            accessToken = accessToken
        )
    }
}