package com.prography.domain.repository

import com.prography.domain.model.LoginProvider
import kotlinx.coroutines.flow.Flow


interface UserPreferenceRepository {
    val isOnboardingShown: Flow<Boolean>
    suspend fun setOnboardingShown(shown: Boolean)

    val isStartTagScreenShown: Flow<Boolean>
    suspend fun setStartTagScreenShown(shown: Boolean)

    val accessToken : Flow<String?>
    val refreshToken: Flow<String?>
    suspend fun clearTokens()
    suspend fun saveTokens(access: String, refresh: String)

    val isDeletePromptEnabled: Flow<Boolean>
    suspend fun setDeletePromptEnabled(enabled: Boolean)


    val isShownDeleteChoiceBottomSheet: Flow<Boolean>
    suspend fun setShownDeleteChoiceBottomSheet(enabled: Boolean)

    val recentLoginProvider: Flow<LoginProvider>
    suspend fun setRecentLoginProvider(provider: LoginProvider)
}