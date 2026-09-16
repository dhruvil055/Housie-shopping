package com.housieshopping.app.domain.model

data class Banner(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val imageUrl: String,
    val targetType: String, // e.g. "PRODUCT", "CATEGORY", "COUPON"
    val targetId: String
)
