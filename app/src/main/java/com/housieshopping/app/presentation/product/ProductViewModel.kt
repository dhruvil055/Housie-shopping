package com.housieshopping.app.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.ProductVariant
import com.housieshopping.app.domain.model.Review
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.ProductRepository
import com.housieshopping.app.domain.repository.WishlistRepository
import com.housieshopping.app.domain.usecase.ValidateVariantSelectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductUiState(
    val product: Product? = null,
    val selectedVariant: ProductVariant? = null,
    val quantity: Int = 1,
    val reviews: List<Review> = emptyList(),
    val relatedProducts: List<Product> = emptyList(),
    val isWishlisted: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val validateVariantSelectionUseCase: ValidateVariantSelectionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val productRes = productRepository.getProductById(productId)
            val product = productRes.getOrNull()

            if (product != null) {
                val reviews = productRepository.getProductReviews(productId).getOrDefault(emptyList())
                val related = productRepository.getRelatedProducts(productId).getOrDefault(emptyList())

                wishlistRepository.isWishlisted(productId).collect { isWish ->
                    _uiState.value = ProductUiState(
                        product = product,
                        selectedVariant = product.variants.firstOrNull(),
                        reviews = reviews,
                        relatedProducts = related,
                        isWishlisted = isWish,
                        isLoading = false
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Product not found"
                )
            }
        }
    }

    fun selectVariant(variant: ProductVariant) {
        _uiState.value = _uiState.value.copy(selectedVariant = variant)
    }

    fun updateQuantity(quantity: Int) {
        if (quantity >= 1) {
            _uiState.value = _uiState.value.copy(quantity = quantity)
        }
    }

    fun addToCart(onSuccess: () -> Unit) {
        val product = _uiState.value.product ?: return
        val variant = _uiState.value.selectedVariant
        val validation = validateVariantSelectionUseCase(product, variant)

        validation.onFailure {
            _uiState.value = _uiState.value.copy(errorMessage = it.message)
            return
        }

        viewModelScope.launch {
            cartRepository.addToCart(product, variant, _uiState.value.quantity)
            onSuccess()
        }
    }

    fun toggleWishlist() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(product)
        }
    }

    fun addReview(rating: Float, title: String, comment: String) {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            productRepository.addProductReview(product.id, rating, title, comment)
            val updated = productRepository.getProductReviews(product.id).getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(reviews = updated)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
