package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getLoggedInUser(): Flow<User?>
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun login(emailOrPhone: String, password: String): Result<User>
    suspend fun register(name: String, email: String, phone: String, password: String): Result<User>
    suspend fun verifyOtp(phone: String, otp: String): Result<Boolean>
    suspend fun sendOtp(phone: String): Result<Boolean>
    suspend fun forgotPassword(emailOrPhone: String): Result<Boolean>
    suspend fun resetPassword(phone: String, otp: String, newPassword: String): Result<Boolean>
    suspend fun logout()
    suspend fun deleteAccount(): Result<Boolean>
}
