package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.Coupon

interface CouponRepository {
    suspend fun getAvailableCoupons(): Result<List<Coupon>>
    suspend fun validateCoupon(code: String, orderAmount: Double): Result<Coupon>
}
