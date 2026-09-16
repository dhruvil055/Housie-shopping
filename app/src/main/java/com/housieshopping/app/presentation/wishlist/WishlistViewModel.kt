package com.housieshopping.app.presentation.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _wishlist = MutableStateFlow<List<Product>>(emptyList())
    val wishlist: StateFlow<List<Product>> = _wishlist

    init {
        loadWishlist()
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            wishlistRepository.getWishlist().collect { list ->
                _wishlist.value = list
            }
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(productId)
        }
    }

    fun moveToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(product, product.variants.firstOrNull(), 1)
            wishlistRepository.removeFromWishlist(product.id)
        }
    }
}
