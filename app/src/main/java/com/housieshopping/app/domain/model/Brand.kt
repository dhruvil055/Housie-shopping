package com.housieshopping.app.domain.model

data class Brand(
    val id: String,
    val name: String,
    val logoUrl: String,
    val isPopular: Boolean = false
)
