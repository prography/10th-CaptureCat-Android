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
    val IS_START_TAG_SCREEN_SHOWN = booleanPreferencesKey("is_start_tag_screen_shown")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val DELETE_PROMPT_ENABLED = booleanPreferencesKey("delete_prompt_enabled")
    val KEY_RECENT_LOGIN = stringPreferencesKey("recent_login_provider")
    val KEY_DELETE_CHOICE_SHOWN = booleanPreferencesKey("has_shown_delete_choice")
}

class UserPreferenceDataStore(private val context: Context) {

    private val dataStore = context.userDataStore

    val isOnboardingShown: Flow<Boolean> =
        dataStore.data.map { it[UserPreferenceKeys.IS_ONBOARDING_SHOWN] ?: false }

    suspend fun setOnboardingShown(shown: Boolean) {
        dataStore.edit { it[UserPreferenceKeys.IS_ONBOARDING_SHOWN] = shown }
    }

    val isStartTagScreenShown: Flow<Boolean> =
        dataStore.data.map { it[UserPreferenceKeys.IS_START_TAG_SCREEN_SHOWN] ?: false }

    suspend fun setStartTagScreenShown(shown: Boolean) {
        dataStore.edit { it[UserPreferenceKeys.IS_START_TAG_SCREEN_SHOWN] = shown }
    }

    val accessToken: Flow<String?> =
        dataStore.data.map { it[UserPreferenceKeys.ACCESS_TOKEN] }

    val refreshToken: Flow<String?> =
        dataStore.data.map { it[UserPreferenceKeys.REFRESH_TOKEN] }

    suspend fun saveTokens(access: String, refresh: String) {
        dataStore.edit {
            it[UserPreferenceKeys.ACCESS_TOKEN] = access
            it[UserPreferenceKeys.REFRESH_TOKEN] = refresh
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferenceKeys.ACCESS_TOKEN)
            preferences.remove(UserPreferenceKeys.REFRESH_TOKEN)
        }
    }

    val isDeletePromptEnabled: Flow<Boolean> =
        dataStore.data.map { it[UserPreferenceKeys.DELETE_PROMPT_ENABLED] ?: true } // 기본 ON

    suspend fun setDeletePromptEnabled(enabled: Boolean) {
        dataStore.edit { it[UserPreferenceKeys.DELETE_PROMPT_ENABLED] = enabled }
    }

    val isShownDeleteChoiceBottomSheet: Flow<Boolean> =
        dataStore.data.map { it[UserPreferenceKeys.KEY_DELETE_CHOICE_SHOWN] ?: false }

    suspend fun setShownDeleteChoiceBottomSheet(enabled: Boolean) {
        dataStore.edit { it[UserPreferenceKeys.KEY_DELETE_CHOICE_SHOWN] = enabled }
    }


    val recentLoginProviderName: Flow<String?> =
        dataStore.data.map { pref -> pref[UserPreferenceKeys.KEY_RECENT_LOGIN] }

    suspend fun setRecentLoginProviderName(name: String) {
        dataStore.edit { it[UserPreferenceKeys.KEY_RECENT_LOGIN] = name }
    }
}