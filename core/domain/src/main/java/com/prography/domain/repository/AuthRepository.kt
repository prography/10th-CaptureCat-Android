package com.prography.domain.repository

interface AuthRepository {
    suspend fun socialLogin(provider: String, idToken: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun isLoggedIn(): Boolean
}