package com.housieshopping.admin.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "housie_admin_prefs")

@Singleton
class AdminPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("admin_is_logged_in")
        val ADMIN_NAME = stringPreferencesKey("admin_name")
        val ADMIN_EMAIL = stringPreferencesKey("admin_email")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val ADMIN_PIN = stringPreferencesKey("admin_pin")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_LOGGED_IN] ?: false
    }

    val adminName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.ADMIN_NAME] ?: "Housie Store Owner"
    }

    val adminEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.ADMIN_EMAIL] ?: "admin@housieshopping.com"
    }

    val authToken: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.AUTH_TOKEN] ?: ""
    }

    val adminPin: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.ADMIN_PIN] ?: "1234" // Default Secure Admin PIN
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean, name: String = "Housie Admin", email: String = "admin@housieshopping.com", token: String = "jwt_admin_token") {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = isLoggedIn
            prefs[Keys.ADMIN_NAME] = name
            prefs[Keys.ADMIN_EMAIL] = email
            prefs[Keys.AUTH_TOKEN] = token
        }
    }

    suspend fun updateAdminPin(newPin: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ADMIN_PIN] = newPin
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = false
            prefs[Keys.AUTH_TOKEN] = ""
        }
    }
}
