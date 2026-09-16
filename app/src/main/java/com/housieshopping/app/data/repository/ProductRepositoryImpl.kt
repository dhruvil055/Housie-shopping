package com.housieshopping.app.data.repository

import com.housieshopping.app.data.local.dao.ProductDao
import com.housieshopping.app.data.local.entity.ProductEntity
import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.data.remote.api.AddReviewRequestDto
import com.housieshopping.app.data.remote.api.ProductApiService
import com.housieshopping.app.data.remote.dto.BannerDto
import com.housieshopping.app.data.remote.dto.CategoryDto
import com.housieshopping.app.data.remote.dto.ProductResponseDto
import com.housieshopping.app.data.remote.dto.ReviewDto
import com.housieshopping.app.domain.model.Banner
import com.housieshopping.app.domain.model.Brand
import com.housieshopping.app.domain.model.Category
import com.housieshopping.app.domain.model.FilterOptions
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.ProductVariant
import com.housieshopping.app.domain.model.Review
import com.housieshopping.app.domain.model.Seller
import com.housieshopping.app.domain.model.Specification
import com.housieshopping.app.domain.repository.ProductRepository
import com.housieshopping.app.domain.usecase.FilterProductsUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productApiService: ProductApiService,
    private val productDao: ProductDao,
    private val filterProductsUseCase: FilterProductsUseCase
) : ProductRepository {

    private val searchHistory = mutableListOf("Cement 53 Grade", "TMT 12mm", "Asian Paints", "Drill Machine")
    private val inMemoryReviews = MockData.mockReviews.toMutableList()

    private fun mapDtoToProduct(dto: ProductResponseDto): Product {
        return Product(
            id = dto.id ?: dto._id ?: "p_${System.currentTimeMillis()}",
            title = dto.title,
            description = dto.description ?: "",
            brand = dto.brand ?: "Housie Verified",
            categoryId = dto.categoryId ?: "general",
            categoryName = dto.categoryName ?: "Building Materials",
            price = dto.price,
            mrp = dto.mrp,
            rating = dto.rating,
            reviewCount = dto.reviewCount,
            stock = dto.stock,
            images = dto.images.ifEmpty { listOf("https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=500") },
            deliveryEstimateDays = dto.deliveryEstimateDays,
            isFeatured = dto.isFeatured,
            isBestSeller = dto.isBestSeller,
            isDealOfDay = dto.isDealOfDay,
            seller = Seller("s1", "Housie Depot", 4.8, "Housie Official Store"),
            variants = dto.variants.map {
                ProductVariant(
                    id = it.id ?: it._id ?: "v_${it.name}",
                    sku = it.id ?: "SKU-${it.name}",
                    name = it.name,
                    price = it.price,
                    mrp = it.mrp,
                    stock = it.stock
                )
            },
            specifications = dto.specifications.map { Specification(it.key, it.value) },
            tags = dto.tags
        )
    }

    private fun mapDtoToBanner(dto: BannerDto): Banner {
        return Banner(
            id = dto.id ?: dto._id ?: "b1",
            title = dto.title,
            subtitle = dto.subtitle ?: "",
            imageUrl = dto.imageUrl,
            targetType = dto.targetType ?: "CATEGORY",
            targetId = dto.targetId ?: ""
        )
    }

    private fun mapDtoToCategory(dto: CategoryDto): Category {
        return Category(
            id = dto.id ?: dto._id ?: "c1",
            name = dto.name,
            imageUrl = dto.iconUrl ?: "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3?w=500"
        )
    }

    private fun mapDtoToReview(dto: ReviewDto, productId: String): Review {
        return Review(
            id = dto.id ?: dto._id ?: "r_${System.currentTimeMillis()}",
            productId = productId,
            userName = dto.userName,
            rating = dto.rating.toFloat(),
            title = dto.title ?: "",
            comment = dto.comment,
            createdAt = dto.createdAt ?: "Recently"
        )
    }

    override suspend fun getBanners(): Result<List<Banner>> {
        return try {
            val response = productApiService.getBanners()
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                Result.success(response.body()!!.data!!.map { mapDtoToBanner(it) })
            } else {
                Result.success(MockData.banners)
            }
        } catch (e: Exception) {
            Result.success(MockData.banners)
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = productApiService.getCategories()
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                Result.success(response.body()!!.data!!.map { mapDtoToCategory(it) })
            } else {
                Result.success(MockData.categories)
            }
        } catch (e: Exception) {
            Result.success(MockData.categories)
        }
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        return Result.success(MockData.brands)
    }

    override suspend fun getFeaturedProducts(): Result<List<Product>> {
        return try {
            val response = productApiService.getFeaturedProducts()
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                val list = response.body()!!.data!!.map { mapDtoToProduct(it) }
                cacheProductsInRoom(list)
                Result.success(list)
            } else {
                Result.success(MockData.products.filter { it.isFeatured })
            }
        } catch (e: Exception) {
            Result.success(MockData.products.filter { it.isFeatured })
        }
    }

    override suspend fun getBestSellers(): Result<List<Product>> {
        return try {
            val response = productApiService.getBestSellers()
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                val list = response.body()!!.data!!.map { mapDtoToProduct(it) }
                cacheProductsInRoom(list)
                Result.success(list)
            } else {
                Result.success(MockData.products.filter { it.isBestSeller })
            }
        } catch (e: Exception) {
            Result.success(MockData.products.filter { it.isBestSeller })
        }
    }

    override suspend fun getDealsOfDay(): Result<List<Product>> {
        return try {
            val response = productApiService.getDealsOfDay()
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                val list = response.body()!!.data!!.map { mapDtoToProduct(it) }
                cacheProductsInRoom(list)
                Result.success(list)
            } else {
                Result.success(MockData.products.filter { it.isDealOfDay })
            }
        } catch (e: Exception) {
            Result.success(MockData.products.filter { it.isDealOfDay })
        }
    }

    override suspend fun getRecentlyViewed(): Result<List<Product>> {
        return Result.success(MockData.products.take(4))
    }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return try {
            val response = productApiService.getProducts(category = if (categoryId == "all") null else categoryId)
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                val list = response.body()!!.data!!.map { mapDtoToProduct(it) }
                cacheProductsInRoom(list)
                Result.success(list)
            } else {
                val fallback = MockData.products.filter { it.categoryId == categoryId || categoryId == "all" }
                Result.success(if (fallback.isEmpty()) MockData.products else fallback)
            }
        } catch (e: Exception) {
            val fallback = MockData.products.filter { it.categoryId == categoryId || categoryId == "all" }
            Result.success(if (fallback.isEmpty()) MockData.products else fallback)
        }
    }

    override suspend fun searchProducts(
        query: String,
        filterOptions: FilterOptions
    ): Result<List<Product>> {
        if (query.isNotBlank()) {
            addRecentSearch(query)
        }

        return try {
            val response = productApiService.getProducts(
                search = query.ifBlank { null },
                minPrice = filterOptions.minPrice,
                maxPrice = filterOptions.maxPrice
            )
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                val remoteList = response.body()!!.data!!.map { mapDtoToProduct(it) }
                val filtered = filterProductsUseCase(remoteList, filterOptions)
                Result.success(filtered)
            } else {
                searchOfflineFallback(query, filterOptions)
            }
        } catch (e: Exception) {
            searchOfflineFallback(query, filterOptions)
        }
    }

    private fun searchOfflineFallback(query: String, filterOptions: FilterOptions): Result<List<Product>> {
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
        return try {
            val response = productApiService.getProductById(id)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(mapDtoToProduct(response.body()!!.data!!))
            } else {
                val product = MockData.products.find { it.id == id } ?: MockData.products.first()
                Result.success(product)
            }
        } catch (e: Exception) {
            val product = MockData.products.find { it.id == id } ?: MockData.products.first()
            Result.success(product)
        }
    }

    override suspend fun getRelatedProducts(productId: String): Result<List<Product>> {
        val current = MockData.products.find { it.id == productId }
        val related = MockData.products.filter { it.id != productId && it.categoryId == current?.categoryId }
        return Result.success(if (related.isEmpty()) MockData.products.take(3) else related)
    }

    override suspend fun getProductReviews(productId: String): Result<List<Review>> {
        return try {
            val response = productApiService.getProductReviews(productId)
            if (response.isSuccessful && response.body()?.success == true && !response.body()?.data.isNullOrEmpty()) {
                Result.success(response.body()!!.data!!.map { mapDtoToReview(it, productId) })
            } else {
                Result.success(inMemoryReviews.filter { it.productId == productId || true })
            }
        } catch (e: Exception) {
            Result.success(inMemoryReviews.filter { it.productId == productId || true })
        }
    }

    override suspend fun addProductReview(
        productId: String,
        rating: Float,
        title: String,
        comment: String
    ): Result<Review> {
        return try {
            val response = productApiService.addProductReview(
                id = productId,
                request = AddReviewRequestDto(rating, title, comment)
            )
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(mapDtoToReview(response.body()!!.data!!, productId))
            } else {
                val newReview = Review(
                    id = "r_${System.currentTimeMillis()}",
                    productId = productId,
                    userName = "Verified Customer",
                    rating = rating,
                    title = title,
                    comment = comment,
                    createdAt = "Just now"
                )
                inMemoryReviews.add(0, newReview)
                Result.success(newReview)
            }
        } catch (e: Exception) {
            val newReview = Review(
                id = "r_${System.currentTimeMillis()}",
                productId = productId,
                userName = "Verified Customer",
                rating = rating,
                title = title,
                comment = comment,
                createdAt = "Just now"
            )
            inMemoryReviews.add(0, newReview)
            Result.success(newReview)
        }
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

    private suspend fun cacheProductsInRoom(products: List<Product>) {
        try {
            val entities = products.map {
                ProductEntity(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    brand = it.brand,
                    categoryId = it.categoryId,
                    categoryName = it.categoryName,
                    price = it.price,
                    mrp = it.mrp,
                    rating = it.rating,
                    reviewCount = it.reviewCount,
                    stock = it.stock,
                    primaryImage = it.images.firstOrNull() ?: "",
                    deliveryEstimateDays = it.deliveryEstimateDays,
                    isFeatured = it.isFeatured,
                    isBestSeller = it.isBestSeller,
                    isDealOfDay = it.isDealOfDay
                )
            }
            productDao.insertProducts(entities)
        } catch (_: Exception) {
        }
    }
}
