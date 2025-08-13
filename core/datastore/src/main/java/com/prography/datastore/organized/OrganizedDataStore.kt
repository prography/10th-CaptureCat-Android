package com.prography.datastore.organized

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.organizedDataStore by preferencesDataStore(name = "organized_prefs")

object OrganizedPreferenceKeys {
    val ORGANIZED_IDS = stringPreferencesKey("organized_ids")
}

class OrganizedDataStore(private val context: Context) {

    private val dataStore = context.organizedDataStore
    private val separator = ","

    val organizedIds: Flow<Set<String>> =
        dataStore.data.map { preferences ->
            val idsString = preferences[OrganizedPreferenceKeys.ORGANIZED_IDS] ?: ""
            if (idsString.isBlank()) emptySet() else idsString.split(separator)
                .filter { it.isNotBlank() }.toSet()
        }

    suspend fun addOrganizedIds(ids: List<String>) {
        dataStore.edit { preferences ->
            val current = preferences[OrganizedPreferenceKeys.ORGANIZED_IDS] ?: ""
            val currentSet = if (current.isBlank()) emptySet() else current.split(separator)
                .filter { it.isNotBlank() }.toSet()
            val updated = (currentSet + ids.toSet()).joinToString(separator)
            preferences[OrganizedPreferenceKeys.ORGANIZED_IDS] = updated
        }
    }
}