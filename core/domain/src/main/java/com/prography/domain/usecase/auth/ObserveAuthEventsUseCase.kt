package com.prography.domain.usecase.auth

import com.prography.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthEventsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthRepository.AuthEvent> {
        return authRepository.observeAuthEvents()
    }
}