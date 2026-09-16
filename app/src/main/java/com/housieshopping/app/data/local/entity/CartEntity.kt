package com.housieshopping.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val variantId: String? = null,
    val quantity: Int = 1,
    val savedForLater: Boolean = false
)
