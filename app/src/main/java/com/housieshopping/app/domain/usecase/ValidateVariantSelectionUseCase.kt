package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.ProductVariant
import javax.inject.Inject

class ValidateVariantSelectionUseCase @Inject constructor() {
    operator fun invoke(product: Product, selectedVariant: ProductVariant?): Result<Unit> {
        if (product.variants.isNotEmpty() && selectedVariant == null) {
            return Result.failure(Exception("Please select a product variant (e.g. size/weight) before adding to cart."))
        }
        return Result.success(Unit)
    }
}
