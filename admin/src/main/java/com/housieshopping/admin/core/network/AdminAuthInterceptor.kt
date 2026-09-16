package com.housieshopping.admin.core.network

import com.housieshopping.admin.data.preferences.AdminPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminAuthInterceptor @Inject constructor(
    private val adminPreferences: AdminPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = try {
            runBlocking { adminPreferences.authToken.first() }
        } catch (e: Exception) {
            ""
        }

        return if (token.isNotBlank()) {
            val authenticatedRequest = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(original)
        }
    }
}