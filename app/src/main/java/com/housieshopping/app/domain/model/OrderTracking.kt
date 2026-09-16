package com.housieshopping.app.domain.model

data class DeliveryPartnerInfo(
    val name: String,
    val phone: String,
    val vehicleNumber: String,
    val photoUrl: String? = null,
    val rating: Double = 4.8
)

data class OrderTracking(
    val orderId: String,
    val orderStatus: OrderStatus,
    val currentLat: Double,
    val currentLng: Double,
    val destLat: Double,
    val destLng: Double,
    val etaMinutes: Int,
    val partner: DeliveryPartnerInfo? = null,
    val isLive: Boolean = true
)
