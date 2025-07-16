package com.prography.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun socialLogin(provider: String, idToken: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun isLoggedIn(): Boolean

    sealed class AuthEvent {
        object RefreshTokenExpired : AuthEvent()
    }

    fun observeAuthEvents(): Flow<AuthEvent>
}