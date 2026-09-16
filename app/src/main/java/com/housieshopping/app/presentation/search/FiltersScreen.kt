package com.housieshopping.app.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.housieshopping.app.domain.model.FilterOptions
import com.housieshopping.app.domain.model.SortOption
import com.housieshopping.app.ui.components.HousieButton

@Composable
fun FiltersScreen(
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var inStockOnly by remember { mutableStateOf(false) }
    var fastDeliveryOnly by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Filter Products ⚙️",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Availability",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = inStockOnly, onCheckedChange = { inStockOnly = it })
                Text("In Stock Only")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = fastDeliveryOnly, onCheckedChange = { fastDeliveryOnly = it })
                Text("Fast Site Delivery (Within 24 Hours)")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            HousieButton(
                text = "Clear All",
                isOutlined = true,
                onClick = {
                    viewModel.updateFilters(FilterOptions())
                    onNavigateBack()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            HousieButton(
                text = "Apply Filters",
                onClick = {
                    viewModel.updateFilters(
                        FilterOptions(
                            inStockOnly = inStockOnly,
                            fastDeliveryOnly = fastDeliveryOnly
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }
    }
}
