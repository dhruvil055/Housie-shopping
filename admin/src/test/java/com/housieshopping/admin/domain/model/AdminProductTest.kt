package com.housieshopping.admin.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminProductTest {

    @Test
    fun discountPercent_calculatedCorrectly() {
        val product = AdminProduct(
            id = "p1",
            title = "Test Cement",
            price = 400.0,
            mrp = 500.0,
            stock = 100,
            imageUrl = ""
        )

        // (500 - 400) / 500 * 100 = 20%
        assertEquals(20, product.discountPercent)
    }

    @Test
    fun discountPercent_whenPriceEqualsMrp_returnsZero() {
        val product = AdminProduct(
            id = "p2",
            title = "Test Cement",
            price = 500.0,
            mrp = 500.0,
            stock = 100,
            imageUrl = ""
        )

        assertEquals(0, product.discountPercent)
    }

    @Test
    fun adminOrder_defaultStatusAndLogistics() {
        val order = AdminOrder(
            id = "ord1",
            customerName = "ABC Builders",
            customerPhone = "+91 9876543210",
            deliveryAddress = "Plot 4, Site 2",
            itemsSummary = "50 Bags Cement",
            totalAmount = 20000.0
        )

        assertEquals("Pending", order.orderStatus)
        assertEquals("Housie Direct Fleet", order.logisticsPartner)
        assertEquals("Paid", order.paymentStatus)
    }
}