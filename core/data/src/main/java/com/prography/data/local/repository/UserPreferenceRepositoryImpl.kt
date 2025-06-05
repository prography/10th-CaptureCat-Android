package com.prography.data.local.repository

import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPrefs: UserPreferenceDataStore
) : UserPreferenceRepository {

    override val isOnboardingShown: Flow<Boolean>
        get() = userPrefs.isOnboardingShown

    override suspend fun setOnboardingShown(shown: Boolean) {
        userPrefs.setOnboardingShown(shown)
    }
}