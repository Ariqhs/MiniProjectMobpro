package com.ariqhisyamsyahputra0025.mobpro1

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pengaturan_aplikasi")

class SettingPreferences(private val context: Context) {
    private val TEMA_KEY = booleanPreferencesKey("tema_gelap_aktif")
    val getThemeSetting: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[TEMA_KEY] ?: false
    }
    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TEMA_KEY] = isDarkModeActive
        }
    }
}