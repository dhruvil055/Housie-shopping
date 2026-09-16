package com.housieshopping.admin.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersPipelineUiState(
    val orders: List<AdminOrder> = emptyList(),
    val selectedFilter: String = "All",
    val searchQuery: String = ""
)

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<OrdersPipelineUiState> = combine(
        adminRepository.getAllOrders(),
        _selectedFilter,
        _searchQuery
    ) { orders, filter, query ->
        val filtered = orders.filter { order ->
            val matchesFilter = filter == "All" || order.orderStatus.equals(filter, ignoreCase = true)
            val matchesQuery = order.id.contains(query, ignoreCase = true) ||
                    order.customerName.contains(query, ignoreCase = true) ||
                    order.customerPhone.contains(query)
            matchesFilter && matchesQuery
        }

        OrdersPipelineUiState(
            orders = filtered,
            selectedFilter = filter,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrdersPipelineUiState()
    )

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        trackingNo: String = "",
        logisticsPartner: String = "Housie Direct Fleet"
    ) {
        viewModelScope.launch {
            val finalTracking = trackingNo.ifEmpty { "TRK-${System.currentTimeMillis().toString().takeLast(6)}" }
            adminRepository.updateOrderStatus(orderId, newStatus, finalTracking, logisticsPartner)
        }
    }
}
