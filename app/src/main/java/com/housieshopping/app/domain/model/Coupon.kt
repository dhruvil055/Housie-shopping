package com.housieshopping.app.domain.model

enum class CouponType {
    PERCENTAGE,
    FIXED_AMOUNT
}

data class Coupon(
    val id: String,
    val code: String,
    val description: String,
    val type: CouponType,
    val discountValue: Double,
    val minOrderAmount: Double,
    val maxDiscountAmount: Double? = null,
    val expiryDate: String,
    val isApplicable: Boolean = true,
    val isFirstOrderOnly: Boolean = false
)
