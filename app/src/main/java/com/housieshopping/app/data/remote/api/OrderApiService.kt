package com.housieshopping.app.data.remote.api

import com.housieshopping.app.data.remote.dto.ApiResponse
import com.housieshopping.app.data.remote.dto.CreateOrderRequestDto
import com.housieshopping.app.data.remote.dto.CreateOrderResponseData
import com.housieshopping.app.data.remote.dto.OrderTrackingResponseDto
import com.housieshopping.app.data.remote.dto.VerifyPaymentRequestDto
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class OrderItemDto(
    val productId: String,
    val title: String,
    val imageUrl: String? = null,
    val variantName: String? = null,
    val unitPrice: Double,
    val quantity: Int,
    val totalPrice: Double
)

@Serializable
data class OrderTimelineStepDto(
    val status: String,
    val title: String,
    val description: String? = null,
    val timestamp: String? = null,
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false
)

@Serializable
data class OrderResponseDto(
    val _id: String? = null,
    val id: String? = null,
    val orderNumber: String,
    val customerName: String? = null,
    val items: List<OrderItemDto> = emptyList(),
    val paymentMethod: String? = null,
    val paymentStatus: String? = null,
    val orderStatus: String,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val couponDiscount: Double = 0.0,
    val tax: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double,
    val estimatedDeliveryDate: String? = null,
    val trackingNumber: String? = null,
    val logisticsPartner: String? = null,
    val createdAt: String? = null,
    val timeline: List<OrderTimelineStepDto> = emptyList()
)

@Serializable
data class CancelOrderRequestDto(val reason: String)

interface OrderApiService {
    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<OrderResponseDto>>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<ApiResponse<OrderResponseDto>>

    @POST("orders/create")
    suspend fun createOrder(
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: CreateOrderRequestDto
    ): Response<ApiResponse<CreateOrderResponseData>>

    @POST("orders/verify-payment")
    suspend fun verifyPayment(@Body request: VerifyPaymentRequestDto): Response<ApiResponse<OrderResponseDto>>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") id: String,
        @Body request: CancelOrderRequestDto
    ): Response<ApiResponse<OrderResponseDto>>

    @GET("orders/{id}/track")
    suspend fun trackOrder(@Path("id") id: String): Response<ApiResponse<OrderTrackingResponseDto>>
}
