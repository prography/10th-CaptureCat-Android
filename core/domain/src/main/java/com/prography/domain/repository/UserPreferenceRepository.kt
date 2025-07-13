package com.prography.domain.repository

import kotlinx.coroutines.flow.Flow


interface UserPreferenceRepository {
    val isOnboardingShown: Flow<Boolean>
    suspend fun setOnboardingShown(shown: Boolean)

    val accessToken : Flow<String?>
    val refreshToken: Flow<String?>
    suspend fun clearTokens()
    suspend fun saveTokens(access: String, refresh: String)
}