package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.Coupon
import javax.inject.Inject

class ApplyCouponUseCase @Inject constructor() {

    operator fun invoke(coupon: Coupon, orderSubtotal: Double): Result<Double> {
        if (!coupon.isApplicable) {
            return Result.failure(Exception("This coupon is no longer active."))
        }
        if (orderSubtotal < coupon.minOrderAmount) {
            return Result.failure(Exception("Minimum order value of ₹${coupon.minOrderAmount.toInt()} required for this coupon."))
        }
        return Result.success(coupon.discountValue)
    }
}
