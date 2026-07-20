package com.faturaapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fatura_app_prefs")

class PreferencesRepository(private val context: Context) {

    companion object {
        private val BACKEND_URL_KEY = stringPreferencesKey("backend_url")
    }

    val backendUrl: Flow<String?> = context.dataStore.data.map { it[BACKEND_URL_KEY] }

    suspend fun setBackendUrl(url: String) {
        context.dataStore.edit { it[BACKEND_URL_KEY] = url }
    }
}
