package com.housieshopping.app.presentation.coupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Coupon
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.CouponRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CouponsViewModel @Inject constructor(
    private val couponRepository: CouponRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _coupons = MutableStateFlow<List<Coupon>>(emptyList())
    val coupons: StateFlow<List<Coupon>> = _coupons

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val result = couponRepository.getAvailableCoupons().getOrDefault(emptyList())
            _coupons.value = result
        }
    }

    fun applyCoupon(code: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            cartRepository.applyCoupon(code)
            onSuccess()
        }
    }
}
