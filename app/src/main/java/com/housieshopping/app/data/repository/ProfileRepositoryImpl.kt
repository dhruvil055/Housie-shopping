package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.User
import com.housieshopping.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {

    private val userFlow = MutableStateFlow(MockData.mockUser)

    override fun getUserProfile(): Flow<User> = userFlow

    override suspend fun updateProfile(name: String, email: String, phone: String): Result<User> {
        val updated = userFlow.value.copy(name = name, email = email, phone = phone)
        userFlow.value = updated
        return Result.success(updated)
    }

    override suspend fun updateProfilePicture(imageUrl: String): Result<User> {
        val updated = userFlow.value.copy(profilePictureUrl = imageUrl)
        userFlow.value = updated
        return Result.success(updated)
    }
}
