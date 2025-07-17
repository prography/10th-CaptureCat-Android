package com.prography.datastore.tag

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.tagDataStore by preferencesDataStore(name = "tag_prefs")

object TagPreferenceKeys {
    val RECENT_TAGS = stringPreferencesKey("recent_tags")
}

class TagDataStore(private val context: Context) {

    private val dataStore = context.tagDataStore
    private val separator = "||"

    val recentTags: Flow<List<String>> =
        dataStore.data.map { preferences ->
            val tagsString = preferences[TagPreferenceKeys.RECENT_TAGS] ?: ""
            if (tagsString.isBlank()) {
                emptyList()
            } else {
                tagsString.split(separator).filter { it.isNotBlank() }
            }
        }

    suspend fun addRecentTags(tags: List<String>) {
        dataStore.edit { preferences ->
            val currentTagsString = preferences[TagPreferenceKeys.RECENT_TAGS] ?: ""
            val currentTags = if (currentTagsString.isBlank()) {
                emptyList()
            } else {
                currentTagsString.split(separator).filter { it.isNotBlank() }
            }

            // 새로운 태그들을 앞에 추가하고 중복 제거
            val updatedTags = (tags + currentTags).distinct()

            preferences[TagPreferenceKeys.RECENT_TAGS] = updatedTags.joinToString(separator)
        }
    }

    suspend fun addRecentTag(tag: String) {
        dataStore.edit { preferences ->
            val currentTagsString = preferences[TagPreferenceKeys.RECENT_TAGS] ?: ""
            val currentTags = if (currentTagsString.isBlank()) {
                emptyList()
            } else {
                currentTagsString.split(separator).filter { it.isNotBlank() }
            }

            // 새로운 태그를 맨 앞에 추가하고 중복 제거
            val updatedTags = listOf(tag) + currentTags.filter { it != tag }

            preferences[TagPreferenceKeys.RECENT_TAGS] = updatedTags.joinToString(separator)
        }
    }

    suspend fun clearRecentTags() {
        dataStore.edit { preferences ->
            preferences.remove(TagPreferenceKeys.RECENT_TAGS)
        }
    }
}