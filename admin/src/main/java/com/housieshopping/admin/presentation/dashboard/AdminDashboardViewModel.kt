package com.housieshopping.admin.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.preferences.AdminPreferences
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminOrder
import com.housieshopping.admin.domain.model.AdminProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val products: List<AdminProduct> = emptyList(),
    val recentOrders: List<AdminOrder> = emptyList(),
    val totalRevenue: Double = 0.0,
    val pendingOrdersCount: Int = 0,
    val lowStockCount: Int = 0,
    val totalProductsCount: Int = 0,
    val adminName: String = "Store Manager"
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val adminPreferences: AdminPreferences
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        adminRepository.getAllProducts(),
        adminRepository.getAllOrders(),
        adminPreferences.adminName
    ) { products, orders, adminName ->
        val revenue = orders.filter { it.orderStatus != "Cancelled" }.sumOf { it.totalAmount }
        val pendingCount = orders.count { it.orderStatus == "Pending" || it.orderStatus == "Packing" }
        val lowStock = products.count { it.stock < 10 || !it.isActive }

        DashboardUiState(
            products = products,
            recentOrders = orders.take(5),
            totalRevenue = revenue,
            pendingOrdersCount = pendingCount,
            lowStockCount = lowStock,
            totalProductsCount = products.size,
            adminName = adminName
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            adminRepository.updateOrderStatus(orderId, newStatus, "TRK-${System.currentTimeMillis().toString().takeLast(6)}", "Housie Direct Fleet")
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminPreferences.logout()
            adminRepository.logAction("ADMIN_LOGOUT", "Admin logged out successfully")
            onSuccess()
        }
    }
}
