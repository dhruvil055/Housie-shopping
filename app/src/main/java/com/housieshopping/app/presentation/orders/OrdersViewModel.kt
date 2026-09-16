package com.housieshopping.app.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Order
import com.housieshopping.app.domain.model.OrderTracking
import com.housieshopping.app.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val selectedOrder: Order? = null,
    val trackingInfo: OrderTracking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            orderRepository.getOrders().collect { list ->
                _uiState.value = _uiState.value.copy(orders = list)
            }
        }
    }

    fun loadOrderById(orderId: String) {
        viewModelScope.launch {
            val result = orderRepository.getOrderById(orderId)
            _uiState.value = _uiState.value.copy(selectedOrder = result.getOrNull())
        }
    }

    fun startTracking(orderId: String) {
        viewModelScope.launch {
            orderRepository.trackOrder(orderId).collect { tracking ->
                _uiState.value = _uiState.value.copy(trackingInfo = tracking)
            }
        }
    }

    fun cancelOrder(orderId: String, reason: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId, reason)
            onSuccess()
        }
    }

    fun reorder(orderId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            orderRepository.reorder(orderId)
            onSuccess()
        }
    }
}
