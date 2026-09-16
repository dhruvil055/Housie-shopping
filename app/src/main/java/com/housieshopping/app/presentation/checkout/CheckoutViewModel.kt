package com.housieshopping.app.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.model.CartSummary
import com.housieshopping.app.domain.model.Order
import com.housieshopping.app.domain.repository.AddressRepository
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.OrderRepository
import com.housieshopping.app.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val selectedAddress: Address? = null,
    val deliveryOption: String = "Standard Site Express (Within 24 Hours)",
    val selectedPaymentMethod: String = "UPI (Google Pay / PhonePe)",
    val cartSummary: CartSummary? = null,
    val createdOrder: Order? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            addressRepository.getDefaultAddress().collect { addr ->
                _uiState.value = _uiState.value.copy(selectedAddress = addr)
            }
        }
        viewModelScope.launch {
            cartRepository.getCartSummary().collect { summary ->
                _uiState.value = _uiState.value.copy(cartSummary = summary)
            }
        }
    }

    fun selectDeliveryOption(option: String) {
        _uiState.value = _uiState.value.copy(deliveryOption = option)
    }

    fun selectPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun placeOrder(onSuccess: (Order) -> Unit) {
        val address = _uiState.value.selectedAddress ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            val result = orderRepository.createOrder(
                address = address,
                paymentMethod = _uiState.value.selectedPaymentMethod,
                couponCode = _uiState.value.cartSummary?.appliedCouponCode
            )
            result.onSuccess { order ->
                cartRepository.clearCart()
                _uiState.value = _uiState.value.copy(createdOrder = order, isProcessing = false)
                onSuccess(order)
            }.onFailure {
                _uiState.value = _uiState.value.copy(errorMessage = it.message, isProcessing = false)
            }
        }
    }
}
