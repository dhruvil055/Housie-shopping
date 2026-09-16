package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepositoryImpl @Inject constructor() : WishlistRepository {

    private val wishlistFlow = MutableStateFlow<List<Product>>(
        listOf(MockData.products[2], MockData.products[3])
    )

    override fun getWishlist(): Flow<List<Product>> = wishlistFlow

    override fun isWishlisted(productId: String): Flow<Boolean> {
        return wishlistFlow.map { list -> list.any { it.id == productId } }
    }

    override suspend fun addToWishlist(product: Product): Result<Unit> {
        val current = wishlistFlow.value.toMutableList()
        if (!current.any { it.id == product.id }) {
            current.add(product)
            wishlistFlow.value = current
        }
        return Result.success(Unit)
    }

    override suspend fun removeFromWishlist(productId: String): Result<Unit> {
        wishlistFlow.value = wishlistFlow.value.filter { it.id != productId }
        return Result.success(Unit)
    }

    override suspend fun toggleWishlist(product: Product): Result<Boolean> {
        val isPresent = wishlistFlow.value.any { it.id == product.id }
        if (isPresent) {
            removeFromWishlist(product.id)
        } else {
            addToWishlist(product)
        }
        return Result.success(!isPresent)
    }
}
