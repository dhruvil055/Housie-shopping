package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.model.Order
import com.housieshopping.app.domain.model.OrderStatus
import com.housieshopping.app.domain.model.OrderTracking
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order>
    suspend fun createOrder(
        address: Address,
        paymentMethod: String,
        couponCode: String?
    ): Result<Order>
    suspend fun cancelOrder(orderId: String, reason: String): Result<Boolean>
    suspend fun trackOrder(orderId: String): Flow<OrderTracking>
    suspend fun reorder(orderId: String): Result<Unit>
}
