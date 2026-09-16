package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.CartItem
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.Seller
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateCartUseCaseTest {

    private lateinit var validateCartUseCase: ValidateCartUseCase
    private lateinit var inStockProduct: Product
    private lateinit var outOfStockProduct: Product

    @Before
    fun setUp() {
        validateCartUseCase = ValidateCartUseCase()
        inStockProduct = Product(
            id = "p1",
            title = "Steel TMT Bar 12mm",
            description = "High ductile steel bar",
            brand = "Tata Tiscon",
            categoryId = "cat-steel",
            categoryName = "Steel",
            price = 600.0,
            mrp = 700.0,
            rating = 4.9,
            reviewCount = 100,
            stock = 25,
            images = listOf("https://example.com/steel.jpg"),
            seller = Seller("s1", "Tata Steel", 4.9, "Depot")
        )
        outOfStockProduct = inStockProduct.copy(id = "p2", title = "Paints 20L", stock = 0)
    }

    @Test
    fun validateCart_emptyCart_returnsInvalid() {
        val result = validateCartUseCase(emptyList())
        assertTrue(result is CartValidationResult.Invalid)
        val invalid = result as CartValidationResult.Invalid
        assertTrue(invalid.errors.any { it.contains("cart is empty", ignoreCase = true) })
    }

    @Test
    fun validateCart_inStockItems_returnsValid() {
        val items = listOf(
            CartItem(id = "c1", product = inStockProduct, quantity = 5)
        )
        val result = validateCartUseCase(items)
        assertTrue(result is CartValidationResult.Valid)
    }

    @Test
    fun validateCart_outOfStockItem_returnsInvalid() {
        val items = listOf(
            CartItem(id = "c1", product = outOfStockProduct, quantity = 1)
        )
        val result = validateCartUseCase(items)
        assertTrue(result is CartValidationResult.Invalid)
        val invalid = result as CartValidationResult.Invalid
        assertTrue(invalid.errors.any { it.contains("out of stock", ignoreCase = true) })
    }

    @Test
    fun validateCart_quantityExceedingStock_returnsInvalid() {
        val items = listOf(
            CartItem(id = "c1", product = inStockProduct, quantity = 30) // stock is 25
        )
        val result = validateCartUseCase(items)
        assertTrue(result is CartValidationResult.Invalid)
        val invalid = result as CartValidationResult.Invalid
        assertTrue(invalid.errors.any { it.contains("Only 25 units", ignoreCase = true) })
    }
}