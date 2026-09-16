package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.model.Order
import com.housieshopping.app.domain.model.OrderItem
import com.housieshopping.app.domain.model.OrderStatus
import com.housieshopping.app.domain.model.OrderTimelineStep
import com.housieshopping.app.domain.model.OrderTracking
import com.housieshopping.app.domain.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor() : OrderRepository {

    private val ordersFlow = MutableStateFlow<List<Order>>(MockData.mockOrders)

    override fun getOrders(): Flow<List<Order>> = ordersFlow

    override suspend fun getOrderById(orderId: String): Result<Order> {
        val order = ordersFlow.value.find { it.id == orderId } ?: ordersFlow.value.first()
        return Result.success(order)
    }

    override suspend fun createOrder(
        address: Address,
        paymentMethod: String,
        couponCode: String?
    ): Result<Order> {
        val newOrder = Order(
            id = "ord_${System.currentTimeMillis()}",
            orderNumber = "HS-2026-${(1000..9999).random()}",
            createdAt = "Just now",
            items = listOf(
                OrderItem("p_1", MockData.products[0].title, MockData.products[0].images.first(), "50 kg Bag", 380.0, 5, 1900.0)
            ),
            deliveryAddress = address,
            paymentMethod = paymentMethod,
            paymentId = "pay_${System.currentTimeMillis()}",
            isPaid = paymentMethod != "Cash on Delivery",
            subtotal = 1900.0,
            discount = if (couponCode != null) 100.0 else 0.0,
            tax = 324.0,
            deliveryFee = 0.0,
            totalAmount = 2124.0,
            status = OrderStatus.PLACED,
            estimatedDeliveryDate = "Tomorrow by 5:00 PM",
            timeline = listOf(
                OrderTimelineStep(OrderStatus.PLACED, "Order Placed", "Your order has been received", "Just now", true, true),
                OrderTimelineStep(OrderStatus.CONFIRMED, "Order Confirmed", "Seller verifying stock", "Pending", false, false),
                OrderTimelineStep(OrderStatus.PACKING, "Packing Material", "Preparing for dispatch", "Pending", false, false),
                OrderTimelineStep(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery", "Driver assignment", "Pending", false, false),
                OrderTimelineStep(OrderStatus.DELIVERED, "Delivered", "Site delivery", "Pending", false, false)
            )
        )
        val current = ordersFlow.value.toMutableList()
        current.add(0, newOrder)
        ordersFlow.value = current
        return Result.success(newOrder)
    }

    override suspend fun cancelOrder(orderId: String, reason: String): Result<Boolean> {
        val current = ordersFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            val order = current[index]
            current[index] = order.copy(status = OrderStatus.CANCELLED)
            ordersFlow.value = current
        }
        return Result.success(true)
    }

    override suspend fun trackOrder(orderId: String): Flow<OrderTracking> = flow {
        var minutes = 18
        while (true) {
            emit(
                OrderTracking(
                    orderId = orderId,
                    orderStatus = OrderStatus.OUT_FOR_DELIVERY,
                    currentLat = 28.4595,
                    currentLng = 77.0266,
                    destLat = 28.4700,
                    destLng = 77.0300,
                    etaMinutes = maxOf(1, minutes)
                )
            )
            delay(5000)
            if (minutes > 1) minutes--
        }
    }

    override suspend fun reorder(orderId: String): Result<Unit> {
        return Result.success(Unit)
    }
}
