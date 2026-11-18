package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "healthmate_prefs")

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English"),
    URDU("ur", "اردو")
}

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val displayName: String = "",
    val hasAcceptedDisclaimer: Boolean = false
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val ACCEPTED_DISCLAIMER = booleanPreferencesKey("accepted_disclaimer")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val langCode = prefs[Keys.LANGUAGE] ?: AppLanguage.ENGLISH.code
        UserPreferences(
            isDarkMode = prefs[Keys.DARK_MODE] ?: false,
            language = if (langCode == AppLanguage.URDU.code) AppLanguage.URDU else AppLanguage.ENGLISH,
            displayName = prefs[Keys.DISPLAY_NAME] ?: "",
            hasAcceptedDisclaimer = prefs[Keys.ACCEPTED_DISCLAIMER] ?: false
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setDisplayName(name: String) {
        context.dataStore.edit { it[Keys.DISPLAY_NAME] = name.trim() }
    }

    suspend fun setDisclaimerAccepted(accepted: Boolean) {
        context.dataStore.edit { it[Keys.ACCEPTED_DISCLAIMER] = accepted }
    }
}
