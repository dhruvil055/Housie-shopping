package com.housieshopping.app.presentation.support

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
import com.housieshopping.app.ui.components.HousieButton
import com.housieshopping.app.ui.components.HousieTextField

@Composable
fun SupportTicketScreen(
    onNavigateBack: () -> Unit,
    viewModel: SupportViewModel = hiltViewModel()
) {
    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Delivery Issue") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Create Support Ticket 🎫",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        HousieTextField(
            value = subject,
            onValueChange = { subject = it },
            label = "Subject"
        )

        Spacer(modifier = Modifier.height(16.dp))

        HousieTextField(
            value = category,
            onValueChange = { category = it },
            label = "Category (Delivery, Billing, Quality)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        HousieTextField(
            value = description,
            onValueChange = { description = it },
            label = "Issue Description",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        HousieButton(
            text = "Submit Ticket",
            onClick = {
                if (subject.isNotBlank() && description.isNotBlank()) {
                    viewModel.createTicket(subject, category, description) {
                        onNavigateBack()
                    }
                }
            }
        )
    }
}
