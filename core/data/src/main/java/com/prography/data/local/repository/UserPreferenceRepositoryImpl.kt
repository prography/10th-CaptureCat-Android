package com.prography.data.local.repository

import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.datastore.user.UserPreferenceKeys
import com.prography.domain.model.LoginProvider
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

    override val isStartTagScreenShown: Flow<Boolean>
        get() = userPrefs.isStartTagScreenShown

    override suspend fun setStartTagScreenShown(shown: Boolean) {
        userPrefs.setStartTagScreenShown(shown)
    }

    override val accessToken: Flow<String?> get() = userPrefs.accessToken

    override val refreshToken: Flow<String?> get() = userPrefs.refreshToken

    override suspend fun clearTokens() {
        userPrefs.clearTokens()
    }

    override suspend fun saveTokens(access: String, refresh: String) {
        userPrefs.saveTokens(access, refresh)
    }

    override val isDeletePromptEnabled: Flow<Boolean>
        get() = userPrefs.isDeletePromptEnabled

    override val isShownDeleteChoiceBottomSheet: Flow<Boolean>
        get() = userPrefs.isShownDeleteChoiceBottomSheet

    override suspend fun setShownDeleteChoiceBottomSheet(enabled: Boolean) {
        userPrefs.setShownDeleteChoiceBottomSheet(enabled)
    }


    override suspend fun setDeletePromptEnabled(enabled: Boolean) {
        userPrefs.setDeletePromptEnabled(enabled)
    }

    override val recentLoginProvider: Flow<LoginProvider> =
        userPrefs.recentLoginProviderName.map { name ->
            when (name) {
                "KAKAO" -> LoginProvider.KAKAO
                "GOOGLE" -> LoginProvider.GOOGLE
                else    -> LoginProvider.NONE
            }
        }

    override suspend fun setRecentLoginProvider(provider: LoginProvider) {
        userPrefs.setRecentLoginProviderName(provider.name)
    }
}