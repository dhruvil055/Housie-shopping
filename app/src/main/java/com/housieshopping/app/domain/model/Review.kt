package com.housieshopping.app.domain.model

data class Review(
    val id: String,
    val productId: String,
    val userName: String,
    val userAvatarUrl: String? = null,
    val rating: Float,
    val title: String,
    val comment: String,
    val createdAt: String,
    val isVerifiedPurchase: Boolean = true,
    val imageUrls: List<String> = emptyList()
)
