package com.housieshopping.admin.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryUiState(
    val products: List<AdminProduct> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false
)

@HiltViewModel
class AdminInventoryViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")

    val uiState: StateFlow<InventoryUiState> = combine(
        adminRepository.getAllProducts(),
        _searchQuery,
        _selectedCategory
    ) { products, query, category ->
        val filtered = products.filter { product ->
            val matchesQuery = product.title.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || product.categoryName.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }

        InventoryUiState(
            products = filtered,
            searchQuery = query,
            selectedCategory = category
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryChange(category: String) {
        _selectedCategory.value = category
    }

    fun toggleStock(product: AdminProduct) {
        viewModelScope.launch {
            adminRepository.toggleStockStatus(product.id, !product.isActive)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            adminRepository.deleteProduct(productId)
        }
    }

    fun saveProduct(product: AdminProduct, onComplete: () -> Unit) {
        viewModelScope.launch {
            adminRepository.saveProduct(product)
            onComplete()
        }
    }
}
