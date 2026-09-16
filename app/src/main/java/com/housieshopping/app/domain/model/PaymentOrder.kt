package com.housieshopping.app.domain.model

data class PaymentOrder(
    val orderId: String,
    val razorpayOrderId: String,
    val amount: Double,
    val currency: String = "INR",
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String
)

sealed class PaymentResult {
    data class Success(val paymentId: String, val orderId: String, val signature: String) : PaymentResult()
    data class Error(val code: Int, val message: String) : PaymentResult()
    object Cancelled : PaymentResult()
}
