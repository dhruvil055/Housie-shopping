package com.housieshopping.app.data.remote.api

import com.housieshopping.app.domain.model.Banner
import com.housieshopping.app.domain.model.Brand
import com.housieshopping.app.domain.model.Category
import com.housieshopping.app.domain.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("banners")
    suspend fun getBanners(): Response<List<Banner>>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("brands")
    suspend fun getBrands(): Response<List<Brand>>

    @GET("products")
    suspend fun getProducts(@Query("category") categoryId: String? = null, @Query("search") search: String? = null): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<Product>
}
