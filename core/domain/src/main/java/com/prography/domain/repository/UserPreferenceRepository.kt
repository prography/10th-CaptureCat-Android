package com.prography.domain.repository

import kotlinx.coroutines.flow.Flow


interface UserPreferenceRepository {
    val isOnboardingShown: Flow<Boolean>
    suspend fun setOnboardingShown(shown: Boolean)
}