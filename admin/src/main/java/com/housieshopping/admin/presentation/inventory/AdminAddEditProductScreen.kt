package com.housieshopping.admin.presentation.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.housieshopping.admin.domain.model.AdminProduct
import com.housieshopping.admin.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditProductScreen(
    productId: String,
    viewModel: AdminInventoryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val existingProduct = uiState.products.find { it.id == productId }

    var title by remember { mutableStateOf(existingProduct?.title ?: "") }
    var brand by remember { mutableStateOf(existingProduct?.brand ?: "") }
    var categoryName by remember { mutableStateOf(existingProduct?.categoryName ?: "Cement") }
    var price by remember { mutableStateOf(existingProduct?.price?.toInt()?.toString() ?: "") }
    var mrp by remember { mutableStateOf(existingProduct?.mrp?.toInt()?.toString() ?: "") }
    var stock by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "100") }
    var grade by remember { mutableStateOf(existingProduct?.grade ?: "53 Grade") }
    var unit by remember { mutableStateOf(existingProduct?.unit ?: "Bag / Unit") }
    var imageUrl by remember { mutableStateOf(existingProduct?.imageUrl ?: "") }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == "new") "Add New Product 🏗️" else "Edit Product Details ✏️", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Product Title / Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand / Manufacturer") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Selling Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = mrp,
                    onValueChange = { mrp = it },
                    label = { Text("MRP (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = grade,
                    onValueChange = { grade = it },
                    label = { Text("Grade / Spec") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL (Unsplash or CDN)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Product Description & Material Specs") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull()
                    val parsedMrp = mrp.toDoubleOrNull()
                    val parsedStock = stock.toIntOrNull()

                    if (title.isEmpty() || brand.isEmpty() || parsedPrice == null || parsedStock == null) {
                        errorMessage = "Please fill in title, brand, valid price and stock quantity."
                        return@Button
                    }

                    val updatedProduct = AdminProduct(
                        id = if (productId == "new") "" else productId,
                        title = title,
                        description = description.ifEmpty { "High quality building construction material." },
                        brand = brand,
                        categoryName = categoryName,
                        price = parsedPrice,
                        mrp = parsedMrp ?: parsedPrice,
                        stock = parsedStock,
                        imageUrl = imageUrl,
                        isActive = true,
                        grade = grade,
                        unit = unit
                    )

                    viewModel.saveProduct(updatedProduct) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text(text = if (productId == "new") "Save New Product 💾" else "Update Product Details 💾", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
