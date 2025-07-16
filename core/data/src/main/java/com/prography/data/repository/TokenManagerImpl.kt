package com.prography.data.repository

import com.prography.domain.repository.UserPreferenceRepository
import com.prography.network.interceptor.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class TokenManagerImpl @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) : TokenManager {

    private val _authEvents = MutableSharedFlow<TokenManager.AuthEvent>()

    override fun getAccessToken(): String? {
        return runBlocking {
            userPreferenceRepository.accessToken.first()
        }
    }

    override fun getRefreshToken(): String? {
        return runBlocking {
            userPreferenceRepository.refreshToken.first()
        }
    }

    override fun saveTokens(accessToken: String, refreshToken: String) {
        runBlocking {
            userPreferenceRepository.saveTokens(accessToken, refreshToken)
        }
    }

    override fun clearTokens() {
        runBlocking {
            userPreferenceRepository.clearTokens()
        }
    }

    override suspend fun emitAuthEvent(event: TokenManager.AuthEvent) {
        _authEvents.emit(event)
    }

    override fun observeAuthEvents(): Flow<TokenManager.AuthEvent> {
        return _authEvents.asSharedFlow()
    }
}