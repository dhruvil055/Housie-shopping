package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.CartItem
import com.housieshopping.app.domain.model.Coupon
import com.housieshopping.app.domain.model.CouponType
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.Seller
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateOrderTotalUseCaseTest {

    private lateinit var calculateOrderTotalUseCase: CalculateOrderTotalUseCase
    private lateinit var dummyProduct: Product

    @Before
    fun setUp() {
        calculateOrderTotalUseCase = CalculateOrderTotalUseCase()
        dummyProduct = Product(
            id = "p1",
            title = "UltraTech Cement 53 Grade",
            description = "High grade cement",
            brand = "UltraTech",
            categoryId = "cat-cement",
            categoryName = "Cement",
            price = 400.0,
            mrp = 450.0,
            rating = 4.8,
            reviewCount = 50,
            stock = 100,
            images = listOf("https://example.com/cement.jpg"),
            seller = Seller("s1", "Official Store", 4.9, "Depot")
        )
    }

    @Test
    fun calculateTotal_standardOrder_belowFreeDeliveryThreshold() {
        // 2 bags @ 400 = 800. Less than 2000 => delivery fee 150
        val items = listOf(
            CartItem(id = "c1", product = dummyProduct, quantity = 2)
        )

        val summary = calculateOrderTotalUseCase(items)

        assertEquals(800.0, summary.subtotal, 0.001)
        assertEquals(100.0, summary.totalDiscount, 0.001) // 2 * (450 - 400)
        assertEquals(0.0, summary.couponDiscount, 0.001)
        assertEquals(150.0, summary.deliveryFee, 0.001)
        assertEquals(144.0, summary.taxAmount, 0.001) // 18% of 800
        assertEquals(800.0 + 144.0 + 150.0, summary.grandTotal, 0.001)
    }

    @Test
    fun calculateTotal_aboveFreeDeliveryThreshold() {
        // 6 bags @ 400 = 2400 > 2000 => free delivery (delivery fee 0)
        val items = listOf(
            CartItem(id = "c1", product = dummyProduct, quantity = 6)
        )

        val summary = calculateOrderTotalUseCase(items)

        assertEquals(2400.0, summary.subtotal, 0.001)
        assertEquals(0.0, summary.deliveryFee, 0.001)
        assertEquals(432.0, summary.taxAmount, 0.001) // 18% of 2400
        assertEquals(2400.0 + 432.0, summary.grandTotal, 0.001)
    }

    @Test
    fun calculateTotal_withPercentageCouponCapped() {
        // Subtotal = 2400. Coupon BUILD10 gives 10% = 240, but capped at 200
        val items = listOf(
            CartItem(id = "c1", product = dummyProduct, quantity = 6)
        )
        val coupon = Coupon(
            id = "cp1",
            code = "BUILD10",
            description = "10% off",
            type = CouponType.PERCENTAGE,
            discountValue = 10.0,
            minOrderAmount = 1000.0,
            maxDiscountAmount = 200.0,
            expiryDate = "2026-12-31"
        )

        val summary = calculateOrderTotalUseCase(items, coupon)

        assertEquals(200.0, summary.couponDiscount, 0.001)
        val taxable = 2400.0 - 200.0
        val tax = taxable * 0.18
        assertEquals(tax, summary.taxAmount, 0.001)
        assertEquals(taxable + tax, summary.grandTotal, 0.001)
    }

    @Test
    fun calculateTotal_excludesSavedForLaterItems() {
        val activeItem = CartItem(id = "c1", product = dummyProduct, quantity = 1, savedForLater = false)
        val savedItem = CartItem(id = "c2", product = dummyProduct, quantity = 5, savedForLater = true)

        val summary = calculateOrderTotalUseCase(listOf(activeItem, savedItem))

        assertEquals(400.0, summary.subtotal, 0.001)
        assertEquals(150.0, summary.deliveryFee, 0.001)
    }
}