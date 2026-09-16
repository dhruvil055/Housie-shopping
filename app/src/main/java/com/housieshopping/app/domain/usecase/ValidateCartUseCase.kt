package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.CartItem
import javax.inject.Inject

sealed class CartValidationResult {
    object Valid : CartValidationResult()
    data class Invalid(val errors: List<String>) : CartValidationResult()
}

class ValidateCartUseCase @Inject constructor() {

    operator fun invoke(items: List<CartItem>): CartValidationResult {
        val activeItems = items.filter { !it.savedForLater }
        if (activeItems.isEmpty()) {
            return CartValidationResult.Invalid(listOf("Your cart is empty. Add items to proceed."))
        }

        val errors = mutableListOf<String>()
        for (item in activeItems) {
            val availableStock = item.selectedVariant?.stock ?: item.product.stock
            if (availableStock <= 0) {
                errors.add("Item '${item.product.title}' is out of stock.")
            } else if (item.quantity > availableStock) {
                errors.add("Only $availableStock units of '${item.product.title}' available in stock.")
            }
        }

        return if (errors.isEmpty()) CartValidationResult.Valid else CartValidationResult.Invalid(errors)
    }
}
