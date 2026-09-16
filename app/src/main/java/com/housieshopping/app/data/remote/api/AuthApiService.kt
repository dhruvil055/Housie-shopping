package com.housieshopping.app.data.remote.api

import com.housieshopping.app.data.remote.dto.ApiResponse
import com.housieshopping.app.data.remote.dto.AuthResponseData
import com.housieshopping.app.data.remote.dto.LoginRequestDto
import com.housieshopping.app.data.remote.dto.RegisterRequestDto
import com.housieshopping.app.data.remote.dto.VerifyOtpRequestDto
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

@Serializable
data class PhoneRequestDto(val phone: String)

@Serializable
data class ForgotPasswordRequestDto(val emailOrPhone: String)

@Serializable
data class ResetPasswordRequestDto(val phone: String, val otp: String, val newPassword: String)

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiResponse<AuthResponseData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<ApiResponse<AuthResponseData>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto): Response<ApiResponse<Map<String, Boolean>>>

    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: PhoneRequestDto): Response<ApiResponse<Unit>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<ApiResponse<Unit>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<ApiResponse<Unit>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @DELETE("auth/account")
    suspend fun deleteAccount(): Response<ApiResponse<Unit>>
}
