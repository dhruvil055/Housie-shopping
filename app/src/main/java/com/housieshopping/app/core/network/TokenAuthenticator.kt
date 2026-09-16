package com.housieshopping.app.core.network

import com.housieshopping.app.BuildConfig
import com.housieshopping.app.core.security.EncryptedPreferencesManager
import com.housieshopping.app.data.remote.dto.ApiResponse
import com.housieshopping.app.data.remote.dto.AuthResponseData
import com.housieshopping.app.data.remote.dto.RefreshTokenRequestDto
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val preferencesManager: EncryptedPreferencesManager,
    private val json: Json
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loops if refresh token itself failed
        if (responseCount(response) >= 3) {
            return null
        }

        val refreshToken = preferencesManager.getRefreshToken() ?: return null

        synchronized(this) {
            val currentToken = preferencesManager.getAccessToken()
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")

            // If another thread already refreshed the token, retry with currentToken
            if (currentToken != null && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Execute synchronous refresh request
            try {
                val refreshPayload = json.encodeToString(
                    RefreshTokenRequestDto.serializer(),
                    RefreshTokenRequestDto(refreshToken)
                )

                val refreshRequest = Request.Builder()
                    .url("${BuildConfig.BASE_URL}auth/refresh-token")
                    .post(refreshPayload.toRequestBody("application/json".toMediaType()))
                    .build()

                val client = OkHttpClient.Builder().build()
                val refreshResponse = client.newCall(refreshRequest).execute()

                if (refreshResponse.isSuccessful) {
                  val responseBody = refreshResponse.body?.string()
                  if (!responseBody.isNullOrBlank()) {
                      val result = json.decodeFromString<ApiResponse<AuthResponseData>>(responseBody)
                      val newAccessToken = result.data?.accessToken
                      val newRefreshToken = result.data?.refreshToken

                      if (!newAccessToken.isNullOrBlank() && !newRefreshToken.isNullOrBlank()) {
                          preferencesManager.saveAuthTokens(
                              accessToken = newAccessToken,
                              refreshToken = newRefreshToken
                          )
                          return response.request.newBuilder()
                              .header("Authorization", "Bearer $newAccessToken")
                              .build()
                      }
                  }
                } else {
                    preferencesManager.clearAuth()
                }
            } catch (e: Exception) {
                preferencesManager.clearAuth()
            }

            return null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
