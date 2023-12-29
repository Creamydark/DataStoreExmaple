package com.creamydark.datastoreexmaple

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class IdsDataStore(val dataStore: DataStore<Preferences>) {

    val KEY_IDS = stringSetPreferencesKey("_ids")

    suspend fun addId(id: String) {
        dataStore.edit { preferences ->
            val currentIds = preferences[KEY_IDS] ?: emptySet() // Retrieve as Set
            val updatedIds = currentIds.toMutableSet().apply {
                add(id) // Add new ID
            }
            preferences[KEY_IDS] = updatedIds.toSet() // Store as Set
        }
    }
    val idsFlow: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences()) // Emit empty preferences on error
            } else {
                throw exception // Rethrow other exceptions
            }
        }
        .map { preferences ->
            preferences[KEY_IDS] as? Set<String> ?: emptySet() // Map to Set
        }
}
