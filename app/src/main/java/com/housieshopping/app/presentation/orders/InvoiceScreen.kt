package com.housieshopping.app.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.housieshopping.app.ui.components.HousieButton

@Composable
fun InvoiceScreen(
    orderId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Tax Invoice 📄",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Housie Building Materials Pvt. Ltd.", fontWeight = FontWeight.Bold)
                Text(text = "GSTIN: 07AAAAA0000A1Z5")
                Text(text = "Invoice No: INV-2026-$orderId")
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "B2B / B2C Tax Compliant Invoice generated for tax filing.")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HousieButton(
            text = "Download Invoice PDF",
            onClick = {}
        )
    }
}
