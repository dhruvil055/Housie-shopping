package com.housieshopping.app.data.remote.api

import com.housieshopping.app.domain.model.Order
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class CreateOrderRequest(
    val addressId: String,
    val paymentMethod: String,
    val couponCode: String?
)

interface OrderApiService {
    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<Order>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<Order>
}
