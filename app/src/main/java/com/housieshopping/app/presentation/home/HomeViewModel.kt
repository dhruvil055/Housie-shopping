package com.housieshopping.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Banner
import com.housieshopping.app.domain.model.Brand
import com.housieshopping.app.domain.model.Category
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.ProductRepository
import com.housieshopping.app.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val banners: List<Banner> = emptyList(),
    val categories: List<Category> = emptyList(),
    val brands: List<Brand> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val bestSellers: List<Product> = emptyList(),
    val dealsOfDay: List<Product> = emptyList(),
    val wishlistedIds: Set<String> = emptySet(),
    val locationName: String = "DLF Phase 2, Gurugram"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        observeWishlist()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val banners = productRepository.getBanners().getOrDefault(emptyList())
            val categories = productRepository.getCategories().getOrDefault(emptyList())
            val brands = productRepository.getBrands().getOrDefault(emptyList())
            val featured = productRepository.getFeaturedProducts().getOrDefault(emptyList())
            val bestSellers = productRepository.getBestSellers().getOrDefault(emptyList())
            val deals = productRepository.getDealsOfDay().getOrDefault(emptyList())

            _uiState.value = HomeUiState(
                isLoading = false,
                banners = banners,
                categories = categories,
                brands = brands,
                featuredProducts = featured,
                bestSellers = bestSellers,
                dealsOfDay = deals
            )
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            wishlistRepository.getWishlist().collect { list ->
                _uiState.value = _uiState.value.copy(
                    wishlistedIds = list.map { it.id }.toSet()
                )
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
