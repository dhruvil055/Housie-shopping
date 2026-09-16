package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.Banner
import com.housieshopping.app.domain.model.Brand
import com.housieshopping.app.domain.model.Category
import com.housieshopping.app.domain.model.FilterOptions
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.Review

interface ProductRepository {
    suspend fun getBanners(): Result<List<Banner>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun getFeaturedProducts(): Result<List<Product>>
    suspend fun getBestSellers(): Result<List<Product>>
    suspend fun getDealsOfDay(): Result<List<Product>>
    suspend fun getRecentlyViewed(): Result<List<Product>>
    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>>
    suspend fun searchProducts(query: String, filterOptions: FilterOptions = FilterOptions()): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
    suspend fun getRelatedProducts(productId: String): Result<List<Product>>
    suspend fun getProductReviews(productId: String): Result<List<Review>>
    suspend fun addProductReview(productId: String, rating: Float, title: String, comment: String): Result<Review>
    suspend fun getRecentSearches(): List<String>
    suspend fun clearSearchHistory()
    suspend fun addRecentSearch(query: String)
}
