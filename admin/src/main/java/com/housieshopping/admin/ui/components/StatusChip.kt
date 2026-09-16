package com.housieshopping.admin.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "delivered", "active", "resolved", "paid" -> Pair(Color(0xFF2E7D32).copy(alpha = 0.15f), Color(0xFF2E7D32))
        "in transit", "shipped", "in progress" -> Pair(Color(0xFF1565C0).copy(alpha = 0.15f), Color(0xFF1565C0))
        "pending", "packing", "open" -> Pair(Color(0xFFE65100).copy(alpha = 0.15f), Color(0xFFE65100))
        "cancelled", "disabled", "closed" -> Pair(Color(0xFFD32F2F).copy(alpha = 0.15f), Color(0xFFD32F2F))
        else -> Pair(Color.Gray.copy(alpha = 0.15f), Color.DarkGray)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
