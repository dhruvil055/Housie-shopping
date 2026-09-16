package com.housieshopping.app.data.remote.api

import com.housieshopping.app.domain.model.CartItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class AddCartRequest(val productId: String, val variantId: String?, val quantity: Int)

interface CartApiService {
    @GET("cart")
    suspend fun getCart(): Response<List<CartItem>>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddCartRequest): Response<CartItem>

    @DELETE("cart/items/{id}")
    suspend fun removeFromCart(@Path("id") id: String): Response<Unit>
}
