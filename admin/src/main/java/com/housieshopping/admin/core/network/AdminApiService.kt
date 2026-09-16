package com.housieshopping.admin.core.network

import com.housieshopping.admin.domain.model.AdminBanner
import com.housieshopping.admin.domain.model.AdminCategory
import com.housieshopping.admin.domain.model.AdminCoupon
import com.housieshopping.admin.domain.model.AdminCustomer
import com.housieshopping.admin.domain.model.AdminOrder
import com.housieshopping.admin.domain.model.AdminProduct
import com.housieshopping.admin.domain.model.AdminReview
import com.housieshopping.admin.domain.model.AdminSupportTicket
import com.housieshopping.admin.domain.model.DashboardAnalytics
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class AdminLoginRequest(
    val email: String,
    val pin: String
)

data class AdminLoginResponse(
    val success: Boolean,
    val token: String,
    val adminName: String,
    val message: String? = null
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class OrderStatusUpdateRequest(
    val status: String,
    val trackingNumber: String,
    val logisticsPartner: String
)

interface AdminApiService {

    @POST("admin/login")
    suspend fun loginAdmin(@Body request: AdminLoginRequest): Response<AdminLoginResponse>

    @GET("admin/analytics")
    suspend fun getDashboardAnalytics(): Response<ApiResponse<DashboardAnalytics>>

    // Products
    @GET("admin/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("query") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<AdminProduct>>>

    @POST("admin/products")
    suspend fun createProduct(@Body product: AdminProduct): Response<ApiResponse<AdminProduct>>

    @PUT("admin/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: AdminProduct): Response<ApiResponse<AdminProduct>>

    @DELETE("admin/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<ApiResponse<Boolean>>

    // Categories
    @GET("admin/categories")
    suspend fun getCategories(): Response<ApiResponse<List<AdminCategory>>>

    @POST("admin/categories")
    suspend fun createCategory(@Body category: AdminCategory): Response<ApiResponse<AdminCategory>>

    // Orders
    @GET("admin/orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<AdminOrder>>>

    @PATCH("admin/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body request: OrderStatusUpdateRequest
    ): Response<ApiResponse<AdminOrder>>

    // Customers
    @GET("admin/customers")
    suspend fun getCustomers(): Response<ApiResponse<List<AdminCustomer>>>

    // Coupons
    @GET("admin/coupons")
    suspend fun getCoupons(): Response<ApiResponse<List<AdminCoupon>>>

    @POST("admin/coupons")
    suspend fun createCoupon(@Body coupon: AdminCoupon): Response<ApiResponse<AdminCoupon>>

    // Banners
    @GET("admin/banners")
    suspend fun getBanners(): Response<ApiResponse<List<AdminBanner>>>

    @POST("admin/banners")
    suspend fun createBanner(@Body banner: AdminBanner): Response<ApiResponse<AdminBanner>>

    // Support
    @GET("admin/support/tickets")
    suspend fun getSupportTickets(): Response<ApiResponse<List<AdminSupportTicket>>>

    // Reviews
    @GET("admin/reviews")
    suspend fun getReviews(): Response<ApiResponse<List<AdminReview>>>
}
