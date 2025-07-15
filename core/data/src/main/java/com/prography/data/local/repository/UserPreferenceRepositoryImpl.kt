package com.prography.data.local.repository

import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.datastore.user.UserPreferenceKeys
import com.prography.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPrefs: UserPreferenceDataStore
) : UserPreferenceRepository {

    override val isOnboardingShown: Flow<Boolean>
        get() = userPrefs.isOnboardingShown

    override suspend fun setOnboardingShown(shown: Boolean) {
        userPrefs.setOnboardingShown(shown)
    }

    override val accessToken: Flow<String?> get() = userPrefs.accessToken

    override val refreshToken: Flow<String?> get() = userPrefs.refreshToken

    override suspend fun clearTokens() {
        userPrefs.clearTokens()
    }

    override suspend fun saveTokens(access: String, refresh: String) {
        userPrefs.saveTokens(access, refresh)
    }
}