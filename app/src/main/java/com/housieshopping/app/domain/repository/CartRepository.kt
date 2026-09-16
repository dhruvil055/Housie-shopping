package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.CartItem
import com.housieshopping.app.domain.model.CartSummary
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.ProductVariant
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    fun getCartSummary(): Flow<CartSummary>
    suspend fun addToCart(product: Product, variant: ProductVariant? = null, quantity: Int = 1): Result<Unit>
    suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(cartItemId: String): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    suspend fun applyCoupon(couponCode: String): Result<CartSummary>
    suspend fun removeCoupon(): Result<CartSummary>
    suspend fun toggleSaveForLater(cartItemId: String): Result<Unit>
}
