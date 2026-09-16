package com.housieshopping.app.domain.model

data class ProductVariant(
    val id: String,
    val sku: String,
    val name: String, // e.g. "50 kg", "Red", "10mm"
    val price: Double,
    val mrp: Double,
    val stock: Int,
    val imageUrl: String? = null,
    val dimensions: String? = null,
    val weight: String? = null,
    val packSize: String? = null
) {
    val discountPercentage: Int
        get() = if (mrp > price) (((mrp - price) / mrp) * 100).toInt() else 0
}
