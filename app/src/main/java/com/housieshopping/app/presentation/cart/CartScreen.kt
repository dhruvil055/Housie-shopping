package com.housieshopping.app.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.housieshopping.app.navigation.ScreenRoute
import com.housieshopping.app.ui.components.EmptyStateView
import com.housieshopping.app.ui.components.HousieButton
import com.housieshopping.app.ui.components.QuantitySelector
import com.housieshopping.app.ui.theme.SuccessGreen

@Composable
fun CartScreen(
    onNavigate: (String) -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val summary = uiState.summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Shopping Cart 🛒",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.items.isEmpty()) {
            EmptyStateView(
                title = "Your Cart is Empty",
                message = "Add cement, steel, tools or fixtures to calculate delivery.",
                buttonText = "Start Shopping",
                onButtonClick = { onNavigate(ScreenRoute.Home.route) }
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = item.product.images.firstOrNull(),
                                contentDescription = item.product.title,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                if (item.selectedVariant != null) {
                                    Text(
                                        text = "Variant: ${item.selectedVariant.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "₹${item.unitPrice.toInt()} / unit",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    QuantitySelector(
                                        quantity = item.quantity,
                                        onIncrease = { viewModel.updateQuantity(item.id, item.quantity + 1) },
                                        onDecrease = { viewModel.updateQuantity(item.id, item.quantity - 1) }
                                    )

                                    IconButton(onClick = { viewModel.removeItem(item.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon Card
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(ScreenRoute.Coupons.route) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (summary?.appliedCouponCode != null) "Applied: ${summary.appliedCouponCode}" else "Apply Coupon or Offer Code",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            if (summary?.appliedCouponCode != null) {
                                Text(
                                    text = "Remove",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.removeCoupon() }
                                )
                            }
                        }
                    }
                }

                // Price Breakup Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (summary != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Order Summary",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                PriceRow("Subtotal", "₹${summary.subtotal.toInt()}")
                                PriceRow("Product Savings", "-₹${summary.totalDiscount.toInt()}", color = SuccessGreen)
                                if (summary.couponDiscount > 0) {
                                    PriceRow("Coupon Discount", "-₹${summary.couponDiscount.toInt()}", color = SuccessGreen)
                                }
                                PriceRow("Estimated GST (18%)", "₹${summary.taxAmount.toInt()}")
                                PriceRow("Site Delivery Fee", if (summary.deliveryFee == 0.0) "FREE" else "₹${summary.deliveryFee.toInt()}")

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                PriceRow("Grand Total", "₹${summary.grandTotal.toInt()}", isBold = true)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HousieButton(
                text = "Proceed to Checkout (₹${summary?.grandTotal?.toInt() ?: 0})",
                onClick = {
                    viewModel.validateAndProceed {
                        onNavigate(ScreenRoute.SelectAddress.route)
                    }
                }
            )
        }
    }
}

@Composable
fun PriceRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = color
        )
    }
}
