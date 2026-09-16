package com.housieshopping.app.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.CartItem
import com.housieshopping.app.domain.model.CartSummary
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.usecase.CartValidationResult
import com.housieshopping.app.domain.usecase.ValidateCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val summary: CartSummary? = null,
    val couponCodeInput: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val validateCartUseCase: ValidateCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _uiState.value = _uiState.value.copy(items = items)
            }
        }
        viewModelScope.launch {
            cartRepository.getCartSummary().collect { summary ->
                _uiState.value = _uiState.value.copy(summary = summary)
            }
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(cartItemId, quantity)
        }
    }

    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItemId)
        }
    }

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val result = cartRepository.applyCoupon(code)
            result.onFailure {
                _uiState.value = _uiState.value.copy(errorMessage = it.message)
            }
        }
    }

    fun removeCoupon() {
        viewModelScope.launch {
            cartRepository.removeCoupon()
        }
    }

    fun validateAndProceed(onValid: () -> Unit) {
        val validation = validateCartUseCase(_uiState.value.items)
        when (validation) {
            is CartValidationResult.Valid -> onValid()
            is CartValidationResult.Invalid -> {
                _uiState.value = _uiState.value.copy(errorMessage = validation.errors.firstOrNull())
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
