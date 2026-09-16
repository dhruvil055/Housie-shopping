package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.Banner
import com.housieshopping.app.domain.model.Brand
import com.housieshopping.app.domain.model.Category
import com.housieshopping.app.domain.model.FilterOptions
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.Review
import com.housieshopping.app.domain.repository.ProductRepository
import com.housieshopping.app.domain.usecase.FilterProductsUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val filterProductsUseCase: FilterProductsUseCase
) : ProductRepository {

    private val searchHistory = mutableListOf("Cement 53 Grade", "TMT 12mm", "Asian Paints", "Drill Machine")
    private val reviewsList = MockData.mockReviews.toMutableList()

    override suspend fun getBanners(): Result<List<Banner>> {
        return Result.success(MockData.banners)
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return Result.success(MockData.categories)
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        return Result.success(MockData.brands)
    }

    override suspend fun getFeaturedProducts(): Result<List<Product>> {
        return Result.success(MockData.products.filter { it.isFeatured })
    }

    override suspend fun getBestSellers(): Result<List<Product>> {
        return Result.success(MockData.products.filter { it.isBestSeller })
    }

    override suspend fun getDealsOfDay(): Result<List<Product>> {
        return Result.success(MockData.products.filter { it.isDealOfDay })
    }

    override suspend fun getRecentlyViewed(): Result<List<Product>> {
        return Result.success(MockData.products.take(4))
    }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        val list = MockData.products.filter { it.categoryId == categoryId || categoryId == "all" }
        return Result.success(if (list.isEmpty()) MockData.products else list)
    }

    override suspend fun searchProducts(
        query: String,
        filterOptions: FilterOptions
    ): Result<List<Product>> {
        if (query.isNotBlank()) {
            addRecentSearch(query)
        }
        val queryFiltered = if (query.isBlank()) {
            MockData.products
        } else {
            MockData.products.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.brand.contains(query, ignoreCase = true) ||
                        it.categoryName.contains(query, ignoreCase = true) ||
                        it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }
        val result = filterProductsUseCase(queryFiltered, filterOptions)
        return Result.success(result)
    }

    override suspend fun getProductById(id: String): Result<Product> {
        val product = MockData.products.find { it.id == id } ?: MockData.products.first()
        return Result.success(product)
    }

    override suspend fun getRelatedProducts(productId: String): Result<List<Product>> {
        val current = MockData.products.find { it.id == productId }
        val related = MockData.products.filter { it.id != productId && it.categoryId == current?.categoryId }
        return Result.success(if (related.isEmpty()) MockData.products.take(3) else related)
    }

    override suspend fun getProductReviews(productId: String): Result<List<Review>> {
        return Result.success(reviewsList.filter { it.productId == productId || true })
    }

    override suspend fun addProductReview(
        productId: String,
        rating: Float,
        title: String,
        comment: String
    ): Result<Review> {
        val newReview = Review(
            id = "r_${System.currentTimeMillis()}",
            productId = productId,
            userName = MockData.mockUser.name,
            rating = rating,
            title = title,
            comment = comment,
            createdAt = "Just now"
        )
        reviewsList.add(0, newReview)
        return Result.success(newReview)
    }

    override suspend fun getRecentSearches(): List<String> = searchHistory.toList()

    override suspend fun clearSearchHistory() {
        searchHistory.clear()
    }

    override suspend fun addRecentSearch(query: String) {
        if (query.isNotBlank() && !searchHistory.contains(query)) {
            searchHistory.add(0, query)
        }
    }
}
