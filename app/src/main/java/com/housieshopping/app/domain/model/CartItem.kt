package com.housieshopping.app.domain.model

data class CartItem(
    val id: String,
    val product: Product,
    val selectedVariant: ProductVariant? = null,
    val quantity: Int = 1,
    val savedForLater: Boolean = false
) {
    val unitPrice: Double
        get() = selectedVariant?.price ?: product.price

    val unitMrp: Double
        get() = selectedVariant?.mrp ?: product.mrp

    val subtotal: Double
        get() = unitPrice * quantity

    val totalMrp: Double
        get() = unitMrp * quantity

    val totalSavings: Double
        get() = (unitMrp - unitPrice) * quantity
}

data class CartSummary(
    val items: List<CartItem>,
    val subtotal: Double,
    val totalDiscount: Double,
    val couponDiscount: Double = 0.0,
    val appliedCouponCode: String? = null,
    val taxAmount: Double,
    val deliveryFee: Double,
    val grandTotal: Double,
    val totalSavings: Double
)
