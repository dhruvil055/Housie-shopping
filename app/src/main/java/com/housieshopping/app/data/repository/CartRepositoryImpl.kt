package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.CartItem
import com.housieshopping.app.domain.model.CartSummary
import com.housieshopping.app.domain.model.Coupon
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.ProductVariant
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.usecase.CalculateOrderTotalUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val calculateOrderTotalUseCase: CalculateOrderTotalUseCase
) : CartRepository {

    private val cartItemsFlow = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(
                id = "c_1",
                product = MockData.products[0],
                selectedVariant = MockData.products[0].variants.firstOrNull(),
                quantity = 5
            ),
            CartItem(
                id = "c_2",
                product = MockData.products[1],
                selectedVariant = MockData.products[1].variants.getOrNull(1),
                quantity = 2
            )
        )
    )

    private val appliedCouponFlow = MutableStateFlow<Coupon?>(null)

    override fun getCartItems(): Flow<List<CartItem>> = cartItemsFlow

    override fun getCartSummary(): Flow<CartSummary> {
        return cartItemsFlow.map { items ->
            calculateOrderTotalUseCase(items, appliedCouponFlow.value)
        }
    }

    override suspend fun addToCart(
        product: Product,
        variant: ProductVariant?,
        quantity: Int
    ): Result<Unit> {
        val current = cartItemsFlow.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.product.id == product.id && it.selectedVariant?.id == variant?.id
        }
        if (existingIndex >= 0) {
            val item = current[existingIndex]
            current[existingIndex] = item.copy(quantity = item.quantity + quantity)
        } else {
            current.add(
                CartItem(
                    id = "c_${System.currentTimeMillis()}",
                    product = product,
                    selectedVariant = variant ?: product.variants.firstOrNull(),
                    quantity = quantity
                )
            )
        }
        cartItemsFlow.value = current
        return Result.success(Unit)
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        val current = cartItemsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == cartItemId }
        if (index >= 0) {
            if (quantity <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = quantity)
            }
            cartItemsFlow.value = current
        }
        return Result.success(Unit)
    }

    override suspend fun removeFromCart(cartItemId: String): Result<Unit> {
        cartItemsFlow.value = cartItemsFlow.value.filter { it.id != cartItemId }
        return Result.success(Unit)
    }

    override suspend fun clearCart(): Result<Unit> {
        cartItemsFlow.value = emptyList()
        appliedCouponFlow.value = null
        return Result.success(Unit)
    }

    override suspend fun applyCoupon(couponCode: String): Result<CartSummary> {
        val coupon = MockData.coupons.find { it.code.equals(couponCode, ignoreCase = true) }
            ?: return Result.failure(Exception("Invalid coupon code."))
        appliedCouponFlow.value = coupon
        val summary = calculateOrderTotalUseCase(cartItemsFlow.value, coupon)
        return Result.success(summary)
    }

    override suspend fun removeCoupon(): Result<CartSummary> {
        appliedCouponFlow.value = null
        val summary = calculateOrderTotalUseCase(cartItemsFlow.value, null)
        return Result.success(summary)
    }

    override suspend fun toggleSaveForLater(cartItemId: String): Result<Unit> {
        val current = cartItemsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == cartItemId }
        if (index >= 0) {
            val item = current[index]
            current[index] = item.copy(savedForLater = !item.savedForLater)
            cartItemsFlow.value = current
        }
        return Result.success(Unit)
    }
}
