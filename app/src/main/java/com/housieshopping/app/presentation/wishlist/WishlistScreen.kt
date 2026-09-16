package com.housieshopping.app.presentation.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.housieshopping.app.navigation.ScreenRoute
import com.housieshopping.app.ui.components.EmptyStateView
import com.housieshopping.app.ui.components.ProductCard

@Composable
fun WishlistScreen(
    onNavigate: (String) -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val items by viewModel.wishlist.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "My Saved Materials ❤️",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            EmptyStateView(
                title = "Your Wishlist is Empty",
                message = "Save cement, steel, tools or fixtures to review later.",
                buttonText = "Explore Home Materials",
                onButtonClick = { onNavigate(ScreenRoute.Home.route) },
                icon = Icons.Default.FavoriteBorder
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(items) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onNavigate(ScreenRoute.ProductDetails.createRoute(product.id)) },
                        onAddToCart = { viewModel.moveToCart(product) },
                        onToggleWishlist = { viewModel.removeFromWishlist(product.id) },
                        isWishlisted = true,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
