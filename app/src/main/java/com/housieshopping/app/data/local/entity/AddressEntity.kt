package com.housieshopping.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val houseFlat: String,
    val street: String,
    val area: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val landmark: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val typeName: String,
    val isDefault: Boolean
)
