package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.PaymentOrder
import com.housieshopping.app.domain.model.PaymentResult

interface PaymentRepository {
    suspend fun createPaymentOrder(orderId: String, amount: Double): Result<PaymentOrder>
    suspend fun verifyPayment(paymentId: String, orderId: String, signature: String): Result<PaymentResult>
}
