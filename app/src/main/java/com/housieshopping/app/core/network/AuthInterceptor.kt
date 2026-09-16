package com.housieshopping.app.core.network

import com.housieshopping.app.core.security.EncryptedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val preferencesManager: EncryptedPreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = preferencesManager.getAccessToken()

        return if (!token.isNullOrBlank()) {
            val authenticatedRequest = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(original)
        }
    }
}
