package com.housieshopping.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBarView(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search cement, TMT, paint, tools...",
    onSearchClick: (() -> Unit)? = null,
    onFilterClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .then(
                if (readOnly && onSearchClick != null) {
                    Modifier.clickable(onClick = onSearchClick)
                } else Modifier
            )
            .padding(horizontal = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (query.isNotBlank()) query else placeholder,
            style = MaterialTheme.typography.bodyLarge,
            color = if (query.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray,
            modifier = Modifier.weight(1f)
        )

        if (onFilterClick != null) {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
