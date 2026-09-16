package com.housieshopping.app.domain.usecase

import com.housieshopping.app.domain.model.FilterOptions
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.Seller
import com.housieshopping.app.domain.model.SortOption
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterProductsUseCaseTest {

    private lateinit var filterProductsUseCase: FilterProductsUseCase
    private lateinit var sampleProducts: List<Product>

    @Before
    fun setUp() {
        filterProductsUseCase = FilterProductsUseCase()
        val seller = Seller("s1", "Official", 4.8, "Depot")
        sampleProducts = listOf(
            Product("1", "UltraTech Cement", "Desc", "UltraTech", "cat1", "Cement", 380.0, 420.0, 4.8, 50, 100, emptyList(), seller = seller),
            Product("2", "Tata Tiscon Rebar", "Desc", "Tata Tiscon", "cat2", "Steel", 650.0, 720.0, 4.9, 120, 50, emptyList(), seller = seller),
            Product("3", "Asian Paints Exterior", "Desc", "Asian Paints", "cat3", "Paints", 4800.0, 5500.0, 4.5, 30, 20, emptyList(), seller = seller),
            Product("4", "Ambuja Cement", "Desc", "Ambuja", "cat1", "Cement", 370.0, 410.0, 4.6, 40, 0, emptyList(), seller = seller)
        )
    }

    @Test
    fun filter_byBrand_returnsMatchingOnly() {
        val options = FilterOptions(selectedBrands = setOf("UltraTech"))
        val result = filterProductsUseCase(sampleProducts, options)

        assertEquals(1, result.size)
        assertEquals("UltraTech Cement", result[0].title)
    }

    @Test
    fun filter_byPriceRange_returnsInRange() {
        val options = FilterOptions(minPrice = 300.0, maxPrice = 500.0)
        val result = filterProductsUseCase(sampleProducts, options)

        assertEquals(2, result.size) // UltraTech (380) and Ambuja (370)
    }

    @Test
    fun filter_inStockOnly_excludesZeroStock() {
        val options = FilterOptions(inStockOnly = true)
        val result = filterProductsUseCase(sampleProducts, options)

        assertEquals(3, result.size)
        assert(!result.any { it.id == "4" }) // Ambuja has 0 stock
    }

    @Test
    fun filter_sortByPriceLowToHigh() {
        val options = FilterOptions(sortBy = SortOption.PRICE_LOW_HIGH)
        val result = filterProductsUseCase(sampleProducts, options)

        assertEquals(370.0, result[0].price, 0.001)
        assertEquals(380.0, result[1].price, 0.001)
        assertEquals(650.0, result[2].price, 0.001)
        assertEquals(4800.0, result[3].price, 0.001)
    }
}