package com.example.medilink2.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val PREFERRED_ORIENTATION_KEY = stringPreferencesKey("preferred_orientation")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }

    val preferredOrientation: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PREFERRED_ORIENTATION_KEY] ?: "PORTRAIT"
        }

    suspend fun setPreferredOrientation(orientation: String) {
        context.dataStore.edit { preferences ->
            preferences[PREFERRED_ORIENTATION_KEY] = orientation
        }
    }
}
