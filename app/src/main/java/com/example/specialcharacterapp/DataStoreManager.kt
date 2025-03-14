package com.example.specialcharacterapp

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "special_characters_prefs")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val RECENT_CHARS_KEY = stringPreferencesKey("recent_chars")
        private val FAVORITE_CHARS_KEY = stringPreferencesKey("favorite_chars")
    }

    val recentCharacters: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[RECENT_CHARS_KEY]?.split(",") ?: emptyList()
    }

    val favoriteCharacters: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[FAVORITE_CHARS_KEY]?.split(",")?.toSet() ?: emptySet()
    }

    suspend fun saveRecentCharacters(chars: List<String>) {
        dataStore.edit { preferences ->
            preferences[RECENT_CHARS_KEY] = chars.joinToString(",")
        }
    }

    suspend fun saveFavoriteCharacters(chars: Set<String>) {
        dataStore.edit { preferences ->
            preferences[FAVORITE_CHARS_KEY] = chars.joinToString(",")
        }
    }

    suspend fun clearRecentCharacters() {
        dataStore.edit { preferences ->
            preferences.remove(RECENT_CHARS_KEY)
        }
    }
}
