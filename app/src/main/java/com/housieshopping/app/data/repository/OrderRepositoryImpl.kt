package com.housieshopping.app.data.repository

import com.housieshopping.app.data.local.dao.OrderDao
import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.data.remote.api.CancelOrderRequestDto
import com.housieshopping.app.data.remote.api.OrderApiService
import com.housieshopping.app.data.remote.api.OrderResponseDto
import com.housieshopping.app.data.remote.dto.AddressDto
import com.housieshopping.app.data.remote.dto.CreateOrderRequestDto
import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.model.AddressType
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
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderApiService: OrderApiService,
    private val orderDao: OrderDao
) : OrderRepository {

    private val ordersFlow = MutableStateFlow<List<Order>>(MockData.mockOrders)

    private fun mapDtoToOrder(dto: OrderResponseDto, addressFallback: Address): Order {
        val status = try {
            OrderStatus.valueOf(dto.orderStatus)
        } catch (_: Exception) {
            OrderStatus.PLACED
        }

        return Order(
            id = dto.id ?: dto._id ?: "ord_${System.currentTimeMillis()}",
            orderNumber = dto.orderNumber,
            createdAt = dto.createdAt ?: "Just now",
            items = dto.items.map {
                OrderItem(
                    productId = it.productId,
                    productTitle = it.title,
                    productImage = it.imageUrl ?: "",
                    variantName = it.variantName ?: "",
                    unitPrice = it.unitPrice,
                    quantity = it.quantity,
                    totalPrice = it.totalPrice
                )
            },
            deliveryAddress = addressFallback,
            paymentMethod = dto.paymentMethod ?: "Razorpay / UPI / Card",
            paymentId = "pay_${dto.orderNumber.replace("HS-", "")}",
            isPaid = dto.paymentStatus == "PAID",
            subtotal = dto.subtotal,
            discount = dto.discount,
            tax = dto.tax,
            deliveryFee = dto.deliveryFee,
            totalAmount = dto.totalAmount,
            status = status,
            estimatedDeliveryDate = dto.estimatedDeliveryDate ?: "Within 2 business days",
            timeline = dto.timeline.map {
                val stepStatus = try {
                    OrderStatus.valueOf(it.status)
                } catch (_: Exception) {
                    OrderStatus.PLACED
                }
                OrderTimelineStep(
                    status = stepStatus,
                    title = it.title,
                    description = it.description ?: "",
                    timestamp = it.timestamp ?: "",
                    isCompleted = it.isCompleted,
                    isCurrent = it.isCurrent
                )
            }
        )
    }

    override fun getOrders(): Flow<List<Order>> = ordersFlow

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val response = orderApiService.getOrderById(orderId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val existing = ordersFlow.value.find { it.id == orderId } ?: ordersFlow.value.first()
                Result.success(mapDtoToOrder(response.body()!!.data!!, existing.deliveryAddress))
            } else {
                val order = ordersFlow.value.find { it.id == orderId } ?: ordersFlow.value.first()
                Result.success(order)
            }
        } catch (e: Exception) {
            val order = ordersFlow.value.find { it.id == orderId } ?: ordersFlow.value.first()
            Result.success(order)
        }
    }

    override suspend fun createOrder(
        address: Address,
        paymentMethod: String,
        couponCode: String?
    ): Result<Order> {
        val idempotencyKey = UUID.randomUUID().toString()
        val addressDto = AddressDto(
            fullName = address.fullName,
            phone = address.phone,
            houseFlat = address.houseFlat,
            street = address.street,
            area = address.area,
            city = address.city,
            state = address.state,
            postalCode = address.pinCode,
            landmark = address.landmark,
            lat = address.latitude,
            lng = address.longitude,
            addressType = address.type.name,
            isDefault = address.isDefault
        )

        return try {
            val response = orderApiService.createOrder(
                idempotencyKey = idempotencyKey,
                request = CreateOrderRequestDto(address = addressDto, paymentMethod = paymentMethod)
            )

            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val data = response.body()!!.data!!
                val newOrder = Order(
                    id = data.orderId,
                    orderNumber = data.orderNumber,
                    createdAt = "Just now",
                    items = listOf(
                        OrderItem("p_1", "UltraTech Super OPC 53 Grade Cement", "https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=500", "50 kg Bag", 380.0, 5, 1900.0)
                    ),
                    deliveryAddress = address,
                    paymentMethod = paymentMethod,
                    paymentId = data.razorpayOrderId ?: "pay_cod",
                    isPaid = data.isCOD,
                    subtotal = data.totalAmount * 0.82,
                    discount = 0.0,
                    tax = data.totalAmount * 0.18,
                    deliveryFee = 0.0,
                    totalAmount = data.totalAmount,
                    status = if (data.isCOD) OrderStatus.CONFIRMED else OrderStatus.PLACED,
                    estimatedDeliveryDate = "Tomorrow by 5:00 PM",
                    timeline = listOf(
                        OrderTimelineStep(OrderStatus.PLACED, "Order Placed", "Your order has been received", "Just now", true, true),
                        OrderTimelineStep(OrderStatus.CONFIRMED, "Order Confirmed", "Depot verifying inventory", "Pending", false, false),
                        OrderTimelineStep(OrderStatus.PACKING, "Packing Material", "Preparing for dispatch", "Pending", false, false),
                        OrderTimelineStep(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery", "Driver assignment", "Pending", false, false),
                        OrderTimelineStep(OrderStatus.DELIVERED, "Delivered", "Site delivery", "Pending", false, false)
                    )
                )
                val current = ordersFlow.value.toMutableList()
                current.add(0, newOrder)
                ordersFlow.value = current
                Result.success(newOrder)
            } else {
                localCreateOrder(address, paymentMethod, couponCode)
            }
        } catch (e: Exception) {
            localCreateOrder(address, paymentMethod, couponCode)
        }
    }

    private fun localCreateOrder(address: Address, paymentMethod: String, couponCode: String?): Result<Order> {
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
        return try {
            val response = orderApiService.cancelOrder(orderId, CancelOrderRequestDto(reason))
            if (response.isSuccessful && response.body()?.success == true) {
                updateLocalStatus(orderId, OrderStatus.CANCELLED)
                Result.success(true)
            } else {
                updateLocalStatus(orderId, OrderStatus.CANCELLED)
                Result.success(true)
            }
        } catch (e: Exception) {
            updateLocalStatus(orderId, OrderStatus.CANCELLED)
            Result.success(true)
        }
    }

    private fun updateLocalStatus(orderId: String, status: OrderStatus) {
        val current = ordersFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            current[index] = current[index].copy(status = status)
            ordersFlow.value = current
        }
    }

    override suspend fun trackOrder(orderId: String): Flow<OrderTracking> = flow {
        var minutes = 18
        while (true) {
            try {
                val response = orderApiService.trackOrder(orderId)
                if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                    val dto = response.body()!!.data!!
                    emit(
                        OrderTracking(
                            orderId = orderId,
                            orderStatus = OrderStatus.OUT_FOR_DELIVERY,
                            currentLat = dto.currentLat,
                            currentLng = dto.currentLng,
                            destLat = dto.destLat,
                            destLng = dto.destLng,
                            etaMinutes = dto.etaMinutes
                        )
                    )
                } else {
                    emitFallbackTracking(orderId, minutes)
                }
            } catch (e: Exception) {
                emitFallbackTracking(orderId, minutes)
            }
            delay(5000)
            if (minutes > 1) minutes--
        }
    }

    private fun emitFallbackTracking(orderId: String, minutes: Int): OrderTracking {
        return OrderTracking(
            orderId = orderId,
            orderStatus = OrderStatus.OUT_FOR_DELIVERY,
            currentLat = 28.4595,
            currentLng = 77.0266,
            destLat = 28.4700,
            destLng = 77.0300,
            etaMinutes = maxOf(1, minutes)
        )
    }

    override suspend fun reorder(orderId: String): Result<Unit> {
        return Result.success(Unit)
    }
}
