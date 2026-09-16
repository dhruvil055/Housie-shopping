package com.housieshopping.app.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "housie_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback for devices with keystore hardware provider issues
            context.getSharedPreferences("housie_secure_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    init {
        _tokenFlow.value = getAccessToken()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "key_access_token"
        private const val KEY_REFRESH_TOKEN = "key_refresh_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_PHONE = "key_user_phone"
        private const val KEY_USER_ROLE = "key_user_role"
    }

    fun saveAuthTokens(
        accessToken: String,
        refreshToken: String,
        userId: String? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        role: String? = null
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            if (userId != null) putString(KEY_USER_ID, userId)
            if (name != null) putString(KEY_USER_NAME, name)
            if (email != null) putString(KEY_USER_EMAIL, email)
            if (phone != null) putString(KEY_USER_PHONE, phone)
            if (role != null) putString(KEY_USER_ROLE, role)
            apply()
        }
        _tokenFlow.value = accessToken
    }

    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = sharedPreferences.getString(KEY_USER_ID, null)

    fun getUserName(): String? = sharedPreferences.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = sharedPreferences.getString(KEY_USER_EMAIL, null)

    fun getUserPhone(): String? = sharedPreferences.getString(KEY_USER_PHONE, null)

    fun getUserRole(): String? = sharedPreferences.getString(KEY_USER_ROLE, "CUSTOMER")

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    fun clearAuth() {
        sharedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_PHONE)
            remove(KEY_USER_ROLE)
            apply()
        }
        _tokenFlow.value = null
    }
}
