package com.housieshopping.app.domain.model

enum class SortOption {
    POPULARITY,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    RATING_HIGH,
    NEWEST,
    DISCOUNT
}

data class FilterOptions(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val selectedBrands: Set<String> = emptySet(),
    val minRating: Float? = null,
    val inStockOnly: Boolean = false,
    val fastDeliveryOnly: Boolean = false,
    val minDiscountPercentage: Int? = null,
    val selectedCategoryIds: Set<String> = emptySet(),
    val sortBy: SortOption = SortOption.POPULARITY
)
