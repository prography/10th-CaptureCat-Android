package com.prography.domain.repository

import com.prography.domain.model.LoginResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun socialLogin(provider: String, idToken: String): Result<LoginResult>
    suspend fun logout(): Result<Unit>
    fun isLoggedIn(): Boolean
    suspend fun completeTutorial(): Result<Unit>

    sealed class AuthEvent {
        object RefreshTokenExpired : AuthEvent()
    }

    fun observeAuthEvents(): Flow<AuthEvent>
}