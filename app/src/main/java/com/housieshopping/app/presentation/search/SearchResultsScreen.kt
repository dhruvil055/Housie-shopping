package com.housieshopping.app.presentation.search

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.housieshopping.app.navigation.ScreenRoute
import com.housieshopping.app.ui.components.EmptyStateView
import com.housieshopping.app.ui.components.ProductCard
import com.housieshopping.app.ui.components.SearchBarView

@Composable
fun SearchResultsScreen(
    query: String,
    onNavigate: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(query) {
        viewModel.search(query)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        SearchBarView(
            query = query,
            onQueryChange = { viewModel.search(it) },
            onFilterClick = { onNavigate(ScreenRoute.Filters.route) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Results for \"$query\"",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.searchResults.isEmpty() && !uiState.isLoading) {
            EmptyStateView(
                title = "No Materials Found",
                message = "We couldn't find any items matching \"$query\". Try checking the spelling or use broader search terms.",
                buttonText = "Browse Categories",
                onButtonClick = { onNavigate(ScreenRoute.Categories.route) }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.searchResults) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onNavigate(ScreenRoute.ProductDetails.createRoute(product.id)) },
                        onAddToCart = { viewModel.addToCart(product) },
                        onToggleWishlist = { viewModel.toggleWishlist(product) },
                        isWishlisted = uiState.wishlistedIds.contains(product.id),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
