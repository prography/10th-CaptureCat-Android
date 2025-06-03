package com.prography.datastore.user


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.userDataStore by preferencesDataStore(name = "user_prefs")

object UserPreferenceKeys {
    val IS_ONBOARDING_SHOWN = booleanPreferencesKey("is_onboarding_shown")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
}

class UserPreferenceDataStore(private val context: Context) {

    private val dataStore = context.userDataStore

    val isOnboardingShown: Flow<Boolean> =
        dataStore.data.map { it[UserPreferenceKeys.IS_ONBOARDING_SHOWN] ?: false }

    suspend fun setOnboardingShown(shown: Boolean) {
        dataStore.edit { it[UserPreferenceKeys.IS_ONBOARDING_SHOWN] = shown }
    }

    val accessToken: Flow<String?> =
        dataStore.data.map { it[UserPreferenceKeys.ACCESS_TOKEN] }

    suspend fun saveTokens(access: String, refresh: String) {
        dataStore.edit {
            it[UserPreferenceKeys.ACCESS_TOKEN] = access
            it[UserPreferenceKeys.REFRESH_TOKEN] = refresh
        }
    }
}