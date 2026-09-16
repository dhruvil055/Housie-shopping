package com.housieshopping.app.domain.model

enum class AddressType {
    HOME,
    WORK,
    SITE_LOCATION
}

data class Address(
    val id: String,
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
    val type: AddressType = AddressType.HOME,
    val isDefault: Boolean = false
) {
    val formattedAddress: String
        get() = "$houseFlat, $street, $area, $city, $state - $pinCode"
}
