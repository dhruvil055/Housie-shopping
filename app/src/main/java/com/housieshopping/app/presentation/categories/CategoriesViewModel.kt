package com.housieshopping.app.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Category
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.ProductRepository
import com.housieshopping.app.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val categoryProducts: List<Product> = emptyList(),
    val wishlistedIds: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState

    init {
        loadCategories()
        observeWishlist()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = productRepository.getCategories().getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(categories = result, isLoading = false)
        }
    }

    fun loadProductsForCategory(categoryId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = productRepository.getProductsByCategory(categoryId).getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(categoryProducts = result, isLoading = false)
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            wishlistRepository.getWishlist().collect { list ->
                _uiState.value = _uiState.value.copy(wishlistedIds = list.map { it.id }.toSet())
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(product, product.variants.firstOrNull(), 1)
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(product)
        }
    }
}
