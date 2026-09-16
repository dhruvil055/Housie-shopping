package com.housieshopping.app.data.remote.api

import com.housieshopping.app.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

data class UpdateProfileRequest(val name: String, val email: String, val phone: String)

interface UserApiService {
    @GET("user/profile")
    suspend fun getProfile(): Response<User>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>
}
