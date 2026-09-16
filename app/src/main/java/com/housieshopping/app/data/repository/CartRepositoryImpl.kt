package com.housieshopping.app.data.repository

import com.housieshopping.app.data.local.dao.CartDao
import com.housieshopping.app.data.local.entity.CartEntity
import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.data.remote.api.CartApiService
import com.housieshopping.app.data.remote.dto.AddToCartRequestDto
import com.housieshopping.app.data.remote.dto.ApplyCouponRequestDto
import com.housieshopping.app.data.remote.dto.CartItemDto
import com.housieshopping.app.data.remote.dto.CartSummaryDto
import com.housieshopping.app.data.remote.dto.UpdateQuantityRequestDto
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
    private val cartApiService: CartApiService,
    private val cartDao: CartDao,
    private val calculateOrderTotalUseCase: CalculateOrderTotalUseCase
) : CartRepository {

    private val cartItemsFlow = MutableStateFlow<List<CartItem>>(emptyList())
    private val appliedCouponFlow = MutableStateFlow<Coupon?>(null)
    private val cartSummaryFlow = MutableStateFlow<CartSummary?>(null)

    init {
        // Seed initial items if empty
        cartItemsFlow.value = listOf(
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
    }

    private fun mapDtoToCartItem(dto: CartItemDto): CartItem {
        val product = MockData.products.find { it.id == dto.productId } ?: MockData.products[0].copy(
            id = dto.productId,
            title = dto.title,
            price = dto.unitPrice,
            mrp = dto.unitMrp,
            stock = dto.availableStock,
            images = if (dto.imageUrl != null) listOf(dto.imageUrl) else emptyList()
        )

        val variant = dto.selectedVariant?.let {
            ProductVariant(
                id = it.id ?: it._id ?: "v_${it.name}",
                sku = it.id ?: "SKU-${it.name}",
                name = it.name,
                price = it.price,
                mrp = it.mrp,
                stock = it.stock
            )
        }

        return CartItem(
            id = dto.id,
            product = product,
            selectedVariant = variant,
            quantity = dto.quantity,
            savedForLater = dto.savedForLater
        )
    }

    private fun mapDtoToSummary(dto: CartSummaryDto): CartSummary {
        val items = dto.items.map { mapDtoToCartItem(it) }
        return CartSummary(
            items = items,
            subtotal = dto.subtotal,
            totalDiscount = dto.totalDiscount,
            couponDiscount = dto.couponDiscount,
            appliedCouponCode = dto.appliedCouponCode,
            taxAmount = dto.taxAmount,
            deliveryFee = dto.deliveryFee,
            grandTotal = dto.grandTotal,
            totalSavings = dto.totalSavings
        )
    }

    override fun getCartItems(): Flow<List<CartItem>> = cartItemsFlow

    override fun getCartSummary(): Flow<CartSummary> {
        return cartItemsFlow.map { items ->
            cartSummaryFlow.value ?: calculateOrderTotalUseCase(items, appliedCouponFlow.value)
        }
    }

    override suspend fun addToCart(
        product: Product,
        variant: ProductVariant?,
        quantity: Int
    ): Result<Unit> {
        return try {
            val response = cartApiService.addToCart(
                AddToCartRequestDto(
                    productId = product.id,
                    variantId = variant?.id,
                    quantity = quantity
                )
            )
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val summaryDto = response.body()!!.data!!
                val summary = mapDtoToSummary(summaryDto)
                cartSummaryFlow.value = summary
                cartItemsFlow.value = summary.items
                Result.success(Unit)
            } else {
                localAddToCart(product, variant, quantity)
            }
        } catch (e: Exception) {
            localAddToCart(product, variant, quantity)
        }
    }

    private fun localAddToCart(product: Product, variant: ProductVariant?, quantity: Int): Result<Unit> {
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
        cartSummaryFlow.value = calculateOrderTotalUseCase(current, appliedCouponFlow.value)
        return Result.success(Unit)
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            val response = cartApiService.updateQuantity(cartItemId, UpdateQuantityRequestDto(quantity))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val summaryDto = response.body()!!.data!!
                val summary = mapDtoToSummary(summaryDto)
                cartSummaryFlow.value = summary
                cartItemsFlow.value = summary.items
                Result.success(Unit)
            } else {
                localUpdateQuantity(cartItemId, quantity)
            }
        } catch (e: Exception) {
            localUpdateQuantity(cartItemId, quantity)
        }
    }

    private fun localUpdateQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        val current = cartItemsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == cartItemId }
        if (index >= 0) {
            if (quantity <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = quantity)
            }
            cartItemsFlow.value = current
            cartSummaryFlow.value = calculateOrderTotalUseCase(current, appliedCouponFlow.value)
        }
        return Result.success(Unit)
    }

    override suspend fun removeFromCart(cartItemId: String): Result<Unit> {
        return try {
            val response = cartApiService.removeFromCart(cartItemId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val summaryDto = response.body()!!.data!!
                val summary = mapDtoToSummary(summaryDto)
                cartSummaryFlow.value = summary
                cartItemsFlow.value = summary.items
                Result.success(Unit)
            } else {
                cartItemsFlow.value = cartItemsFlow.value.filter { it.id != cartItemId }
                cartSummaryFlow.value = calculateOrderTotalUseCase(cartItemsFlow.value, appliedCouponFlow.value)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            cartItemsFlow.value = cartItemsFlow.value.filter { it.id != cartItemId }
            cartSummaryFlow.value = calculateOrderTotalUseCase(cartItemsFlow.value, appliedCouponFlow.value)
            Result.success(Unit)
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartApiService.clearCart()
            cartItemsFlow.value = emptyList()
            appliedCouponFlow.value = null
            cartSummaryFlow.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            cartItemsFlow.value = emptyList()
            appliedCouponFlow.value = null
            cartSummaryFlow.value = null
            Result.success(Unit)
        }
    }

    override suspend fun applyCoupon(couponCode: String): Result<CartSummary> {
        return try {
            val response = cartApiService.applyCoupon(ApplyCouponRequestDto(couponCode))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val summary = mapDtoToSummary(response.body()!!.data!!)
                cartSummaryFlow.value = summary
                cartItemsFlow.value = summary.items
                Result.success(summary)
            } else {
                localApplyCoupon(couponCode)
            }
        } catch (e: Exception) {
            localApplyCoupon(couponCode)
        }
    }

    private fun localApplyCoupon(couponCode: String): Result<CartSummary> {
        val coupon = MockData.coupons.find { it.code.equals(couponCode, ignoreCase = true) }
            ?: return Result.failure(Exception("Invalid coupon code."))
        appliedCouponFlow.value = coupon
        val summary = calculateOrderTotalUseCase(cartItemsFlow.value, coupon)
        cartSummaryFlow.value = summary
        return Result.success(summary)
    }

    override suspend fun removeCoupon(): Result<CartSummary> {
        return try {
            val response = cartApiService.removeCoupon()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val summary = mapDtoToSummary(response.body()!!.data!!)
                cartSummaryFlow.value = summary
                cartItemsFlow.value = summary.items
                Result.success(summary)
            } else {
                appliedCouponFlow.value = null
                val summary = calculateOrderTotalUseCase(cartItemsFlow.value, null)
                cartSummaryFlow.value = summary
                Result.success(summary)
            }
        } catch (e: Exception) {
            appliedCouponFlow.value = null
            val summary = calculateOrderTotalUseCase(cartItemsFlow.value, null)
            cartSummaryFlow.value = summary
            Result.success(summary)
        }
    }

    override suspend fun toggleSaveForLater(cartItemId: String): Result<Unit> {
        val current = cartItemsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == cartItemId }
        if (index >= 0) {
            val item = current[index]
            current[index] = item.copy(savedForLater = !item.savedForLater)
            cartItemsFlow.value = current
            cartSummaryFlow.value = calculateOrderTotalUseCase(current, appliedCouponFlow.value)
        }
        return Result.success(Unit)
    }
}
