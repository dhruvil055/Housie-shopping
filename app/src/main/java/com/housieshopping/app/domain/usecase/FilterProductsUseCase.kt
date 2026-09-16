package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.FilterOptions
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.SortOption
import javax.inject.Inject

class FilterProductsUseCase @Inject constructor() {

    operator fun invoke(products: List<Product>, options: FilterOptions): List<Product> {
        var filtered = products.filter { product ->
            val matchesMinPrice = options.minPrice == null || product.price >= options.minPrice
            val matchesMaxPrice = options.maxPrice == null || product.price <= options.maxPrice
            val matchesBrand = options.selectedBrands.isEmpty() || options.selectedBrands.contains(product.brand)
            val matchesRating = options.minRating == null || product.rating >= options.minRating
            val matchesStock = !options.inStockOnly || product.isInStock
            val matchesDelivery = !options.fastDeliveryOnly || product.deliveryEstimateDays <= 1
            val matchesDiscount = options.minDiscountPercentage == null || product.discountPercentage >= options.minDiscountPercentage
            val matchesCategory = options.selectedCategoryIds.isEmpty() || options.selectedCategoryIds.contains(product.categoryId)

            matchesMinPrice && matchesMaxPrice && matchesBrand && matchesRating &&
                    matchesStock && matchesDelivery && matchesDiscount && matchesCategory
        }

        filtered = when (options.sortBy) {
            SortOption.POPULARITY -> filtered.sortedByDescending { it.rating * it.reviewCount }
            SortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.price }
            SortOption.RATING_HIGH -> filtered.sortedByDescending { it.rating }
            SortOption.NEWEST -> filtered.sortedByDescending { it.id }
            SortOption.DISCOUNT -> filtered.sortedByDescending { it.discountPercentage }
        }

        return filtered
    }
}
