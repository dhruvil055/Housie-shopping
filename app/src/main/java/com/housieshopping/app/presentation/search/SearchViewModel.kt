package com.housieshopping.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.FilterOptions
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.ProductRepository
import com.housieshopping.app.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val recentSearches: List<String> = emptyList(),
    val searchResults: List<Product> = emptyList(),
    val filterOptions: FilterOptions = FilterOptions(),
    val wishlistedIds: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        loadRecentSearches()
        observeWishlist()
    }

    fun loadRecentSearches() {
        viewModelScope.launch {
            val searches = productRepository.getRecentSearches()
            _uiState.value = _uiState.value.copy(recentSearches = searches)
        }
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(query = query, isLoading = true)
        viewModelScope.launch {
            val result = productRepository.searchProducts(query, _uiState.value.filterOptions).getOrDefault(emptyList())
            val searches = productRepository.getRecentSearches()
            _uiState.value = _uiState.value.copy(
                searchResults = result,
                recentSearches = searches,
                isLoading = false
            )
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            productRepository.clearSearchHistory()
            _uiState.value = _uiState.value.copy(recentSearches = emptyList())
        }
    }

    fun updateFilters(options: FilterOptions) {
        _uiState.value = _uiState.value.copy(filterOptions = options)
        search(_uiState.value.query)
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
