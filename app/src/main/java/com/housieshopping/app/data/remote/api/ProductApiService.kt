package com.housieshopping.app.data.remote.api

import com.housieshopping.app.data.remote.dto.ApiResponse
import com.housieshopping.app.data.remote.dto.BannerDto
import com.housieshopping.app.data.remote.dto.CategoryDto
import com.housieshopping.app.data.remote.dto.ProductResponseDto
import com.housieshopping.app.data.remote.dto.ReviewDto
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class AddReviewRequestDto(
    val rating: Float,
    val title: String,
    val comment: String
)

interface ProductApiService {
    @GET("products/banners")
    suspend fun getBanners(): Response<ApiResponse<List<BannerDto>>>

    @GET("products/categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    @GET("products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("brand") brand: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("minRating") minRating: Float? = null,
        @Query("inStock") inStock: Boolean? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<ProductResponseDto>>>

    @GET("products/featured")
    suspend fun getFeaturedProducts(): Response<ApiResponse<List<ProductResponseDto>>>

    @GET("products/best-sellers")
    suspend fun getBestSellers(): Response<ApiResponse<List<ProductResponseDto>>>

    @GET("products/deals-of-day")
    suspend fun getDealsOfDay(): Response<ApiResponse<List<ProductResponseDto>>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ApiResponse<ProductResponseDto>>

    @GET("products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") id: String): Response<ApiResponse<List<ReviewDto>>>

    @POST("products/{id}/reviews")
    suspend fun addProductReview(
        @Path("id") id: String,
        @Body request: AddReviewRequestDto
    ): Response<ApiResponse<ReviewDto>>
}
