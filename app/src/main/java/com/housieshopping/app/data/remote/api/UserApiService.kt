package com.housieshopping.app.data.remote.api

import com.housieshopping.app.data.remote.dto.AddressDto
import com.housieshopping.app.data.remote.dto.ApiResponse
import com.housieshopping.app.data.remote.dto.AuthResponseData
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Serializable
data class UpdateProfileRequestDto(
    val name: String? = null,
    val email: String? = null,
    val avatar: String? = null
)

interface UserApiService {
    @GET("auth/profile")
    suspend fun getProfile(): Response<ApiResponse<AuthResponseData>>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): Response<ApiResponse<AuthResponseData>>

    @GET("users/addresses")
    suspend fun getAddresses(): Response<ApiResponse<List<AddressDto>>>

    @POST("users/addresses")
    suspend fun addAddress(@Body address: AddressDto): Response<ApiResponse<AddressDto>>

    @PUT("users/addresses/{id}")
    suspend fun updateAddress(@Path("id") id: String, @Body address: AddressDto): Response<ApiResponse<AddressDto>>

    @DELETE("users/addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: String): Response<ApiResponse<Unit>>

    @PATCH("users/addresses/{id}/default")
    suspend fun setDefaultAddress(@Path("id") id: String): Response<ApiResponse<AddressDto>>
}
