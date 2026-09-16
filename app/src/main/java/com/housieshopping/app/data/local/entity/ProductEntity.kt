package com.housieshopping.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
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
    val primaryImage: String,
    val deliveryEstimateDays: Int,
    val isFeatured: Boolean,
    val isBestSeller: Boolean,
    val isDealOfDay: Boolean
)
