package com.housieshopping.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val emailOrPhone: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val phone: String, val password: String)
data class OtpRequest(val phone: String, val otp: String)
data class AuthResponse(val success: Boolean, val token: String?, val userId: String?, val message: String?)

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<AuthResponse>
}
