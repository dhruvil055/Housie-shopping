package com.housieshopping.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val createdAt: String,
    val statusName: String,
    val totalAmount: Double,
    val isPaid: Boolean,
    val jsonContent: String // Complete JSON snapshot of order
)
