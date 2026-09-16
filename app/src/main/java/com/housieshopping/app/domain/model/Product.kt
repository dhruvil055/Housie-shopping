package com.housieshopping.app.domain.model

data class Specification(
    val key: String,
    val value: String
)

data class Seller(
    val id: String,
    val name: String,
    val rating: Double,
    val storeName: String
)

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val brand: String,
    val categoryId: String,
    val categoryName: String,
    val price: Double,
    val mrp: Double,
    val rating: Double,
    val reviewCount: Int,
    val stock: Int,
    val images: List<String>,
    val deliveryEstimateDays: Int = 2,
    val isFeatured: Boolean = false,
    val isBestSeller: Boolean = false,
    val isDealOfDay: Boolean = false,
    val isRecentlyViewed: Boolean = false,
    val seller: Seller,
    val variants: List<ProductVariant> = emptyList(),
    val specifications: List<Specification> = emptyList(),
    val tags: List<String> = emptyList()
) {
    val discountPercentage: Int
        get() = if (mrp > price) (((mrp - price) / mrp) * 100).toInt() else 0

    val isInStock: Boolean
        get() = stock > 0
}
