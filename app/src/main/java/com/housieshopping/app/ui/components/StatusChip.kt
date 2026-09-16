package com.housieshopping.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.housieshopping.app.domain.model.OrderStatus
import com.housieshopping.app.ui.theme.ErrorRed
import com.housieshopping.app.ui.theme.OrangePrimary
import com.housieshopping.app.ui.theme.SuccessGreen
import com.housieshopping.app.ui.theme.WarningYellow

@Composable
fun StatusChip(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        OrderStatus.PLACED, OrderStatus.CONFIRMED -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Confirmed")
        OrderStatus.PACKING, OrderStatus.READY_FOR_PICKUP -> Triple(Color(0xFFFEF3C7), WarningYellow, "Packing")
        OrderStatus.OUT_FOR_DELIVERY -> Triple(Color(0xFFFFEDD5), OrangePrimary, "Out for Delivery")
        OrderStatus.DELIVERED -> Triple(Color(0xFFDCFCE7), SuccessGreen, "Delivered")
        OrderStatus.CANCELLED -> Triple(Color(0xFFFEE2E2), ErrorRed, "Cancelled")
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
