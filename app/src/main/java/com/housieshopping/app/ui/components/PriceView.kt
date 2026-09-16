package com.housieshopping.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriceView(
    price: Double,
    mrp: Double? = null,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₹${String.format("%,.0f", price)}",
            style = if (isLarge) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        if (mrp != null && mrp > price) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "₹${String.format("%,.0f", mrp)}",
                style = if (isLarge) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(6.dp))
            val discountPercent = (((mrp - price) / mrp) * 100).toInt()
            Text(
                text = "$discountPercent% OFF",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
