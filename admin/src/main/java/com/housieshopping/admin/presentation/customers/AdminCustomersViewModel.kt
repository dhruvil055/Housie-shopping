package com.housieshopping.admin.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminCustomer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomersUiState(
    val customers: List<AdminCustomer> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class AdminCustomersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<CustomersUiState> = combine(
        adminRepository.getAllCustomers(),
        _searchQuery
    ) { customers, query ->
        val filtered = customers.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.phone.contains(query)
        }
        CustomersUiState(customers = filtered, searchQuery = query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CustomersUiState())

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleCustomerStatus(customer: AdminCustomer) {
        viewModelScope.launch {
            adminRepository.toggleCustomerStatus(customer.id, !customer.isActive)
        }
    }
}
