package com.housieshopping.app.presentation.location

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
import com.housieshopping.app.navigation.ScreenRoute
import com.housieshopping.app.ui.components.HousieButton
import com.housieshopping.app.ui.components.HousieTextField

@Composable
fun AddressConfirmationScreen(
    lat: Double,
    lng: Double,
    onNavigate: (String) -> Unit
) {
    var houseFlat by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("Sector 14, MG Road") }
    var landmark by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "Confirm Exact Details 📍",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        HousieTextField(
            value = houseFlat,
            onValueChange = { houseFlat = it },
            label = "House / Flat / Plot / Site Number"
        )

        Spacer(modifier = Modifier.height(16.dp))

        HousieTextField(
            value = area,
            onValueChange = { area = it },
            label = "Street / Area"
        )

        Spacer(modifier = Modifier.height(16.dp))

        HousieTextField(
            value = landmark,
            onValueChange = { landmark = it },
            label = "Landmark (Optional)"
        )

        Spacer(modifier = Modifier.height(32.dp))

        HousieButton(
            text = "Save & Continue",
            onClick = { onNavigate(ScreenRoute.Home.route) }
        )
    }
}
