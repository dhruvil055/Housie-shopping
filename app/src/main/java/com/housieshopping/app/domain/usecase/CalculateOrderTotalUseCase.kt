package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.CartItem
import com.housieshopping.app.domain.model.CartSummary
import com.housieshopping.app.domain.model.Coupon
import com.housieshopping.app.domain.model.CouponType
import javax.inject.Inject

class CalculateOrderTotalUseCase @Inject constructor() {

    operator fun invoke(
        items: List<CartItem>,
        appliedCoupon: Coupon? = null
    ): CartSummary {
        val activeItems = items.filter { !it.savedForLater }

        var subtotal = 0.0
        var totalMrp = 0.0

        for (item in activeItems) {
            subtotal += item.subtotal
            totalMrp += item.totalMrp
        }

        val totalDiscount = totalMrp - subtotal

        var couponDiscount = 0.0
        if (appliedCoupon != null && subtotal >= appliedCoupon.minOrderAmount) {
            couponDiscount = when (appliedCoupon.type) {
                CouponType.PERCENTAGE -> {
                    val calc = (subtotal * appliedCoupon.discountValue) / 100.0
                    if (appliedCoupon.maxDiscountAmount != null) {
                        minOf(calc, appliedCoupon.maxDiscountAmount)
                    } else calc
                }
                CouponType.FIXED_AMOUNT -> {
                    minOf(appliedCoupon.discountValue, subtotal)
                }
            }
        }

        val taxableAmount = maxOf(0.0, subtotal - couponDiscount)
        val taxAmount = taxableAmount * 0.18 // 18% GST for construction materials
        val deliveryFee = if (subtotal > 2000.0 || activeItems.isEmpty()) 0.0 else 150.0
        val grandTotal = taxableAmount + taxAmount + deliveryFee

        return CartSummary(
            items = items,
            subtotal = subtotal,
            totalDiscount = totalDiscount,
            couponDiscount = couponDiscount,
            appliedCouponCode = appliedCoupon?.code,
            taxAmount = taxAmount,
            deliveryFee = deliveryFee,
            grandTotal = grandTotal,
            totalSavings = totalDiscount + couponDiscount
        )
    }
}
