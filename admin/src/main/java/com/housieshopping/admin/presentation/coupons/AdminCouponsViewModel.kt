package com.housieshopping.admin.presentation.coupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminCoupon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminCouponsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    val coupons: StateFlow<List<AdminCoupon>> = adminRepository.getAllCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCoupon(code: String, percent: Int, minAmount: Double) {
        viewModelScope.launch {
            val newCoupon = AdminCoupon(
                id = "",
                code = code,
                discountPercent = percent,
                maxDiscountAmount = 1500.0,
                minOrderAmount = minAmount,
                validUntil = "31 Dec 2026",
                isActive = true
            )
            adminRepository.saveCoupon(newCoupon)
        }
    }

    fun toggleCoupon(coupon: AdminCoupon) {
        viewModelScope.launch {
            adminRepository.toggleCoupon(coupon.id, !coupon.isActive)
        }
    }
}
