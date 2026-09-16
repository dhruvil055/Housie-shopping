package com.housieshopping.app.domain.model

data class Category(
    val id: String,
    val name: String,
    val imageUrl: String,
    val parentId: String? = null,
    val subcategories: List<Category> = emptyList(),
    val itemCount: Int = 0
)
