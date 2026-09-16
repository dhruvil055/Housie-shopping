package com.housieshopping.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.navigation.ScreenRoute
import com.housieshopping.app.ui.components.BannerCarousel
import com.housieshopping.app.ui.components.CategoryCard
import com.housieshopping.app.ui.components.ProductCard
import com.housieshopping.app.ui.components.SearchBarView

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Location Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(ScreenRoute.SelectLocation.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Deliver to Site",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.locationName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            IconButton(onClick = { onNavigate(ScreenRoute.Notifications.route) }) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }

        // Search Bar
        PaddingValues(horizontal = 16.dp).let {
            SearchBarView(
                query = "",
                onQueryChange = {},
                readOnly = true,
                onSearchClick = { onNavigate(ScreenRoute.Search.route) },
                onFilterClick = { onNavigate(ScreenRoute.Filters.route) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Promotional Banner
            if (uiState.banners.isNotEmpty()) {
                BannerCarousel(
                    banners = uiState.banners,
                    onBannerClick = { banner ->
                        onNavigate(ScreenRoute.CategoryProducts.createRoute(banner.targetId, banner.title))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Categories
            SectionHeader(title = "Shop by Category", onViewAll = { onNavigate(ScreenRoute.Categories.route) })
            LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
                items(uiState.categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = {
                            onNavigate(ScreenRoute.CategoryProducts.createRoute(category.id, category.name))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Deals of the Day
            if (uiState.dealsOfDay.isNotEmpty()) {
                SectionHeader(title = "Deals of the Day 🔥")
                ProductRow(
                    products = uiState.dealsOfDay,
                    wishlistedIds = uiState.wishlistedIds,
                    onProductClick = { onNavigate(ScreenRoute.ProductDetails.createRoute(it.id)) },
                    onAddToCart = { viewModel.addToCart(it) },
                    onToggleWishlist = { viewModel.toggleWishlist(it) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Best Sellers
            if (uiState.bestSellers.isNotEmpty()) {
                SectionHeader(title = "Best Selling Materials 🏆")
                ProductRow(
                    products = uiState.bestSellers,
                    wishlistedIds = uiState.wishlistedIds,
                    onProductClick = { onNavigate(ScreenRoute.ProductDetails.createRoute(it.id)) },
                    onAddToCart = { viewModel.addToCart(it) },
                    onToggleWishlist = { viewModel.toggleWishlist(it) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Featured
            if (uiState.featuredProducts.isNotEmpty()) {
                SectionHeader(title = "Featured Heavy Materials 🏗️")
                ProductRow(
                    products = uiState.featuredProducts,
                    wishlistedIds = uiState.wishlistedIds,
                    onProductClick = { onNavigate(ScreenRoute.ProductDetails.createRoute(it.id)) },
                    onAddToCart = { viewModel.addToCart(it) },
                    onToggleWishlist = { viewModel.toggleWishlist(it) }
                )
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onViewAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (onViewAll != null) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
        }
    }
}

@Composable
fun ProductRow(
    products: List<Product>,
    wishlistedIds: Set<String>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product) },
                onAddToCart = { onAddToCart(product) },
                onToggleWishlist = { onToggleWishlist(product) },
                isWishlisted = wishlistedIds.contains(product.id),
                modifier = Modifier.padding(end = 12.dp)
            )
        }
    }
}
