package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(): Flow<User>
    suspend fun updateProfile(name: String, email: String, phone: String): Result<User>
    suspend fun updateProfilePicture(imageUrl: String): Result<User>
}
