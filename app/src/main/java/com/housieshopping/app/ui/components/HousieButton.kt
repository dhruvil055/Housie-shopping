package com.housieshopping.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HousieButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isOutlined: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    var lastClickTime by remember { mutableStateOf(0L) }

    val safeClick = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 600L && !isLoading && enabled) {
            lastClickTime = currentTime
            onClick()
        }
    }

    if (isOutlined) {
        OutlinedButton(
            onClick = safeClick,
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            ButtonContent(text = text, isLoading = isLoading, color = containerColor)
        }
    } else {
        Button(
            onClick = safeClick,
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            ButtonContent(text = text, isLoading = isLoading, color = contentColor)
        }
    }
}

@Composable
private fun ButtonContent(text: String, isLoading: Boolean, color: Color) {
    Box(contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = color,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
