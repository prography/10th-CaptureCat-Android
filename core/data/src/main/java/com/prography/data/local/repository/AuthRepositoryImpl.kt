package com.prography.data.local.repository

import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userPreferenceDataStore: UserPreferenceDataStore
) : AuthRepository {

    override suspend fun getAuthToken(): String? {
        return userPreferenceDataStore.accessToken.first()
    }

    override suspend fun saveAuthToken(token: String) {
        userPreferenceDataStore.saveTokens(token, "")
    }

    override suspend fun clearAuthToken() {
        userPreferenceDataStore.clearTokens()
    }

    override suspend fun isLoggedIn(): Boolean {
        return userPreferenceDataStore.accessToken.first().isNullOrEmpty().not()
    }
}
