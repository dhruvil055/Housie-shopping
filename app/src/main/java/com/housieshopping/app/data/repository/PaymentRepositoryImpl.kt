package com.housieshopping.app.data.repository

import com.housieshopping.app.domain.model.PaymentOrder
import com.housieshopping.app.domain.model.PaymentResult
import com.housieshopping.app.domain.repository.PaymentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {

    override suspend fun createPaymentOrder(orderId: String, amount: Double): Result<PaymentOrder> {
        val pOrder = PaymentOrder(
            orderId = orderId,
            razorpayOrderId = "rzp_order_${System.currentTimeMillis()}",
            amount = amount,
            customerName = "Rahul Sharma",
            customerEmail = "rahul.sharma@example.com",
            customerPhone = "+91 98765 43210"
        )
        return Result.success(pOrder)
    }

    override suspend fun verifyPayment(
        paymentId: String,
        orderId: String,
        signature: String
    ): Result<PaymentResult> {
        return Result.success(PaymentResult.Success(paymentId, orderId, signature))
    }
}
