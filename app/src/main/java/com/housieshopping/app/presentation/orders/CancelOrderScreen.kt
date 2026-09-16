package com.housieshopping.app.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.housieshopping.app.navigation.ScreenRoute
import com.housieshopping.app.ui.components.HousieButton
import com.housieshopping.app.ui.components.HousieTextField

@Composable
fun CancelOrderScreen(
    orderId: String,
    onNavigate: (String) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    var reason by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "Cancel Order #$orderId 🚫",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        HousieTextField(
            value = reason,
            onValueChange = { reason = it },
            label = "Reason for Cancellation",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        HousieButton(
            text = "Confirm Cancellation",
            onClick = {
                if (reason.isNotBlank()) {
                    viewModel.cancelOrder(orderId, reason) {
                        onNavigate(ScreenRoute.RefundStatus.createRoute(orderId))
                    }
                }
            }
        )
    }
}
