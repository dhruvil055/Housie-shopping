package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getWishlist(): Flow<List<Product>>
    fun isWishlisted(productId: String): Flow<Boolean>
    suspend fun addToWishlist(product: Product): Result<Unit>
    suspend fun removeFromWishlist(productId: String): Result<Unit>
    suspend fun toggleWishlist(product: Product): Result<Boolean>
}
