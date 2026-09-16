package com.housieshopping.app.data.repository

import com.housieshopping.app.data.local.UserPreferencesRepository
import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.User
import com.housieshopping.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : AuthRepository {

    override fun getLoggedInUser(): Flow<User?> = flow {
        emit(MockData.mockUser)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return userPreferencesRepository.isOnboardingCompleted
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        userPreferencesRepository.setOnboardingCompleted(completed)
    }

    override suspend fun login(emailOrPhone: String, password: String): Result<User> {
        if (emailOrPhone.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please enter both email/phone and password."))
        }
        userPreferencesRepository.saveAuthToken(
            token = MockData.mockUser.token!!,
            userId = MockData.mockUser.id,
            name = MockData.mockUser.name,
            email = MockData.mockUser.email,
            phone = MockData.mockUser.phone
        )
        return Result.success(MockData.mockUser)
    }

    override suspend fun register(name: String, email: String, phone: String, password: String): Result<User> {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.length < 6) {
            return Result.failure(Exception("Please fill out all fields properly. Password must be at least 6 characters."))
        }
        val user = MockData.mockUser.copy(name = name, email = email, phone = phone)
        userPreferencesRepository.saveAuthToken(
            token = user.token!!,
            userId = user.id,
            name = user.name,
            email = user.email,
            phone = user.phone
        )
        return Result.success(user)
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<Boolean> {
        return if (otp == "1234" || otp.length == 4) {
            Result.success(true)
        } else {
            Result.failure(Exception("Invalid OTP. Enter 1234 or any 4-digit code."))
        }
    }

    override suspend fun sendOtp(phone: String): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun forgotPassword(emailOrPhone: String): Result<Boolean> {
        if (emailOrPhone.isBlank()) return Result.failure(Exception("Enter email or phone number."))
        return Result.success(true)
    }

    override suspend fun resetPassword(phone: String, otp: String, newPassword: String): Result<Boolean> {
        if (newPassword.length < 6) return Result.failure(Exception("Password must be at least 6 characters."))
        return Result.success(true)
    }

    override suspend fun logout() {
        userPreferencesRepository.clearAuth()
    }

    override suspend fun deleteAccount(): Result<Boolean> {
        userPreferencesRepository.clearAuth()
        return Result.success(true)
    }
}
