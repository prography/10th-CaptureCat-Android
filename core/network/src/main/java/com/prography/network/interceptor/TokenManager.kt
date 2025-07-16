package com.prography.network.interceptor

import kotlinx.coroutines.flow.Flow

interface TokenManager {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clearTokens()

    sealed class AuthEvent {
        object RefreshTokenExpired : AuthEvent()
    }

    suspend fun emitAuthEvent(event: AuthEvent)
    fun observeAuthEvents(): Flow<AuthEvent>
}
