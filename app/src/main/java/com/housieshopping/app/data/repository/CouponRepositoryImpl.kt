package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.Coupon
import com.housieshopping.app.domain.repository.CouponRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CouponRepositoryImpl @Inject constructor() : CouponRepository {

    override suspend fun getAvailableCoupons(): Result<List<Coupon>> {
        return Result.success(MockData.coupons)
    }

    override suspend fun validateCoupon(code: String, orderAmount: Double): Result<Coupon> {
        val coupon = MockData.coupons.find { it.code.equals(code, ignoreCase = true) }
            ?: return Result.failure(Exception("Coupon code '$code' does not exist."))
        if (orderAmount < coupon.minOrderAmount) {
            return Result.failure(Exception("Minimum order of ₹${coupon.minOrderAmount.toInt()} required for this coupon."))
        }
        return Result.success(coupon)
    }
}
