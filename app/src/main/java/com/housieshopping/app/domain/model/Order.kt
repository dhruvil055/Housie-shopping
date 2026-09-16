package com.housieshopping.app.domain.model

enum class OrderStatus {
    PLACED,
    CONFIRMED,
    PACKING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

data class OrderTimelineStep(
    val status: OrderStatus,
    val title: String,
    val description: String,
    val timestamp: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class OrderItem(
    val productId: String,
    val productTitle: String,
    val productImage: String,
    val variantName: String? = null,
    val unitPrice: Double,
    val quantity: Int,
    val totalPrice: Double
)

data class Order(
    val id: String,
    val orderNumber: String,
    val createdAt: String,
    val items: List<OrderItem>,
    val deliveryAddress: Address,
    val paymentMethod: String, // e.g. "UPI", "Razorpay Card", "Cash on Delivery"
    val paymentId: String? = null,
    val isPaid: Boolean,
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val status: OrderStatus,
    val estimatedDeliveryDate: String,
    val invoiceUrl: String? = null,
    val isReturnable: Boolean = false,
    val timeline: List<OrderTimelineStep> = emptyList()
)
