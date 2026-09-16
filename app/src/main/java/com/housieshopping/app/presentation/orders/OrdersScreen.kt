package com.housieshopping.app.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.housieshopping.app.navigation.ScreenRoute
import com.housieshopping.app.ui.components.EmptyStateView
import com.housieshopping.app.ui.components.HousieButton
import com.housieshopping.app.ui.components.StatusChip

@Composable
fun OrdersScreen(
    onNavigate: (String) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "My Orders 📦",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.orders.isEmpty()) {
            EmptyStateView(
                title = "No Orders Placed Yet",
                message = "Your material orders will appear here for live site tracking.",
                buttonText = "Start Shopping",
                onButtonClick = { onNavigate(ScreenRoute.Home.route) }
            )
        } else {
            LazyColumn {
                items(uiState.orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onNavigate(ScreenRoute.OrderDetails.createRoute(order.id)) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.orderNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusChip(status = order.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Placed: ${order.createdAt}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Total: ₹${order.totalAmount.toInt()} (${order.items.size} items)",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row {
                                HousieButton(
                                    text = "Track Live",
                                    onClick = { onNavigate(ScreenRoute.LiveTracking.createRoute(order.id)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 6.dp)
                                )
                                HousieButton(
                                    text = "Invoice",
                                    isOutlined = true,
                                    onClick = { onNavigate(ScreenRoute.Invoice.createRoute(order.id)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
