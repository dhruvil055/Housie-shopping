package com.housieshopping.app.data.remote.api

import com.housieshopping.app.data.remote.dto.AddToCartRequestDto
import com.housieshopping.app.data.remote.dto.ApiResponse
import com.housieshopping.app.data.remote.dto.ApplyCouponRequestDto
import com.housieshopping.app.data.remote.dto.CartSummaryDto
import com.housieshopping.app.data.remote.dto.UpdateQuantityRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {
    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<CartSummaryDto>>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequestDto): Response<ApiResponse<CartSummaryDto>>

    @PUT("cart/items/{id}")
    suspend fun updateQuantity(
        @Path("id") id: String,
        @Body request: UpdateQuantityRequestDto
    ): Response<ApiResponse<CartSummaryDto>>

    @DELETE("cart/items/{id}")
    suspend fun removeFromCart(@Path("id") id: String): Response<ApiResponse<CartSummaryDto>>

    @DELETE("cart")
    suspend fun clearCart(): Response<ApiResponse<CartSummaryDto>>

    @POST("cart/coupon")
    suspend fun applyCoupon(@Body request: ApplyCouponRequestDto): Response<ApiResponse<CartSummaryDto>>

    @DELETE("cart/coupon")
    suspend fun removeCoupon(): Response<ApiResponse<CartSummaryDto>>
}
