package com.housieshopping.app.presentation.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.housieshopping.app.domain.model.AddressType
import com.housieshopping.app.ui.components.HousieButton
import com.housieshopping.app.ui.components.HousieTextField

@Composable
fun AddEditAddressScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddressViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("Rahul Sharma") }
    var phone by remember { mutableStateOf("+91 98765 43210") }
    var houseFlat by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Gurugram") }
    var state by remember { mutableStateOf("Haryana") }
    var pinCode by remember { mutableStateOf("122002") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add Delivery Location 🏗️",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        HousieTextField(value = name, onValueChange = { name = it }, label = "Contact Person Name")
        Spacer(modifier = Modifier.height(10.dp))
        HousieTextField(value = phone, onValueChange = { phone = it }, label = "Mobile Number")
        Spacer(modifier = Modifier.height(10.dp))
        HousieTextField(value = houseFlat, onValueChange = { houseFlat = it }, label = "House / Flat / Site Plot No.")
        Spacer(modifier = Modifier.height(10.dp))
        HousieTextField(value = street, onValueChange = { street = it }, label = "Street / Sector / Colony")
        Spacer(modifier = Modifier.height(10.dp))
        HousieTextField(value = area, onValueChange = { area = it }, label = "Area / Landmark")
        Spacer(modifier = Modifier.height(10.dp))
        HousieTextField(value = city, onValueChange = { city = it }, label = "City")
        Spacer(modifier = Modifier.height(10.dp))
        HousieTextField(value = state, onValueChange = { state = it }, label = "State")
        Spacer(modifier = Modifier.height(10.dp))
        HousieTextField(value = pinCode, onValueChange = { pinCode = it }, label = "PIN Code")

        Spacer(modifier = Modifier.height(24.dp))

        HousieButton(
            text = "Save Address",
            onClick = {
                if (houseFlat.isNotBlank() && street.isNotBlank()) {
                    viewModel.saveAddress(
                        fullName = name,
                        phone = phone,
                        houseFlat = houseFlat,
                        street = street,
                        area = area,
                        city = city,
                        state = state,
                        pinCode = pinCode,
                        landmark = null,
                        type = AddressType.HOME,
                        isDefault = true
                    ) {
                        onNavigateBack()
                    }
                }
            }
        )
    }
}
